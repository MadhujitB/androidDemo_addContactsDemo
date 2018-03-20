package demo.dada.com.addcontactsdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ManageSession manageSession;
    private DatabaseHelper databaseHelper;
    private EditText phoneNumber, password;
    private String passdRcvd;
    private long phone;
    private TextInputLayout containsUsername, containsPassword;
    private LinearLayout rootLinearLayout;
    private int psswdCount;
    private Snackbar ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rootLinearLayout = findViewById(R.id.rootLinearLayout);
        phoneNumber = findViewById(R.id.username);
        password = findViewById(R.id.password);

        //Intialising the sign-in and sign-up buttons
        findViewById(R.id.btn_sign_up).setOnClickListener(this);
        findViewById(R.id.btn_sign_in).setOnClickListener(this);

        containsUsername = findViewById(R.id.containsUsername);
        containsPassword = findViewById(R.id.containsPassword);

        manageSession = new ManageSession(this);
        databaseHelper = new DatabaseHelper(this);

        ss = Snackbar.make(rootLinearLayout, "Reset Your Password...", Snackbar.LENGTH_INDEFINITE);

        psswdCount = 0;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_sign_in:

                containsUsername.setError("");
                containsPassword.setError("");


                String phnum = phoneNumber.getText().toString().trim();
                passdRcvd = password.getText().toString().trim();

                if(!phnum.isEmpty() && !passdRcvd.isEmpty())
                {
                    psswdCount++; //Checking the password count for displaying RESET password via Snackbar
                    phone = Long.parseLong(phnum);
                    checkAvailability();
                }

                else if(phnum.isEmpty() && passdRcvd.isEmpty())
                    Toast.makeText(this, "Please fill up both the fields", Toast.LENGTH_SHORT).show();

                else if(phnum.isEmpty())
                    containsUsername.setError("Please enter the phone number");

                else if(passdRcvd.isEmpty())
                    containsPassword.setError("Please enter the password");

                break;

            case R.id.btn_sign_up:
                Intent gotoRegistrationActivity = new Intent(this, RegistrationActivity.class);
                startActivity(gotoRegistrationActivity);
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(manageSession.isLogIn()) {
            Intent gotoMainActivity = new Intent(this, MainActivity.class);
            gotoMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            gotoMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            gotoMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(gotoMainActivity);
        }

    }



    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void checkAvailability()
    {
        ArrayList arrayList = new ArrayList<>();
        arrayList.clear();
        arrayList = databaseHelper.readData_Pssd(phone);
        int count = arrayList.size();

        if(count >= 1)
        {
            String phn = arrayList.get(1).toString(); //Getting user phone number
            String pswd = arrayList.get(2).toString(); //Getting password
            String name = arrayList.get(3).toString(); //Getting username
            long ph = Long.parseLong(phn);

            int loginCount = 0;

            if(phone == ph && pswd.equals(passdRcvd))
            {
                loginCount = databaseHelper.getLoginCount_Registration(ph);
                loginCount = loginCount + 1;

                //A login count is maintained for the display of instruction screen ViewContactsActivity
                int val = databaseHelper.setLoginCount_Registration(ph, loginCount);

                if(val >= 1)
                {
                    Toast.makeText(this, "Welcome " + name + "!!!", Toast.LENGTH_SHORT).show();

                    //Storing the name, password and password in the ManageSession for further use and maintaining the session
                    manageSession.createLoginSession(name, phn, pswd);

                    onResume();
                }


            }

            else
            {
                if (psswdCount != 2 ) {
                    containsPassword.setError("Wrong password");

                }
                else if (psswdCount == 2) {


                    psswdCount = 0;

                    showSnackbar();
                }


            }
        }
        else if(count == 0)
            containsUsername.setError("Sorry! No record found of this number.");


    }

    private void showPopUp_SetPassword(final long mobNum)
    {
        //AlertDialog pop-up for setting reset password.

        try {
            //Using layout inflater to create a view object of the layout
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.customised_password_layout, null, false);

            //Initialising TextInputLayouts
            final TextInputLayout til_contains_newPassd = view.findViewById(R.id.til_contains_newPassd);
            final TextInputLayout til_contains_cnfPassd = view.findViewById(R.id.til_contains_cnfPassd);

            //Initialising EditTexts
            final EditText ed_new_pssd = view.findViewById(R.id.ed_new_pssd);
            final EditText ed_cnf_pssd = view.findViewById(R.id.ed_cnf_pssd);

            //Initialising Buttons
            Button btn_set_submit = view.findViewById(R.id.btn_set_submit);
            Button btn_set_cancel = view.findViewById(R.id.btn_set_cancel);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view); //Setting up the view in the Alert Dialog
            final AlertDialog dialog = builder.create();
            dialog.show(); //Displaying the Alert Dialog
            dialog.setCanceledOnTouchOutside(false); //Alert Dialog cannot be dismissed by clicking outside of it. It can only be dismissed by pressing the Cancel button

            btn_set_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String newPsswd = ed_new_pssd.getText().toString().trim();
                    String cnfPsswd = ed_cnf_pssd.getText().toString().trim();

                    if(!newPsswd.isEmpty() && !cnfPsswd.isEmpty())
                        if(newPsswd.equals(cnfPsswd)) {

                            //Re-setting the password into the SQLite Database
                            int cnt = databaseHelper.setPassword_Registration(mobNum,cnfPsswd);

                            //Checks whether the resetting is done or not
                            if(cnt > 0) {
                                Toast.makeText(LoginActivity.this, "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                        else
                            Toast.makeText(LoginActivity.this, "Passwords mismatch.", Toast.LENGTH_SHORT).show();

                    else if(newPsswd.isEmpty())
                        til_contains_newPassd.setError("Please enter new password");

                    else if (cnfPsswd.isEmpty())
                        til_contains_cnfPassd.setError("Please enter new password");

                    else
                        Toast.makeText(LoginActivity.this, "Please fill both the fields", Toast.LENGTH_SHORT).show();
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

    private void showSnackbar()
    {

        final long mobNum = Long.parseLong(phoneNumber.getText().toString().trim());

        //Setting the action for Sanckbar
        ss.setAction("Reset", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp_SetPassword(mobNum);
            }
        });
        ss.setActionTextColor(ContextCompat.getColor(this,R.color.colorCustom_yellow_A200)); //Changing the the color of action button in the Snackbar
        ss.show(); //Displaying the Snackbar
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        databaseHelper.close(); //Closing database helper object
    }
}
