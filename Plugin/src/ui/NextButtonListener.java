package ui;

import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ui.core.Recorder;
import ui.core.Task;
import ui.utils.Utils;

public class NextButtonListener implements SelectionListener {

    private ExperimentShell shell;

    public NextButtonListener(ExperimentShell experimentShell) {
        this.shell = experimentShell;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {

        try {
            Recorder.getInstance().dumpRecords();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // compute recommendations for the previous task
        List<String> commandUsageList = Utils.getCommandsUsedInCurrentTask();
        HashSet<String> commandUsageHashSet = (commandUsageList == null ? new HashSet<String>() : new HashSet<String>(
                commandUsageList));

        Task task = Utils.taskList.get(Utils.currentTaskNumber);
        HashSet<String> taskRecommendations = task.getRecommendations();

        taskRecommendations.removeAll(commandUsageHashSet);
        Utils.recommendationQueue.clear(); // added to do the second trial of the study. This trial flushes the recommendation list for every task.
        Utils.currentTaskRecos.clear(); // added to do the second trial of the study. This trial flushes the recommendation list for every task.

        shell.clearRecommendations();

        /*
         * try{
         * for (String str : taskRecommendations){
         * Recommendation reco = new Recommendation(str);
         * boolean added = Utils.allRecommendations.add(reco);
         * if (added){
         * Utils.recommendationQueue.add(reco);
         * }
         * }
         * shell.refreshRecommendationLists();
         * }
         * catch (Exception ex){
         * ex.printStackTrace();
         * }
         */

        // record the responses
        Recorder.getInstance().recordResponse(shell.getText().getText());

        // new task
        Utils.currentTaskNumber++;
        if (Utils.currentTaskNumber == Utils.taskList.size()) {
            shell.close();
            return;
        }
        shell.getTaskNumberLabel().setText("Task " + (Utils.currentTaskNumber + 1) + " of " + Utils.taskList.size());
        shell.getTaskLabel().setText(Utils.taskList.get(Utils.currentTaskNumber).getTaskDetails());
        shell.getText().setText("");

        if (Utils.currentTaskNumber == (Utils.taskList.size() - 1))
            shell.getBtnNext().setText("Finish");
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {

    }

}
