package com.mad.maintenancemanager.adapter;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mad.maintenancemanager.R;

/**
 * Created by lucaswinterbottom on 19/5/17.
 */

/**
 * ViewHolder for member objects, used in groupFragment - RecyclerView
 */
public class MemberHolder extends RecyclerView.ViewHolder {


    private final TextView mMemberName;

    public MemberHolder(View itemView) {
        super(itemView);
        mMemberName = (TextView) itemView.findViewById(R.id.name_name);
    }

    /**
     * Places a string in the appropriate textview of External Task Card
     *
     * @param name The string that will be placed in the textview
     */
    public void setMemberName(String name) {
        mMemberName.setText(name);
    }


}
