package com.mad.maintenancemanager.useractivites;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.model.Group;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner mGroupMemberSpinner;
    private List<String> mGroupMembers;
    private Spinner mContractorSpinner;
    private Switch mContractorSwitch;
    private TextInputEditText mTaskNameEt, mTaskDescriptionEt, mTaskExtraItemsEt, mTaskDueDateEt;
    private Button mSubmitButton;
    private PlaceAutocompleteFragment mAutocompleteFragment;
    private LinearLayout mContractorInfo;
    private Place mPlace;
    private int mDay, mMonth, mYear;
    private Date mDueDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        mGroupMemberSpinner = (Spinner) findViewById(R.id.new_task_assign_to_spnr);
        mContractorSpinner = (Spinner) findViewById(R.id.new_task_contractor_type_spnr);
        mContractorSwitch = (Switch) findViewById(R.id.new_task_contractor_switch);
        mTaskNameEt = (TextInputEditText) findViewById(R.id.new_task_task_name_et);
        mTaskDescriptionEt = (TextInputEditText) findViewById(R.id.new_task_task_description_et);
        mContractorInfo = (LinearLayout) findViewById(R.id.new_task_contractor_info);
        mTaskExtraItemsEt = (TextInputEditText) findViewById(R.id.new_task_needed_items_et);
        mTaskDueDateEt = (TextInputEditText) findViewById(R.id.new_task_task_date_et);


        mSubmitButton = (Button) findViewById(R.id.new_task_submit_button);
        mSubmitButton.setOnClickListener(this);
        getSupportActionBar().setTitle(getString(R.string.create_a_new_task));
        setupPlaceFragment();
        setupTypeSpinner();
        mContractorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mContractorInfo.setVisibility(View.VISIBLE);
                } else {
                    mContractorInfo.setVisibility(View.GONE);
                }
            }
        });

        DatabaseHelper.getInstance().getGroup(new DatabaseHelper.IGroupListener() {
            @Override
            public void onGroup(Group group) {
                mGroupMembers = group.getGroupMembers();
                findViewById(R.id.new_task_progress_bar).setVisibility(View.GONE);
                setupMemberSpinner();
            }
        });
    }

    private void setupMemberSpinner() {
//         Create an ArrayAdapter using the group member array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mGroupMembers);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mGroupMemberSpinner.setAdapter(adapter);
        mGroupMemberSpinner.setVisibility(View.VISIBLE);
    }

    private void setupTypeSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.contractor_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mContractorSpinner.setAdapter(adapter);
    }

    //// TODO: 26/5/17 maybe make activity for result
    private void setupPlaceFragment() {
        mAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        mAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mPlace = place;
                Log.i("newtask", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("newtask", "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent result = new Intent();
        result.putExtra(Constants.TASK_NAME, mTaskNameEt.getText().toString());
        result.putExtra(Constants.TASK_DESCRIPTION, mTaskDescriptionEt.getText().toString());
        result.putExtra(Constants.TASK_ITEMS, mTaskExtraItemsEt.getText().toString());
        result.putExtra(Constants.ASSIGNED_MEMBER, mGroupMemberSpinner.getSelectedItem().toString());
        result.putExtra(Constants.CONTRACTOR_NEEDED, mContractorSwitch.isChecked());
        result.putExtra(Constants.DUE_DATE,mDueDate);
        if (mContractorSwitch.isChecked()) {
            Gson gson = new GsonBuilder().create();
            String stringPlace = gson.toJson(mPlace, String.class);
            result.putExtra(Constants.TASK_LOCATION, stringPlace);
            result.putExtra(Constants.CONTRACTOR_TYPE, mContractorSpinner.getSelectedItem().toString());
        }
        setResult(RESULT_OK, result);
        finish();
    }

    private String contructDate(int selectedYear, int selectedMonth, int selectedDay) {
        return selectedYear + "-" + selectedMonth + "-" + selectedDay;
    }

    public void showDatePicker(View view) {
        //To show current date in the datepicker
        Calendar mCurrentDate = Calendar.getInstance();
        mYear = mCurrentDate.get(Calendar.YEAR);
        mMonth = mCurrentDate.get(Calendar.MONTH);
        mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(NewTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                // TODO Auto-generated method stub
                String date = contructDate(selectedYear, selectedMonth, selectedDay);
                mDueDate = Date.valueOf(date);
                mTaskDueDateEt.setText(date);
                    /*      Your code   to get date and time    */
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select date");
        mDatePicker.show();
    }
}

