package com.mad.maintenancemanager.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.adapter.MaintenanceTaskHolder;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.model.MaintenanceTask;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.List;

/**
 * Class to create recycyler and attach business logic to items in the recycler
 */

public class TasksPresenter {
    private Context mContext;

    public TasksPresenter(Context context) {
        mContext = context;
    }

    private FirebaseRecyclerAdapter adapter;

    /**
     * Creates a recycler adapter to provide back to any screen with active/completed tasks
     */
    public void getTasksRecyclerAdapter(final Query databasePath,
                                        final IOnRecyclerAdapterListener listener,
                                        final boolean isCompletedTasks) {

        adapter = new FirebaseRecyclerAdapter<MaintenanceTask,
                MaintenanceTaskHolder>(MaintenanceTask.class,
                R.layout.task_card, MaintenanceTaskHolder.class,
                databasePath) {


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

    /**
     * listeners for the recyclerAdapter
     */
    public interface IOnRecyclerAdapterListener {
        void onRecyclerAdapter(FirebaseRecyclerAdapter adapter);

    }

    /**
     * Calculates the number of days between task due date and current date
     *
     * @param date
     * @return
     */
    private String calculateDays(long date) {
        String daysText;
        LocalDate dueDate = LocalDate.ofEpochDay(date);
        LocalDate today = LocalDate.now();
        long days = ChronoUnit.DAYS.between(today, dueDate);
        if (days == 0) {
            return mContext.getString(R.string.due_today);
        } else if (days > 1) {
            return String.valueOf(days) + mContext.getString(R.string.days_remaining);
        } else if (days == 1) {
            return mContext.getString(R.string.due_tomorrow);
        } else {
            days = Math.abs(days);
            return days + mContext.getString(R.string.days_overdue);
        }
    }

    /**
     * Creates a long click that diplays and dialog and shows options for the long-presssed task
     *
     * @param taskName
     * @param position
     * @param adapter
     * @param activityContext
     */
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
                                DatabaseReference ref = adapter.getRef(position);
                                FirebaseDatabase.getInstance().getReference(Constants.EXTERNAL_TASKS).child(ref.getKey()).removeValue();
                                ref.removeValue();
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

