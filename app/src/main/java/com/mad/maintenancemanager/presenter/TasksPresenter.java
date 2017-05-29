package com.mad.maintenancemanager.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Switch;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.adapter.MaintenanceTaskHolder;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.model.MaintenanceTask;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;
import org.threeten.bp.temporal.Temporal;

import java.sql.Date;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by lucaswinterbottom on 22/5/17.
 */

public class TasksPresenter {
    private Context mContext;

    public TasksPresenter(Context context) {
        mContext = context;
    }

    private FirebaseRecyclerAdapter adapter;

    /**
     * Gets the user data from Firebase and then extracts the group key to setup recycler
     */

    public void getRecyclerAdapter(final String databasePath, final IOnRecyclerAdapterListener listener, final boolean isCompletedTasks) {
        DatabaseHelper.getInstance().getGroupKey(new DatabaseHelper.IGroupKeyListener() {
            @Override
            public void onGroupKey(String key) {
                if (key != null) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(databasePath).child(key);
                    adapter = new FirebaseRecyclerAdapter<MaintenanceTask,
                            MaintenanceTaskHolder>(MaintenanceTask.class,
                            R.layout.task_card, MaintenanceTaskHolder.class, reference) {

                        @Override
                        protected void populateViewHolder(MaintenanceTaskHolder maintenanceTaskHolder,
                                                          final MaintenanceTask maintenanceTask,
                                                          final int i) {
                            maintenanceTaskHolder.setDescription(maintenanceTask.getDescription());
                            maintenanceTaskHolder.setName(maintenanceTask.getName());
                            maintenanceTaskHolder.setTaskType(maintenanceTask.isTaskType());
                            maintenanceTaskHolder.setAssignee(mContext.getString(R.string.assigned_to_)
                                    + maintenanceTask.getAssignedTo());
                            maintenanceTaskHolder.setDueDate(calculateDays(maintenanceTask.getDueDate()));

                            maintenanceTaskHolder.setItems(maintenanceTask.getNeededItems());
                            if (!isCompletedTasks) {
                                maintenanceTaskHolder.setLongClick(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        makeLongClick(maintenanceTask.getName(), i, adapter, mContext);
                                        return true;
                                    }
                                });
                                maintenanceTaskHolder.setClick();
                            }
                        }

                        @Override
                        protected void onDataChanged() {
                            super.onDataChanged();
                            listener.onRecyclerAdapter(this);
                        }

                    };
                }
            }
        });
    }


    public interface IOnRecyclerAdapterListener {
        void onRecyclerAdapter(FirebaseRecyclerAdapter adapter);
    }

    private String calculateDays(String date) {
        String daysText;
        LocalDate dueDate = LocalDate.parse(date);
        LocalDate today = LocalDate.now();
        Period period = Period.between(dueDate, today);
        if (period.getDays() == 0) {
            return "Today";
        }


        daysText = String.valueOf(period.getDays());
        return daysText;
    }

    private void makeLongClick(final String taskName, final int position
            , final FirebaseRecyclerAdapter adapter, final Context activityContext) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setTitle(taskName)
                .setItems(R.array.options_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {

                            case 0:
                                DatabaseHelper.getInstance().markDone(adapter.getRef(position));
                                //adapter.getRef(position).removeValue();
                                break;
                            case 1:
                                adapter.getRef(position).removeValue();
                                break;
                            case 2:
                                break;
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

