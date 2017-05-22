package com.mad.maintenancemanager.useractivites;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.model.Group;
import com.mad.maintenancemanager.model.User;

import java.util.List;

public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner mGroupMemberSpinner;
    private FirebaseAuth mAuth;
    private List<String> mGroupMembers;
    private Spinner mContractorSpinner;
    private Switch mContractorSwitch;
    private TextInputEditText mTaskNameEt;
    private TextInputEditText mTaskDescriptionEt;
    private TextInputEditText mTaskExtraItemsEt;
    private Button mSubmitButton;
    private String mGroupKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        mGroupMemberSpinner = (Spinner) findViewById(R.id.new_task_assign_to_spnr);
        mContractorSpinner = (Spinner) findViewById(R.id.new_task_contractor_type_spnr);
        mContractorSwitch = (Switch) findViewById(R.id.new_task_contractor_switch);
        mTaskNameEt = (TextInputEditText) findViewById(R.id.new_task_task_name_et);
        mTaskDescriptionEt = (TextInputEditText) findViewById(R.id.new_task_task_description_et);
        mTaskExtraItemsEt = (TextInputEditText) findViewById(R.id.new_task_needed_items_et);
        mSubmitButton = (Button) findViewById(R.id.new_task_submit_button);
        mSubmitButton.setOnClickListener(this);
        mContractorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mContractorSpinner.setVisibility(View.VISIBLE);
                } else {
                    mContractorSpinner.setVisibility(View.GONE);
                }
            }
        });
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.contractor_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mContractorSpinner.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();

        getGroup();
    }

    public void getGroup() {
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(User.class);
                if (user.getGroupKey() != null) {
                    getGroupMembers(user.getGroupKey());
                    mGroupKey = user.getGroupKey();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    public void getGroupMembers(final String groupKey) {
        DatabaseReference group = FirebaseDatabase.getInstance().getReference(Constants.GROUPS);
        group.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.child(groupKey).getValue(Group.class);
                mGroupMembers = group.getGroupMembers();
                setupMemberSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupMemberSpinner() {
//         Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mGroupMembers);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mGroupMemberSpinner.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        Intent result = new Intent();
        result.putExtra(Constants.TASK_NAME, mTaskNameEt.getText().toString());
        result.putExtra(Constants.TASK_DESCRIPTION, mTaskDescriptionEt.getText().toString());
        result.putExtra(Constants.TASK_ITEMS, mTaskExtraItemsEt.getText().toString());
        result.putExtra(Constants.ASSIGNED_MEMBER, mGroupMemberSpinner.getSelectedItem().toString());
        result.putExtra(Constants.CONTRACTOR_NEEDED, mContractorSwitch.isChecked());
        result.putExtra(Constants.GROUP_KEY,mGroupKey);
        setResult(RESULT_OK, result);
        finish();
    }
}
