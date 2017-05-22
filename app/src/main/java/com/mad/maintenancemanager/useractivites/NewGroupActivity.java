package com.mad.maintenancemanager.useractivites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.auth.ResultCodes;
import com.mad.maintenancemanager.Constants;
import com.mad.maintenancemanager.R;

public class NewGroupActivity extends AppCompatActivity {


    private EditText mGroupName;
    private EditText mGroupPin;
    private Button mCreateBtn;
    private Button mCancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);


        mGroupName = (EditText)findViewById(R.id.add_group_name_et);
        mGroupPin = (EditText)findViewById(R.id.add_group_pin_et);
        mCancelBtn = (Button) findViewById(R.id.new_group_cancel_button);
        mCreateBtn = (Button) findViewById(R.id.new_group_create_button);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(Constants.GROUP_NAME, mGroupName.getText().toString());
                result.putExtra(Constants.GROUP_PIN, mGroupPin.getText().toString());
                setResult(RESULT_OK, result);
                finish();
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
}
