package com.taimoor.TodoNotifier.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

//Annotation only works if room dependencies are added
//used to create a table

@Entity(tableName = "task_table")
public class Task {
    //Column in database for primary key
    @ColumnInfo(name = "task_id")
    @PrimaryKey(autoGenerate = true)
    public long taskId;

    public String task;

    //Create a separate Enum Class named Priority which will contain the priority constants
    public Priority priority;

    @ColumnInfo(name = "due_date")
    public Date DueDate;

    @ColumnInfo(name = "created_at")
    public Date DateCreated;

    @ColumnInfo(name = "is_done")
    public boolean isDone;

    @ColumnInfo(name = "location")
    public String address;

    @ColumnInfo(name = "notify_key")
    public int noti_key;

    @ColumnInfo(name = "notify_work_tag")
    public String notifyWorkTag;

    @ColumnInfo(name = "imageList", typeAffinity = ColumnInfo.BLOB)
    private byte[] images;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    //Generate Constructor for variables except task_id
    public Task(String task, Priority priority, Date dueDate, Date dateCreated, boolean isDone, byte[] Images, String Address, int notiKey, String WorkTag ) {
        this.task = task;
        this.priority = priority;
        DueDate = dueDate;
        DateCreated = dateCreated;
        this.isDone = isDone;
        this.images = Images;
        this.address = Address;
        this.noti_key = notiKey;
        this.notifyWorkTag = WorkTag;
    }

    public Task() {
    }

    public byte[] getImages() {
        return images;
    }

    public void setImages(byte[] images) {
        this.images = images;
    }

    //Generate Getter and Setters for All of the variables
    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Date getDueDate() {
        return DueDate;
    }

    public void setDueDate(Date dueDate) {
        DueDate = dueDate;
    }

    public Date getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        DateCreated = dateCreated;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public int getNoti_key() {
        return noti_key;
    }

    public void setNoti_key(int noti_key) {
        this.noti_key = noti_key;
    }


    public String getNotifyWorkTag() {
        return notifyWorkTag;
    }

    public void setNotifyWorkTag(String notifyWorkTag) {
        this.notifyWorkTag = notifyWorkTag;
    }

    //Override toString method to get the String representation of this class
    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", task='" + task + '\'' +
                ", priority=" + priority +
                ", DueDate=" + DueDate +
                ", DateCreated=" + DateCreated +
                ", isDone=" + isDone +
                '}';
    }
}
