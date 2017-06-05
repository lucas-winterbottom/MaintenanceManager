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
 * This class is the viewHolder for external tasks that are used in the MapActivity
 * - LocationFragment - RecyclerView
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

    /**
     * Places a string in the appropriate textview of External Task Card
     *
     * @param name The string that will be placed in the textview
     */
    public void setName(String name) {
        mName.setText(name);
    }

    /**
     * Places a string in the appropriate textview of External Task Card
     *
     * @param text The string that will be placed in the textview
     */
    public void setDescription(String text) {
        mDescription.setText(text);
    }

    /**
     * Places a string in the appropriate textview of External Task Card
     *
     * @param text The string that will be placed in the textview
     */
    public void setCreator(String text) {
        mCreator.setText(text);
    }


    /**
     * Sets the onclick listener of the floating action button
     *
     * @param click Action to start the dialer with provided number
     */
    public void setCall(View.OnClickListener click) {
        mCall.setOnClickListener(click);
    }


}



