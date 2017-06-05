package com.mad.maintenancemanager.model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * POJO for Group object
 */

public class Group {
    private String mGroupName;
    private String mGroupCreator;
    //next release group passwords
    //private int mGroupKey;
    private List<String> mGroupMembers = new ArrayList<>();

    public Group(String groupName) {
        mGroupName = groupName;
    }

    public Group() {

    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        mGroupName = groupName;
    }

    public String getGroupCreator() {
        return mGroupCreator;
    }

    public void setGroupCreator(String groupCreator) {
        mGroupCreator = groupCreator;
    }

    public List<String> getGroupMembers() {
        return mGroupMembers;
    }

    public void setGroupMembers(List<String> groupMembers) {
        mGroupMembers = groupMembers;
    }


}
