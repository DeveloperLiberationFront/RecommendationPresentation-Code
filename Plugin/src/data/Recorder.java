package data;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.epp.usagedata.internal.gathering.UsageDataCaptureActivator;

import ui.core.Recommendation;
import ui.utils.Utils;

/**
 * Queues up the data and then stores it to xml
 * @author KevinLubick
 *
 */
public class Recorder {

    private static Recorder instance;

    private HashMap<Integer, ArrayList<Recommendation>> clicks;

    private HashMap<Integer, ArrayList<Recommendation>> recommendations;

    private ArrayList<String> responses;

    private Recorder() {
        clicks = new HashMap<Integer, ArrayList<Recommendation>>();
        recommendations = new HashMap<Integer, ArrayList<Recommendation>>();
        responses = new ArrayList<String>();
    }

    public static Recorder getInstance() {
        if (instance == null)
            instance = new Recorder();
        return instance;
    }

    public void recordClick(Integer currentTaskNumber, Recommendation reco) {
        if (clicks.containsKey(currentTaskNumber)) {
            ArrayList<Recommendation> list = clicks.get(currentTaskNumber);
            list.add(reco);
            clicks.put(currentTaskNumber, list);
        }
        else {
            ArrayList<Recommendation> list = new ArrayList<Recommendation>();
            list.add(reco);
            clicks.put(currentTaskNumber, list);
        }
    }

    public void recordRecommendation(Integer currentTaskNumber, Recommendation reco) {
        if (recommendations.containsKey(currentTaskNumber)) {
            ArrayList<Recommendation> list = recommendations.get(currentTaskNumber);
            list.add(reco);
            recommendations.put(currentTaskNumber, list);
        }
        else {
            ArrayList<Recommendation> list = new ArrayList<Recommendation>();
            list.add(reco);
            recommendations.put(currentTaskNumber, list);
        }
    }

    public void recordResponse(String response) {
        responses.add(response);
    }

    public void dumpRecords() throws Exception {
        Date date = new Date();
        String timeStamp = new Timestamp(date.getTime()).toString();
        timeStamp = timeStamp.replace(':', '.');
        String dirName = Utils.getUserFolder() + File.separator + timeStamp;

        File dir = new File(dirName);
        boolean success = dir.mkdirs();

        if (!success)
            throw new Exception();

        // dump the clicks
        String fileNameClicks = dirName + File.separator + "clicks.xml";
        try (PrintWriter out = new PrintWriter(fileNameClicks);) {
            out.println("<experiment>");
            for (Integer task : clicks.keySet()) {
                out.println("<task>");
                out.println("<number>" + task + "</number>");
                for (Recommendation reco : clicks.get(task)) {
                    out.println("<recommendation>");
                    out.println("<commandid>" + reco.getId() + "</commandid>");
                    out.println("<condition>" + reco.getConditionShortString() + "</condition>");
                    out.println("</recommendation>");
                }
                out.println("</task>");
            }
            out.println("</experiment>");
        }

        // dump everything that was recommended to the user
        String fileNameRecos = dirName + File.separator + "recos.xml";
        try (PrintWriter out = new PrintWriter(fileNameRecos);) {
            out.println("<experiment>");
            for (Integer task : recommendations.keySet()) {
                out.println("<task>");
                out.println("<number>" + task + "</number>");
                for (Recommendation reco : recommendations.get(task)) {
                    out.println("<recommendation>");
                    out.println("<commandid>" + reco.getId() + "</commandid>");
                    out.println("<condition>" + reco.getConditionShortString() + "</condition>");
                    out.println("</recommendation>");
                }
                out.println("</task>");
            }
            out.println("</experiment>");
        }

        // dump all commands the user used
        String fileNameUsage = dirName + File.separator + "usage.xml";
        
        dumpCommandsUsed(fileNameUsage);

        // dump user's responses to the tasks
        String fileNameResponses = dirName + File.separator + "responses.xml";
        try (PrintWriter out = new PrintWriter(fileNameResponses);) {
            out.println("<experiment>");
            for (String response : responses) {
                out.println("<response>" + response + "</response>");
            }
            out.println("</experiment>");
        }
    }
    
    private static void dumpCommandsUsed(String fileName) {
        try (PrintWriter out = new PrintWriter(fileName);) {
            out.println("<experiment>");
            for (Entry<Integer, List<String>> commandsUsedThisTask: Utils.getCommandsEntrySet()) {
                out.println("<task>");
                out.println("<number>" + commandsUsedThisTask.getKey() + "</number>");
                out.println("<usedcommands>");
                for (String commandId : commandsUsedThisTask.getValue()) {
                    out.println("<id>" + commandId + "</id>");
                }
                out.println("</usedcommands>");
                out.println("</task>");
            }
            out.println("</experiment>");
        }
        catch (Exception e) {
            e.printStackTrace();
            UsageDataCaptureActivator.logException("Problem dumping commandUsages", e);
        }
    }
}
