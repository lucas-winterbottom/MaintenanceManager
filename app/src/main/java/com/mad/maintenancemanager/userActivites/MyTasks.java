package com.mad.maintenancemanager.userActivites;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private FirebaseAuth mAuth;

    public MyTasks() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_tasks, container, false);

        mProgress = (ProgressBar) rootView.findViewById(R.id.my_tasks_progress);
        RecyclerView recycler = (RecyclerView) rootView.findViewById(R.id.my_tasks_recycler);
        recycler.setHasFixedSize(false);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        mTaskRef = FirebaseDatabase.getInstance().getReference().child("Tasks");
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.my_tasks_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewTaskActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        showProgress();


        mAdapter = new FirebaseRecyclerAdapter<MaintenanceTask, MaintenanceTaskHolder>(MaintenanceTask.class, R.layout.task_card, MaintenanceTaskHolder.class, mTaskRef) {
            @Override
            protected void populateViewHolder(MaintenanceTaskHolder maintenanceTaskHolder, MaintenanceTask maintenanceTask, int i) {
                maintenanceTaskHolder.setCreatorId(maintenanceTask.getCreatorID());
                maintenanceTaskHolder.setDescription(maintenanceTask.getDescription());
                maintenanceTaskHolder.setName(maintenanceTask.getName());
                maintenanceTaskHolder.setTaskType(maintenanceTask.isTaskType());
                hideProgress();
            }

            @Override
            protected void onDataChanged() {
                super.onDataChanged();
                hideProgress();
            }
        };
        recycler.setAdapter(mAdapter);


        return rootView;
    }

    private void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }


    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

}


