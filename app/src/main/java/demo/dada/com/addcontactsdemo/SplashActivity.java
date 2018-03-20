package demo.dada.com.addcontactsdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends Activity {

    private ManageSession manageSession;
    private  DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        manageSession = new ManageSession(this);
        databaseHelper = new DatabaseHelper(this);


        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //If the user is logged in, then he/she will be taken to MainActivity
                if (manageSession.isLogIn())
                    takeTo_MainActivity();

                else //If the user is not logged in, then he/she will be taken to LoginActivity
                    takeTo_LoginActivity();
            }
        }, 3000);
    }

    private void takeTo_MainActivity()
    {
        Intent gotoMainActivity = new Intent(this, MainActivity.class);
        gotoMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        gotoMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        gotoMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(gotoMainActivity);
    }

    private void takeTo_LoginActivity()
    {
        Intent gotoLoginActivity = new Intent(this, LoginActivity.class);
        gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        gotoLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(gotoLoginActivity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseHelper.close();
    }
}
