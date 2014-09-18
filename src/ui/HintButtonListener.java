package ui;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ui.core.Recommendation;
import ui.core.Task;
import ui.utils.Utils;

public class HintButtonListener implements SelectionListener {

    private ExperimentShell experimentShell;

    public HintButtonListener(ExperimentShell shell) {
        this.experimentShell = shell;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        // compute recommendations for the this task
        ArrayList<String> commandUsageArrayList = Utils.commandUsage.get(Utils.currentTaskNumber);

        HashSet<String> commandUsageHashSet = (commandUsageArrayList == null ? new HashSet<String>() : new HashSet<String>(
                commandUsageArrayList));

        Task task = Utils.taskList.get(Utils.currentTaskNumber);
        HashSet<String> taskRecommendations = task.getRecommendations();

        taskRecommendations.removeAll(commandUsageHashSet);

        try {
            for (String str : taskRecommendations) {
                Recommendation reco = new Recommendation(str);

                boolean alreadyThere = Utils.allRecommendations.contains(reco);

                if (!alreadyThere) { // add condition to the reco if its a new recommendation, then add it to the allRecommendations list

                    // this means this is being recommended for the first time

                    if (!Utils.commandUsageVector.containsKey(reco.getId())) { // check if the user has already used this command, if yes, then don't recommend it
                        reco.addCondition();
                        Utils.allRecommendations.add(reco);
                        boolean added = Utils.currentTaskRecos.add(reco);
                        if (added) {
                            Utils.recommendationQueue.add(reco);
                        }
                    }
                }
                else { // get it from the allRecommendations set and use the same condition

                    // this means the recommendation has been shown before at least once
                    Integer commandCount = Utils.commandUsageVector.get(reco.getId());
                    if (commandCount == null)
                        commandCount = 0;
                    if (commandCount < 4) { // Now we want to check if the user has used the command more than 3 times, if yes, then we don't show the recommendation
                        // (we assume that he learned the command)
                        for (Recommendation r : Utils.allRecommendations) {
                            if (r.equals(reco)) {
                                reco = r;
                                break;
                            }
                        }
                        boolean added = Utils.currentTaskRecos.add(reco);
                        if (added) {
                            Utils.recommendationQueue.add(reco);
                        }
                    }
                }

            }
            experimentShell.refreshRecommendationLists();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub

    }

}
