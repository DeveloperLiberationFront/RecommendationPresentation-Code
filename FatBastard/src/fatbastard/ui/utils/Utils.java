package fatbastard.ui.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.epp.usagedata.internal.gathering.UsageDataCaptureActivator;
import org.osgi.framework.Bundle;

import fatbastard.ui.core.Recommendation;
import fatbastard.ui.core.Task;

public class Utils {

	public static int currentTaskNumber = 0;
	public static HashMap<Integer, ArrayList<String>> commandUsage = new HashMap<Integer, ArrayList<String>>();
	public static boolean experimentRunning = false;
	public static ArrayList<Task> taskList;
	public static HashSet<Recommendation> allRecommendations = new HashSet<Recommendation>();
	public static TreeSet<Recommendation> recommendationQueue = new TreeSet<Recommendation>();
	public static HashSet<Recommendation> currentTaskRecos = new HashSet<Recommendation>();
	
	public static int conditions[] = new int[4] ;
	
	public static void resetGlobals(){
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
	}

	public static String getResourceFolder() throws URISyntaxException, IOException {
		
		Plugin plugin = UsageDataCaptureActivator.getDefault();
		Bundle bundle = plugin.getBundle();
		URL fileURL = bundle.getEntry("res");
		File file = new File(FileLocator.resolve(fileURL).toURI());
		return file.getAbsolutePath();
	}
	
	public static String getUserFolder(){
		return System.getProperty("user.home") + File.separator + ".ubc";
	}
}
