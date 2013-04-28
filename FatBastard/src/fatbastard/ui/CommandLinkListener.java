package fatbastard.ui;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.xml.sax.SAXException;

import fatbastard.ui.core.Recorder;

public abstract class CommandLinkListener implements MouseListener {

	ExperimentShell shell;
	RecommendationList newList;
	RecommendationList oldList;
	
	public CommandLinkListener() {
		shell = ExperimentShell.getInstance();
		newList = shell.getNewRecoList();
		oldList = shell.getOldRecoList();
	}
	
	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseUp(MouseEvent e) {
	}

}
