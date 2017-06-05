package com.mad.maintenancemanager.useractivites;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.model.Group;
import com.mad.maintenancemanager.model.MaintenanceTask;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.threeten.bp.LocalDate;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

/**
 * Activity that provides the user the required fields to create a maintenance task
 * has validation to make sure the user inputs data correctly
 */

public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener {

    public static final String AN_ERROR_OCCURRED = "An error occurred: ";
    private Spinner mGroupMemberSpinner;
    private List<String> mGroupMembers;
    private Spinner mContractorSpinner;
    private Switch mContractorSwitch;
    @NotEmpty
    private TextInputEditText mTaskNameEt, mTaskDescriptionEt, mTaskExtraItemsEt;
    private TextInputEditText mTaskDueDateEt;
    private Button mSubmitButton;
    private PlaceAutocompleteFragment mAutocompleteFragment;
    private LinearLayout mContractorInfo;
    private Place mPlace = null;
    private int mDay, mMonth, mYear;
    private LocalDate mDueDate;
    private Validator mValidator;
    private boolean mValidDate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        //XML element binding
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

        //Activity Setup
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

        //Saripaar Validation
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
    }

    /**
     * Sets up memeber spinner
     */
    private void setupMemberSpinner() {
//         Create an ArrayAdapter using the group member array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mGroupMembers);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mGroupMemberSpinner.setAdapter(adapter);
        mGroupMemberSpinner.setVisibility(View.VISIBLE);
    }

    /**
     * Sets up type Spinner
     */
    private void setupTypeSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.contractor_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mContractorSpinner.setAdapter(adapter);
    }

    /**
     * Sets up Place Fragment
     */
    private void setupPlaceFragment() {
        mAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        mAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mPlace = place;
            }

            @Override
            public void onError(Status status) {
                Log.i(Constants.NEWTASK, AN_ERROR_OCCURRED + status);
            }
        });
    }

    @Override
    public void onClick(View v) {
        mValidator.validate();
    }

    /**
     * Constructs date from the data provided
     * @param selectedYear
     * @param selectedMonth
     * @param selectedDay
     * @return
     */
    private String constructDate(int selectedYear, int selectedMonth, int selectedDay) {
        return Date.valueOf(selectedYear + "-" + selectedMonth + "-" + selectedDay).toString();
    }

    /**
     * Shows date picker and tells user if they put a date in the past
     * @param view
     */
    public void showDatePicker(View view) {
        //To show current date in the DatePicker
        Calendar mCurrentDate = Calendar.getInstance();
        mYear = mCurrentDate.get(Calendar.YEAR);
        mMonth = mCurrentDate.get(Calendar.MONTH);
        mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(NewTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                String date = constructDate(selectedYear, selectedMonth + 1, selectedDay);
                mDueDate = LocalDate.parse(date);
                if (mDueDate.isBefore(LocalDate.now())) {
                    Toast.makeText(getApplicationContext(), R.string.future_date, Toast.LENGTH_LONG).show();
                    mTaskDueDateEt.setError(getString(R.string.exclamation));
                    mValidDate = false;
                } else {
                    mTaskDueDateEt.setError(null);
                    mValidDate = true;
                }
                mTaskDueDateEt.setText(mDueDate.toString());

                    /*      Your code   to get date and time    */
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle(getString(R.string.select_date));
        mDatePicker.show();
    }

    /**
     * passes the data back to create the task
     */
    @Override
    public void onValidationSucceeded() {
        if (mValidDate) {
            Intent result = new Intent();
            MaintenanceTask task = new MaintenanceTask(DatabaseHelper.getInstance().getDisplayName(),
                    mTaskNameEt.getText().toString(),
                    mTaskDescriptionEt.getText().toString(),
                    mContractorSwitch.isChecked(),
                    mGroupMemberSpinner.getSelectedItem().toString(),
                    mTaskExtraItemsEt.getText().toString(),
                    mDueDate.toEpochDay(),
                    mContractorSpinner.getSelectedItem().toString());
            Gson gson = new GsonBuilder().create();
            String stringTask = gson.toJson(task, MaintenanceTask.class);
            result.putExtra(Constants.TASKS, stringTask);
            if (mContractorSwitch.isChecked() && mPlace != null) {
                result.putExtra(Constants.PLACE, mPlace.getId());
                setResult(RESULT_OK, result);
                finish();
            } else if (mContractorSwitch.isChecked() && mPlace == null){
                Toast.makeText(getApplicationContext(), R.string.select_place_promt, Toast.LENGTH_LONG).show();
                return;
            }
            setResult(RESULT_OK, result);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.future_date, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view.getId() == R.id.new_task_task_date_et) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                ((TextInputEditText) view).setError(message);
            } else if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}

