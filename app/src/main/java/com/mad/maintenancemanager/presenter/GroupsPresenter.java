package com.mad.maintenancemanager.presenter;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.adapter.MemberHolder;
import com.mad.maintenancemanager.api.DatabaseHelper;

/**
 * Created by lucaswinterbottom on 23/5/17.
 */

public class GroupsPresenter {

    public void getGroupRecycler(final TasksPresenter.IOnRecyclerAdapterListener listener) {
        String key = DatabaseHelper.getInstance().getGroupKey();
        if (key != null) {
            DatabaseReference group = FirebaseDatabase.getInstance().getReference(Constants.GROUPS).child(key).child(Constants.GROUP_MEMBERS);
            FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<String, MemberHolder>(String.class,
                    R.layout.name_card, MemberHolder.class, group) {

                @Override
                protected void onDataChanged() {
                    super.onDataChanged();
                }

                @Override
                protected void populateViewHolder(MemberHolder memberHolder, String s, int i) {
                    memberHolder.setMemberName(s);
                }
            };
            listener.onRecyclerAdapter(adapter);
        } else {
            listener.onRecyclerAdapter(null);
        }
    }

}
