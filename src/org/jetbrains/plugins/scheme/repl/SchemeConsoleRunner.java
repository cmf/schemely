package org.jetbrains.plugins.scheme.repl;

import com.google.common.collect.Lists;
import com.intellij.execution.*;
import com.intellij.execution.configurations.CommandLineBuilder;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.actions.CloseAction;
import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleSourceOrderEntry;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.PairProcessor;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.scheme.SchemeBundle;
import org.jetbrains.plugins.scheme.utils.SchemeConfigUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SchemeConsoleRunner
{
  public static final String REPL_TITLE;
  public static final String EXECUTE_ACTION_IMMEDIATELY_ID = "Scheme.Console.Execute.Immediately";
  public static final String EXECUTE_ACTION_ID = "Scheme.Console.Execute";
  private final Module myModule;
  private final Project myProject;
  private final String myConsoleTitle;
  private final CommandLineArgumentsProvider myProvider;
  private final String myWorkingDir;
  private final ConsoleHistoryModel myHistory;
  private SchemeConsoleView myConsoleView;
  private ProcessHandler myProcessHandler;
  private SchemeConsoleExecuteActionHandler myConsoleExecuteActionHandler;
  private AnAction myRunAction;

  public SchemeConsoleRunner(@NotNull Module module,
                             @NotNull String consoleTitle,
                             @NotNull CommandLineArgumentsProvider provider,
                             @Nullable String workingDir)
  {
    this.myModule = module;
    this.myProject = module.getProject();
    this.myConsoleTitle = consoleTitle;
    this.myProvider = provider;
    this.myWorkingDir = workingDir;
    this.myHistory = new ConsoleHistoryModel();
  }

  public static void run(@NotNull Module module, String workingDir, String[] statements2execute) throws CantRunException
  {
    final List<String> args = createRuntimeArgs(module, workingDir);

    CommandLineArgumentsProvider provider = new CommandLineArgumentsProvider()
    {
      public String[] getArguments()
      {
        return args.toArray(new String[args.size()]);
      }

      public boolean passParentEnvs()
      {
        return false;
      }

      public Map<String, String> getAdditionalEnvs()
      {
        return new HashMap<String, String>();
      }
    };
    Project project = module.getProject();
    SchemeConsoleRunner runner = new SchemeConsoleRunner(module, REPL_TITLE, provider, workingDir);
    try
    {
      runner.initAndRun(statements2execute);
    }
    catch (ExecutionException e)
    {
      ExecutionHelper.showErrors(project, Arrays.<Exception>asList(e), REPL_TITLE, null);
    }
  }

  public void initAndRun(String[] statements2execute) throws ExecutionException
  {
    Process process = createProcess(this.myProvider);
    this.myConsoleView = createConsoleView();
    this.myProcessHandler =
      new SchemeConsoleProcessHandler(process, this.myProvider.getCommandLineString(), getLanguageConsole());
    this.myConsoleExecuteActionHandler =
      new SchemeConsoleExecuteActionHandler(getProcessHandler(), getProject(), false);
    getLanguageConsole().setExecuteHandler(this.myConsoleExecuteActionHandler);
    myConsoleView.getConsole().setPrompt("> ");

    ProcessTerminatedListener.attach(this.myProcessHandler);

    this.myProcessHandler.addProcessListener(new ProcessAdapter()
    {
      public void processTerminated(ProcessEvent event)
      {
        SchemeConsoleRunner.this.myRunAction.getTemplatePresentation().setEnabled(false);
        SchemeConsoleRunner.this.myConsoleView.getConsole().setPrompt("");
        SchemeConsoleRunner.this.myConsoleView.getConsole().getConsoleEditor().setRendererMode(true);
        ApplicationManager.getApplication().invokeLater(new Runnable()
        {
          public void run()
          {
            SchemeConsoleRunner.this.myConsoleView.getConsole().getConsoleEditor().getComponent().updateUI();
          }
        });
      }
    });
    this.myConsoleView.attachToProcess(this.myProcessHandler);

    Executor defaultExecutor = ExecutorRegistry.getInstance().getExecutorById(DefaultRunExecutor.EXECUTOR_ID);
    DefaultActionGroup toolbarActions = new DefaultActionGroup();
    ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("unknown", toolbarActions, false);

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(actionToolbar.getComponent(), "West");
    panel.add(this.myConsoleView.getComponent(), "Center");

    RunContentDescriptor myDescriptor =
      new RunContentDescriptor(this.myConsoleView, this.myProcessHandler, panel, this.myConsoleTitle);

    AnAction[] actions = fillToolBarActions(toolbarActions, defaultExecutor, myDescriptor);
    registerActionShortcuts(actions, getLanguageConsole().getConsoleEditor().getComponent());
    registerActionShortcuts(actions, panel);
    panel.updateUI();

    createAndRegisterEnterAction(panel);

    ExecutionManager.getInstance(this.myProject).getContentManager().showRunContent(defaultExecutor, myDescriptor);

    ToolWindow window = ToolWindowManager.getInstance(this.myProject).getToolWindow(defaultExecutor.getId());
    window.activate(new Runnable()
    {
      public void run()
      {
        IdeFocusManager.getInstance(SchemeConsoleRunner.this.myProject)
          .requestFocus(SchemeConsoleRunner.this.getLanguageConsole().getCurrentEditor().getContentComponent(), true);
      }
    });
    this.myProcessHandler.startNotify();

    SchemeConsole console = getConsoleView().getConsole();
    for (String statement : statements2execute)
    {
      String st = statement + "\n";
      SchemeConsoleHighlightingUtil.processOutput(console, st, ProcessOutputTypes.SYSTEM);
      SchemeConsoleExecuteActionHandler actionHandler = getConsoleExecuteActionHandler();
      actionHandler.processLine(st);
    }
  }

  private void createAndRegisterEnterAction(JPanel panel)
  {
    AnAction enterAction =
      new SchemeConsoleEnterAction(getLanguageConsole(), getProcessHandler(), getConsoleExecuteActionHandler());
    enterAction.registerCustomShortcutSet(enterAction.getShortcutSet(),
                                          getLanguageConsole().getConsoleEditor().getComponent());
    enterAction.registerCustomShortcutSet(enterAction.getShortcutSet(), panel);
  }

  private static void registerActionShortcuts(AnAction[] actions, JComponent component)
  {
    for (AnAction action : actions)
    {
      if (action.getShortcutSet() != null)
      {
        action.registerCustomShortcutSet(action.getShortcutSet(), component);
      }
    }
  }

  protected AnAction[] fillToolBarActions(DefaultActionGroup toolbarActions,
                                          Executor defaultExecutor,
                                          RunContentDescriptor myDescriptor)
  {
    List<AnAction> actionList = Lists.newArrayList();

    AnAction stopAction = createStopAction();
    actionList.add(stopAction);

    AnAction closeAction = createCloseAction(defaultExecutor, myDescriptor);
    actionList.add(closeAction);

    List<AnAction> executionActions = createConsoleExecActions(getLanguageConsole(),
                                                               this.myProcessHandler,
                                                               this.myConsoleExecuteActionHandler,
                                                               getHistoryModel());

    this.myRunAction = executionActions.get(0);
    actionList.addAll(executionActions);

    actionList.add(CommonActionsManager.getInstance().createHelpAction("interactive_console"));

    AnAction[] actions = actionList.toArray(new AnAction[actionList.size()]);
    toolbarActions.addAll(actions);
    return actions;
  }

  public static List<AnAction> createConsoleExecActions(final SchemeConsole languageConsole,
                                                        ProcessHandler processHandler,
                                                        SchemeConsoleExecuteActionHandler consoleExecuteActionHandler,
                                                        ConsoleHistoryModel historyModel)
  {
    AnAction runImmediatelyAction =
      new SchemeExecuteImmediatelyAction(languageConsole, processHandler, consoleExecuteActionHandler);

    PairProcessor<AnActionEvent, String> historyProcessor = new PairProcessor<AnActionEvent, String>()
    {
      public boolean process(AnActionEvent e, final String s)
      {
        new WriteCommandAction(languageConsole.getProject(), languageConsole.getFile())
        {
          protected void run(Result result) throws Throwable
          {
            languageConsole.getEditorDocument().setText(s == null ? "" : s);
          }
        }.execute();

        return true;
      }
    };
    EditorEx consoleEditor = languageConsole.getConsoleEditor();
    AnAction upAction =
      ConsoleHistoryModel.createConsoleHistoryUpAction(AbstractConsoleRunnerWithHistory.createCanMoveUpComputable(
        consoleEditor), historyModel, historyProcessor);

    AnAction downAction =
      ConsoleHistoryModel.createConsoleHistoryDownAction(AbstractConsoleRunnerWithHistory.createCanMoveDownComputable(
        consoleEditor), historyModel, historyProcessor);

    List<AnAction> list = new ArrayList<AnAction>();
    list.add(runImmediatelyAction);
    list.add(downAction);
    list.add(upAction);

    return list;
  }

  protected AnAction createCloseAction(Executor defaultExecutor, RunContentDescriptor myDescriptor)
  {
    return new CloseAction(defaultExecutor, myDescriptor, this.myProject);
  }

  protected AnAction createStopAction()
  {
    return ActionManager.getInstance().getAction("Stop");
  }

  protected SchemeConsoleView createConsoleView()
  {
    return new SchemeConsoleView(getProject(), getConsoleTitle(), getHistoryModel(), getConsoleExecuteActionHandler());
  }

  private static List<String> createRuntimeArgs(Module module, String workingDir) throws CantRunException
  {
    JavaParameters params = new JavaParameters();
    params.configureByModule(module, JavaParameters.JDK_AND_CLASSES);

    boolean sdkConfigured = SchemeConfigUtil.isKawaConfigured(module);
    if (!sdkConfigured)
    {
      String jarPath = SchemeConfigUtil.KAWA_SDK;
      assert (jarPath != null);
      params.getClassPath().add(jarPath);
    }

    Set<VirtualFile> cpVFiles = new HashSet<VirtualFile>();
    ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
    OrderEntry[] entries = moduleRootManager.getOrderEntries();
    for (OrderEntry orderEntry : entries)
    {
      if ((orderEntry instanceof ModuleSourceOrderEntry))
      {
        cpVFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.SOURCES)));
      }
    }

    for (VirtualFile file : cpVFiles)
    {
      params.getClassPath().add(file.getPath());
    }

    String replPath = PathUtil.getJarPathForClass(KawaRepl.class);
    params.getClassPath().add(replPath);

    params.setMainClass(KawaRepl.class.getName());
    params.setWorkingDirectory(new File(workingDir));

    Project project = PlatformDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext());
    GeneralCommandLine line = CommandLineBuilder.createFromJavaParameters(params, project, true);

    Sdk sdk = params.getJdk();
    assert (sdk != null);
    SdkType type = sdk.getSdkType();
    String executablePath = ((JavaSdkType) type).getVMExecutablePath(sdk);

    List<String> cmd = new ArrayList<String>();
    cmd.add(executablePath);
    cmd.addAll(line.getParametersList().getList());

    //    TODO
    //    if (!sdkConfigured)
    //    {
    //      SchemeConfigUtil.warningDefaultSchemeJar(module);
    //    }
    return cmd;
  }

  protected Process createProcess(CommandLineArgumentsProvider provider) throws ExecutionException
  {
    List<String> cmd = createRuntimeArgs(this.myModule, getWorkingDir());

    Process process = null;
    try
    {
      process =
        Runtime.getRuntime().exec(cmd.toArray(new String[cmd.size()]), new String[0], new File(getWorkingDir()));
    }
    catch (IOException e)
    {
      ExecutionHelper.showErrors(getProject(), Arrays.<Exception>asList(e), REPL_TITLE, null);
    }

    return process;
  }

  public Project getProject()
  {
    return this.myProject;
  }

  public String getConsoleTitle()
  {
    return this.myConsoleTitle;
  }

  public SchemeConsole getLanguageConsole()
  {
    return this.myConsoleView.getConsole();
  }

  public SchemeConsoleView getConsoleView()
  {
    return this.myConsoleView;
  }

  public ProcessHandler getProcessHandler()
  {
    return this.myProcessHandler;
  }

  public SchemeConsoleExecuteActionHandler getConsoleExecuteActionHandler()
  {
    return this.myConsoleExecuteActionHandler;
  }

  public ConsoleHistoryModel getHistoryModel()
  {
    return this.myHistory;
  }

  public String getWorkingDir()
  {
    return this.myWorkingDir;
  }

  static
  {
    REPL_TITLE = SchemeBundle.message("repl.toolWindowName");
  }
}
