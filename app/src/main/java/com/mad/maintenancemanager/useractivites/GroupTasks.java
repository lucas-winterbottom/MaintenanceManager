package com.mad.maintenancemanager.useractivites;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.auth.ResultCodes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.adapter.MaintenanceTaskHolder;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.model.MaintenanceTask;
import com.mad.maintenancemanager.model.User;
import com.mad.maintenancemanager.presenter.TasksPresenter;

/**
 * Fragment that shows the user the tasks for the group they are currently in, gathers the users
 * group and then uses that to gather the
 */
public class GroupTasks extends Fragment {

    public static final int REQUEST_CODE = 123;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private DatabaseReference mTaskRef;
    private RecyclerView mRecycler;
    private FirebaseRecyclerAdapter<MaintenanceTask, MaintenanceTaskHolder> mAdapter;

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
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.group_tasks_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewTaskActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        // showProgress();
        TasksPresenter presenter = new TasksPresenter();
        presenter.getRecyclerAdapter(Constants.TASKS_ACTIVE_TASKS, new TasksPresenter.IOnRecyclerAdapterListener() {
            @Override
            public void onRecyclerAdapter(FirebaseRecyclerAdapter adapter) {
                mRecycler.setAdapter(adapter);
                //hideProgress();
            }
        },getActivity());

        return rootView;
    }



    /**
     * Handles the feedback from the NewTaskActivity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == ResultCodes.OK) {
                DatabaseHelper.getInstance().saveTask(new MaintenanceTask(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                        data.getStringExtra(Constants.TASK_NAME),
                        data.getStringExtra(Constants.TASK_DESCRIPTION),
                        data.getBooleanExtra(Constants.CONTRACTOR_NEEDED, false),
                        data.getStringExtra(Constants.ASSIGNED_MEMBER),
                        data.getStringExtra(Constants.TASK_ITEMS)));
            }


        }
    }
}
