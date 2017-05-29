package com.mad.maintenancemanager.api;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.model.Group;
import com.mad.maintenancemanager.model.MaintenanceTask;
import com.mad.maintenancemanager.model.User;

/**
 * Created by lucaswinterbottom on 22/5/17.
 */

public class DatabaseHelper {
    private static DatabaseHelper mDatabaseHelper;

    public static DatabaseHelper getInstance() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper();
        }
        return mDatabaseHelper;
    }

    public void getGroupKey(final IGroupKeyListener listener) {
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(currentUser).getValue(User.class);
                if (user != null) {
                    if (user.getGroupKey() != null) {
                        listener.onGroupKey(user.getGroupKey());
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
        getGroupKey(new IGroupKeyListener() {
            @Override
            public void onGroupKey(String key) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.TASKS_ACTIVE_TASKS).child(key);
                String taskKey = databaseReference.push().getKey();
                databaseReference.child(taskKey).setValue(maintenanceTask);
            }
        });
    }

    public void markDone(final DatabaseReference ref) {
        getGroupKey(new IGroupKeyListener() {
            @Override
            public void onGroupKey(String key) {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.TASKS_COMPLETED_TASKS).child(key);
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
                ref.removeValue();
            }
        });

    }

    public void getGroup(final IGroupListener listener) {
        getGroupKey(new IGroupKeyListener() {
            @Override
            public void onGroupKey(final String key) {
                if(key!=null){
                    DatabaseReference group = FirebaseDatabase.getInstance().getReference(Constants.GROUPS);
                    group.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Group group = dataSnapshot.child(key).getValue(Group.class);
                            listener.onGroup(group);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    public String getUserID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Checks if the user already has data on the server if not, sets up the base user on the server
     */
    public void checkUserData() {
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference(Constants.USERS).child(getUserID());
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    dataRef.setValue(new User(getDisplayName(), null, false));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public String getDisplayName() {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    public void createGroup(Group group) {
        group.getGroupMembers().add(getDisplayName());
        group.setGroupCreator(getDisplayName());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String key = reference.child(Constants.GROUPS).push().getKey();
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

    public interface IGroupKeyListener {
        void onGroupKey(String key);
    }

    public interface IGroupListener {
        void onGroup(Group group);
    }
}
