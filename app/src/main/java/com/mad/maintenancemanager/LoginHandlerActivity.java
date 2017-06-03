package com.mad.maintenancemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.tradeactivities.MapActivity;

public class LoginHandlerActivity extends AppCompatActivity {

    private String mTradeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_handler);


        boolean test = getIntent().getBooleanExtra(Constants.CONTRACTOR_NEEDED, false);


        DatabaseHelper.getInstance().initialSetupCheck(new IOnUserTypeListener() {
            @Override
            public void onUserType(boolean isTradie) {
                Intent intent;
                if (!isTradie) {
                    intent = new Intent(LoginHandlerActivity.this, SignedInUserActivity.class);
                    startActivity(intent);
                    Toast.makeText(LoginHandlerActivity.this,"not trade",Toast.LENGTH_LONG).show();
                } else {
                    buildDialog();
                }
            }

            @Override
            public void onTradeSelected() {
                Intent intent = new Intent(LoginHandlerActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        },test);

    }

    private void buildDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginHandlerActivity.this);
        View spnrView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        builder.setTitle("Choose your trade");
        final Spinner spinner = (Spinner) spnrView.findViewById(R.id.login_select_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.contractor_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTradeType = spinner.getSelectedItem().toString();
                DatabaseHelper.getInstance().setTradeType(mTradeType);
                Intent intent = new Intent(LoginHandlerActivity.this, MapActivity.class);
                startActivity(intent);
                finish();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setView(spnrView);
        builder.show();
    }

    /**
     * Creates an intent that includes the extra data from login in the intent
     *
     * @param context     Application Context
     * @param idpResponse Response from login services
     * @return The intent that includes the IDP data
     */

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = IdpResponse.getIntent(idpResponse);
        in.setClass(context, SignedInUserActivity.class);
        return in;
    }

    public interface IOnUserTypeListener {
        void onUserType(boolean isTradie);
        void onTradeSelected();
    }
}
