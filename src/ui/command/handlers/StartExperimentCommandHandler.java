package ui.command.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import ui.ExperimentShell;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class StartExperimentCommandHandler extends AbstractHandler {
    /**
     * The constructor.
     */
    public StartExperimentCommandHandler() {
    }

    /**
     * the command has been executed, so extract extract the needed information
     * from the application context.
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

        Display display = PlatformUI.getWorkbench().getDisplay();
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    ExperimentShell shell = ExperimentShell.getInstance();
                    shell.open();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });

        return null;
    }
}
