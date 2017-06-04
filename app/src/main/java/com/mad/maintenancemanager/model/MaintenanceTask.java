package com.mad.maintenancemanager.model;

import com.google.android.gms.location.places.Place;

/**
 * Created by lucaswinterbottom on 14/5/17.
 */

public class MaintenanceTask {
    private String mCreatorID;
    private String mName;
    private String mDescription;
    private boolean mTaskType;
    private String mAssignedTo;
    private String mNeededItems;
    //Potential future release
    //private URL mImageURL;
    private long mDueDate;
    private String mTradeType;
    private String mTaskLocationData;

    public String getMobile() {
        return mMobile;
    }

    public void setMobile(String mobile) {
        mMobile = mobile;
    }

    private String mMobile;



    public MaintenanceTask(String creatorID, String name, String description, boolean taskType, String assignedTo, String neededItems, long dueDate, String tradeType) {
        mCreatorID = creatorID;
        mName = name;
        mDescription = description;
        mTaskType = taskType;
        mAssignedTo = assignedTo;
        mNeededItems = neededItems;
        mDueDate = dueDate;
        mTradeType = tradeType;
    }

    public long getDueDate() {
        return mDueDate;
    }

    public void setDueDate(int dueDate) {
        mDueDate = dueDate;
    }

    public String getTaskLocationData() {
        return mTaskLocationData;
    }

    public void setTaskLocationData(String taskLocationData) {
        mTaskLocationData = taskLocationData;
    }

    public MaintenanceTask(){
        //For Firebase
    }

    public String getAssignedTo() {
        return mAssignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        mAssignedTo = assignedTo;
    }

    public String getNeededItems() {
        return mNeededItems;
    }

    public void setNeededItems(String neededItems) {
        mNeededItems = neededItems;
    }

//    public URL getImageURL() {
//        return mImageURL;
//    }
//
//    public void setImageURL(URL imageURL) {
//        mImageURL = imageURL;
//    }

    public String getTradeType() {
        return mTradeType;
    }

    public void setTradeType(String tradeType) {
        mTradeType = tradeType;
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
