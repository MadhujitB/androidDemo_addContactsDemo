package demo.dada.com.addcontactsdemo;

/**
 * Created by DADA on 04-03-2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "addContactsDemo";
    private static final String TABLE_NAME_REGISTRATION = "register";
    private static final String TABLE_NAME_CONTACTS = "contacts";
    private Context context;


    private static final String CREATE_TABLE_DEMO = "CREATE TABLE " + TABLE_NAME_REGISTRATION + "("
            + ConstantClass.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ConstantClass.KEY_NAME + " TEXT, "
            + ConstantClass.KEY_PHONE + " LONG, "
            + ConstantClass.KEY_EMAIL + " TEXT, "
            + ConstantClass.KEY_CITY + " TEXT, "
            + ConstantClass.KEY_STATE + " TEXT, "
            + ConstantClass.KEY_PASSWORD + " TEXT, "
            + ConstantClass.KEY_LOGIN_COUNT + " INTEGER)";

    private static final String CREATE_TABLE_CONTACTS = "CREATE TABLE " + TABLE_NAME_CONTACTS + "("
            + ConstantClass.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ConstantClass.KEY_FIRST_NAME + " TEXT, "
            + ConstantClass.KEY_LAST_NAME + " TEXT, "
            + ConstantClass.KEY_GENDER + " TEXT, "
            + ConstantClass.KEY_AGE + " INTEGER, "
            + ConstantClass.KEY_PHONE + " LONG, "
            + ConstantClass.KEY_EMAIL + " TEXT, "
            + ConstantClass.KEY_ACCOUNT_PHONE + " LONG)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_DEMO);
        db.execSQL(CREATE_TABLE_CONTACTS);

        Log.d("onCreate()","Called Successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REGISTRATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CONTACTS);
        onCreate(db);

        Log.d("onUpgrade()","Called Successfully");
    }

    public long insertData_Registration(String name, long phone, String email,
                                        String city, String state, String password)
    {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantClass.KEY_NAME, name);
        contentValues.put(ConstantClass.KEY_PHONE, phone);
        contentValues.put(ConstantClass.KEY_EMAIL, email);
        contentValues.put(ConstantClass.KEY_CITY, city);
        contentValues.put(ConstantClass.KEY_STATE, state);
        contentValues.put(ConstantClass.KEY_PASSWORD, password);
        contentValues.put(ConstantClass.KEY_LOGIN_COUNT, 0);

        long rowId = db.insert(TABLE_NAME_REGISTRATION, null, contentValues);

        return rowId;
    }

    public ArrayList<RegistrationData> readData_Registration()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<RegistrationData> arrayList = new ArrayList<>();

        String[] projection = {
                ConstantClass.KEY_ID,
                ConstantClass.KEY_NAME,
                ConstantClass.KEY_PHONE,
                ConstantClass.KEY_EMAIL,
                ConstantClass.KEY_CITY,
                ConstantClass.KEY_STATE,
                ConstantClass.KEY_PASSWORD,
        };

        Cursor cursor = db.query(
           TABLE_NAME_REGISTRATION,
            projection,
                null,
                null,
                null,
                null,
                null
        );

        arrayList.clear();

        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ConstantClass.KEY_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ConstantClass.KEY_NAME));
            long phone = cursor.getLong(cursor.getColumnIndexOrThrow(ConstantClass.KEY_PHONE));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(ConstantClass.KEY_EMAIL));
            String city = cursor.getString(cursor.getColumnIndexOrThrow(ConstantClass.KEY_CITY));
            String state = cursor.getString(cursor.getColumnIndexOrThrow(ConstantClass.KEY_STATE));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(ConstantClass.KEY_PASSWORD));

            arrayList.add(new RegistrationData(id, name, phone, email, city, state, password));

        }

        if(!cursor.isClosed())
            cursor.close();

        if(db.isOpen())
            db.close();

        return arrayList;
    }

    public ArrayList readData_Pssd(long phone)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList arrayList = new ArrayList<>();

        String[] projection = {
                ConstantClass.KEY_ID,
                ConstantClass.KEY_PHONE,
                ConstantClass.KEY_PASSWORD,
                ConstantClass.KEY_NAME,
                ConstantClass.KEY_EMAIL,
                ConstantClass.KEY_CITY,
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = ConstantClass.KEY_PHONE + " = ?";
        String[] selectionArgs = { String.valueOf(phone) };

        Cursor cursor = db.query(
                TABLE_NAME_REGISTRATION,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        try {

            int id = 0;
            long ph = 0;
            String password = "";
            String name = "";
            String email = "";
            String city = "";

            Log.d("Cursor","Count: " + cursor.getCount());
            if (cursor.getCount() >= 1)

                while (cursor.moveToNext())
                {
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(ConstantClass.KEY_ID));
                    ph = cursor.getLong(cursor.getColumnIndexOrThrow(ConstantClass.KEY_PHONE));
                    password = cursor.getString(cursor.getColumnIndexOrThrow(ConstantClass.KEY_PASSWORD));
                    name =  cursor.getString(cursor.getColumnIndexOrThrow(ConstantClass.KEY_NAME));
                    email =  cursor.getString(cursor.getColumnIndexOrThrow(ConstantClass.KEY_EMAIL));
                    city =  cursor.getString(cursor.getColumnIndexOrThrow(ConstantClass.KEY_CITY));

                    arrayList.add(id);
                    arrayList.add(ph);
                    arrayList.add(password);
                    arrayList.add(name);
                    arrayList.add(email);
                    arrayList.add(city);

                }

                Log.d("Inside Database Handler","id: " + id + "\nph: " + ph +"\npassword: " + password);
        }
        catch (Exception e)
        {e.printStackTrace();}

        if(!cursor.isClosed())
            cursor.close();

        if(db.isOpen())
            db.close();

        return arrayList;
    }

    public int setLoginCount_Registration(long mobile, int count)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ConstantClass.KEY_LOGIN_COUNT, count);

        String selection = ConstantClass.KEY_PHONE + " LIKE ?";
        String[] selectionArgs = {String.valueOf(mobile)};

        int lgnCnt = db.update(TABLE_NAME_REGISTRATION, cv,selection,selectionArgs);

        if(db.isOpen())
           db.close();

        return lgnCnt;
    }

    public int getLoginCount_Registration(long mobile)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = {
            ConstantClass.KEY_LOGIN_COUNT
        };

        String selection = ConstantClass.KEY_PHONE + " LIKE ?";
        String[] selectionArgs = {String.valueOf(mobile)};

        Cursor cursor = db.query(
                TABLE_NAME_REGISTRATION,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
                );

        int cntVal = 0;

        if(cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                cntVal = cursor.getInt(cursor.getColumnIndexOrThrow(ConstantClass.KEY_LOGIN_COUNT));
            }
        }

        if(!cursor.isClosed())
            cursor.close();

        if(db.isOpen())
            db.close();

        return cntVal;
    }



    public long insertData_Contacts(String fname, String lname, String gender, int age, long phone, String email, long accPh)
    {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantClass.KEY_FIRST_NAME, fname);
        contentValues.put(ConstantClass.KEY_LAST_NAME, lname);
        contentValues.put(ConstantClass.KEY_GENDER, gender);
        contentValues.put(ConstantClass.KEY_AGE, age);
        contentValues.put(ConstantClass.KEY_PHONE, phone);
        contentValues.put(ConstantClass.KEY_EMAIL, email);
        contentValues.put(ConstantClass.KEY_ACCOUNT_PHONE, accPh);

        long rowId = db.insert(TABLE_NAME_CONTACTS, null, contentValues);

        return rowId;
    }

    public ArrayList<ContactsData> readData_Contacts(long accPh)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<ContactsData> arrayList = new ArrayList<>();

        String[] projection = {
                ConstantClass.KEY_ID,
                ConstantClass.KEY_FIRST_NAME,
                ConstantClass.KEY_LAST_NAME,
                ConstantClass.KEY_GENDER,
                ConstantClass.KEY_AGE,
                ConstantClass.KEY_PHONE,
                ConstantClass.KEY_EMAIL
        };

        String selection = ConstantClass.KEY_ACCOUNT_PHONE + " = ?";
        String[] selectionArgs = {String.valueOf(accPh)};

        Cursor cursor = db.query(
                TABLE_NAME_CONTACTS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ConstantClass.KEY_ID));
            String fname = cursor.getString(cursor.getColumnIndexOrThrow(ConstantClass.KEY_FIRST_NAME));
            String lname = cursor.getString(cursor.getColumnIndexOrThrow(ConstantClass.KEY_LAST_NAME));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow(ConstantClass.KEY_GENDER));
            int age = cursor.getInt(cursor.getColumnIndexOrThrow(ConstantClass.KEY_AGE));
            long phone = cursor.getLong(cursor.getColumnIndexOrThrow(ConstantClass.KEY_PHONE));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(ConstantClass.KEY_EMAIL));

            arrayList.add(new ContactsData(id, fname, lname, gender, age, phone, email));

        }

        if(!cursor.isClosed())
            cursor.close();

        if(db.isOpen())
            db.close();

        return arrayList;
    }


    public int updateData_Contacts(ContactsData cd)
    {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ConstantClass.KEY_FIRST_NAME, cd.getFirst_name());
        cv.put(ConstantClass.KEY_LAST_NAME, cd.getLast_name());
        cv.put(ConstantClass.KEY_GENDER, cd.getGender());
        cv.put(ConstantClass.KEY_AGE, cd.getAge());
        cv.put(ConstantClass.KEY_PHONE, cd.getPhone());
        cv.put(ConstantClass.KEY_EMAIL, cd.getEmail());

        String selection = ConstantClass.KEY_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(cd.getId())};

        int count = db.update(TABLE_NAME_CONTACTS, cv, selection, selectionArgs);

        return count;

    }

    public int deleteData_Contacts(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = ConstantClass.KEY_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id)};

        int retRowCount = db.delete(TABLE_NAME_CONTACTS, selection, selectionArgs);

        db.close();

        return retRowCount;

    }

    int verifyContact(long mob)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                            ConstantClass.KEY_ID,
                            ConstantClass.KEY_PHONE
        };
        String selection = ConstantClass.KEY_PHONE + " = ?";
        String[] selectionArgs = {String.valueOf(mob)};
        Cursor cursor = db.query(
                        TABLE_NAME_REGISTRATION,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
        );

        int count = cursor.getCount();

        if(!cursor.isClosed())
            cursor.close();

        if (db.isOpen())
            db.close();

        return count;
    }

    public int setPassword_Registration(long phone, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ConstantClass.KEY_PASSWORD, password);

        String selection = ConstantClass.KEY_PHONE + " = ?";
        String[] selectionArgs = {String.valueOf(phone)};

        int cnt = db.update(TABLE_NAME_REGISTRATION, cv, selection, selectionArgs);

        return cnt;
    }

}
