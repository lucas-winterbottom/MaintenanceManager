package com.mad.maintenancemanager.useractivites;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.ResultCodes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.adapter.MaintenanceTaskHolder;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.model.MaintenanceTask;
import com.mad.maintenancemanager.presenter.TasksPresenter;

/**
 * Fragment that shows the user the tasks for the group they are currently in
 */
public class GroupTasks extends Fragment {

    public static final int REQUEST_CODE = 123;
    private RecyclerView mRecycler;
    private FirebaseRecyclerAdapter<MaintenanceTask, MaintenanceTaskHolder> mAdapter;
    private Query mRef;
    private ProgressBar mProgress;
    private TextView mNoTasksMessageTv;

    /**
     * Empty constructor for fragment use
     */
    public GroupTasks() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_tasks, container, false);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.group_tasks_recycler);
        mRecycler.setHasFixedSize(false);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mProgress = (ProgressBar) rootView.findViewById(R.id.group_tasks_progress);
        mNoTasksMessageTv = (TextView) rootView.findViewById(R.id.no_group_task_message);


        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.group_tasks_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewTaskActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        mRef = DatabaseHelper.getInstance().getGroupTasksRef();
        TasksPresenter presenter = new TasksPresenter(getActivity());
        presenter.getTasksRecyclerAdapter(mRef, new TasksPresenter.IOnRecyclerAdapterListener() {
            @Override
            public void onRecyclerAdapter(FirebaseRecyclerAdapter adapter) {
                if (adapter.getItemCount() == 0) {
                    mNoTasksMessageTv.setVisibility(View.VISIBLE);
                } else {
                    mNoTasksMessageTv.setVisibility(View.GONE);
                }
                mRecycler.setAdapter(adapter);
                hideProgress();
            }
        }, false);

        return rootView;
    }

    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    /**
     * Handles the feedback from the NewTaskActivity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == ResultCodes.OK) {
                Gson gson = new GsonBuilder().create();
                MaintenanceTask task = gson.fromJson(data.getStringExtra(Constants.TASKS),
                        MaintenanceTask.class);
                String place = data.getStringExtra(Constants.PLACE);
                task.setTaskLocationData(place);
                if (task.isTaskType()) {
                    DatabaseHelper.getInstance().saveExternalTask(task);
                } else {
                    DatabaseHelper.getInstance().saveTask(task);
                }
            }
        }
    }
}
