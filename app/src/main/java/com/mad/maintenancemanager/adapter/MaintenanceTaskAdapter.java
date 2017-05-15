package com.mad.maintenancemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.model.MaintenanceTask;

import java.util.List;

/**
 * Created by lucaswinterbottom on 15/4/17.
 *
 * This class is the adapter for the MaintenanceTask data to appear correctly in the recycler view
 */

public class MaintenanceTaskAdapter extends RecyclerView.Adapter<MaintenanceTaskAdapter.ViewHolder> {

    private List<MaintenanceTask> mMaintenanceTasksList;
    private Context mApplicationContext;

    /**
     * This class is the holder of the elements that will be filled by the MaintenanceTask data.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName, mDescription, mCreatorId;
        public ImageView mTaskType;

        /**
         * This constructor attaches the xml elements of the MaintenanceTask_item.xml to java objects to be able to be accessed.
         *
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mName = (TextView) view.findViewById(R.id.task_name);
            mDescription = (TextView) view.findViewById(R.id.task_desc);
            mCreatorId = (TextView) view.findViewById(R.id.task_creator_id);
            mTaskType = (ImageView) view.findViewById(R.id.task_type);
        }
    }

    /**
     * This is just a constructor for MaintenanceTaskAdapter, that takes in the list of MaintenanceTasks , and the
     * current context of the application.
     *
     * @param context
     * @param mMaintenanceTasksList
     */
    public MaintenanceTaskAdapter(Context context, List<MaintenanceTask> mMaintenanceTasksList) {
        this.mMaintenanceTasksList = mMaintenanceTasksList;
        mApplicationContext = context;
    }


    /**
     * This method is unchanged
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_card, parent, false);

        return new ViewHolder(itemView);
    }


    /**
     * This method binds the data from the MaintenanceTask list to each element of the ViewHolder, which was
     * gathered from the MaintenanceTask_item.xml
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MaintenanceTask maintenanceTask = mMaintenanceTasksList.get(position);
        holder.mName.setText(maintenanceTask.getName());
        holder.mDescription.setText(maintenanceTask.getDescription());
        holder.mCreatorId.setText(maintenanceTask.getCreatorID());


        //Depending on what the status of the MaintenanceTask is change the text color accordingly
        if (maintenanceTask.isTaskType()) {
            holder.mTaskType.setImageResource(R.drawable.ic_internal_task);
        } else {
            holder.mTaskType.setImageResource(R.drawable.ic_external_task);
        }
    }

    @Override
    public int getItemCount() {
        return mMaintenanceTasksList.size();
    }
}