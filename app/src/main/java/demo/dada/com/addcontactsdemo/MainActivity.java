package demo.dada.com.addcontactsdemo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ManageSession manageSession;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imgView_menuIcon;
    private TextView tv_displayName, tv_displayPhone, tv_displayEmail, tv_displayCity, tv_displayAppInfo;
    private DatabaseHelper databaseHelper;
    private String gender_str = "";
    private long phn = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manageSession = new ManageSession(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        imgView_menuIcon = findViewById(R.id.imgView_hmbgIcon);
        imgView_menuIcon.setOnClickListener(this);

        //Initialising the TextViews
        tv_displayName = findViewById(R.id.tv_displayName);
        tv_displayPhone = findViewById(R.id.tv_displayPhone);
        tv_displayEmail = findViewById(R.id.tv_displayEmail);
        tv_displayCity = findViewById(R.id.tv_displayCity);
        tv_displayAppInfo = findViewById(R.id.tv_displayAppInfo);

        //Setting up click events and visibility for Buttons, ImageView and TextView
        findViewById(R.id.btn_showPopUp).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
        findViewById(R.id.btn_showContacts).setOnClickListener(this);
        tv_displayAppInfo.setOnClickListener(this);
        findViewById(R.id.imgView_infoIcon).setVisibility(View.INVISIBLE); //Making Info Icon invisible from toolbar in the MainActivity
        imgView_menuIcon.setVisibility(View.VISIBLE); //Making Menu Icon visible which will be used for opening of Navigation Drawer
        findViewById(R.id.imgView_backIcon).setVisibility(View.INVISIBLE); //Making back Back Icon (or Back Button) invisible because in place of it, Menu Icon will be visible

        //Initialising the object of DatabaseHelper class
        databaseHelper = new DatabaseHelper(this);



        setDataInTheMenu();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.btn_showPopUp: //This button calls a method which displays an alert dialog consists of necessary fields to add a contact
                showPopUp();
                break;

            case R.id.btn_logout: //This button calls a method which performs log out operation
                logOutConfirmation();
                break;

            case R.id.imgView_hmbgIcon: //This image view opens navigation drawer
                drawerLayout.openDrawer(Gravity.START);
                break;

            case R.id.btn_showContacts: //This button will take into ViewContactsActivity where one can see the contacts which he/she added
                    Intent gotoViewContactsActivity = new Intent(this, ViewContactsActivity.class);
                    gotoViewContactsActivity.putExtra("Account Mob Num", phn);
                    startActivity(gotoViewContactsActivity);
                    break;

            case R.id.tv_displayAppInfo: //This button closes the navigation drawer first and second it calls a method displays information about this app
                if(drawerLayout.isDrawerOpen(Gravity.START))
                    drawerLayout.closeDrawers();
                coachAppInfo();
                break;

        }
    }

    private void logOut()
    {
        manageSession.logOut(); //Logging out from the app
        Intent gotoLoginActivity = new Intent(this, LoginActivity.class);
        gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(gotoLoginActivity);
    }

    private void setDataInTheMenu()
    {
        try {

        HashMap<String, String> hashMap =  manageSession.getUserDetails(); //Getting the user details from Manage Session class
        Set es = hashMap.entrySet();

        Iterator it = es.iterator();

        String phone = "";
        String name = "";
        String email = "";
        String city = "";

        //Iterating the Hash Map to get the values
        while(it.hasNext())
        {
                Map.Entry me = (Map.Entry) it.next();

                if(me.getKey().equals(ConstantClass.KEY_PHONE))
                    phone = me.getValue().toString();

                if(me.getKey().equals(ConstantClass.KEY_NAME))
                    name = me.getValue().toString();

        }
        phn = Long.parseLong(phone);
        ArrayList aL = databaseHelper.readData_Pssd(phn);
        email = aL.get(4).toString();
        city = aL.get(5).toString();

        /*Displaying name, phone number, email and city in the navigation drawer*/
        tv_displayName.setText(name);
        tv_displayPhone.setText(phone);
        tv_displayEmail.setText(email);
        tv_displayCity.setText(city);
        /*---------------------------------------------------------------------*/

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!manageSession.isLogIn())
        {
            Intent gotoLoginActivity = new Intent(this, LoginActivity.class);
            gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(gotoLoginActivity);
        }
    }

    private void logOutConfirmation()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log Out ?");
        builder.setMessage("Are you sure want to do log out ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    logOut();
                    drawerLayout.closeDrawers();
                    dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.show();

        //Setting the color of Negative and Positive buttons of the Alert Dialog Box
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorCustom_brown_900));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MainActivity.this,R.color.colorCustom_brown_900));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseHelper.close(); //Closing the database object
    }

    private void showPopUp()
    {
        //Initialising the View object with the Layout Inflater
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.customised_add_contacts_layout, null, false);

        //Initialising the TextInputLayouts...
        final TextInputLayout contains_firstName = view.findViewById(R.id.contains_firstName);
        final TextInputLayout contains_lastName = view.findViewById(R.id.contains_lastName);
        final TextInputLayout contains_age = view.findViewById(R.id.contains_age);
        final TextInputLayout contains_phone = view.findViewById(R.id.contains_phone);
        final TextInputLayout contains_email = view.findViewById(R.id.contains_email);

        //Initialising the TextInputEditTexts (where tiet is the short form of TextInputEditText) ...
        final TextInputEditText tiet_firstName = view.findViewById(R.id.tiet_firstName);
        final TextInputEditText tiet_lastName = view.findViewById(R.id.tiet_lastName);
        final TextInputEditText tiet_age = view.findViewById(R.id.tiet_age);
        final TextInputEditText tiet_phone = view.findViewById(R.id.tiet_phone);
        final TextInputEditText tiet_email = view.findViewById(R.id.tiet_email);

        //Initialising the Button
        final Button btn_submit_details = view.findViewById(R.id.btn_submit_details);

        //Initialising the array from string resource
        final String[] gender = this.getResources().getStringArray(R.array.gender);

        //Initialising the Array Adapter of type String by passing context, custom layout and an String array as arguments
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, R.layout.custom_layout_simple_spinner_item,gender);
        genderAdapter.setDropDownViewResource(R.layout.custom_layout_simple_spinner_dropdown); //Setting the custom drop down layout of the Spinner

        //Initialising the Spinner
        final Spinner spinner_gender = view.findViewById(R.id.spinner_gender);
        spinner_gender.setAdapter(genderAdapter); //Setting the Array Adapter in the spinner

        //Initialising the AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view); //Setting up of the view in the AlertDialog

        //Initialising the AlertDialog
        final AlertDialog dialog = builder.create();
        dialog.show(); //Displaying the AlertDialog

        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                gender_str = parent.getSelectedItem().toString(); //Selecting an item from the spinner dropdown
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_submit_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Getting the values which were entered by the user in the below fields*/
                String firstName = tiet_firstName.getText().toString().trim();
                String lastName = tiet_lastName.getText().toString().trim();
                String age_str = tiet_age.getText().toString().trim();
                String phone_str = tiet_phone.getText().toString().trim();
                String email = tiet_email.getText().toString().trim();
                /*----------------------------------------------------------------------*/


                //Validating whether the fields empty or not, or required data has been entered or not
                if(!firstName.isEmpty() && !lastName.isEmpty() && !age_str.isEmpty() && !phone_str.isEmpty() && !email.isEmpty())
                {
                    int age =  Integer.parseInt(age_str); //Changing the entered value of Age from String to Integer
                    long phone = Long.parseLong(phone_str); //Changing the entered value of Phone from String to Long

                    if(age > 0)
                        if(phone_str.length() == 10) //Validating whether the number inserted in the tiet_phone field is of 10 digits or not
                            if(!gender_str.equals("Select gender"))
                                if(validateEmail(email)){
                                   long rowVal = databaseHelper.insertData_Contacts(firstName, lastName, gender_str, age, phone, email, phn);
                                    Log.d("Return Row Id", "Value: " + rowVal);
                                    dialog.dismiss();
                                }

                                else
                                    contains_email.setError("Please enter a valid email id");

                            else
                                Toast.makeText(MainActivity.this, "Please select a gender", Toast.LENGTH_SHORT).show();

                        else
                            contains_phone.setError("Please enter a 10 digit number");

                    else if(age == 0)
                        contains_age.setError("Age can not be zero");
                }

                else if(firstName.isEmpty())
                    contains_firstName.setError("Please enter the first name");

                else if(lastName.isEmpty())
                    contains_lastName.setError("Please enter the last name");

                else if(phone_str.isEmpty())
                    contains_lastName.setError("Please enter the phone number");

                else if(age_str.isEmpty())
                    contains_age.setError("Please enter the age");

                else if(email.isEmpty())
                    contains_email.setError("Please enter the email");

                else
                    Toast.makeText(MainActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateEmail(String email)
    {
        //This method validates the pattern of the email id and returns a boolean value which suggests the email is valid or not
        Pattern pattern = Pattern.compile(ConstantClass.EMAIL_PATTERN);

        Matcher matcher = pattern.matcher(email);

        boolean val = matcher.matches();

        return val;
    }

    private void coachAppInfo()
    {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_info_layout);

        dialog.show();


    }
}
