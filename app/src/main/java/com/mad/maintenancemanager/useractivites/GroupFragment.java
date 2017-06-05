package com.mad.maintenancemanager.useractivites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.ResultCodes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.model.Group;
import com.mad.maintenancemanager.presenter.GroupsPresenter;
import com.mad.maintenancemanager.presenter.TasksPresenter;


public class GroupFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_CODE = 123;
    public static final int REQUEST_CODE1 = 1234;
    private TextView mNoGroupMessageTv, mGroupNameTV, mGroupMembersTV;
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
        mNewGroupTab = (FloatingActionButton) rootView.findViewById(R.id.group_new_group_fab);
        mNewGroupTab.setOnClickListener(this);
        mExistingGroupFab = (FloatingActionButton) rootView.findViewById(R.id.group_existing_group_fab);
        mExistingGroupFab.setOnClickListener(this);
        mAddMemberFab = (FloatingActionButton) rootView.findViewById(R.id.group_new_member_fab);
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
                    mRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
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
        if (id == R.id.group_new_group_fab) {
            Intent intent = new Intent(getContext(), NewGroupActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
        if (id == R.id.group_existing_group_fab) {
            Intent intent2 = new Intent(getContext(), JoinExisting.class);
            startActivityForResult(intent2, REQUEST_CODE1);
        }
        if (id == R.id.group_new_member_fab) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(Constants.TEXT_PLAIN);
            String message = getString(R.string.join_group_mm) +
                    getString(R.string.group_code_qulaifier) + DatabaseHelper.getInstance().getGroupKey();
            intent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(intent);

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
//                String groupPin = data.getStringExtra(Constants.GROUP_PIN);
                String groupName = data.getStringExtra(Constants.GROUP_NAME);
                DatabaseHelper.getInstance().createGroup(new Group(groupName));
                mUnlocker.unlockNavDrawer();
            } else {
                Snackbar.make(getView(), R.string.creation_cancelled, Snackbar.LENGTH_SHORT).show();
            }
            refreshFragment();
        }
        if (requestCode == REQUEST_CODE1) {
            if (resultCode == ResultCodes.OK) {
                DatabaseHelper.getInstance().joinExistingGroup(data.getStringExtra(Constants.GROUP_KEY), new DatabaseHelper.IJoinGroupListener() {
                    @Override
                    public void onTryJoinResult(boolean joined) {
                        if (joined) {
                            refreshFragment();
                            mUnlocker.unlockNavDrawer();
                        } else {
                            Toast.makeText(getContext(), "Invalid group code", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                Snackbar.make(getView(), R.string.joining_cancelled, Snackbar.LENGTH_SHORT).show();
            }

        }
    }

    public interface INavUnlocker {
        void unlockNavDrawer();
    }

}
