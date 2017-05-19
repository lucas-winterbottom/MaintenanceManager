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

public class MemberHolder extends RecyclerView.ViewHolder {


    private final TextView mMemberName;
    private final Button mDeleteBtn;

    public MemberHolder(View itemView) {
        super(itemView);
        mMemberName = (TextView) itemView.findViewById(R.id.name_name);
        mDeleteBtn = (Button) itemView.findViewById(R.id.group_page_delete);
    }

    public void setMemberName(String name) {
        mMemberName.setText(name);
    }

    public void setColour(int color) {
        mMemberName.setTextColor(color);
    }

    public void setDeleteBtn(boolean isCreator) {
        if (isCreator) {
            mDeleteBtn.setVisibility(View.VISIBLE);
        } else {
            mDeleteBtn.setVisibility(View.INVISIBLE);
        }
    }
}
