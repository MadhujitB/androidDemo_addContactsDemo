package demo.dada.com.addcontactsdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ManageSession manageSession;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imgView_menuIcon;
    private TextView tv_displayName, tv_displayPhone, tv_displayEmail, tv_displayCity;
    private DatabaseHelper databaseHelper;
    private String gender_str = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manageSession = new ManageSession(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        imgView_menuIcon = findViewById(R.id.imgView_menuIcon);
        imgView_menuIcon.setOnClickListener(this);

        tv_displayName = findViewById(R.id.tv_displayName);
        tv_displayPhone = findViewById(R.id.tv_displayPhone);
        tv_displayEmail = findViewById(R.id.tv_displayEmail);
        tv_displayCity = findViewById(R.id.tv_displayCity);

        findViewById(R.id.btn_showPopUp).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
        findViewById(R.id.btn_showContacts).setOnClickListener(this);

        databaseHelper = new DatabaseHelper(this);

        setDataInTheMenu();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.btn_showPopUp:
                showPopUp();
                break;

            case R.id.btn_logout:
                logOutConfirmation();
                break;

            case R.id.imgView_menuIcon:
                drawerLayout.openDrawer(Gravity.START);
                break;

            case R.id.btn_showContacts:
                Intent gotoViewContactsActivity = new Intent(this, ViewContactsActivity.class);
                startActivity(gotoViewContactsActivity);
        }
    }

    private void logOut()
    {
        manageSession.logOut();
        Intent gotoLoginActivity = new Intent(this, LoginActivity.class);
        gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(gotoLoginActivity);
    }

    private void setDataInTheMenu()
    {
        try {

        HashMap<String, String> hashMap =  manageSession.getUserDetails();
        Set es = hashMap.entrySet();

        Iterator it = es.iterator();

        String phone = "";
        String name = "";
        String email = "";
        String city = "";

        while(it.hasNext())
        {
                Map.Entry me = (Map.Entry) it.next();

                if(me.getKey().equals(ConstantClass.KEY_PHONE))
                    phone = me.getValue().toString();

                if(me.getKey().equals(ConstantClass.KEY_NAME))
                    name = me.getValue().toString();

        }
        long phn = Long.parseLong(phone);
        ArrayList aL = databaseHelper.readData_Pssd(phn);
        email = aL.get(4).toString();
        city = aL.get(5).toString();

        tv_displayName.setText(name);
        tv_displayPhone.setText(phone);
        tv_displayEmail.setText(email);
        tv_displayCity.setText(city);

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

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseHelper.close();
    }

    private void showPopUp()
    {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.customised_add_contacts_layout, null, false);

        final TextInputLayout contains_firstName = view.findViewById(R.id.contains_firstName);
        final TextInputLayout contains_lastName = view.findViewById(R.id.contains_lastName);
        final TextInputLayout contains_age = view.findViewById(R.id.contains_age);
        final TextInputLayout contains_phone = view.findViewById(R.id.contains_phone);
        final TextInputLayout contains_email = view.findViewById(R.id.contains_email);

        final TextInputEditText tiet_firstName = view.findViewById(R.id.tiet_firstName);
        final TextInputEditText tiet_lastName = view.findViewById(R.id.tiet_lastName);
        final TextInputEditText tiet_age = view.findViewById(R.id.tiet_age);
        final TextInputEditText tiet_phone = view.findViewById(R.id.tiet_phone);
        final TextInputEditText tiet_email = view.findViewById(R.id.tiet_email);

        final Button btn_submit_details = view.findViewById(R.id.btn_submit_details);

        final String[] gender = this.getResources().getStringArray(R.array.gender);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,gender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner_gender = view.findViewById(R.id.spinner_gender);
        spinner_gender.setAdapter(genderAdapter);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                gender_str = parent.getSelectedItem().toString();
                Log.d("Spinner Value","Item Selected: " + gender_str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_submit_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = tiet_firstName.getText().toString().trim();
                String lastName = tiet_lastName.getText().toString().trim();
                String age_str = tiet_age.getText().toString().trim();
                String phone_str = tiet_phone.getText().toString().trim();
                String email = tiet_email.getText().toString().trim();



                if(!firstName.isEmpty() && !lastName.isEmpty() && !age_str.isEmpty() && !phone_str.isEmpty() && !email.isEmpty())
                {
                    int age =  Integer.parseInt(age_str);
                    long phone = Long.parseLong(phone_str);

                    if(age > 0)
                        if(phone_str.length() == 10)
                            if(!gender_str.equals("Select gender")) {

                                long rowVal = databaseHelper.insertData_Contacts(firstName, lastName, gender_str, age, phone, email);
                                Log.d("Return Row Id", "Value: " + rowVal);
                                dialog.dismiss();
                            }
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
}
