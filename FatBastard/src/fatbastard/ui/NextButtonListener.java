package fatbastard.ui;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Label;

import fatbastard.ui.core.Recommendation;
import fatbastard.ui.core.Recorder;
import fatbastard.ui.core.Task;
import fatbastard.ui.utils.Utils;

public class NextButtonListener implements SelectionListener {

	private ExperimentShell shell;

	public NextButtonListener(ExperimentShell experimentShell) {
		this.shell = experimentShell;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		
		//compute recommendations for the previous task
		ArrayList<String> commandUsageArrayList = Utils.commandUsage.get(Utils.currentTaskNumber);
		HashSet<String> commandUsageHashSet = (commandUsageArrayList == null ? new HashSet<String>() : new HashSet<String>(commandUsageArrayList));

		Task task = Utils.taskList.get(Utils.currentTaskNumber);
		HashSet<String> taskRecommendations = task.getRecommendations();

		taskRecommendations.removeAll(commandUsageHashSet);
		Utils.recommendationQueue.clear(); // added to do the second trial of the study. This trial flushes the recommendation list for every task.
		Utils.currentTaskRecos.clear(); // added to do the second trial of the study. This trial flushes the recommendation list for every task.
		
		shell.clearRecommendations();
		
		/*try{
			for (String str : taskRecommendations){
				Recommendation reco = new Recommendation(str);
				boolean added = Utils.allRecommendations.add(reco);
				if (added){
					Utils.recommendationQueue.add(reco);
				}
			}
			shell.refreshRecommendationLists();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}*/

		//record the responses
		Recorder.getInstance().recordResponse(shell.getText().getText());
		
		//new task
		Utils.currentTaskNumber++;
		if (Utils.currentTaskNumber == Utils.taskList.size()){
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
