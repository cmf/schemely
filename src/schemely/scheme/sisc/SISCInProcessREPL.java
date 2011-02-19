package schemely.scheme.sisc;

import com.intellij.execution.ExecutionBundle;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ExportableOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import com.intellij.ui.content.Content;
import schemely.repl.SchemeConsole;
import schemely.repl.SchemeConsoleView;
import schemely.repl.actions.NewSchemeConsoleAction;
import schemely.scheme.REPLException;
import schemely.scheme.Scheme;
import schemely.scheme.common.ReaderThread;
import sisc.REPL;
import sisc.data.Procedure;
import sisc.data.SchemeThread;
import sisc.env.DynamicEnvironment;
import sisc.interpreter.AppContext;
import sisc.interpreter.Context;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Colin Fleming
 */
public class SISCInProcessREPL implements Scheme.REPL
{
  private enum State
  {
    INITIAL, RUNNING, STOPPED
  }

  private final Project project;
  private final SchemeConsoleView consoleView;
  private volatile State state = State.INITIAL;
  private final AtomicBoolean terminated = new AtomicBoolean(false);
  private final CountDownLatch replFinished = new CountDownLatch(1);
  private Writer toREPL;
  private Reader fromREPL;

  ExecutorService executor = Executors.newCachedThreadPool();

  public SISCInProcessREPL(Project project, SchemeConsoleView consoleView)
  {
    this.project = project;
    this.consoleView = consoleView;
  }

  @Override
  public void start() throws REPLException
  {
    verifyState(State.INITIAL);

    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    URLClassLoader classLoader = new URLClassLoader(getLibraryURLs(), AppContext.class.getClassLoader());

    try
    {
      Thread.currentThread().setContextClassLoader(classLoader);

      AppContext appContext = new AppContext();
      Context.setDefaultAppContext(appContext);
      URL heap = AppContext.findHeap(null);
      if (heap == null)
      {
        throw new REPLException("Heap is null");
      }
      try
      {
        if (!appContext.addHeap(AppContext.openHeap(heap)))
        {
          throw new REPLException("Error adding heap");
        }
      }
      catch (ClassNotFoundException e)
      {
        throw new REPLException("Error adding heap", e);
      }
      catch (IOException e)
      {
        throw new REPLException("Error opening heap", e);
      }

      PipedInputStream replInputStream = new PipedInputStream();
      PipedOutputStream replOutputStream = new PipedOutputStream();

      Charset charset = EncodingManager.getInstance().getDefaultCharset();
      try
      {
        toREPL = new OutputStreamWriter(new PipedOutputStream(replInputStream), charset);
        fromREPL = new InputStreamReader(new PipedInputStream(replOutputStream), charset);
      }
      catch (IOException e)
      {
        throw new REPLException("Error opening pipe", e);
      }

      DynamicEnvironment dynamicEnvironment = new DynamicEnvironment(appContext, replInputStream, replOutputStream);
      LocalREPLThread replThread = new LocalREPLThread(dynamicEnvironment, REPL.getCliProc(appContext));
      REPL repl = new REPL(replThread);
      repl.go();

      executor.execute(new ReaderThread(fromREPL, terminated)
      {
        @Override
        protected void textAvailable(String text)
        {
          SISCProcessREPLHandler.processOutput(consoleView.getConsole(), text);
        }
      });
    }
    finally
    {
      Thread.currentThread().setContextClassLoader(oldClassLoader);
    }

    state = State.RUNNING;
  }

  @Override
  public void stop()
  {
    verifyState(State.RUNNING);

    execute("(exit)");

    try
    {
      replFinished.await();
    }
    catch (InterruptedException ignored)
    {
      Thread.currentThread().interrupt();
    }
    state = State.STOPPED;
  }

  @Override
  public void execute(String command)
  {
    verifyState(State.RUNNING);

    try
    {
      toREPL.write(command + "\n");
      toREPL.flush();
    }
    catch (IOException ignored)
    {
    }
  }

  @Override
  public void clear()
  {
    verifyState(State.RUNNING);

    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isActive()
  {
    return state == State.RUNNING;
  }

  @Override
  public SchemeConsoleView getConsoleView()
  {
    return consoleView;
  }

  @Override
  public AnAction[] getToolbarActions()
  {
    return new AnAction[] { new StopAction(), new CloseAction() };
  }

  private class StopAction extends DumbAwareAction
  {
    private StopAction()
    {
      copyShortcutFrom(ActionManager.getInstance().getAction(IdeActions.ACTION_STOP_PROGRAM));
      Presentation templatePresentation = getTemplatePresentation();
      templatePresentation.setIcon(IconLoader.getIcon("/actions/suspend.png"));
      templatePresentation.setText("Stop REPL");
      templatePresentation.setDescription(null);
    }

    @Override
    public void update(AnActionEvent e)
    {
      e.getPresentation().setEnabled(isActive());
    }

    @Override
    public void actionPerformed(AnActionEvent e)
    {
      stop();
    }
  }

  private class CloseAction extends DumbAwareAction
  {
    private CloseAction()
    {
      copyShortcutFrom(ActionManager.getInstance().getAction(IdeActions.ACTION_CLOSE));
      Presentation templatePresentation = getTemplatePresentation();
      templatePresentation.setIcon(IconLoader.getIcon("/actions/cancel.png"));
      templatePresentation.setText("Close REPL tab");
      templatePresentation.setDescription(null);
    }

    @Override
    public void actionPerformed(AnActionEvent e)
    {
      if (isActive())
      {
        stop();
      }

      Content content = consoleView.getConsole().getConsoleEditor().getUserData(NewSchemeConsoleAction.CONTENT_KEY);
      if (content != null)
      {
        content.getManager().removeContent(content, true);
      }
    }
  }

  private void verifyState(State expected)
  {
    if (state != expected)
    {
      throw new IllegalStateException("Expected state " + expected + ", found " + state);
    }
  }

  private URL[] getLibraryURLs() throws REPLException
  {
    List<URL> urls = new ArrayList<URL>();

    Module module = findSuitableModule();
    ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
    OrderEntry[] entries = moduleRootManager.getOrderEntries();
    Set<VirtualFile> virtualFiles = new HashSet<VirtualFile>();
    for (OrderEntry orderEntry : entries)
    {
      if (orderEntry instanceof ExportableOrderEntry)
      {
        virtualFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.COMPILATION_CLASSES)));
        //
        //      // Add module sources
        //      if (orderEntry instanceof ModuleSourceOrderEntry)
        //      {
        //        virtualFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.SOURCES)));
        //      }
      }
    }

    try
    {
      for (VirtualFile file : virtualFiles)
      {
        String path = file.getPath();
        int jarSeparatorIndex = path.indexOf(JarFileSystem.JAR_SEPARATOR);
        if (jarSeparatorIndex > 0)
        {
          path = path.substring(0, jarSeparatorIndex);
          urls.add(new URL("file://" + path));
        }
      }
    }
    catch (MalformedURLException e)
    {
      throw new REPLException("Bad library URL: " + e.getMessage());
    }

    return urls.toArray(new URL[urls.size()]);
  }

  private Module findSuitableModule()
  {
    Module[] modules = ModuleManager.getInstance(project).getModules();
    int i = 0;
    while (i < modules.length && !isSuitableModule(modules[i]))
    {
      i++;
    }
    return modules[i];
  }

  private boolean isSuitableModule(Module module)
  {
    ModuleType type = module.getModuleType();
    return ModuleTypeManager.getInstance().isClasspathProvider(type) &&
           ((type instanceof JavaModuleType) || "PLUGIN_MODULE".equals(type.getId()));
  }

  public class LocalREPLThread extends SchemeThread
  {
    public LocalREPLThread(DynamicEnvironment environment, Procedure thunk)
    {
      super(environment, thunk);
    }

    public void run()
    {
      super.run();
      terminated.set(true);

      try
      {
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        executor.shutdownNow();
      }
      catch (InterruptedException ignored)
      {
      }

      try
      {
        toREPL.close();
        fromREPL.close();
      }
      catch (IOException ignored)
      {
      }

      hideEditor();

      SISCInProcessREPL.this.state = State.STOPPED;

      replFinished.countDown();
    }

  }

  private void hideEditor()
  {
    ApplicationManager.getApplication().invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        SchemeConsole console = consoleView.getConsole();
        JComponent component = consoleView.getComponent();
        Container parent = component.getParent();
        if (parent instanceof JPanel)
        {
          EditorEx historyViewer = console.getHistoryViewer();
          parent.add(historyViewer.getComponent());
          parent.remove(component);
          ((JPanel) parent).updateUI();
        }
      }
    });
  }
}
