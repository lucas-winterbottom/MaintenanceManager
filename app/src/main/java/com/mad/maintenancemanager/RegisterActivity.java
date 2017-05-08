package com.mad.maintenancemanager;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    private AutoCompleteTextView mEmail;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = (AutoCompleteTextView) findViewById(R.id.register_email);
        mPassword = (EditText) findViewById(R.id.register_password);

        Button cancelButton = (Button) findViewById(R.id.register_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        Button addBtn = (Button) findViewById(R.id.register_register_button);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(Constants.EMAIL, mEmail.getText().toString());
                result.putExtra(Constants.PASSWORD, mPassword.getText().toString());
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });


    }
}
