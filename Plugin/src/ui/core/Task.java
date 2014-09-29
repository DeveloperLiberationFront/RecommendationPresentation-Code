package ui.core;

import java.util.HashSet;

public class Task {

    private int taskNumber;

    private String taskDetails;

    private HashSet<String> recommendations = new HashSet<String>();

    public Task(String taskDetails, HashSet<String> recommendations) {
        this.taskDetails = taskDetails;
        this.recommendations = recommendations;
    }

    public Task() {

    }

    public int getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    public String getTaskDetails() {
        return taskDetails;
    }

    public HashSet<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(HashSet<String> recommendations) {
        this.recommendations = recommendations;
    }

    public void setTaskDetails(String taskDetails) {
        this.taskDetails = taskDetails;
    }

    public void addRecommendation(String reco) {
        this.recommendations.add(reco);
    }
}
