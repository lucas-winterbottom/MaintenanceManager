package com.mad.maintenancemanager.userActivites;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.SignedInUserActivity;
import com.mad.maintenancemanager.adapter.MaintenanceTaskAdapter;
import com.mad.maintenancemanager.model.MaintenanceTask;

import java.util.ArrayList;
import java.util.List;

public class MyTasks extends Fragment {

    private RecyclerView mTasks;
    private FirebaseRecyclerAdapter mAdapter;
    private List<MaintenanceTask> mTaskList;
    private DatabaseReference mRef;
    private ProgressBar mProgress;

    public MyTasks() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_my_tasks, container, false);
        mProgress = (ProgressBar)rootView.findViewById(R.id.my_tasks_progress);
        RecyclerView recycler = (RecyclerView) rootView.findViewById(R.id.my_tasks_recycler);
        recycler.setHasFixedSize(false);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRef = FirebaseDatabase.getInstance().getReference();


        new MaintenanceTask()
        showProgress();

        mAdapter = new FirebaseRecyclerAdapter<MaintenanceTask, MaintenanceTaskHolder>(MaintenanceTask.class, R.layout.task_card, MaintenanceTaskHolder.class, mRef) {
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


    public static class MaintenanceTaskHolder extends RecyclerView.ViewHolder {
        private final TextView mName, mDescription, mCreatorId;
        private final ImageView mTaskType;

        public MaintenanceTaskHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.task_name);
            mDescription = (TextView) itemView.findViewById(R.id.task_desc);
            mCreatorId = (TextView) itemView.findViewById(R.id.task_creator_id);
            mTaskType = (ImageView) itemView.findViewById(R.id.task_type);
        }

        public void setName(String name) {
            mName.setText(name);
        }

        public void setDescription(String text) {
            mDescription.setText(text);
        }

        public void setCreatorId(String text) {
            mCreatorId.setText(text);
        }

        public void setTaskType(boolean taskType) {
            if (taskType) {
                mTaskType.setImageResource(R.drawable.ic_internal_task);
            } else {
                mTaskType.setImageResource(R.drawable.ic_external_task);
            }
        }


    }

}
