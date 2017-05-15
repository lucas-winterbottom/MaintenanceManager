package com.mad.maintenancemanager.userActivites;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.SignedInUserActivity;

public class GroupTasks extends Fragment {

    public GroupTasks() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_group_tasks, container, false);
        return rootView;
    }
}
