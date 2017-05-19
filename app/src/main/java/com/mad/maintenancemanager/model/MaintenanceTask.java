package com.mad.maintenancemanager.model;

import java.net.URL;
import java.util.List;

/**
 * Created by lucaswinterbottom on 14/5/17.
 */

public class MaintenanceTask {
    private int mID;
    private String mCreatorID;
    private String mName;
    private String mDescription;
    private boolean mTaskType;
    private String mAssignedTo;
    private List<String> mNeededItems;
    private URL mImageURL;
    private String mTradeType;

    public MaintenanceTask(int ID, String creatorID, String name, String description, boolean taskType, String assignedTo, List<String> neededItems, URL imageURL, String tradeType) {
        mID = ID;
        mCreatorID = creatorID;
        mName = name;
        mDescription = description;
        mTaskType = taskType;
        mAssignedTo = assignedTo;
        mNeededItems = neededItems;
        mImageURL = imageURL;
        mTradeType = tradeType;
    }

    public MaintenanceTask() {
        //For Firebase
    }

    public String getAssignedTo() {
        return mAssignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        mAssignedTo = assignedTo;
    }

    public List<String> getNeededItems() {
        return mNeededItems;
    }

    public void setNeededItems(List<String> neededItems) {
        mNeededItems = neededItems;
    }

    public URL getImageURL() {
        return mImageURL;
    }

    public void setImageURL(URL imageURL) {
        mImageURL = imageURL;
    }

    public String getTradeType() {
        return mTradeType;
    }

    public void setTradeType(String tradeType) {
        mTradeType = tradeType;
    }

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


}
