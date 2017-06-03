package com.mad.maintenancemanager.useractivites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.ResultCodes;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class NewGroupActivity extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty
    private EditText mGroupName;
    @Password(min = 4)
    private EditText mGroupPin;
    private Button mCreateBtn;
    private Button mCancelBtn;
    private Validator mValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);


        mGroupName = (EditText) findViewById(R.id.add_group_name_et);
        mGroupPin = (EditText) findViewById(R.id.add_group_pin_et);
        mCancelBtn = (Button) findViewById(R.id.new_group_cancel_button);
        mCreateBtn = (Button) findViewById(R.id.new_group_create_button);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValidator.validate();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(ResultCodes.CANCELED);
                finish();
            }
        });


    }

    @Override
    public void onValidationSucceeded() {
        Intent result = new Intent();
        result.putExtra(Constants.GROUP_NAME, mGroupName.getText().toString());
        result.putExtra(Constants.GROUP_PIN, mGroupPin.getText().toString());
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            }
        }
    }
}
