package ui.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
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

    private static Map<Integer, List<String>> commandUsage = new HashMap<Integer, List<String>>();
    private static Map<String, Integer> commandUsageVector = new HashMap<String, Integer>();
    
    public static boolean experimentRunning = false;

    public static ArrayList<Task> taskList;

    public static final HashSet<Recommendation> allRecommendations = new HashSet<Recommendation>();

    public static final TreeSet<Recommendation> recommendationQueue = new TreeSet<Recommendation>();

    public static final HashSet<Recommendation> currentTaskRecos = new HashSet<Recommendation>();

    public static final HashSet<Recommendation> filterList = new HashSet<Recommendation>();
    
    private static Map<String,Integer> userMap = null;

    private static int participantId;

    public static int conditions[] = new int[4];

    private static boolean didUserConsent;

    private static String userName;

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
            //URL fileURL = bundle.getEntry(bundledPath);
            URL fileURL = bundle.getResource(bundledPath);
            return new File(FileLocator.toFileURL(fileURL).toURI());
            //return new File(fileURL.toURI());
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

    public static File getStrangersFile() {
        return getResourceFile("res/strangers.txt");
    }
    
    public static File getEmailFile() {
        return getResourceFile("res/email.ini");
    }

    public static File getUserMappingFile(){
        return getResourceFile("res/userMapping.txt");
    }

    public static RecommendationBundle getRecommendationsBundle(String id) {
          File xmlFile = getResourceFile("res/recommendations/"+id+"/reco.xml");
          File htmlFile = getResourceFile("res/recommendations/"+id+"/reco.html");
          
          return new RecommendationBundle(xmlFile, htmlFile);
    }

    public static File getTaskList() {
        return getResourceFile("res/tasks.xml");
    }

    public static void commandWasUsedInCurrentTask(String commandId) {
        //add a that the user used the command in the current task
        
        List<String> list = Utils.commandUsage.get(Utils.currentTaskNumber);
        
        if (list == null) { 
            list = new ArrayList<String>();
        }
        list.add(commandId);
        commandUsage.put(Utils.currentTaskNumber, list);

      //Increment the total uses for the given tool
        if (commandUsageVector.containsKey(commandId)) {
            commandUsageVector.put(commandId, (Utils.commandUsageVector.get(commandId) + 1));
        }
        else {
            commandUsageVector.put(commandId, 1);
        }
    }

    public static boolean userHasUsedCommand(String id) {
        return commandUsageVector.containsKey(id);
    }

    public static int getCommandUsage(String id) {
        Integer retVal = commandUsageVector.get(id);
        return retVal == null ? 0 : retVal.intValue();
    }

    public static List<String> getCommandsUsedInCurrentTask() {
        List<String> retVal = Utils.commandUsage.get(Utils.currentTaskNumber);
        return retVal == null ? Collections.<String>emptyList() : retVal;
    }
    
    public static String getUserName() {
        return userName;
    }

    public static boolean didUserConsent() {
        return didUserConsent;
    }

    public static int getParticipantID() {
        return participantId;
    }

    public static Set<Entry<Integer, List<String>>> getCommandsEntrySet() {
        return commandUsage.entrySet();
    }

    /**
     * A user types in a school id or something similar.  
     * This function maps them to their pre-assigned id.
     * @param userName
     */
    public static void assignUserNumericId(String enteredUserName) {
        userName = enteredUserName;
        
        Integer integer = getUserMap().get(userName);
        participantId = integer == null? 0 : integer.intValue();
    }
    
    public static boolean doesUserExist(String enteredUserName){
        return getUserMap().containsKey(enteredUserName);
    }

    private static Map<String, Integer> getUserMap() {
        if (userMap == null) {
            userMap = readInUserMap();
        }
        return userMap;
    }

    private static Map<String, Integer> readInUserMap() {
        File file = getUserMappingFile();
        Map<String, Integer> retVal = new HashMap<>();

        try (Scanner scanner = new Scanner(file);){
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] sections = line.split(":");
                if (sections.length != 2) continue;
                retVal.put(sections[0], Integer.valueOf(sections[1]));
            }
        } catch (FileNotFoundException | NumberFormatException | NoSuchElementException e) {
            e.printStackTrace();
        }
        
        return retVal;
    }

    public static void setConsent(boolean b) {
       didUserConsent = b;        
    }

}
