package fatbastard.ui.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import fatbastard.ui.utils.Utils;

public class Recorder {
	
	private static Recorder instance;
	
	private ArrayList<Recommendation> clicks;
	private ArrayList<Recommendation> recommendations;
	private ArrayList<String> responses;
	
	private Recorder() {
		clicks = new ArrayList<Recommendation>();
		recommendations = new ArrayList<Recommendation>();
		responses = new ArrayList<String>();
	}
	
	public static Recorder getInstance(){
		if (instance == null)
			instance = new Recorder();
		return instance;
	}

	public void recordClick(Recommendation reco){
		clicks.add(reco);
	}
	
	public void recordRecommendation(Recommendation reco){
		recommendations.add(reco);
	}
	
	public void recordResponse(String response){
		responses.add(response);
	}
	
	public void dumpRecords() throws FileNotFoundException {
		Date date= new Date();
		String timeStamp = new Timestamp(date.getTime()).toString();
		String dirName = Utils.getUserFolder() + File.separator + timeStamp;
		
		File dir = new File(dirName);
		dir.mkdirs();
		
		//dump the clicks
		String fileNameClicks = dirName + File.separator + "clicks.xml"; 
		PrintWriter out = new PrintWriter(fileNameClicks);
		out.println("<experiment>");
		for (Recommendation reco : clicks){
			out.println("<click>");
			out.println("<commandid>" + reco.getId() + "</commandid>");
			out.println("<condition>" + reco.getConditionShortString() + "</condition>");
			out.println("</click>");
			
		}
		out.println("</experiment>");
		out.close();
		
		//dump everything that was recommended to the user
		String fileNameRecos = dirName + File.separator + "recos.xml";
		out = new PrintWriter(fileNameRecos);
		out.println("<experiment>");
		for (Recommendation reco : recommendations){
			out.println("<recommendation>");
			out.println("<commandid>" + reco.getId() + "</commandid>");
			out.println("<condition>" + reco.getConditionShortString() + "</condition>");
			out.println("</recommendation>");
			
		}
		out.println("</experiment>");
		out.close();
		
		//dump all commands the user used
		String fileNameUsage = dirName + File.separator + "usage.xml"; 
		Set<Integer> keys = Utils.commandUsage.keySet();
		out = new PrintWriter(fileNameUsage);
		out.println("<experiment>");
		for (int key : keys){
			out.println("<task>");
			ArrayList<String> usage = Utils.commandUsage.get(key);
			out.println("<number>" + key + "</number>");
			out.println("<usedcommands>");
			for(String str : usage){
				out.println("<id>" + str + "</id>");
			}
			out.println("</usedcommands>");
			out.println("</task>");
		}
		out.println("</experiment>");
		out.close();
		
		//dump user's responses to the tasks
		String fileNameResponses = dirName + File.separator + "responses.xml"; 
		out = new PrintWriter(fileNameResponses);
		out.println("<experiment>");
		for (String response : responses){
			out.println("<response>" + response + "</response>");
		}
		out.println("</experiment>");
		out.close();
	}
}
