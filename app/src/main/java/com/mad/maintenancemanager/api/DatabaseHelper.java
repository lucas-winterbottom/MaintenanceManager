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
 * Class that handles most if not all of the FirebaseDatabase interaction
 * Implemented in a singleton so that only one instance is present at a time
 * It is also disposed of at user sign out as its member fields will change
 */

public class DatabaseHelper {
    private static DatabaseHelper mDatabaseHelper;
    private String mDisplayName;
    private String mUID;
    private String mGroupKey;
    private String mTrade;
    private String mMobile;

    /**
     * Checks if DatabaseHelper already exists if not creates and instance of it
     *
     * @return the Databasehelper object
     */
    public static DatabaseHelper getInstance() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper();
        }
        return mDatabaseHelper;
    }

    /**
     * Private constructor
     */
    private DatabaseHelper() {
        mUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDisplayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    /**
     * @return Current users mobile
     */
    public String getMobile() {
        return mMobile;
    }

    /**
     * @return Current users groupkey
     */
    public String getGroupKey() {
        return mGroupKey;
    }

    /**
     * Calls firebase for general user data and store it in this object if userdata exists
     * (mGroupKey,mMobile)
     *
     * @param listener to return group key and essentially inform the view when the data is loaded
     */
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

    /**
     * Takes a task and store it in the database
     *
     * @param maintenanceTask to store in the database
     */
    public void saveTask(final MaintenanceTask maintenanceTask) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constants.TASKS_ACTIVE_TASKS).child(mGroupKey);
        String taskKey = databaseReference.push().getKey();
        databaseReference.child(taskKey).setValue(maintenanceTask);
    }

    /**
     * Takes a task and stores it in two locations Active tasks and External Tasks
     *
     * @param maintenanceTask
     */
    public void saveExternalTask(final MaintenanceTask maintenanceTask) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constants.TASKS_ACTIVE_TASKS).child(mGroupKey);
        String taskKey = databaseReference.push().getKey();

        databaseReference.child(taskKey).setValue(maintenanceTask);
        maintenanceTask.setMobile(getMobile());
        FirebaseDatabase.getInstance().getReference(Constants.EXTERNAL_TASKS).child(taskKey).setValue(maintenanceTask);
    }

    /**
     * Moves the task to them complected tasks section of the database, also removes any instance of
     * the same task in external tasks
     *
     * @param ref references location of task
     */
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

    /**
     * Pulls the current uses group from the database
     *
     * @param listener returns the group object to the view to use data it provides
     */
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

    /**
     * Used to nullify the object when a user logs out
     */
    public void userLogout() {
        mDatabaseHelper = null;
    }

    /**
     * Returns userID of current user
     */
    public String getUserID() {
        return mUID;
    }

    /**
     * Returns DisplayName of current user
     */
    public String getDisplayName() {
        return mDisplayName;
    }

    /**
     * Creates group in the database from group object provided by GroupFragment
     * @param group
     */
    public void createGroup(Group group) {
        group.getGroupMembers().add(getDisplayName());
        group.setGroupCreator(getDisplayName());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String key = reference.child(Constants.GROUPS).push().getKey();
        mGroupKey = key;
        reference.child(Constants.GROUPS).child(key).setValue(group);
        reference.child(Constants.USERS).child(getUserID()).child(Constants.GROUP_KEY).setValue(key);
    }

    /**
     * Tries to joing user to group using GroupKey
     * @param groupKey the key the user entered to join group
     * @param iJoinGroupListener informs the view whether or not is was successful(Key is valid)
     */
    public void joinExistingGroup(final String groupKey, final IJoinGroupListener iJoinGroupListener) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Constants.GROUPS).child(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // // TODO: 26/5/17 Change this so that i just add below the current one also check if the key is correct
                Group group = dataSnapshot.getValue(Group.class);
                if (group != null) {
                    group.getGroupMembers().add(DatabaseHelper.getInstance().getDisplayName());
                    reference.child(Constants.GROUPS).child(groupKey).setValue(group);
                    reference.child(Constants.USERS).child(getUserID()).
                            child(Constants.GROUP_KEY).setValue(groupKey);
                    mGroupKey = groupKey;
                    iJoinGroupListener.onTryJoinResult(true);
                } else {
                    iJoinGroupListener.onTryJoinResult(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /**
     * @return reference to the current users tasks
     */
    public Query getMyTasksRef() {
        return FirebaseDatabase.getInstance().getReference(Constants.TASKS_ACTIVE_TASKS)
                .child(mGroupKey)
                .orderByChild(Constants.ASSIGNED_TO)
                .equalTo(getDisplayName());
    }

    /**
     * @return reference to the tasks of the current users group
     */
    public Query getGroupTasksRef() {
        return FirebaseDatabase.getInstance().getReference(Constants.TASKS_ACTIVE_TASKS)
                .child(mGroupKey)
                .orderByChild(Constants.DUE_DATE);
    }
    /**
     * @return reference to the completed tasks of the current users group
     */
    public Query getCompletedTasksRef() {
        return FirebaseDatabase.getInstance().getReference(Constants.TASKS_COMPLETED_TASKS)
                .child(mGroupKey)
                .orderByChild(Constants.DUE_DATE);
    }

    /**
     * Gets user data from the server, potentially null, however the view handles this
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

    /**
     * Sets up a general user on the database
     * @param mobileNo
     */
    public void setGeneralUser(String mobileNo) {
        FirebaseDatabase.getInstance().getReference(Constants.USERS).child(getUserID())
                .setValue(new User(getDisplayName(), null, mobileNo, false));
    }
    /**
     * Sets up a general user on the database
     * @param trade
     */
    public void setTradeUser(String trade) {
        FirebaseDatabase.getInstance().getReference(Constants.USERS).child(getUserID())
                .setValue(new User(getDisplayName(), null, true, trade));
        mTrade = trade;
    }

    /**
     * Fetches the External tasks from the server
     * @param listener returns the tasks when then have finished loading
     */
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

    /**
     * @return the trade of the current user
     */
    public String getTrade() {
        return mTrade;
    }

    /**
     * gets the trade from firebase and stores it here for later user
     * @param listener Informs the view when the data is loaded
     */
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

    /**
     * Listens for trade to be downloaded
     */
    public interface IOnTradeListener {
        void onTrade();
    }

    /**
     * listens for the groupKey to be downloaded
     */
    public interface IGroupKeyListener {
        void onGroupKey(String key);
    }

    /**
     *listens for the group to be downloaded
     */
    public interface IGroupListener {
        void onGroup(Group group);
    }

    /**
     * listens for the external tasks to be downloaded
     */
    public interface IExternalTasksListener {
        void onTasks(List<MaintenanceTask> tasks);
    }

    /**
     * listens for group join status
     */
    public interface IJoinGroupListener {
        void onTryJoinResult(boolean result);
    }
}
