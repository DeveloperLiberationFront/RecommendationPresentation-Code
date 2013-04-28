package sample;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.xml.sax.SAXException;

import fatbastard.ui.RecoDetailsShell;
import fatbastard.ui.RecommendationList;
import fatbastard.ui.core.Recommendation;
import fatbastard.ui.core.Task;
import fatbastard.ui.core.TaskReader;
import fatbastard.ui.utils.Utils;

public class SampleClass {

	public static void main(String[] args) throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
		
		System.out.println(System.getenv("USERPROFILE"));
		System.out.println(System.getProperty("user.home"));
		
		/*String resourceFolder = Utils.getResourceFolder();
		String recoFolder = resourceFolder + "/recommendations";
		
		TaskReader reader = new TaskReader();
		ArrayList<Task> taskList = reader.getTaskList();
		
		for (Task t : taskList){
			HashSet<String> recos = t.getRecommendations();
			for (String r : recos){
				File dir = new File(recoFolder + "/" + r);
				dir.mkdir();
			}
		}*/
		
		/*Display display = new Display();
		Recommendation reco = new Recommendation("Recommendaiton");
		reco.setLabel("Recommendaiton");
		RecoDetailsShell shell = new RecoDetailsShell(reco, display);
		
		Shell shell = new Shell(display);

		shell.setLayout(new FillLayout());

		SashForm sash = new SashForm(shell, SWT.BORDER | SWT.VERTICAL);
		//Composite sash = new Composite(shell, SWT.BORDER | SWT.VERTICAL);
		sash.setLayout(new FillLayout());
		
		RecommendationList cll = new RecommendationList(sash, SWT.NONE);

		for (int i = 0; i < 5; i++){
			Recommendation reco = new Recommendation("New Recommendation");
			reco.setLabel("New Recommendation");
			cll.addRecommendation(reco);
		}
		
		RecommendationList cll2 = new RecommendationList(sash, SWT.NONE);
		for (int i = 0; i < 5; i++){
			Recommendation reco = new Recommendation("New Recommendation");
			reco.setLabel("New Recommendation 2");
			cll2.addRecommendation(reco);
		}
		

		shell.open();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();*/



	}
}
