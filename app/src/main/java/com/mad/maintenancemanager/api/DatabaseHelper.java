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
                if (user.getGroupKey() != null) {
                    listener.onGroupKey(user.getGroupKey());
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

    public void getGroupMembers(final IGroupListener listener) {
        getGroupKey(new IGroupKeyListener() {
            @Override
            public void onGroupKey(final String key) {
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
        });

    }

    public interface IGroupKeyListener {
        void onGroupKey(String key);
    }

    public interface IGroupListener {
        void onGroup(Group group);
    }
}
