package com.mad.maintenancemanager.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.adapter.MaintenanceTaskHolder;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.model.MaintenanceTask;

/**
 * Created by lucaswinterbottom on 22/5/17.
 */

public class TasksPresenter {
    private Context mContext;

    public TasksPresenter(Context context){
        mContext = context;
    }

    /**
     * Gets the user data from Firebase and then extracts the group key to setup recycler
     *
     */

    public void getRecyclerAdapter(final String databasePath, final IOnRecyclerAdapterListener listener, final boolean isCompletedTasks) {
        DatabaseHelper.getInstance().getGroupKey(new DatabaseHelper.IGroupKeyListener() {
            @Override
            public void onGroupKey(String key) {
                if (key != null) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(databasePath).child(key);
                    FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<MaintenanceTask,
                            MaintenanceTaskHolder>(MaintenanceTask.class,
                            R.layout.task_card, MaintenanceTaskHolder.class, reference) {

                        @Override
                        protected void populateViewHolder(MaintenanceTaskHolder maintenanceTaskHolder,
                                                          final MaintenanceTask maintenanceTask,
                                                          final int i) {
                            maintenanceTaskHolder.setCreatorId(maintenanceTask.getCreatorID());
                            maintenanceTaskHolder.setDescription(maintenanceTask.getDescription());
                            maintenanceTaskHolder.setName(maintenanceTask.getName());
                            maintenanceTaskHolder.setTaskType(maintenanceTask.isTaskType());
                            maintenanceTaskHolder.setAssignee(maintenanceTask.getAssignedTo());
                            if (!isCompletedTasks) {
                                maintenanceTaskHolder.setLongClick(makeLongClick(maintenanceTask.getName(),
                                        i, this, mContext));
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


    private View.OnLongClickListener makeLongClick(final String taskName, final int position
            , final FirebaseRecyclerAdapter adapter, final Context activityContext) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //// TODO: 22/5/17 Move outside
                AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
                builder.setTitle(taskName)
                        .setItems(R.array.options_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {

                                    case 0:
                                        DatabaseHelper.getInstance().markDone(adapter.getRef(position));
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
                return true;
            }
        };
    }

}

