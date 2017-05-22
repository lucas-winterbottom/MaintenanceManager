package com.mad.maintenancemanager.userActivites;

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
import com.mad.maintenancemanager.model.Group;
import com.mad.maintenancemanager.model.MaintenanceTask;
import com.mad.maintenancemanager.model.User;

public class GroupTasks extends Fragment {

    public static final int REQUEST_CODE = 123;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private DatabaseReference mTaskRef;
    private RecyclerView mRecycler;
    private FirebaseRecyclerAdapter<MaintenanceTask, MaintenanceTaskHolder> mAdapter;

    public GroupTasks() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_tasks, container, false);


        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.group_tasks_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewTaskActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        // showProgress();

        mRecycler = (RecyclerView) rootView.findViewById(R.id.group_tasks_recycler);
        getGroupKey(mAuth.getCurrentUser().getUid());


        return rootView;
    }
    public void getGroupKey(final String userID){
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(userID).getValue(User.class);
                if (user.getGroupKey() != null) {
                    setupRecycler(user.getGroupKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }


    public void setupRecycler(String groupKey) {
        mRecycler.setHasFixedSize(false);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();


        mTaskRef = FirebaseDatabase.getInstance().getReference(Constants.TASKS).child(groupKey);


        mAdapter = new FirebaseRecyclerAdapter<MaintenanceTask, MaintenanceTaskHolder>(MaintenanceTask.class, R.layout.task_card, MaintenanceTaskHolder.class, mTaskRef) {

            @Override
            protected void populateViewHolder(MaintenanceTaskHolder maintenanceTaskHolder, final MaintenanceTask maintenanceTask, final int i) {
                maintenanceTaskHolder.setCreatorId(maintenanceTask.getCreatorID());
                maintenanceTaskHolder.setDescription(maintenanceTask.getDescription());
                maintenanceTaskHolder.setName(maintenanceTask.getName());
                maintenanceTaskHolder.setTaskType(maintenanceTask.isTaskType());
                if (maintenanceTask.getAssignedTo().equals(user.getDisplayName())) {
                    maintenanceTaskHolder.setAssignee("You");
                } else {
                    maintenanceTaskHolder.setAssignee(maintenanceTask.getAssignedTo());
                }
                //hideProgress();
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
                //hideProgress();
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.TASKS).child(data.getStringExtra(Constants.GROUP_KEY));
        FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (requestCode == REQUEST_CODE) {
            if (resultCode == ResultCodes.OK) {
                String key = databaseReference.push().getKey();
                databaseReference.child(key).
                        setValue(new MaintenanceTask(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                data.getStringExtra(Constants.TASK_NAME),
                                data.getStringExtra(Constants.TASK_DESCRIPTION),
                                data.getBooleanExtra(Constants.CONTRACTOR_NEEDED, false),
                                data.getStringExtra(Constants.ASSIGNED_MEMBER),
                                data.getStringArrayListExtra(Constants.TASK_ITEMS)));
            }


        }
    }
}
