package schemely.repl;

import com.google.common.collect.Lists;
import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.CommandLineArgumentsProvider;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.actions.CloseAction;
import com.intellij.ide.CommonActionsManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.PairProcessor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.SchemeBundle;
import schemely.scheme.Scheme;
import schemely.scheme.Scheme.REPL;
import schemely.scheme.SchemeImplementation;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.List;
import java.util.Map;

public class SchemeConsoleRunner
{
  public static final String REPL_TITLE;
  @NonNls
  public static final String EXECUTE_ACTION_IMMEDIATELY_ID = "Scheme.Console.Execute.Immediately";
  @NonNls
  public static final String EXECUTE_ACTION_ID = "Scheme.Console.Execute";
  private static final String[] EMPTY_ENV = new String[0];

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
    myModule = module;
    myProject = module.getProject();
    myConsoleTitle = consoleTitle;
    myProvider = provider;
    myWorkingDir = workingDir;
    myHistory = new ConsoleHistoryModel();
  }

  public static void run(@NotNull Module module, String workingDir, String[] statements2execute) throws CantRunException
  {
    Scheme scheme = SchemeImplementation.from(module.getProject());
    final List<String> args = scheme.getRepl().createRuntimeArgs(module, workingDir);

    CommandLineArgumentsProvider provider = new CommandLineArgumentsProvider()
    {
      @Override
      public String[] getArguments()
      {
        return args.toArray(new String[args.size()]);
      }

      @Override
      public boolean passParentEnvs()
      {
        return false;
      }

      @Override
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
    Process process = createProcess(myProvider);
    myConsoleView = createConsoleView();
    myProcessHandler =
      new SchemeConsoleProcessHandler(process, myProvider.getCommandLineString(), getLanguageConsole());
    myConsoleExecuteActionHandler =
      new SchemeConsoleExecuteActionHandler(myProcessHandler, myProject, false);
    getLanguageConsole().setExecuteHandler(myConsoleExecuteActionHandler);
    myConsoleView.getConsole().setPrompt("> ");

    ProcessTerminatedListener.attach(myProcessHandler);

    myProcessHandler.addProcessListener(new ProcessAdapter()
    {
      @Override
      public void processTerminated(ProcessEvent event)
      {
        myRunAction.getTemplatePresentation().setEnabled(false);
        myConsoleView.getConsole().setPrompt("");
        myConsoleView.getConsole().getConsoleEditor().setRendererMode(true);
        ApplicationManager.getApplication().invokeLater(new Runnable()
        {
          @Override
          public void run()
          {
            myConsoleView.getConsole().getConsoleEditor().getComponent().updateUI();
          }
        });
      }
    });
    myConsoleView.attachToProcess(myProcessHandler);

    Executor defaultExecutor = ExecutorRegistry.getInstance().getExecutorById(DefaultRunExecutor.EXECUTOR_ID);
    DefaultActionGroup toolbarActions = new DefaultActionGroup();
    ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("unknown", toolbarActions, false);

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(actionToolbar.getComponent(), "West");
    panel.add(myConsoleView.getComponent(), "Center");

    RunContentDescriptor myDescriptor =
      new RunContentDescriptor(myConsoleView, myProcessHandler, panel, myConsoleTitle);

    AnAction[] actions = fillToolBarActions(toolbarActions, defaultExecutor, myDescriptor);
    registerActionShortcuts(actions, getLanguageConsole().getConsoleEditor().getComponent());
    registerActionShortcuts(actions, panel);
    panel.updateUI();

    createAndRegisterEnterAction(panel);

    ExecutionManager.getInstance(myProject).getContentManager().showRunContent(defaultExecutor, myDescriptor);

    ToolWindow window = ToolWindowManager.getInstance(myProject).getToolWindow(defaultExecutor.getId());
    window.activate(new Runnable()
    {
      @Override
      public void run()
      {
        IdeFocusManager.getInstance(myProject)
          .requestFocus(getLanguageConsole().getCurrentEditor().getContentComponent(), true);
      }
    });
    myProcessHandler.startNotify();

    SchemeConsole console = myConsoleView.getConsole();
    for (String statement : statements2execute)
    {
      String st = statement + '\n';
      REPL repl = SchemeImplementation.from(myProject).getRepl();
      repl.processOutput(console, st, ProcessOutputTypes.SYSTEM);
      SchemeConsoleExecuteActionHandler actionHandler = myConsoleExecuteActionHandler;
      actionHandler.processLine(st);
    }
  }

  private void createAndRegisterEnterAction(JPanel panel)
  {
    AnAction enterAction =
      new SchemeConsoleEnterAction(getLanguageConsole(), myProcessHandler, myConsoleExecuteActionHandler);
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
                                                               myProcessHandler,
                                                               myConsoleExecuteActionHandler, myHistory);

    myRunAction = executionActions.get(0);
    actionList.addAll(executionActions);

    actionList.add(CommonActionsManager.getInstance().createHelpAction("interactive_console"));

    AnAction[] actions = actionList.toArray(new AnAction[actionList.size()]);
    toolbarActions.addAll(actions);
    return actions;
  }

  public static List<AnAction> createConsoleExecActions(final SchemeConsole languageConsole,
                                                        ProcessHandler processHandler,
                                                        SchemeConsoleExecuteActionHandler executeActionHandler,
                                                        ConsoleHistoryModel historyModel)
  {
    AnAction runImmediatelyAction =
      new SchemeExecuteImmediatelyAction(languageConsole, processHandler, executeActionHandler);

    PairProcessor<AnActionEvent, String> historyProcessor = new PairProcessor<AnActionEvent, String>()
    {
      @Override
      public boolean process(AnActionEvent e, final String s)
      {
        new WriteCommandAction(languageConsole.getProject(), languageConsole.getFile())
        {
          @Override
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
    return new CloseAction(defaultExecutor, myDescriptor, myProject);
  }

  protected static AnAction createStopAction()
  {
    return ActionManager.getInstance().getAction("Stop");
  }

  protected SchemeConsoleView createConsoleView()
  {
    return new SchemeConsoleView(myProject, myConsoleTitle, myHistory, myConsoleExecuteActionHandler);
  }

  protected Process createProcess(CommandLineArgumentsProvider provider) throws ExecutionException
  {
    Process process = null;
    try
    {
      process = Runtime.getRuntime().exec(provider.getArguments(), EMPTY_ENV, new File(myWorkingDir));
    }
    catch (IOException e)
    {
      ExecutionHelper.showErrors(myProject, Arrays.<Exception>asList(e), REPL_TITLE, null);
    }

    return process;
  }

  public Project getProject()
  {
    return myProject;
  }

  public String getConsoleTitle()
  {
    return myConsoleTitle;
  }

  public SchemeConsole getLanguageConsole()
  {
    return myConsoleView.getConsole();
  }

  public SchemeConsoleView getConsoleView()
  {
    return myConsoleView;
  }

  public ProcessHandler getProcessHandler()
  {
    return myProcessHandler;
  }

  public SchemeConsoleExecuteActionHandler getConsoleExecuteActionHandler()
  {
    return myConsoleExecuteActionHandler;
  }

  public ConsoleHistoryModel getHistoryModel()
  {
    return myHistory;
  }

  public String getWorkingDir()
  {
    return myWorkingDir;
  }

  static
  {
    REPL_TITLE = SchemeBundle.message("repl.toolWindowName");
  }
}
