package ui;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

public abstract class CommandLinkAdapter implements MouseListener {

    ExperimentShell shell;

    RecommendationList newList;

    RecommendationList oldList;

    public CommandLinkAdapter() {
        shell = ExperimentShell.getInstance();
        newList = shell.getNewRecoList();
        // oldList = shell.getOldRecoList();

    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {
        //let the children overwrite what they want
    }

    @Override
    public void mouseUp(MouseEvent e) {
        // let the children overwrite what they want
    }

}
