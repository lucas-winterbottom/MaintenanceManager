package com.mad.maintenancemanager.api;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.LoginHandlerActivity;
import com.mad.maintenancemanager.model.Group;
import com.mad.maintenancemanager.model.MaintenanceTask;
import com.mad.maintenancemanager.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucaswinterbottom on 22/5/17.
 */

public class DatabaseHelper {
    private static DatabaseHelper mDatabaseHelper;
    private String mDisplayName;
    private String mUID;
    private String mGroupKey;
    private String mTrade;
    private String mMobile;

    public static DatabaseHelper getInstance() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper();
        }
        return mDatabaseHelper;
    }

    private DatabaseHelper() {
        mUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDisplayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    public String getMobile() {
        return mMobile;
    }

    public String getGroupKey() {
        return mGroupKey;
    }

    public void setGeneralUserData(final IGroupKeyListener listener) {
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(getUserID()).getValue(User.class);
                if (user != null) {
                    mMobile = user.getMobileNo();
                    if (user.getGroupKey() != null) {
                        listener.onGroupKey(user.getGroupKey());
                        mGroupKey = user.getGroupKey();
                    } else {
                        listener.onGroupKey(null);
                    }
                } else {
                    listener.onGroupKey(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }

    public void saveTask(final MaintenanceTask maintenanceTask) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constants.TASKS_ACTIVE_TASKS).child(mGroupKey);
        String taskKey = databaseReference.push().getKey();
        databaseReference.child(taskKey).setValue(maintenanceTask);
    }

    public void saveExternalTask(final MaintenanceTask maintenanceTask) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constants.TASKS_ACTIVE_TASKS).child(mGroupKey);
        String taskKey = databaseReference.push().getKey();

        databaseReference.child(taskKey).setValue(maintenanceTask);
        maintenanceTask.setMobile(getMobile());
        FirebaseDatabase.getInstance().getReference(Constants.EXTERNAL_TASKS).child(taskKey).setValue(maintenanceTask);
    }

    public void markDone(final DatabaseReference ref) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.TASKS_COMPLETED_TASKS).child(mGroupKey);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MaintenanceTask task = dataSnapshot.getValue(MaintenanceTask.class);
                String keyOne = databaseReference.push().getKey();
                databaseReference.child(keyOne).setValue(task);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference(Constants.EXTERNAL_TASKS).child(ref.getKey()).removeValue();
        ref.removeValue();
    }

    public void getGroup(final IGroupListener listener) {
        if (mGroupKey != null) {
            DatabaseReference group = FirebaseDatabase.getInstance().getReference(Constants.GROUPS);
            group.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Group group = dataSnapshot.child(mGroupKey).getValue(Group.class);
                    listener.onGroup(group);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void userLogout() {
        mDatabaseHelper = null;
    }

    public String getUserID() {
        return mUID;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void createGroup(Group group) {
        group.getGroupMembers().add(getDisplayName());
        group.setGroupCreator(getDisplayName());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String key = reference.child(Constants.GROUPS).push().getKey();
        mGroupKey = key;
        reference.child(Constants.GROUPS).child(key).setValue(group);
        reference.child(Constants.USERS).child(getUserID()).child(Constants.GROUP_KEY).setValue(key);
    }

    public void joinExistingGroup(final String stringExtra) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Constants.GROUPS).child(stringExtra).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // // TODO: 26/5/17 Change this so that i just add below the current one also check if the key is correct
                Group group = dataSnapshot.getValue(Group.class);
                group.getGroupMembers().add(DatabaseHelper.getInstance().getDisplayName());
                reference.child(Constants.GROUPS).child(stringExtra).setValue(group);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        reference.child(Constants.USERS).child(getUserID()).
                child(Constants.GROUP_KEY).setValue(stringExtra);
    }

    public Query getMyTasksRef() {
        return FirebaseDatabase.getInstance().getReference(Constants.TASKS_ACTIVE_TASKS)
                .child(mGroupKey)
                .orderByChild(Constants.ASSIGNED_TO)
                .equalTo(getDisplayName());
    }

    public Query getGroupTasksRef() {
        return FirebaseDatabase.getInstance().getReference(Constants.TASKS_ACTIVE_TASKS)
                .child(mGroupKey)
                .orderByChild(Constants.DUE_DATE);
    }

    public Query getCompletedTasksRef() {
        return FirebaseDatabase.getInstance().getReference(Constants.TASKS_COMPLETED_TASKS)
                .child(mGroupKey)
                .orderByChild(Constants.DUE_DATE);
    }


    /**
     * Checks if the user already has data on the server if not, sets up the base user on the server,
     * also gives back the type
     */
    public void initialSetupCheck(final LoginHandlerActivity.IOnUserTypeListener listener) {
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference(Constants.USERS).child(getUserID());
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                listener.onUserType(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setGeneralUser(String mobileNo) {
        FirebaseDatabase.getInstance().getReference(Constants.USERS).child(getUserID())
                .setValue(new User(getDisplayName(), null, mobileNo, false));
    }

    public void setTradeUser(String trade) {
        FirebaseDatabase.getInstance().getReference(Constants.USERS).child(getUserID())
                .setValue(new User(getDisplayName(), null, true, trade));
        mTrade = trade;
    }

    public void getTradieTasks(final IExternalTasksListener listener) {
        if (mTrade != null) {
            Query tasks = FirebaseDatabase.getInstance().getReference(Constants.EXTERNAL_TASKS).orderByChild(Constants.TRADE_TYPE).equalTo(getTrade());
            tasks.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<MaintenanceTask> tasks = new ArrayList<MaintenanceTask>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        MaintenanceTask task = ds.getValue(MaintenanceTask.class);
                        tasks.add(task);
                    }
                    listener.onTasks(tasks);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    public String getTrade() {
        return mTrade;
    }

    public void setTrade(final IOnTradeListener listener) {
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(getUserID()).getValue(User.class);
                if (user != null) {
                    if (user.getTrade() != null) {
                        mTrade = user.getTrade();
                        listener.onTrade();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }


    public interface IOnTradeListener {
        void onTrade();
    }

    public interface IGroupKeyListener {
        void onGroupKey(String key);
    }

    public interface IGroupListener {
        void onGroup(Group group);
    }

    public interface IExternalTasksListener {
        void onTasks(List<MaintenanceTask> tasks);
    }
}
