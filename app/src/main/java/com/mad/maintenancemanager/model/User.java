package com.mad.maintenancemanager.model;

/**
 * POJO for user
 */

public class User {
    private String mDisplayName;
    private String mGroupKey;
    private boolean mIsContractor;
    private String mTrade;
    private String mMobileNo;

    public String getMobileNo() {
        return mMobileNo;
    }

    public void setMobileNo(String mobileNo) {
        mMobileNo = mobileNo;
    }

    public String getTrade() {
        return mTrade;
    }

    public void setTrade(String trade) {
        mTrade = trade;
    }

    public User() {
    }

    public User(String displayName, String groupKey, boolean isContractor, String trade) {
        mDisplayName = displayName;
        mGroupKey = groupKey;
        mIsContractor = isContractor;
        mTrade = trade;
    }

    public User(String displayName, String groupKey, String mobileNo, boolean isContractor) {

        mDisplayName = displayName;
        mGroupKey = groupKey;
        mIsContractor = isContractor;
        mMobileNo = mobileNo;
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
