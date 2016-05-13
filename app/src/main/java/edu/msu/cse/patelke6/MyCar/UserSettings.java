package edu.msu.cse.patelke6.MyCar;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;



public class UserSettings extends Activity {

    private String mAuthUserBSSID;
    private CarData mCarData;
    private EditText mMaxSpeedView;
    private CheckBox mSeatBeltEnforcedCheckBox;
    private TextView mSeatPositionView;
    private Button mEditBtn;
    private Button mSubmitBtn;
    private Button mDeactivateBtn;
    private ListView mDriversListView;
    private LinearLayout mChildSettingsLayout;

    private TextView mRadio1View;
    private TextView mRadio2View;
    private TextView mRadio3View;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        TabHost tabs= (TabHost) findViewById(R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec=tabs.newTabSpec("tag1");

		spec.setContent(R.id.tab1);
		spec.setIndicator("User Settings");
		tabs.addTab(spec);

		spec=tabs.newTabSpec("tag2");
		spec.setContent(R.id.tab2);
		spec.setIndicator("Manage Drivers");
		tabs.addTab(spec);

        Intent intent = getIntent();
        mAuthUserBSSID = intent.getStringExtra("bssid");
        String carDataFile = intent.getStringExtra("carDataFile");
        mCarData = new CarData(carDataFile,getApplicationContext());
        mCarData.setAuthenticatedUserID(mAuthUserBSSID);

        final Boolean isUserAuth = mCarData.isUserAdmin(mAuthUserBSSID);
        mDriversListView = (ListView) findViewById(R.id.driversListView);
        mMaxSpeedView = (EditText) findViewById(R.id.maxSpeedView);
        mMaxSpeedView.setEnabled(false);
        mSeatBeltEnforcedCheckBox = (CheckBox) findViewById(R.id.enforceSeatBeltCheckBox);
        mSeatBeltEnforcedCheckBox.setEnabled(false);
        mSeatPositionView = (TextView) findViewById(R.id.lowerSeatPositionView);
        mEditBtn = (Button) findViewById(R.id.editBtn);
        mSubmitBtn = (Button) findViewById(R.id.updateSettingsBtn);
        mSubmitBtn.setVisibility(View.INVISIBLE);
        mChildSettingsLayout = (LinearLayout) findViewById(R.id.childSettingsLayout);
        mChildSettingsLayout.setVisibility(View.INVISIBLE);
        mDeactivateBtn = (Button) findViewById(R.id.deactivateBtn);
        mRadio1View = (TextView) findViewById(R.id.radio1View);
        mRadio2View = (TextView) findViewById(R.id.radio2View);
        mRadio3View = (TextView) findViewById(R.id.radio3View);

        String radioInfo = mCarData.getRadioStations(mAuthUserBSSID);
        if (radioInfo == null || radioInfo.isEmpty() || radioInfo.equals("")){
            mRadio1View.setText("Radio1: Not Set");
            mRadio2View.setText("Radio2: Not Set");
            mRadio3View.setText("Radio3: Not Set");
        } else{
            String[] radioStations = radioInfo.split(",");
            mRadio1View.setText("Radio Station1: " + radioStations[0]);
            mRadio2View.setText("Radio Station2: "+ radioStations[1]);
            mRadio3View.setText("Radio Station3: " + radioStations[2]);
        }

        mDeactivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCarData.deactivate(mAuthUserBSSID);
                Toast.makeText(getApplicationContext(), "User Deactivated", Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserAuth) {
                    mMaxSpeedView.setEnabled(true);
                    mSeatBeltEnforcedCheckBox.setEnabled(true);
                }
                mSubmitBtn.setVisibility(View.VISIBLE);
            }
        });
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubmitBtn.setVisibility(View.INVISIBLE);
                mMaxSpeedView.setEnabled(false);
                mSeatBeltEnforcedCheckBox.setEnabled(false);
                updateSettings();
            }
        });



        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("tag1")) {
                    mChildSettingsLayout.setVisibility(View.INVISIBLE);
                }

            }
        });

        Boolean isSeatBeltedEnforced = mCarData.getEnforceSeatBelt(mAuthUserBSSID);
        int userMaxSpeed = mCarData.getMaxSpeed(mAuthUserBSSID);

        if(userMaxSpeed==1000) {
            mMaxSpeedView.setText("Not Set");
        } else
            mMaxSpeedView.setText("" + userMaxSpeed);
        mSeatBeltEnforcedCheckBox.setChecked(isSeatBeltedEnforced);
        mSeatPositionView.setText("Position: " + mCarData.getLowerSeatPosition(mAuthUserBSSID));

        if(isUserAuth) {
            ArrayList<User> enrolledUsers = getEnrolledDrivers();
            setUpListView(enrolledUsers);
        }else{
            tabs.getTabWidget().getChildAt(1).setVisibility(View.GONE);
        }

        Log.i("UserSettings", "Is User Admin " + isUserAuth + " isseatbeld " + isSeatBeltedEnforced + " maxspeed " + userMaxSpeed);

    }

    private void updateSettings(){
        mCarData.getCarDataXML();
        if(mMaxSpeedView.getText().toString().equals("Not Set")){
            mCarData.setMaxSpeed(mAuthUserBSSID, Integer.parseInt("1000"));
        } else {
            mCarData.setMaxSpeed(mAuthUserBSSID, Integer.parseInt(mMaxSpeedView.getText().toString()));
        }
        if(mSeatBeltEnforcedCheckBox.isChecked()){
            mCarData.setEnforceSeatBelt(mAuthUserBSSID, "1");
        } else
            mCarData.setEnforceSeatBelt(mAuthUserBSSID, "0");

        mCarData.setLowerSeatPosition(mAuthUserBSSID);
        mCarData.updateXMLFile();


        Boolean isSeatBeltedEnforced = mCarData.getEnforceSeatBelt(mAuthUserBSSID);
        int userMaxSpeed = mCarData.getMaxSpeed(mAuthUserBSSID);
        if(userMaxSpeed == 1000)
            mMaxSpeedView.setText("Not Set" );
        else
            mMaxSpeedView.setText("" + userMaxSpeed);
        mSeatBeltEnforcedCheckBox.setChecked(isSeatBeltedEnforced);
        mSeatPositionView.setText("Position: " + mCarData.getLowerSeatPosition(mAuthUserBSSID));
    }


    private  ArrayList<User> getEnrolledDrivers(){
        ArrayList<User> userArrayList = new ArrayList<>();
        ArrayList<String> enrolledUsers = mCarData.getEnrolledDrivers();
        for(String driverBSSID : enrolledUsers){
            if(driverBSSID.equals(mAuthUserBSSID) || driverBSSID.trim().isEmpty())
                continue;
            String firstName = mCarData.getUserFirstName(driverBSSID);
            String lastName = mCarData.getUserLastName(driverBSSID);
            Boolean isAdmin = mCarData.isUserAdmin(driverBSSID);
            User usr= new User(driverBSSID,firstName,lastName,isAdmin);
            userArrayList.add(usr);
        }
        return userArrayList;
    }

    private void setUpListView(final ArrayList<User> userArrayList){
        mDriversListView.setAdapter(new UserListAdapter(this, R.layout.row_users, userArrayList));
        mDriversListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User userSelected = (User) parent.getItemAtPosition(position);
                displayUserContent(userSelected);
            }
        });
    }

    private void displayUserContent(final User user){
        Boolean isSeatBeltedEnforced = mCarData.getEnforceSeatBelt(user.getmBSSID());
        int userMaxSpeed = mCarData.getMaxSpeed(user.getmBSSID());

        final EditText maxSpeedView = (EditText) findViewById(R.id.maxSpeedChildView);
        final CheckBox seatBeltEnforcedCheckbox = (CheckBox) findViewById(R.id.enforceChildSeatBeltCheckBox);
        final Button updateChildBtn = (Button) findViewById(R.id.updateChildSettingsBtn);
        if(user.getmAdminUser()) {
            updateChildBtn.setVisibility(View.GONE);
            seatBeltEnforcedCheckbox.setEnabled(false);
            maxSpeedView.setEnabled(false);
        } else {
            updateChildBtn.setVisibility(View.VISIBLE);
            seatBeltEnforcedCheckbox.setEnabled(true);
            maxSpeedView.setEnabled(true);
        }

        if(userMaxSpeed == 1000){
            maxSpeedView.setText("Not Set");
        } else {
            maxSpeedView.setText("" + userMaxSpeed);
        }
        seatBeltEnforcedCheckbox.setChecked(isSeatBeltedEnforced);
        mChildSettingsLayout.setVisibility(View.VISIBLE);

        updateChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCarData.getCarDataXML();

                if(maxSpeedView.getText().toString().equals("Not Set")){
                    mCarData.setMaxSpeed(user.getmBSSID(), Integer.parseInt("1000"));
                } else {
                    mCarData.setMaxSpeed(user.getmBSSID(), Integer.parseInt(maxSpeedView.getText().toString()));
                }

                if(seatBeltEnforcedCheckbox.isChecked()){
                    mCarData.setEnforceSeatBelt(user.getmBSSID(), "1");
                } else
                    mCarData.setEnforceSeatBelt(user.getmBSSID(), "0");

                mCarData.updateXMLFile();
                Toast.makeText(UserSettings.this, "Settings Updated", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private class UserListAdapter extends ArrayAdapter<User> {

        private List<User> mUsers;

        /**
         * @param context
         * @param textViewResourceId
         * @param objects
         */
        public UserListAdapter(Context context, int textViewResourceId,
                                   List<User> objects) {
            super(context, textViewResourceId, objects);
            mUsers = objects;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_users, null);
            }
            User user = mUsers.get(position);
            if (user != null) {
                TextView top = (TextView) v.findViewById(R.id.user_name);
                TextView bottom = (TextView) v.findViewById(R.id.user_details);
                if (top != null) {
                    top.setText(user.getmFirstName() + " " + user.getmLastname());
                }
                if (bottom != null) {
                    if(user.getmAdminUser())
                        bottom.setText("Admin");
                    else {
                        bottom.setVisibility(View.GONE);
                        bottom.setText("");
                    }
                }
            }

            return v;

        }
    }

    class User{
        private String mBSSID;
        private String mFirstName;
        private String mLastName;
        private Boolean mAdminUser;



        User(String bssid, String firstName, String lastName, Boolean adminUser){
            mBSSID = bssid;
            mFirstName = firstName;
            mLastName = lastName;
            mAdminUser = adminUser;
        }

        public Boolean getmAdminUser() {
            return mAdminUser;
        }

        public String getmBSSID() {
            return mBSSID;
        }


        public String getmFirstName() {
            return mFirstName;
        }

        public String getmLastname() {
            return mLastName;
        }

    }


}
