package ui;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

public abstract class CommandLinkListener implements MouseListener {

    ExperimentShell shell;

    RecommendationList newList;

    RecommendationList oldList;

    public CommandLinkListener() {
        shell = ExperimentShell.getInstance();
        newList = shell.getNewRecoList();
        // oldList = shell.getOldRecoList();

    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {
    }

    @Override
    public void mouseUp(MouseEvent e) {
    }

}
