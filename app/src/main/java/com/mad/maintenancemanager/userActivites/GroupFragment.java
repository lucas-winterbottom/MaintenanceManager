package com.mad.maintenancemanager.userActivites;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.mad.maintenancemanager.adapter.MemberHolder;
import com.mad.maintenancemanager.model.Group;
import com.mad.maintenancemanager.model.User;

import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_CODE = 123;
    public static final int REQUEST_CODE1 = 1234;
    public static final String GROUP_NAME_DATA = "groupName";
    public static final String JOINING_CANCELLED = "Joining Cancelled";
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private TextView mGroupName;
    private FirebaseRecyclerAdapter<String, MemberHolder> mAdapter;
    private RecyclerView mRecycler;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mGroupName = (TextView) rootView.findViewById(R.id.group_group_name_tv);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.group_group_recycler);


        //FAB Setup
        final FloatingActionButton newGroupFab = (FloatingActionButton) rootView.findViewById(R.id.group_new_group);
        newGroupFab.setOnClickListener(this);
        final FloatingActionButton existingGroupFab = (FloatingActionButton) rootView.findViewById(R.id.group_existing_group);
        existingGroupFab.setOnClickListener(this);
        final FloatingActionButton addMember = (FloatingActionButton) rootView.findViewById(R.id.group_new_member);
        existingGroupFab.setOnClickListener(this);


        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(User.class);
                if (user.getGroupKey() != null) {
                    existingGroupFab.setVisibility(View.GONE);
                    newGroupFab.setVisibility(View.GONE);
                    addMember.setVisibility(View.VISIBLE);

                    getGroupKey(user.getGroupKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });


        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.group_new_group){
            Intent intent = new Intent(getContext(), NewGroupActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
        if(id == R.id.group_existing_group){
            Intent intent2 = new Intent(getContext(), JoinExisting.class);
            startActivityForResult(intent2, REQUEST_CODE1);
        }

        //// TODO: 20/5/17 Add method to invite user.
    }

    public void getGroupKey(final String groupKey) {

        DatabaseReference group = FirebaseDatabase.getInstance().getReference(Constants.GROUPS);
        group.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.child(groupKey).getValue(Group.class);
                mGroupName.setText(group.getGroupName());
                setUpRecycler(groupKey, group.getGroupCreator());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpRecycler(String groupKey, final String creatorID) {
        DatabaseReference group = FirebaseDatabase.getInstance().getReference(Constants.GROUPS).child(groupKey).child(Constants.GROUP_MEMBERS);
        mRecycler.setHasFixedSize(false);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new FirebaseRecyclerAdapter<String, MemberHolder>(String.class, R.layout.name_card, MemberHolder.class, group) {

            @Override
            protected void onDataChanged() {
                super.onDataChanged();
            }

            @Override
            protected void populateViewHolder(MemberHolder memberHolder, String s, int i) {
                memberHolder.setMemberName(s);
                if (creatorID.equals(mAuth.getCurrentUser().getDisplayName()) && i > 0) {
                    memberHolder.setDeleteBtn(true);
                } else if (creatorID.equals(mAuth.getCurrentUser().getDisplayName()) && i == 0) {
                    memberHolder.setColour(R.color.colorAccent);
                }

            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final FirebaseUser user = mAuth.getCurrentUser();
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == REQUEST_CODE) {
            if (resultCode == ResultCodes.OK) {
                String groupName = data.getStringExtra(Constants.GROUP_NAME);
                String groupPin = data.getStringExtra(Constants.GROUP_PIN);

                List<String> userList = new ArrayList<>();
                userList.add(user.getDisplayName());
                Group group = new Group(groupName, user.getDisplayName(), Integer.parseInt(groupPin), userList);
                String key = mRef.child(Constants.GROUPS).push().getKey();
                mRef.child(Constants.GROUPS).child(key).setValue(group);
                mRef.child(Constants.USERS).child(user.getUid()).child(Constants.GROUP_KEY).setValue(key);
            } else {
                Snackbar.make(getView(), R.string.creation_cancelled, Snackbar.LENGTH_SHORT).show();
            }
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
        if (requestCode == REQUEST_CODE1) {
            if (resultCode == ResultCodes.OK) {
                final String groupKey = data.getStringExtra(Constants.GROUP_KEY);
                mRef.child(Constants.GROUPS).child(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Group group = dataSnapshot.getValue(Group.class);
                        group.getGroupMembers().add(user.getDisplayName());
                        mRef.child(Constants.GROUPS).child(groupKey).setValue(group);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mRef.child(Constants.USERS).child(user.getUid()).child(Constants.GROUP_KEY).setValue(groupKey);
            } else {
                Snackbar.make(getView(), JOINING_CANCELLED, Snackbar.LENGTH_SHORT).show();

            }

        }
    }


}
