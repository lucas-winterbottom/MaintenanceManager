package com.mad.maintenancemanager.useractivites;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.presenter.TasksPresenter;

public class CompletedTasksFragment extends Fragment {


    private RecyclerView mRecycler;
    private ProgressBar mProgress;
    private TextView mNoCompletionText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_completed_tasks, container, false);

        mProgress = (ProgressBar) rootView.findViewById(R.id.completed_tasks_progress);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.completed_tasks_recycler);
        mNoCompletionText = (TextView) rootView.findViewById(R.id.no_completed_task_message);
        mRecycler.setHasFixedSize(false);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        Query ref = DatabaseHelper.getInstance().getCompletedTasksRef();
        TasksPresenter presenter = new TasksPresenter(getContext());
        presenter.getTasksRecyclerAdapter(ref, new TasksPresenter.IOnRecyclerAdapterListener() {
            @Override
            public void onRecyclerAdapter(FirebaseRecyclerAdapter adapter) {
                mRecycler.setAdapter(adapter);
                hideProgress();
                if (adapter.getItemCount()==0){
                    mNoCompletionText.setVisibility(View.VISIBLE);
                }
            }
        },true );



        return rootView;
    }

    private void hideProgress(){
        mProgress.setVisibility(View.GONE);
    }
}