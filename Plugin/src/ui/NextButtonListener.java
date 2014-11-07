package ui;

import java.util.HashSet;
import java.util.List;

import org.eclipse.epp.usagedata.internal.gathering.UsageDataCaptureActivator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import data.Recorder;
import ui.core.Task;
import ui.utils.Utils;

public class NextButtonListener implements SelectionListener {

    private ExperimentShell shell;

    public NextButtonListener(ExperimentShell experimentShell) {
        this.shell = experimentShell;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        
        String response = shell.getCurrentAnswer();
        
        if ("".equals(response) || "Enter your answer here...".equals(response)) {
            return;
        }

        try {
            Recorder.getInstance().dumpRecords();
        } catch (Exception e1) {
            UsageDataCaptureActivator.logException("problem dumping", e1);
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


        // record the responses
        
        Recorder.getInstance().recordResponse(response);

        // new task
        Utils.currentTaskNumber++;
        if (Utils.currentTaskNumber == Utils.taskList.size()) {
            shell.close();
            return;
        }
        
        HintButtonListener.showRecommendations(shell);
        
        shell.setTaskNumber(Utils.currentTaskNumber + 1, Utils.taskList.size());
        shell.setTaskDescription(Utils.taskList.get(Utils.currentTaskNumber).getTaskDetails());
        shell.setAnswerBoxText("");

        if (Utils.currentTaskNumber == (Utils.taskList.size() - 1))
            shell.getBtnNext().setText("Finish");
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {

    }

}
