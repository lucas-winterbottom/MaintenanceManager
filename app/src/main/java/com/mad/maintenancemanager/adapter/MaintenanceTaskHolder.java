package com.mad.maintenancemanager.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mad.maintenancemanager.R;

/**
 * Viewholder for Maintenance Tasks when used in MyTasks, GroupTasks, Completed Tasks
 */
public class MaintenanceTaskHolder extends RecyclerView.ViewHolder {
    private final TextView mName, mDescription, mDueDate, mAssignedTo, mItems;
    private final ImageView mTaskType;
    private final CardView mCardView;
    private final LinearLayout mItemsLayout;

    public MaintenanceTaskHolder(View itemView) {
        super(itemView);
        mName = (TextView) itemView.findViewById(R.id.task_name);
        mDescription = (TextView) itemView.findViewById(R.id.task_desc);
        mDueDate = (TextView) itemView.findViewById(R.id.task_due_date);
        mTaskType = (ImageView) itemView.findViewById(R.id.task_type);
        mAssignedTo = (TextView) itemView.findViewById(R.id.task_assignee);
        mCardView = (CardView) itemView.findViewById(R.id.task_card_view);
        mItems = (TextView) itemView.findViewById(R.id.task_items);
        mItemsLayout = (LinearLayout) itemView.findViewById(R.id.task_needed_items_view);
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
    public void setDueDate(String text) {
        mDueDate.setText(text);
    }

    /**
     * Sets the displpayed icon for the task to reporesent internal and external tasks
     *
     * @param taskType whether or not the task is internal(false) or external(true)
     */
    public void setTaskType(boolean taskType) {
        if (!taskType) {
            mTaskType.setImageResource(R.drawable.ic_internal_task);
        } else {
            mTaskType.setImageResource(R.drawable.ic_external_task);
        }
    }

    /**
     * Places a string in the appropriate textview of External Task Card
     *
     * @param items The string that will be placed in the textview
     */
    public void setItems(String items) {
        mItems.setText(items);
    }

    /**
     * Sets the visibility of the needed items to show (called in makeclick)
     */
    public void setItemsVisibility() {
        if (mItems.getVisibility() == View.GONE) {
            mItems.setVisibility(View.VISIBLE);
        } else {
            mItems.setVisibility(View.GONE);
        }
    }

    /**
     * Places a string in the appropriate textview of External Task Card
     *
     * @param assignee The string that will be placed in the textview
     */
    public void setAssignee(String assignee) {

        mAssignedTo.setText(assignee);
    }

    /**
     * Sets the long click option of the view
     * @param longClick the long click listen with the action to show dilog when held
     */
    public void setLongClick(View.OnLongClickListener longClick) {
        mCardView.setOnLongClickListener(longClick);
    }

    /**
     * Sets the click method of the view
     */
    public void setClick() {
        mCardView.setOnClickListener(makeClick());
    }

    /**
     * Method to make on click listener
     * @return listener that calls setItemsVisibility onClick
     */
    private View.OnClickListener makeClick() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemsLayout.getVisibility() == View.GONE) {
                    mItemsLayout.setVisibility(View.VISIBLE);
                } else {
                    mItemsLayout.setVisibility(View.GONE);
                }
            }
        };
        return listener;
    }

}

