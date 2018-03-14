package com.crowderia.mytoz.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.crowderia.mytoz.R;
import com.crowderia.mytoz.activity.HomeScreen;
import com.crowderia.mytoz.activity.ProfileScreen;
import com.crowderia.mytoz.db.DBHelper;
import com.crowderia.mytoz.model.ProfileModel;
import com.crowderia.mytoz.util.CommonUtills;
import com.crowderia.mytoz.util.ValidationUtil;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                //onBackPressed();
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

    public void setDatePicker(){Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

// Create the DatePickerDialog instance
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                R.style.AppTheme, myDateListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.setTitle("Select the date");
        datePicker.show();}

    public void setDate(View view) {
        getActivity().showDialog(999);
    }

    private void showDate(int year, int month, int day) {
        etDob.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(getActivity(), myDateListener, year, month, day);
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
                    CommonUtills.showToastMessage(getActivity(), getResources().getString(R.string.profile_error_name));
                    etName.requestFocus();
                    isValidated = false;
                } else if (phone.length() > 0 && !ValidationUtil.validatePhoneNumber(phone)){
                    etPhone.setError(getResources().getString(R.string.profile_error_phone));
                    CommonUtills.showToastMessage(getActivity(), getResources().getString(R.string.profile_error_phone));
                    etPhone.requestFocus();
                    isValidated = false;
                } else if (email.length() > 0 && !ValidationUtil.validEmail(email)){
                    etEmail.setError(getResources().getString(R.string.profile_error_email));
                    CommonUtills.showToastMessage(getActivity(), getResources().getString(R.string.profile_error_email));
                    etEmail.requestFocus();
                    isValidated = false;
                } else if (dob.equalsIgnoreCase("")){
                    etDob.setError(getResources().getString(R.string.profile_error_dob));
                    CommonUtills.showToastMessage(getActivity(), getResources().getString(R.string.profile_error_dob));
                    isValidated = false;
                } else if (!isValidDate) {
                    etDob.setError(getResources().getString(R.string.profile_error_dob_invalid));
                    CommonUtills.showToastMessage(getActivity(), getResources().getString(R.string.profile_error_dob_invalid));
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
                        db = new DBHelper(getActivity());

                    }

                    int profileSuccess = db.insertProfileInfo(profile);
                    if ("1".equalsIgnoreCase(String.valueOf(profileSuccess))) {
                        CommonUtills.showAlertSuccessMessage(getActivity(), "Success", getResources().getString(R.string.profile_success_create));
                    }else if("2".equalsIgnoreCase(String.valueOf(profileSuccess))){
                        CommonUtills.showAlertSuccessMessage(getActivity(), "Success", getResources().getString(R.string.profile_success_update));
                    }
                    else {
                        CommonUtills.showAlertWarnMessage(getActivity(), "Failed", getResources().getString(R.string.profile_error_update));
                    }

                    //hideSoftKeyboard();
                }

                break;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        db = new DBHelper(getActivity());

        etName= (EditText)view.findViewById(R.id.etName);
        etPhone =(EditText)view.findViewById(R.id.etPhone);

        etEmail=(EditText)view.findViewById(R.id.etEmail);
        etDob = (EditText)view.findViewById(R.id.etDob);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        //showDate(year, month + 1, day);

        etName.setOnFocusChangeListener(onFocusChangeListener);
        etPhone.setOnFocusChangeListener(onFocusChangeListener);
        etEmail.setOnFocusChangeListener(onFocusChangeListener);
        etDob.setOnFocusChangeListener(onFocusChangeListener);
        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDatePicker();
            }
        });
        //etSex =(EditText)findViewById(R.id.etSex);
        sexSpinner = (Spinner) view.findViewById(R.id.spinSex);
        ArrayAdapter<CharSequence> record_length_spinner_adapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.gender, android.R.layout.simple_spinner_item);
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

        save = (Button)view.findViewById(R.id.btn_save_profile);
        save.setOnClickListener(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getActivity().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

}
