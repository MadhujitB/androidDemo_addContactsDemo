package demo.dada.com.addcontactsdemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayAdapter<String> spinnerAdapter;
    private Spinner spinner_state;
    private EditText editText_name, editText_phone, editText_email, editText_city;
    private String name, email, city, state;
    private long phone;
    private TextInputLayout contains_name, contains_phone, contains_email, contains_city;
    private String password;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Initialising the buttons
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);

        //Initialising the EditTexts
        editText_name = findViewById(R.id.editText_name);
        editText_phone = findViewById(R.id.editText_phone);
        editText_email = findViewById(R.id.editText_email);
        editText_city = findViewById(R.id.editText_city);

        //Initialising the TextInputLayouts
        contains_name = findViewById(R.id.contains_name);
        contains_phone = findViewById(R.id.contains_phone);
        contains_email = findViewById(R.id.contains_email);
        contains_city = findViewById(R.id.contains_city);

        spinner_state = findViewById(R.id.spinner_state);

        databaseHelper = new DatabaseHelper(this);

        Resources rs = this.getResources();
        String[] spinnerArray = rs.getStringArray(R.array.states); //Setting up array

        //Setting up spinner adapter
        spinnerAdapter = new ArrayAdapter<>(this, R.layout.custom_layout_simple_spinner_item, spinnerArray);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_layout_simple_spinner_dropdown);
        spinner_state.setAdapter(spinnerAdapter); //Setting adapter in the spinner

        state = "";

        //Selecting an item from spinner dropdown list
        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = spinner_state.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_register:

                name = editText_name.getText().toString().trim();
                String ph = editText_phone.getText().toString().trim();
                email = editText_email.getText().toString().trim();
                city = editText_city.getText().toString().trim();

                //Checking the whether all the fields are filled or not
                if(!name.isEmpty() && !ph.isEmpty() && !email.isEmpty() && !city.isEmpty()) {

                    if(state.equals("Select states") || state.equals(""))
                        Toast.makeText(this, "Please select a state", Toast.LENGTH_SHORT).show();

                     else{
                        //Checking the validity of the phone number in terms of digits
                        if(ph.length() == 10) {
                            phone = Long.parseLong(ph);

                            ArrayList aL = databaseHelper.readData_Pssd(phone);
                            int count = aL.size();


                            if (count == 0)//Verifying whether the phone number already exits or not
                                if (validateEmail(email)) //Verifying the email pattern
                                    showPopUp_SetPassword();
                                else
                                    contains_email.setError("Please enter a valid email id");
                            else if (count >= 1)
                                contains_phone.setError("Phone number already exits");
                        }
                        else
                            contains_phone.setError("Please enter a valid phone number");

                    }

                }
                else if(name.isEmpty() && ph.isEmpty() && email.isEmpty() && city.isEmpty())
                    Toast.makeText(this, "Please fill up all the fields", Toast.LENGTH_SHORT).show();

                else if(name.isEmpty())
                    contains_name.setError("Please enter name");

                else if(ph.isEmpty())
                    contains_phone.setError("Please enter phone number");

                else if(email.isEmpty())
                    contains_email.setError("Please enter email id");

                else if(city.isEmpty())
                    contains_city.setError("Please enter city");


                break;

            case R.id.btn_clear:
                //Clearing the Edit Text fields

                editText_name.setText("");
                editText_name.clearFocus();

                editText_phone.setText("");
                editText_phone.clearFocus();

                editText_email.setText("");
                editText_email.clearFocus();

                editText_city.setText("");
                editText_city.clearFocus();

                spinner_state.setSelection(0);
                break;
        }
    }

    private void showPopUp_SetPassword()
    {
        //AlertDialog pop-up for setting up new password.

        try {
            //Using layout inflater to create a view object of the layout

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.customised_password_layout, null, false);

            final TextInputLayout til_contains_newPassd = view.findViewById(R.id.til_contains_newPassd);
            final TextInputLayout til_contains_cnfPassd = view.findViewById(R.id.til_contains_cnfPassd);

            final EditText ed_new_pssd = view.findViewById(R.id.ed_new_pssd);
            final EditText ed_cnf_pssd = view.findViewById(R.id.ed_cnf_pssd);

            Button btn_set_submit = view.findViewById(R.id.btn_set_submit);
            Button btn_set_cancel = view.findViewById(R.id.btn_set_cancel);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);//Alert Dialog cannot be dismissed by clicking outside of it. It can only be dismissed by pressing the Cancel button.

            btn_set_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String newPsswd = ed_new_pssd.getText().toString().trim();
                    String cnfPsswd = ed_cnf_pssd.getText().toString().trim();

                    if(!newPsswd.isEmpty() && !cnfPsswd.isEmpty())
                        if(newPsswd.equals(cnfPsswd)) {

                            password = newPsswd;
                            showPopUp_ConfirmScreen();
                            dialog.dismiss();
                        }
                        else
                            Toast.makeText(RegistrationActivity.this, "Passwords mismatch.", Toast.LENGTH_SHORT).show();

                    else if(newPsswd.isEmpty())
                        til_contains_newPassd.setError("Please enter new password");

                    else if (cnfPsswd.isEmpty())
                        til_contains_cnfPassd.setError("Please enter new password");

                    else
                        Toast.makeText(RegistrationActivity.this, "Please fill both the fields", Toast.LENGTH_SHORT).show();
                }
            });

            btn_set_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showPopUp_ConfirmScreen()
    {
        try {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.customised_confirmation_layout, null, false);

            Button btn_close = view.findViewById(R.id.btn_close);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);

            long rowId = databaseHelper.insertData_Registration(name, phone, email, city, state, password);

            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent gotoLoginActivity = new Intent(RegistrationActivity.this, LoginActivity.class);
                    gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(gotoLoginActivity);
                    dialog.dismiss();
                }
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean validateEmail(String email)
    {
        Pattern pattern = Pattern.compile(ConstantClass.EMAIL_PATTERN);

        Matcher matcher = pattern.matcher(email);

        boolean val = matcher.matches();

        return val;
    }
}
