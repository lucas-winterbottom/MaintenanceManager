package com.mad.maintenancemanager.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.mad.maintenancemanager.R;

/**
 * Created by lucaswinterbottom on 4/6/17.
 */

public class ExternalTaskHolder extends RecyclerView.ViewHolder {


    private final TextView mName, mDescription;
    private final TextView mCreator;
    private final FloatingActionButton mCall;

    public ExternalTaskHolder(View itemView) {
        super(itemView);
        mName = (TextView) itemView.findViewById(R.id.external_task_name);
        mDescription = (TextView) itemView.findViewById(R.id.external_task_desc);
        mCreator = (TextView) itemView.findViewById(R.id.external_task_creator_id);
        mCall = (FloatingActionButton) itemView.findViewById(R.id.external_call);
    }

    public void setName(String name) {
        mName.setText(name);
    }

    public void setDescription(String text) {
        mDescription.setText(text);
    }

    public void setCreator(String text) {
        mCreator.setText(text);
    }

    public void setCall(View.OnClickListener click) {
        mCall.setOnClickListener(click);
    }


}



