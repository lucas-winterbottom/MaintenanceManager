package com.mad.maintenancemanager.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.adapter.ExternalTaskHolder;
import com.mad.maintenancemanager.adapter.MaintenanceTaskHolder;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.model.MaintenanceTask;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import static android.content.Intent.ACTION_DIAL;

/**
 * Class to create recycyler and attach business logic to items in the recycler
 */

public class LocationTasksPresenter {
    private Context mContext;

    public LocationTasksPresenter(Context context) {
        mContext = context;
    }

    /**
     * Creates a recylcer adapter to provide back to any screen with external tasks
     */
    public void getTasksRecyclerAdapter(final Query databasePath,
                                        final TasksPresenter.IOnRecyclerAdapterListener listener) {
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<MaintenanceTask,
                ExternalTaskHolder>(MaintenanceTask.class,
                R.layout.external_task_card, ExternalTaskHolder.class,
                databasePath) {

            @Override
            protected void populateViewHolder(ExternalTaskHolder maintenanceTaskHolder,
                                              final MaintenanceTask maintenanceTask,
                                              final int i) {
                maintenanceTaskHolder.setDescription(maintenanceTask.getDescription());
                maintenanceTaskHolder.setName(maintenanceTask.getName());
                maintenanceTaskHolder.setCreator(maintenanceTask.getCreatorID());
                maintenanceTaskHolder.setCall(getClick(maintenanceTask.getMobile()));
            }


            @Override
            protected void onDataChanged() {
                super.onDataChanged();
                listener.onRecyclerAdapter(this);
            }

        };
    }

    /**
     * Create click listener that start dialer
     * @param number
     * @return
     */
    private OnClickListener getClick(final String number) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION_DIAL);
                intent.setData(Uri.parse(Constants.TEL + number));
                mContext.startActivity(intent);
            }
        };
    }


}
