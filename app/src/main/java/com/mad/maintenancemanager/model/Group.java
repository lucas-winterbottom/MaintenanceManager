package com.mad.maintenancemanager.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lucaswinterbottom on 17/5/17.
 */

public class Group {
    private String mGroupName;
    private String mGroupCreator;
    private int mGroupKey;
    private List<String> mGroupMembers;

    public Group(String groupName, String groupCreator, int groupKey, List<String> groupMembers) {
        mGroupName = groupName;
        mGroupCreator = groupCreator;
        mGroupKey = groupKey;
        mGroupMembers = groupMembers;
    }

    public Group(String groupCreator, int groupKey, List<String> groupMembers) {
        mGroupCreator = groupCreator;
        mGroupKey = groupKey;
        mGroupMembers = groupMembers;
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

    public int getGroupKey() {
        return mGroupKey;
    }

    public void setGroupKey(int groupKey) {
        mGroupKey = groupKey;
    }

    public List<String> getGroupMembers() {
        return mGroupMembers;
    }

    public void setGroupMembers(List<String> groupMembers) {
        mGroupMembers = groupMembers;
    }


}
