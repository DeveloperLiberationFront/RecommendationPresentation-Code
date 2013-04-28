package fatbastard.ui.command.handlers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xml.sax.SAXException;

import fatbastard.ui.ExperimentShell;
import fatbastard.ui.RecoDetailsShell;
import fatbastard.ui.core.Recommendation;
import fatbastard.ui.core.Task;
import fatbastard.ui.core.TaskReader;
import fatbastard.ui.utils.Utils;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
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
				}
			}
		});

		return null;
	}
}
