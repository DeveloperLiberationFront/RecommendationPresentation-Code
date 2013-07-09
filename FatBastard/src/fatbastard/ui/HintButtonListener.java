package fatbastard.ui;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import fatbastard.ui.core.Recommendation;
import fatbastard.ui.core.Task;
import fatbastard.ui.utils.Utils;

public class HintButtonListener implements SelectionListener {

	private ExperimentShell experimentShell;

	public HintButtonListener(ExperimentShell shell) {
		this.experimentShell = shell;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		//compute recommendations for the this task
		ArrayList<String> commandUsageArrayList = Utils.commandUsage.get(Utils.currentTaskNumber);
		HashSet<String> commandUsageHashSet = (commandUsageArrayList == null ? new HashSet<String>() : new HashSet<String>(commandUsageArrayList));

		Task task = Utils.taskList.get(Utils.currentTaskNumber);
		HashSet<String> taskRecommendations = task.getRecommendations();

		taskRecommendations.removeAll(commandUsageHashSet);
		
		try{
			for (String str : taskRecommendations){
				Recommendation reco = new Recommendation(str);
				
				boolean alreadyThere = Utils.allRecommendations.contains(reco);
				
				if (!alreadyThere) {
					reco.addCondition();
					Utils.allRecommendations.add(reco);
				}
				else {
					for (Recommendation r : Utils.allRecommendations){
						if (r.equals(reco)){
							reco = r;
						}
					}
				}
				
				boolean added = Utils.currentTaskRecos.add(reco);
				if (added){
					Utils.recommendationQueue.add(reco);
				}
			}
			experimentShell.refreshRecommendationLists();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}

	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

}
