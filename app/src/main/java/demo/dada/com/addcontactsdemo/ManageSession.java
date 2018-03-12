package demo.dada.com.addcontactsdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * Created by DADA on 02-03-2018.
 */

public class ManageSession {

    private Context context;
    private SharedPreferences sharedPreferences;
    private static final String MY_PREF_NAME = "My Shared Pref";
    private Editor editor;
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";

    private static final String KEY_EMAIL = "email";
    private static final String KEY_CITY = "city";
    private static final String KEY_STATE = "state";

    private static final String IS_LogIn = "Logged In";
    private static final String KEY_PASSWORD = "password";

    ManageSession(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String name, String phone, /*String email, String city, String state,*/ String password)
    {
        editor.putBoolean(IS_LogIn, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PHONE, phone);
        /*editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_CITY, city);
        editor.putString(KEY_STATE, state);*/
        editor.putString(KEY_PASSWORD, password);

        editor.commit();


    }

    public HashMap<String, String> getUserDetails()
    {
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(KEY_NAME, sharedPreferences.getString(KEY_NAME,""));
        hashMap.put(KEY_PHONE, sharedPreferences.getString(KEY_PHONE,""));
        /*hashMap.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL,""));
        hashMap.put(KEY_CITY, sharedPreferences.getString(KEY_CITY,""));
        hashMap.put(KEY_STATE, sharedPreferences.getString(KEY_STATE,""));*/
        hashMap.put(KEY_PASSWORD, sharedPreferences.getString(KEY_PASSWORD,""));

        return hashMap;
    }


    public boolean isLogIn()
    {
        return sharedPreferences.getBoolean(IS_LogIn, false);
    }


    public boolean logOut()
    {
        boolean ret = false;

        editor.clear();
        ret = editor.commit();

        return ret;
    }


}
