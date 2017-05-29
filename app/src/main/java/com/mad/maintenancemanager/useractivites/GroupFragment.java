package com.mad.maintenancemanager.useractivites;

import android.content.Context;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.model.Group;
import com.mad.maintenancemanager.presenter.GroupsPresenter;
import com.mad.maintenancemanager.presenter.TasksPresenter;


public class GroupFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_CODE = 123;
    public static final int REQUEST_CODE1 = 1234;
    public static final String JOINING_CANCELLED = "Joining Cancelled";
    private TextView mNoGroupMessageTv,mGroupNameTV, mGroupMembersTV;
    private RecyclerView mRecycler;
    private FloatingActionButton mNewGroupTab, mAddMemberFab, mExistingGroupFab;
    private INavUnlocker mUnlocker;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mUnlocker = (INavUnlocker) context;
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);
        mNoGroupMessageTv = (TextView) rootView.findViewById(R.id.group_please_create_or_join_group_message_tv);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.group_group_recycler);
        mGroupNameTV = (TextView) rootView.findViewById(R.id.group_group_name_tv);
        mGroupMembersTV = (TextView) rootView.findViewById(R.id.group_group_members_tv);

        //FAB Setup
        mNewGroupTab = (FloatingActionButton) rootView.findViewById(R.id.group_new_group);
        mNewGroupTab.setOnClickListener(this);
        mExistingGroupFab = (FloatingActionButton) rootView.findViewById(R.id.group_existing_group);
        mExistingGroupFab.setOnClickListener(this);
        mAddMemberFab = (FloatingActionButton) rootView.findViewById(R.id.group_new_member);
        mAddMemberFab.setOnClickListener(this);


        DatabaseHelper.getInstance().getGroup(new DatabaseHelper.IGroupListener() {
            @Override
            public void onGroup(Group group) {
                mGroupNameTV.setText(group.getGroupName());
                mGroupMembersTV.append(String.valueOf(group.getGroupMembers().size()));
                mGroupMembersTV.setVisibility(View.VISIBLE);
            }
        });
        GroupsPresenter presenter = new GroupsPresenter();
        presenter.getGroupRecycler(new TasksPresenter.IOnRecyclerAdapterListener() {
            @Override
            public void onRecyclerAdapter(FirebaseRecyclerAdapter adapter) {
                if (adapter != null) {
                    alternateFabs();
                    mRecycler.setHasFixedSize(false);
                    mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    mRecycler.setAdapter(adapter);
                } else {
                    mNoGroupMessageTv.setVisibility(View.VISIBLE);
                }

            }
        });
        return rootView;
    }


    private void alternateFabs() {
        mExistingGroupFab.setVisibility(View.GONE);
        mNewGroupTab.setVisibility(View.GONE);
        mAddMemberFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.group_new_group) {
            Intent intent = new Intent(getContext(), NewGroupActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
        if (id == R.id.group_existing_group) {
            Intent intent2 = new Intent(getContext(), JoinExisting.class);
            startActivityForResult(intent2, REQUEST_CODE1);
        }

        //// TODO: 20/5/17 Add method to invite user.
    }

    public void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == ResultCodes.OK) {
                String groupName = data.getStringExtra(Constants.GROUP_NAME);
                String groupPin = data.getStringExtra(Constants.GROUP_PIN);
                //// TODO: 26/5/17 check validation
                DatabaseHelper.getInstance().createGroup(new Group(groupName, Integer.parseInt(groupPin)));
                mUnlocker.unlockNavDrawer();
            } else {
                Snackbar.make(getView(), R.string.creation_cancelled, Snackbar.LENGTH_SHORT).show();
            }
            refreshFragment();
        }
        if (requestCode == REQUEST_CODE1) {
            if (resultCode == ResultCodes.OK) {
                //// TODO: 24/5/17 check if the group exist and the key matches if not send correct error message
                DatabaseHelper.getInstance().joinExistingGroup(data.getStringExtra(Constants.GROUP_KEY));
                mUnlocker.unlockNavDrawer();
            } else {
                Snackbar.make(getView(), JOINING_CANCELLED, Snackbar.LENGTH_SHORT).show();
            }

        }
    }

    public interface INavUnlocker {
        void unlockNavDrawer();
    }

}
