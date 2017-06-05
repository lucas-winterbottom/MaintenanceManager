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

/**
 * Activity to join existing group
 */
public class JoinExisting extends AppCompatActivity {

    private Button mCancelBtn;
    private Button mJoinBtn;
    private EditText mGroupKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_existing);
        getSupportActionBar().setTitle(R.string.join_e_group);

        mCancelBtn = (Button) findViewById(R.id.existing_group_cancel_btn);
        mJoinBtn = (Button) findViewById(R.id.existing_group_join_btn);
        mGroupKey = (EditText) findViewById(R.id.existing_group_key_et);

        mJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(Constants.GROUP_KEY, mGroupKey.getText().toString());
                setResult(ResultCodes.OK, result);
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

    @Override
    public void onBackPressed() {
        setResult(ResultCodes.CANCELED);
        super.onBackPressed();
    }
}
