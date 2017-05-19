package com.mad.maintenancemanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.maintenancemanager.R;

public class MaintenanceTaskHolder extends RecyclerView.ViewHolder {
    private final TextView mName, mDescription, mCreatorId;
    private final ImageView mTaskType;

    public MaintenanceTaskHolder(View itemView) {
        super(itemView);
        mName = (TextView) itemView.findViewById(R.id.task_name);
        mDescription = (TextView) itemView.findViewById(R.id.task_desc);
        mCreatorId = (TextView) itemView.findViewById(R.id.task_creator_id);
        mTaskType = (ImageView) itemView.findViewById(R.id.task_type);
    }

    public void setName(String name) {
        mName.setText(name);
    }

    public void setDescription(String text) {
        mDescription.setText(text);
    }

    public void setCreatorId(String text) {
        mCreatorId.setText(text);
    }

    public void setTaskType(boolean taskType) {
        if (taskType) {
            mTaskType.setImageResource(R.drawable.ic_internal_task);
        } else {
            mTaskType.setImageResource(R.drawable.ic_external_task);
        }
    }
}

