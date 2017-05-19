package com.mad.maintenancemanager.model;

/**
 * Created by lucaswinterbottom on 18/5/17.
 */

public class User {
    private String mDisplayName;
    private String mGroupKey;
    private boolean mIsContractor;

    public User() {
    }

    public User(String displayName, String groupKey, boolean isContractor) {

        mDisplayName = displayName;
        mGroupKey = groupKey;
        mIsContractor = isContractor;
    }



    public void setDisplayName(String displayName) {

        mDisplayName = displayName;
    }

    public void setGroupKey(String groupKey) {
        mGroupKey = groupKey;
    }

    public void setContractor(boolean contractor) {
        mIsContractor = contractor;
    }

    public String getDisplayName() {

        return mDisplayName;
    }

    public String getGroupKey() {
        return mGroupKey;
    }

    public boolean isContractor() {
        return mIsContractor;
    }
}
