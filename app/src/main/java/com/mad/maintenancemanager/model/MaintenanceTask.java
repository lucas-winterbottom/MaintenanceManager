package com.mad.maintenancemanager.model;

import com.google.android.gms.location.places.Place;

import org.threeten.bp.LocalDate;

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
    //private URL mImageURL;
    private long mDueDate;
    private String mTradeType;
    private Place mTaskLocation;


    public MaintenanceTask(String creatorID, String name, String description, boolean taskType, String assignedTo, String neededItems, long dueDate, String tradeType, Place taskLocation) {
        mCreatorID = creatorID;
        mName = name;
        mDescription = description;
        mTaskType = taskType;
        mAssignedTo = assignedTo;
        mNeededItems = neededItems;
        mDueDate = dueDate;
        mTradeType = tradeType;
        mTaskLocation = taskLocation;
    }

    public long getDueDate() {
        return mDueDate;
    }

    public void setDueDate(int dueDate) {
        mDueDate = dueDate;
    }

    public Place getTaskLocation() {
        return mTaskLocation;
    }

    public void setTaskLocation(Place taskLocation) {
        mTaskLocation = taskLocation;
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
