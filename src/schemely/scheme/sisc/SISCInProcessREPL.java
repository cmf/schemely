package schemely.scheme.sisc;

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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.ui.content.Content;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.repl.SchemeConsole;
import schemely.repl.SchemeConsoleElement;
import schemely.repl.SchemeConsoleView;
import schemely.repl.actions.NewSchemeConsoleAction;
import schemely.scheme.REPLException;
import schemely.scheme.common.ReaderThread;
import sisc.REPL;
import sisc.data.Procedure;
import sisc.data.SchemeThread;
import sisc.data.SchemeVoid;
import sisc.data.Symbol;
import sisc.data.Value;
import sisc.env.DynamicEnvironment;
import sisc.env.MemorySymEnv;
import sisc.env.SymbolicEnvironment;
import sisc.interpreter.AppContext;
import sisc.interpreter.Context;
import sisc.interpreter.Interpreter;
import sisc.interpreter.SchemeCaller;
import sisc.interpreter.SchemeException;
import sisc.util.Util;

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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Colin Fleming
 */
public class SISCInProcessREPL implements schemely.scheme.REPL
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
  private AppContext appContext;

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

      appContext = new AppContext();
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
    return new AnAction[]{new StopAction(), new CloseAction()};
  }

  @Override
  public Collection<PsiNamedElement> getSymbolVariants(PsiManager manager, SchemeIdentifier symbol)
  {
    GetCompletions getCompletions = new GetCompletions(manager);
    try
    {
      Context.execute(appContext, getCompletions);
    }
    catch (SchemeException ignored)
    {
      // TODO
    }
    return getCompletions.getCompletions();
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

  private static class GetCompletions implements SchemeCaller
  {
    private final Collection<PsiNamedElement> completions = new ArrayList<PsiNamedElement>();
    private final PsiManager psiManager;

    public GetCompletions(PsiManager psiManager)
    {
      this.psiManager = psiManager;
    }

    @Override
    public Object execute(Interpreter r) throws SchemeException
    {
      SymbolicEnvironment symbolicEnvironment = r.getContextEnv(Util.TOPLEVEL);

      SymbolicEnvironment syntaxEnvironment = symbolicEnvironment.getSidecarEnvironment(Util.EXPSC);
      addCompletions(syntaxEnvironment);

      SymbolicEnvironment topEnvironment = symbolicEnvironment.getSidecarEnvironment(Util.EXPTOP);
      addCompletions(topEnvironment);

      addCompletions(symbolicEnvironment);

      return new SchemeVoid();
    }

    private void addCompletions(SymbolicEnvironment symbolicEnvironment)
    {
      if (symbolicEnvironment instanceof MemorySymEnv)
      {
        MemorySymEnv symEnv = (MemorySymEnv) symbolicEnvironment;
        Map symbolMap = symEnv.symbolMap;
        for (Object object : symbolMap.keySet())
        {
          if (object instanceof Symbol)
          {
            String name = object.toString();
            if (!hasCompletion(name))
            {
              completions.add(new SchemeConsoleElement(psiManager, name));
            }
          }
        }
      }
    }

    private boolean hasCompletion(String name)
    {
      for (PsiElement completion : completions)
      {
        if (((SchemeConsoleElement) completion).getName().equals(name))
        {
          return true;
        }
      }
      return false;
    }

    private void dump(SymbolicEnvironment environment)
    {
      Set<SymbolicEnvironment> seen = Collections.newSetFromMap(new IdentityHashMap<SymbolicEnvironment, Boolean>());
      dump(environment, seen, "");
    }

    private void dump(SymbolicEnvironment environment, Set<SymbolicEnvironment> seen, String indent)
    {
      if (environment == null)
      {
        return;
      }

      if (seen.contains(environment))
      {
        System.out.println(indent + environment.getName() + ": already seen");
        return;
      }

      seen.add(environment);
      if (environment.getName() != null)
      {
        System.out.println(indent + environment.getName());
      }

      if (environment instanceof MemorySymEnv)
      {
        MemorySymEnv symEnv = (MemorySymEnv) environment;
        Map<Symbol, Integer> symbolMap = symEnv.symbolMap;
        for (Symbol key : sortSymbols(symbolMap.keySet()))
        {
          Value value = symEnv.env[symbolMap.get(key)];
          System.out.println(indent + key + ": " + value.getClass().getSimpleName());
        }

        if (environment.getParent() != null)
        {
          System.out.println(indent + "Parent:");
          dump(environment.getParent(), seen, indent + "  ");
        }

        Map<Symbol, SymbolicEnvironment> sidecarMap = symEnv.sidecars;
        if (!sidecarMap.isEmpty())
        {
          String nextIndent = indent + "  ";
          System.out.println(nextIndent + "Sidecars:");
          for (Symbol key : sortSymbols(sidecarMap.keySet()))
          {
            System.out.println(nextIndent + key);
            dump(sidecarMap.get(key), seen, nextIndent + "  ");
          }
        }
      }
      else
      {
        System.out.println(indent + environment.getName() + ": is a " + environment.getClass().getSimpleName());
      }
    }

    public Collection<Symbol> sortSymbols(Collection<Symbol> unsorted)
    {
      List<Symbol> symbols = new ArrayList<Symbol>(unsorted);
      Collections.sort(symbols, new Comparator<Symbol>()
      {
        @Override
        public int compare(Symbol o1, Symbol o2)
        {
          return o1.toString().compareTo(o2.toString());
        }
      });
      return symbols;
    }

    public Collection<PsiNamedElement> getCompletions()
    {
      return completions;
    }
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
}
