package com.mad.maintenancemanager.userActivites;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.adapter.MaintenanceTaskHolder;
import com.mad.maintenancemanager.model.MaintenanceTask;
import com.mad.maintenancemanager.model.User;

import java.util.List;

public class MyTasks extends Fragment {

    public static final int REQUEST_CODE = 123;
    private RecyclerView mTasks;
    private FirebaseRecyclerAdapter mAdapter;
    private List<MaintenanceTask> mTaskList;
    private DatabaseReference mTaskRef;
    private DatabaseReference mUserRef;
    private ProgressBar mProgress;
    private FloatingActionButton mAddTask;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RecyclerView mRecycler;

    public MyTasks() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_tasks, container, false);

        mProgress = (ProgressBar) rootView.findViewById(R.id.my_tasks_progress);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.my_tasks_recycler);
        mRecycler.setHasFixedSize(false);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        showProgress();
        getGroupKey();

        return rootView;
    }

    private void getGroupKey() {
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(User.class);
                if (user.getGroupKey() != null) {
                    setUpRecycler(user.getGroupKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }

    private void setUpRecycler(String groupKey) {
        mTaskRef = FirebaseDatabase.getInstance().getReference(Constants.TASKS).child(groupKey);
        mAdapter = new FirebaseRecyclerAdapter<MaintenanceTask, MaintenanceTaskHolder>(MaintenanceTask.class, R.layout.task_card, MaintenanceTaskHolder.class, mTaskRef) {

            @Override
            protected void populateViewHolder(MaintenanceTaskHolder maintenanceTaskHolder, final MaintenanceTask maintenanceTask, final int i) {
                maintenanceTaskHolder.setCreatorId(maintenanceTask.getCreatorID());
                maintenanceTaskHolder.setDescription(maintenanceTask.getDescription());
                maintenanceTaskHolder.setName(maintenanceTask.getName());
                maintenanceTaskHolder.setTaskType(maintenanceTask.isTaskType());
//                if (maintenanceTask.getAssignedTo().equals(user.getDisplayName())) {
//                    maintenanceTaskHolder.setAssignee("You");
//                } else {
//                    maintenanceTaskHolder.setAssignee(maintenanceTask.getAssignedTo());
//                }
                hideProgress();
                maintenanceTaskHolder.setLongClick(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(maintenanceTask.getName())

                                .setItems(R.array.options_array, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        return true;
                    }
                });
            }

            @Override
            protected void onDataChanged() {
                super.onDataChanged();
                hideProgress();
            }
        };
        mRecycler.setAdapter(mAdapter);

    }

    private void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

}


