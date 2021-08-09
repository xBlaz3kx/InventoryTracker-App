package com.inventorytracker.tasks.data;

import com.google.firebase.firestore.IgnoreExtraProperties;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

@IgnoreExtraProperties
public class UserTask {

    private String uploaderID, taskTitle, taskContent, taskStatus;
    private int priority;
    private Date taskDate, taskFinished;

    public UserTask() {
    }

    public UserTask(String uploaderID, String title, String content, String status, int priority, Date timestamp) {
        this.uploaderID = uploaderID;
        this.taskContent = normalizeSpace(content);
        this.taskTitle = normalizeSpace(title);
        this.taskStatus = status;
        this.priority = priority;
        this.taskDate = timestamp;
    }

    public String getUploaderID() {
        return uploaderID;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public int getPriority() {
        return priority;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public Date getTaskDate() {
        return taskDate;
    }

    public Date getTaskFinished() {
        return taskFinished;
    }

    public void setUploaderID(String uploaderID) {
        this.uploaderID = uploaderID;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setTaskDate(Date taskDate) {
        this.taskDate = taskDate;
    }

    public void setTaskFinished(Date taskFinished) {
        this.taskFinished = taskFinished;
    }
}
