package com.crowderia.mytoz.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.crowderia.mytoz.R;
import com.crowderia.mytoz.db.DBHelper;
import com.crowderia.mytoz.model.ProfileModel;
import com.crowderia.mytoz.util.CommonUtills;
import com.crowderia.mytoz.util.ValidationUtil;

import java.text.ParseException;
import java.util.Calendar;

public class ProfileScreen extends AppCompatActivity implements View.OnClickListener {

    EditText etName, etPhone, etEmail, etDob;
    Spinner sexSpinner;
    Button save;
    DBHelper db;
    String number;

    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    // Set listener on date view
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {


            showDate(arg1, arg2 + 1, arg3);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DBHelper(ProfileScreen.this);

        etName= (EditText)findViewById(R.id.etName);
        etPhone =(EditText)findViewById(R.id.etPhone);

        etEmail=(EditText)findViewById(R.id.etEmail);
        etDob = (EditText)findViewById(R.id.etDob);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);

        etName.setOnFocusChangeListener(onFocusChangeListener);
        etPhone.setOnFocusChangeListener(onFocusChangeListener);
        etEmail.setOnFocusChangeListener(onFocusChangeListener);
        etDob.setOnFocusChangeListener(onFocusChangeListener);
        //etSex =(EditText)findViewById(R.id.etSex);
        sexSpinner = (Spinner) findViewById(R.id.spinSex);
        ArrayAdapter<CharSequence> record_length_spinner_adapter = ArrayAdapter
                .createFromResource(ProfileScreen.this, R.array.gender, android.R.layout.simple_spinner_item);
        record_length_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setAdapter(record_length_spinner_adapter);

        ProfileModel profileBean = db.getProfileData(number);
        if (profileBean != null) {
            Log.e("ProfileScreen : ", profileBean.getName() + " "
                    + profileBean.getPhone() + " "
                    + profileBean.getEmail() + " "
                    + profileBean.getDob() + " "
                    + profileBean.getSex());
            etName.setText(profileBean.getName());
            etPhone.setText(profileBean.getPhone());
            etEmail.setText(profileBean.getEmail());
            etDob.setText(profileBean.getDob());
            sexSpinner.setSelection(getIndex(sexSpinner, profileBean.getSex()));

        } else {

        }

        // Hide the key board
        hideSoftKeyboard();

        // Focus enable when tough on the edit text
        setFocusEnableOnEditText();

        save = (Button)findViewById(R.id.btn_save_profile);
        save.setOnClickListener(ProfileScreen.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Set cursor to end of text in edittext when user clicks Next on Keyboard.
     */
    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                ((EditText) view).setSelection(((EditText) view).getText().length());
            }
        }
    };

    private void setFocusEnableOnEditText(){
        etName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etName.setFocusableInTouchMode(true);
                return false;
            }
        });
        etPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etPhone.setFocusableInTouchMode(true);
                return false;
            }
        });
        etEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etEmail.setFocusableInTouchMode(true);
                return false;
            }
        });

    }

    public void setDate(View view) {
        showDialog(999);
    }

    private void showDate(int year, int month, int day) {
        etDob.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }


    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save_profile:
                boolean isValidated = true;
                boolean isValidDate = false;
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                String email = etEmail.getText().toString();
                String dob = etDob.getText().toString();
                try {
                    isValidDate = ValidationUtil.isValidDate(dob);
                    Log.e("isValidDate", isValidDate+"");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String sex = sexSpinner.getSelectedItem().toString();

                if (name.trim().equalsIgnoreCase("")){
                    etName.setError(getResources().getString(R.string.profile_error_name));
                    CommonUtills.showToastMessage(ProfileScreen.this, getResources().getString(R.string.profile_error_name));
                    etName.requestFocus();
                    isValidated = false;
                } else if (phone.length() > 0 && !ValidationUtil.validatePhoneNumber(phone)){
                    etPhone.setError(getResources().getString(R.string.profile_error_phone));
                    CommonUtills.showToastMessage(ProfileScreen.this, getResources().getString(R.string.profile_error_phone));
                    etPhone.requestFocus();
                    isValidated = false;
                } else if (email.length() > 0 && !ValidationUtil.validEmail(email)){
                    etEmail.setError(getResources().getString(R.string.profile_error_email));
                    CommonUtills.showToastMessage(ProfileScreen.this, getResources().getString(R.string.profile_error_email));
                    etEmail.requestFocus();
                    isValidated = false;
                } else if (dob.equalsIgnoreCase("")){
                    etDob.setError(getResources().getString(R.string.profile_error_dob));
                    CommonUtills.showToastMessage(ProfileScreen.this, getResources().getString(R.string.profile_error_dob));
                    isValidated = false;
                } else if (!isValidDate) {
                    etDob.setError(getResources().getString(R.string.profile_error_dob_invalid));
                    CommonUtills.showToastMessage(ProfileScreen.this, getResources().getString(R.string.profile_error_dob_invalid));
                    isValidated = false;
                }

                if (isValidated) {
                    etName.setError(null);
                    etPhone.setError(null);
                    etEmail.setError(null);
                    etDob.setError(null);
                    Log.e("ProfileScreen : ", name.trim() + " " + phone + " " + email + " " + sex);

                    ProfileModel profile = new ProfileModel();

                    profile.setName(name.trim());
                    profile.setPhone(phone);
                    profile.setEmail(email);
                    profile.setDob(dob);
                    profile.setSex(sex);

                    if (db == null) {
                        db = new DBHelper(ProfileScreen.this);

                    }

                    int profileSuccess = db.insertProfileInfo(profile);
                    if ("1".equalsIgnoreCase(String.valueOf(profileSuccess))) {
                        CommonUtills.showAlertSuccessMessage(ProfileScreen.this, "Success", getResources().getString(R.string.profile_success_create));
                    }else if("2".equalsIgnoreCase(String.valueOf(profileSuccess))){
                        CommonUtills.showAlertSuccessMessage(ProfileScreen.this, "Success", getResources().getString(R.string.profile_success_update));
                    }
                    else {
                        CommonUtills.showAlertWarnMessage(ProfileScreen.this, "Failed", getResources().getString(R.string.profile_error_update));
                    }

                    hideSoftKeyboard();
                }

                break;

        }
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent  = new Intent(ProfileScreen.this, HomeScreen.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Don't hang around.
        finish();
    }

}
