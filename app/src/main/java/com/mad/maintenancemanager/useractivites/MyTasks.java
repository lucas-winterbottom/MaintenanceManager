package com.mad.maintenancemanager.useractivites;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.presenter.TasksPresenter;

/**
 * Fragment that shows the user the tasks currently assigned to them
 */
public class MyTasks extends Fragment {

    public static final int REQUEST_CODE = 123;
    private FirebaseRecyclerAdapter mAdapter;
    private ProgressBar mProgress;
    private RecyclerView mRecycler;
    private TextView mNoTasksMessageTv;

    /**
     * Empty Constructor for fragment use
     */
    public MyTasks() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_tasks, container, false);

        mProgress = (ProgressBar) rootView.findViewById(R.id.my_tasks_progress);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.my_tasks_recycler);
        mNoTasksMessageTv = (TextView) rootView.findViewById(R.id.no_task_message);
        mRecycler.setHasFixedSize(false);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Query ref = DatabaseHelper.getInstance().getMyTasksRef();
        TasksPresenter presenter = new TasksPresenter(getActivity());
        presenter.getTasksRecyclerAdapter(ref, new TasksPresenter.IOnRecyclerAdapterListener() {
            @Override
            public void onRecyclerAdapter(FirebaseRecyclerAdapter adapter) {
                mRecycler.setAdapter(adapter);
                hideProgress();
                if (adapter.getItemCount() == 0) {
                    mNoTasksMessageTv.setVisibility(View.VISIBLE);
                } else {
                    mNoTasksMessageTv.setVisibility(View.GONE);

                }

            }
        }, false);

        return rootView;
    }


    /**
     * hides the progress
     */
    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }


}


