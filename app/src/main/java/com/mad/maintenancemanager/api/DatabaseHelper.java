package com.mad.maintenancemanager.api;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.model.User;

/**
 * Created by lucaswinterbottom on 22/5/17.
 */

public class DatabaseHelper {
    private static DatabaseHelper mDatabaseHelper;

    public static DatabaseHelper getInstance(){
        if (mDatabaseHelper == null){
            mDatabaseHelper = new DatabaseHelper();
        }
        return mDatabaseHelper;
    }

    public void getGroupKey(final IGroupKeyListener listener, final String userID) {
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(userID).getValue(User.class);
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

    public interface IGroupKeyListener {
        void onGroupKey(String key);
    }
}
