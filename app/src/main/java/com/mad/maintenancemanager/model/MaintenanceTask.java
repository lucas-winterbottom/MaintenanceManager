package com.mad.maintenancemanager.model;

/**
 * Created by lucaswinterbottom on 14/5/17.
 */

public class MaintenanceTask {
    private int mID;
    private String mCreatorID;
    private String mName;
    private String mDescription;
    private boolean mTaskType;


    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        this.mID = ID;
    }

    public String getCreatorID() {
        return mCreatorID;
    }

    public void setCreatorID(String creatorID) {
        mCreatorID = creatorID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public boolean isTaskType() {
        return mTaskType;
    }

    public void setTaskType(boolean taskType) {
        mTaskType = taskType;
    }

    public MaintenanceTask(int mID, String mCreatorID, String mName, String mDescription, boolean mTaskType) {
        this.mID = mID;
        this.mCreatorID = mCreatorID;
        this.mName = mName;
        this.mDescription = mDescription;
        this.mTaskType = mTaskType;
    }
    public  MaintenanceTask(){
        //For Firebase
    }

}
