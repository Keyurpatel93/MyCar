package edu.msu.cse.patelke6.MyCar;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class UserEnollment extends Activity {
    private String mBSSID;
    private CarData mCarData = null;
    private EditText mKeyTextView;
    private EditText mAdminTextView;
    private CheckBox mAdminCheckBoxView;
    private EditText mFirstNameView;
    private EditText mLastNameView;

    private Button mSubmitBtn;
    String rootSDPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_enollment);


        Bundle intent = getIntent().getExtras();
        mBSSID = intent.getString("bssid");
        Log.i("UserEnrollment", "BSSID value passed in " + mBSSID);

        rootSDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mCarData = new CarData(rootSDPath+"/cardata.xml", getApplicationContext());

        mKeyTextView = (EditText) findViewById(R.id.carKeyInputText);
        mAdminTextView = (EditText) findViewById(R.id.carAdminKeyInputText);
        mAdminCheckBoxView = (CheckBox) findViewById(R.id.adminCheckBox);
        mAdminTextView.setVisibility(View.GONE);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        mFirstNameView = (EditText) findViewById(R.id.firstNameView);
        mLastNameView = (EditText) findViewById(R.id.lastNameView);

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCredentials();
            }
        });

        mAdminCheckBoxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                if(checkBox.isChecked()){
                    mAdminTextView.setVisibility(View.VISIBLE);
                }else
                    mAdminTextView.setVisibility(View.GONE);
            }
        });
    }


    //In a non-prototype application key and admin key should not be stored in a secure location
    //default password is "key" and default admin password is "admin"
    public void verifyCredentials(){
        boolean carKeyVerified = mCarData.verifyCarKey(mKeyTextView.getText().toString());
        boolean adminCarKeyVerified = mCarData.verifyAdminCarKey(mAdminTextView.getText().toString());
        if(mAdminCheckBoxView.isChecked()){
            if(carKeyVerified && adminCarKeyVerified){
                enrollUser(true);
            } else {
                YoYo.with(Techniques.Shake)
                        .duration(700)
                        .playOn(findViewById(R.id.carKeyInputText));
                YoYo.with(Techniques.Shake)
                        .duration(700)
                        .playOn(findViewById(R.id.carAdminKeyInputText));
                mKeyTextView.setText("");
                mAdminTextView.setText("");
            }
        } else {
            if(carKeyVerified){
                enrollUser(false);
            } else {
                YoYo.with(Techniques.Shake)
                        .duration(700)
                        .playOn(findViewById(R.id.carKeyInputText));
                mKeyTextView.setText("");
            }
        }
    }


    private void enrollUser(Boolean isAdmin){
        if(mFirstNameView.getText().toString().trim().equals("") ||  mLastNameView.getText().toString().trim().equals("")){
            Toast.makeText(this, "Please Fill in User Info", Toast.LENGTH_LONG).show();

        } else {
            //get empty node, set name and bssid values;
            mCarData.enrollUser(mFirstNameView.getText().toString(), mLastNameView.getText().toString(), mBSSID, isAdmin);
            Intent intent =  new Intent(this, UserSettings.class);
            intent.putExtra("bssid",mBSSID);
            intent.putExtra("carDataFile",rootSDPath+"/cardata.xml");
            startActivity(intent);
            //Todo when pressing back button go back main activity
        }
    }

}
