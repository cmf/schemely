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
import schemely.scheme.SchemeImplementation;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

  private final Project project;
  private final String consoleTitle;
  private final CommandLineArgumentsProvider provider;
  private final String workingDir;
  private final ConsoleHistoryModel history;
  private SchemeConsoleView consoleView;
  private ProcessHandler processHandler;
  private SchemeConsoleExecuteActionHandler executeActionHandler;
  private AnAction runAction;

  public SchemeConsoleRunner(@NotNull Module module,
                             @NotNull String consoleTitle,
                             @NotNull CommandLineArgumentsProvider provider,
                             @Nullable String workingDir)
  {
    project = module.getProject();
    this.consoleTitle = consoleTitle;
    this.provider = provider;
    this.workingDir = workingDir;
    history = new ConsoleHistoryModel();
  }

  public static void run(@NotNull Module module, String workingDir, String[] statements2execute) throws CantRunException
  {
    Scheme scheme = SchemeImplementation.from(module.getProject());
    // TODO
    final List<String> args = null; // scheme.getProcessReplHandler().createRuntimeArgs(module, workingDir);

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
    Process process = createProcess(provider);
    consoleView = new SchemeConsoleView(project, consoleTitle, history);
    processHandler =
      new SchemeConsoleProcessHandler(process, provider.getCommandLineString(), getLanguageConsole());
    executeActionHandler =
      new SchemeConsoleExecuteActionHandler(processHandler, project, false);
    getLanguageConsole().setExecuteHandler(executeActionHandler);
    consoleView.getConsole().setPrompt("> ");

    ProcessTerminatedListener.attach(processHandler);

    processHandler.addProcessListener(new ProcessAdapter()
    {
      @Override
      public void processTerminated(ProcessEvent event)
      {
        runAction.getTemplatePresentation().setEnabled(false);
        consoleView.getConsole().setPrompt("");
        consoleView.getConsole().getConsoleEditor().setRendererMode(true);
        ApplicationManager.getApplication().invokeLater(new Runnable()
        {
          @Override
          public void run()
          {
            consoleView.getConsole().getConsoleEditor().getComponent().updateUI();
          }
        });
      }
    });
    consoleView.attachToProcess(processHandler);

    Executor defaultExecutor = ExecutorRegistry.getInstance().getExecutorById(DefaultRunExecutor.EXECUTOR_ID);

    DefaultActionGroup toolbarActions = new DefaultActionGroup();
    ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("unknown", toolbarActions, false);

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(actionToolbar.getComponent(), "West");
    panel.add(consoleView.getComponent(), "Center");

    RunContentDescriptor descriptor =
      new RunContentDescriptor(consoleView, processHandler, panel, consoleTitle);

    AnAction[] actions = fillToolBarActions(toolbarActions, defaultExecutor, descriptor);
    registerActionShortcuts(actions, getLanguageConsole().getConsoleEditor().getComponent());
    registerActionShortcuts(actions, panel);
    panel.updateUI();

    createAndRegisterEnterAction(panel);

    ExecutionManager.getInstance(project).getContentManager().showRunContent(defaultExecutor, descriptor);

    ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(defaultExecutor.getId());
    window.activate(new Runnable()
    {
      @Override
      public void run()
      {
        IdeFocusManager.getInstance(project)
          .requestFocus(getLanguageConsole().getCurrentEditor().getContentComponent(), true);
      }
    });
    processHandler.startNotify();

    SchemeConsole console = consoleView.getConsole();
    for (String statement : statements2execute)
    {
      String line = statement + '\n';
//      Scheme.ProcessREPLHandler processReplHandler = SchemeImplementation.from(project).getProcessReplHandler();
//      processReplHandler.processOutput(console, line, ProcessOutputTypes.SYSTEM);
      SchemeConsoleExecuteActionHandler actionHandler = executeActionHandler;
      actionHandler.processLine(line);
    }
  }

  private void createAndRegisterEnterAction(JPanel panel)
  {
    AnAction enterAction =
      new SchemeConsoleEnterAction(getLanguageConsole(), processHandler, executeActionHandler);
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
                                          RunContentDescriptor descriptor)
  {
    List<AnAction> actionList = Lists.newArrayList();

    AnAction stopAction = createStopAction();
    actionList.add(stopAction);

    AnAction closeAction = createCloseAction(defaultExecutor, descriptor);
    actionList.add(closeAction);

    List<AnAction> executionActions = createConsoleExecActions(getLanguageConsole(), processHandler,
                                                               executeActionHandler, history);

    runAction = executionActions.get(0);
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

  protected AnAction createCloseAction(Executor defaultExecutor, RunContentDescriptor descriptor)
  {
    return new CloseAction(defaultExecutor, descriptor, project);
  }

  protected static AnAction createStopAction()
  {
    return ActionManager.getInstance().getAction("Stop");
  }

  protected Process createProcess(CommandLineArgumentsProvider provider) throws ExecutionException
  {
    Process process = null;
    try
    {
      process = Runtime.getRuntime().exec(provider.getArguments(), EMPTY_ENV, new File(workingDir));
    }
    catch (IOException e)
    {
      ExecutionHelper.showErrors(project, Arrays.<Exception>asList(e), REPL_TITLE, null);
    }

    return process;
  }

  public Project getProject()
  {
    return project;
  }

  public String getConsoleTitle()
  {
    return consoleTitle;
  }

  public SchemeConsole getLanguageConsole()
  {
    return consoleView.getConsole();
  }

  public SchemeConsoleView getConsoleView()
  {
    return consoleView;
  }

  public ProcessHandler getProcessHandler()
  {
    return processHandler;
  }

  public SchemeConsoleExecuteActionHandler getConsoleExecuteActionHandler()
  {
    return executeActionHandler;
  }

  public ConsoleHistoryModel getHistoryModel()
  {
    return history;
  }

  public String getWorkingDir()
  {
    return workingDir;
  }

  static
  {
    REPL_TITLE = SchemeBundle.message("repl.toolWindowName");
  }
}
