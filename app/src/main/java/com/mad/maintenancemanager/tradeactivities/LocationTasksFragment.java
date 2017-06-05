package com.mad.maintenancemanager.tradeactivities;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.model.TempPlace;
import com.mad.maintenancemanager.presenter.LocationTasksPresenter;
import com.mad.maintenancemanager.presenter.TasksPresenter;

/**
 * fragment to show  the available tasks at the clicked location
 */
public class LocationTasksFragment extends DialogFragment {


    private IRetrieveClickedLocation mLocationGetter;
    private ProgressBar mProgress;
    private RecyclerView mRecycler;
    private TempPlace mMarker;
    private TextView mTitle;

    public LocationTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getArguments().getString(Constants.TASK_LOCATION);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_location_tasks, container, false);
        mProgress = (ProgressBar) rootView.findViewById(R.id.location_tasks_progress);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.location_tasks_recycler);
        mTitle = (TextView) rootView.findViewById(R.id.location_name);
        mTitle.setText(getArguments().getString(Constants.LOCATION_NAME));
        mRecycler.setHasFixedSize(false);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //Setup Recycler
        Query ref = FirebaseDatabase.getInstance().getReference(Constants.EXTERNAL_TASKS)
                .orderByChild(Constants.TASK_LOCATION).equalTo(getArguments().getString(Constants.TASK_LOCATION));
        LocationTasksPresenter presenter = new LocationTasksPresenter(getActivity());
        presenter.getTasksRecyclerAdapter(ref, new TasksPresenter.IOnRecyclerAdapterListener() {
            @Override
            public void onRecyclerAdapter(FirebaseRecyclerAdapter adapter) {
                mRecycler.setAdapter(adapter);
                hideProgress();

            }
        });

        return rootView;
    }

    /**
     * hide the progress bar
     */
    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDisplayMetrics().heightPixels / 2);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mLocationGetter = (LocationTasksFragment.IRetrieveClickedLocation) context;
            mMarker = mLocationGetter.getClickedMarker();
        } catch (ClassCastException castException) {

        }
    }

    public interface IRetrieveClickedLocation {
        TempPlace getClickedMarker();
    }

}
