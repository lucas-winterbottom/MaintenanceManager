package com.mad.maintenancemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.model.User;
import com.mad.maintenancemanager.tradeactivities.MapActivity;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

/**
 * Activity that handles login requestes, makes sure user has required data server side
 * while doing so displays a progressbar
 */
public class LoginHandlerActivity extends AppCompatActivity {

    private String mTradeType;
    private LinearLayout mChoices;
    private ProgressBar mProgressBar;
    private EditText mEdt;
    private boolean mStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_handler);
        mChoices = (LinearLayout) findViewById(R.id.post_login_choice);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_post_login);

        final Button emailSignInButton = (Button) findViewById(R.id.normal_sign_in_button);
        emailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildGeneralDialog();
            }
        });
        final Button tradeLogin = (Button) findViewById(R.id.tradie_sign_in_button);
        tradeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildTradeDialog();
            }
        });


        DatabaseHelper.getInstance().initialSetupCheck(new IOnUserTypeListener() {
            @Override
            public void onUserType(User user) {
                final Intent intent;
                if (user == null) {
                    mChoices.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                } else if (user.isContractor()) {
                    intent = new Intent(LoginHandlerActivity.this, MapActivity.class);
                    DatabaseHelper.getInstance().setTrade(new DatabaseHelper.IOnTradeListener() {
                        @Override
                        public void onTrade() {
                            startActivity(intent);
                        }
                    });

                } else if (!user.isContractor()) {
                    intent = new Intent(LoginHandlerActivity.this, SignedInUserActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    /**
     *  Builds a dialog so trade users can select their trade
     */
    private void buildTradeDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginHandlerActivity.this);
        View spnrView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        builder.setTitle(R.string.choose_trade);
        final Spinner spinner = (Spinner) spnrView.findViewById(R.id.login_select_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.contractor_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        builder.setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTradeType = spinner.getSelectedItem().toString();
                DatabaseHelper.getInstance().setTradeUser(mTradeType);
                Intent intent = new Intent(LoginHandlerActivity.this, MapActivity.class);
                startActivity(intent);
                finish();

            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(LoginHandlerActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setView(spnrView);
        builder.show();
    }

    /**
     * Builds a dialog so that general users can input their mobile no
     */
    public void buildGeneralDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginHandlerActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        builder.setTitle(R.string.mobile_no_promt);
        mEdt = (EditText) dialogView.findViewById(R.id.mobile_no);
        mEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus == false) {
                    if (mEdt.getText().length() < 10) {
                        Toast.makeText(getApplicationContext(), getString(R.string.valid_mobile_prompt), Toast.LENGTH_LONG).show();
                    } else {
                        mStatus = true;
                    }
                }
            }
        });

        builder.setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mEdt.getText().length() == 10) {
                    DatabaseHelper.getInstance().setGeneralUser(mEdt.getText().toString());
                    Intent intent = new Intent(LoginHandlerActivity.this, SignedInUserActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), getString(R.string.valid_mobile_prompt), Toast.LENGTH_LONG).show();
                    mEdt.setText("");
                }


            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(LoginHandlerActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setView(dialogView);
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
        void onUserType(User user);
    }
}
