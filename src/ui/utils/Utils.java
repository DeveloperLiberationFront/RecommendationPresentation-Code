package ui.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.epp.usagedata.internal.gathering.UsageDataCaptureActivator;
import org.osgi.framework.Bundle;

import ui.core.Recommendation;
import ui.core.RecommendationBundle;
import ui.core.Task;

public class Utils {

    public static int currentTaskNumber = 0;

    public static HashMap<Integer, ArrayList<String>> commandUsage = new HashMap<Integer, ArrayList<String>>();

    public static boolean experimentRunning = false;

    public static ArrayList<Task> taskList;

    public static HashSet<Recommendation> allRecommendations = new HashSet<Recommendation>();

    public static TreeSet<Recommendation> recommendationQueue = new TreeSet<Recommendation>();

    public static HashSet<Recommendation> currentTaskRecos = new HashSet<Recommendation>();

    public static HashSet<Recommendation> filterList = new HashSet<Recommendation>();

    public static HashMap<String, Integer> commandUsageVector = new HashMap<String, Integer>();

    public static int conditions[] = new int[4];

    public static void resetGlobals() {
        conditions[0] = 0;
        conditions[1] = 0;
        conditions[2] = 0;
        conditions[3] = 0;

        commandUsage.clear();
        taskList = null;
        experimentRunning = true;
        currentTaskNumber = 0;

        allRecommendations.clear();
        recommendationQueue.clear();
        currentTaskRecos.clear();
        filterList.clear();
        commandUsageVector.clear();
        commandUsage.clear();
    }

    private static File getResourceFile(String bundledPath) {
        try {
            Plugin plugin = UsageDataCaptureActivator.getDefault();
            Bundle bundle = plugin.getBundle();
            URL fileURL = bundle.getEntry(bundledPath);
            return new File(FileLocator.resolve(fileURL).toURI());
        } catch (URISyntaxException | IOException e) {
            UsageDataCaptureActivator.logException("Problem finding resource", e);
            return null;
        }

    }

    public static String getUserFolder() {
        return System.getProperty("user.home") + File.separator + ".ubc";
    }

    public static File getFriendsFile() {
        return getResourceFile("res/friends.txt");
    }

    public static RecommendationBundle getRecommendationsBundle(String id) {
          File xmlFile = getResourceFile("res/recommendations/"+id+"/reco.xml");
          File htmlFile = getResourceFile("res/recommendations/"+id+"/reco.html");
          
          return new RecommendationBundle(xmlFile, htmlFile);
    }

    public static File getTaskList() {
        return getResourceFile("res/tasks.xml");
    }
    
//    private String getFilename() throws URISyntaxException, IOException {
//        return Utils.getResourceFolder() + File.separator + "tasks.xml";
//    }
    
//  private String getRecommendationsFolder(String id)
//  throws URISyntaxException, IOException {
//return Utils.getResourceFolder() + File.separator + "recommendations" + File.separator + id;
//}
}
