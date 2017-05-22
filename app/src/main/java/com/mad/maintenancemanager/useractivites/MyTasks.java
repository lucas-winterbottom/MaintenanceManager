package com.mad.maintenancemanager.useractivites;

import android.content.DialogInterface;
import android.os.Bundle;
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

/**
 * Fragment that shows the user the tasks currently assigned to them
 */
public class MyTasks extends Fragment {

    public static final int REQUEST_CODE = 123;
    private FirebaseRecyclerAdapter mAdapter;
    private DatabaseReference mTaskRef;
    private ProgressBar mProgress;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RecyclerView mRecycler;

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
        mRecycler.setHasFixedSize(false);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        getGroupKey(new IGroupKeyListener() {
            @Override
            public void onGroupKey(String key) {
                setUpRecycler(key);
                hideProgress();
            }
        });

        return rootView;
    }

    /**
     * Gets the user data from Firebase and then extracts the group key to setup recycler
     */
    private void getGroupKey(final IGroupKeyListener listener) {
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(User.class);
                String groupKey = "";
                if (user.getGroupKey() != null) {
                    groupKey = user.getGroupKey();
                }
                listener.onGroupKey(groupKey);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }

    /**
     * Method to setup the FirebaseRecyclerView with only tasks assigned to you
     *
     * @param groupKey
     */
    private void setUpRecycler(String groupKey) {
        mTaskRef = FirebaseDatabase.getInstance().getReference(Constants.TASKS_ACTIVE_TASKS).child(groupKey);
        mAdapter = new FirebaseRecyclerAdapter<MaintenanceTask, MaintenanceTaskHolder>(MaintenanceTask.class,
                R.layout.task_card, MaintenanceTaskHolder.class, mTaskRef) {

            @Override
            protected void populateViewHolder(final MaintenanceTaskHolder maintenanceTaskHolder,
                                              final MaintenanceTask maintenanceTask, final int i) {
                maintenanceTaskHolder.setCreatorId(maintenanceTask.getCreatorID());
                maintenanceTaskHolder.setDescription(maintenanceTask.getDescription());
                maintenanceTaskHolder.setName(maintenanceTask.getName());
                maintenanceTaskHolder.setTaskType(maintenanceTask.isTaskType());
                maintenanceTaskHolder.setItems(maintenanceTask.getNeededItems().toString());

                //// TODO: 22/5/17 move into maintenancetaskholder

                if (maintenanceTask.getAssignedTo().equals(mAuth.getCurrentUser().getDisplayName())) {
                    maintenanceTaskHolder.setAssignee(getString(R.string.assigned_to_) + " You");
                } else {
                    maintenanceTaskHolder.setAssignee(getString(R.string.assigned_to_) + maintenanceTask.getAssignedTo());
                }

                maintenanceTaskHolder.setLongClick(makeLongClick(maintenanceTask.getName(), i));
                maintenanceTaskHolder.setClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        maintenanceTaskHolder.setItemsVisibility();
                    }
                });
                hideProgress();
            }

            @Override
            protected void onDataChanged() {
                super.onDataChanged();
                hideProgress();
            }
        };
        mRecycler.setAdapter(mAdapter);

    }

    private View.OnLongClickListener makeLongClick(final String taskName, final int position) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //// TODO: 22/5/17 Move outside
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(taskName)
                        .setItems(R.array.options_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {

                                    case 0:
                                        mAdapter.getRef(position).removeValue();
                                        break;
                                    case 1:
                                        mAdapter.getRef(position).removeValue();
                                        break;
                                    case 2:
                                        break;
                                }
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        };
    }

    /**
     * hides the progress
     */
    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }


    private interface IGroupKeyListener {
        void onGroupKey(String key);
    }
}


