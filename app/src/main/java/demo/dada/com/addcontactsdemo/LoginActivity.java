package demo.dada.com.addcontactsdemo;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ManageSession manageSession;
    private DatabaseHelper databaseHelper;
    private EditText phoneNumber, password;
    private String passdRcvd;
    private long phone;
    private TextInputLayout containsUsername, containsPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumber = findViewById(R.id.username);
        password = findViewById(R.id.password);

        findViewById(R.id.btn_sign_up).setOnClickListener(this);
        findViewById(R.id.btn_sign_in).setOnClickListener(this);

        containsUsername = findViewById(R.id.containsUsername);
        containsPassword = findViewById(R.id.containsPassword);

        manageSession = new ManageSession(this);
        databaseHelper = new DatabaseHelper(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_sign_in:
                String phnum = phoneNumber.getText().toString().trim();
                passdRcvd = password.getText().toString().trim();

                if(!phnum.isEmpty() && !passdRcvd.isEmpty())
                {
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

        System.out.println("Retrive List: \n" + arrayList);


        if(count >= 1)
        {
            String phn = arrayList.get(1).toString();
            String pswd = arrayList.get(2).toString();
            String name = arrayList.get(3).toString();
            long ph = Long.parseLong(phn);


            if(phone == ph && pswd.equals(passdRcvd))
            {
                Toast.makeText(this, "Credentials matched", Toast.LENGTH_SHORT).show();
                Log.d("Credentials","Matched" + " Name: " + name);

                manageSession.createLoginSession(name, phn, pswd);
                onResume();
            }
            else
            {
                Toast.makeText(this, "Credentials mis-matched", Toast.LENGTH_SHORT).show();
                Log.d("Credentials","Mis-Matched");
                Log.d("Values","Phone Number: " + ph +"\nPassword: " + pswd + "\nName: "+name);

            }
        }
        else if(count == 0)
            Toast.makeText(this, "Credentials not found", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseHelper.close();
    }
}
