package schemely.repl.actions;

import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.process.ConsoleHistoryModel;
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.PairProcessor;
import schemely.SchemeIcons;
import schemely.repl.SchemeConsole;
import schemely.repl.SchemeConsoleView;
import schemely.repl.toolwindow.REPLToolWindowFactory;
import schemely.scheme.REPLException;
import schemely.scheme.Scheme;
import schemely.scheme.SchemeImplementation;
import schemely.utils.Actions;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class NewSchemeConsoleAction extends AnAction implements DumbAware
{
  public static final Key<Scheme.REPL> REPL_KEY = Key.create("Scheme.REPL");
  public static final Key<Content> CONTENT_KEY = Key.create("Scheme.REPL.Content");

  protected static final String CONSOLE_TITLE = "Console title";

  public NewSchemeConsoleAction()
  {
    getTemplatePresentation().setIcon(SchemeIcons.SCHEME_ICON);
  }

  public void update(AnActionEvent e)
  {
    Module m = Actions.getModule(e);
    Presentation presentation = e.getPresentation();
    if (m == null)
    {
      presentation.setEnabled(false);
      return;
    }

    Scheme scheme = SchemeImplementation.from(m.getProject());
    presentation.setEnabled(scheme.supportsInProcessREPL());
    super.update(e);
  }

  public void actionPerformed(AnActionEvent event)
  {
    Module module = Actions.getModule(event);
    assert (module != null) : "Module is null";
    //    String path = com.intellij.openapi.roots.ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();

    // Find the tool window
    final Project project = module.getProject();
    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(REPLToolWindowFactory.TOOL_WINDOW_ID);
    assert (toolWindow != null) : "ToolWindow is null";

    // Create the console
    ConsoleHistoryModel history = new ConsoleHistoryModel();
    SchemeConsoleView consoleView = new SchemeConsoleView(project, CONSOLE_TITLE, history);
    final SchemeConsole schemeConsole = consoleView.getConsole();
    schemeConsole.setPrompt("> ");

    // Create toolbar
    DefaultActionGroup toolbarActions = new DefaultActionGroup();
    ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("unknown", toolbarActions, false);

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.add(actionToolbar.getComponent(), "West");
    panel.add(consoleView.getComponent(), "Center");

    Scheme scheme = SchemeImplementation.from(project);
    Scheme.REPL repl = scheme.getNewInProcessREPL(project, consoleView);

    AnAction[] actions;
    try
    {
      actions = getToolbarActions(repl);
      repl.start();
    }
    catch (REPLException e)
    {
      ExecutionHelper.showErrors(project, Arrays.<Exception>asList(e), CONSOLE_TITLE, null);
      return;
    }

    toolbarActions.addAll(actions);

    registerActionShortcuts(actions, schemeConsole.getConsoleEditor().getComponent());
    registerActionShortcuts(actions, panel);
    panel.updateUI();

    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    final Content content = contentFactory.createContent(panel, "Local", false);
    final ContentManager contentManager = toolWindow.getContentManager();
    contentManager.addContent(content);
    content.putUserData(REPL_KEY, repl);
    schemeConsole.getConsoleEditor().putUserData(REPL_KEY, repl);
    schemeConsole.getConsoleEditor().putUserData(CONTENT_KEY, content);

    if (toolWindow.isActive())
    {
      contentManager.addContent(content);
      contentManager.setSelectedContent(content);
    }
    else
    {
      toolWindow.activate(new Runnable()
      {
        @Override
        public void run()
        {
          contentManager.addContent(content);
          contentManager.setSelectedContent(content);
        }
      });
    }

    toolWindow.show(new Runnable()
    {
      @Override
      public void run()
      {
        IdeFocusManager focusManager = IdeFocusManager.getInstance(project);
        focusManager.requestFocus(schemeConsole.getCurrentEditor().getContentComponent(), true);
      }
    });
  }

  private AnAction[] getToolbarActions(Scheme.REPL repl) throws REPLException
  {
    java.util.List<AnAction> actions = new ArrayList<AnAction>();
    actions.addAll(Arrays.asList(repl.getToolbarActions()));

    final SchemeConsole console = repl.getConsoleView().getConsole();
    PairProcessor<AnActionEvent, String> historyProcessor = new PairProcessor<AnActionEvent, String>()
    {
      @Override
      public boolean process(AnActionEvent e, final String s)
      {
        new WriteCommandAction(console.getProject(), console.getFile())
        {
          @Override
          protected void run(Result result) throws Throwable
          {
            console.getEditorDocument().setText(s == null ? "" : s);
            console.getCurrentEditor().getCaretModel().moveToOffset(s == null ? 0 : s.length());
          }
        }.execute();

        return true;
      }
    };

    EditorEx consoleEditor = console.getConsoleEditor();

    Computable<Boolean> upComputable = AbstractConsoleRunnerWithHistory.createCanMoveUpComputable(consoleEditor);
    actions.add(ConsoleHistoryModel.createConsoleHistoryUpAction(upComputable,
                                                                 console.getHistoryModel(),
                                                                 historyProcessor));
    Computable<Boolean> downComputable = AbstractConsoleRunnerWithHistory.createCanMoveDownComputable(consoleEditor);
    actions.add(ConsoleHistoryModel.createConsoleHistoryDownAction(downComputable,
                                                                   console.getHistoryModel(),
                                                                   historyProcessor));

    return actions.toArray(new AnAction[actions.size()]);
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
}
