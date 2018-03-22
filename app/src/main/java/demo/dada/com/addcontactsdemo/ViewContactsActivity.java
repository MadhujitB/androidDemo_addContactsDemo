package demo.dada.com.addcontactsdemo;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewContactsActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private DatabaseHelper databaseHelper;
    private ArrayList<ContactsData> arrayList;
    private String gender_str;
    private TextView tv_no_contacts;
    private int position = 0;
    private ManageSession manageSession;
    private long accountMobNum = 0;
    private int loginCount = 0;
    private TextView tv_setTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);


        tv_setTitle = findViewById(R.id.tv_setTitle);
        tv_no_contacts = findViewById(R.id.tv_no_contacts);
        recView = findViewById(R.id.recView);
        layoutManager = new LinearLayoutManager(this);
        recView.setLayoutManager(layoutManager);
        recView.setHasFixedSize(true);

        databaseHelper = new DatabaseHelper(this);
        arrayList = new ArrayList<ContactsData>();

        manageSession = new ManageSession(this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recView);

        accountMobNum = this.getIntent().getLongExtra("Account Mob Num",0);

        String title = "View Contacts";
        tv_setTitle.setText(title);

        findViewById(R.id.imgView_backIcon).setVisibility(View.VISIBLE);
        findViewById(R.id.imgView_backIcon).setOnClickListener(this);
        findViewById(R.id.imgView_hmbgIcon).setVisibility(View.INVISIBLE);
        findViewById(R.id.imgView_infoIcon).setOnClickListener(this);


        setDataInAdapter();
    }

    private void setDataInAdapter()
    {
        arrayList.clear();
        arrayList = databaseHelper.readData_Contacts(accountMobNum);

        if(arrayList.size() > 0) {
            recView.setVisibility(View.VISIBLE);
            tv_no_contacts.setVisibility(View.GONE);
            adapter = new ContactsViewAdapter(ViewContactsActivity.this, arrayList);
            recView.setAdapter(adapter);

        }
        else if (arrayList.size() == 0)
        {
            recView.setVisibility(View.GONE);
            tv_no_contacts.setVisibility(View.VISIBLE);
        }



        loginCount = databaseHelper.getLoginCount_Registration(accountMobNum);

        if(loginCount == 1)
            onCoachMark();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseHelper.close();
    }

    public void updateContact(int position)
    {
        final ContactsData cd = arrayList.get(position);


        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.customised_add_contacts_layout, null, false);

        final TextInputLayout contains_firstName = view.findViewById(R.id.contains_firstName);
        final TextInputLayout contains_lastName = view.findViewById(R.id.contains_lastName);
        final TextInputLayout contains_age = view.findViewById(R.id.contains_age);
        final TextInputLayout contains_phone = view.findViewById(R.id.contains_phone);
        final TextInputLayout contains_email = view.findViewById(R.id.contains_email);

        TextView tv_display_enter_details = view.findViewById(R.id.tv_display_enter_details);
        tv_display_enter_details.setText(R.string.tv_edit_details);

        final TextInputEditText tiet_firstName = view.findViewById(R.id.tiet_firstName);
        tiet_firstName.setText(cd.getFirst_name());

        final TextInputEditText tiet_lastName = view.findViewById(R.id.tiet_lastName);
        tiet_lastName.setText(cd.getLast_name());

        final TextInputEditText tiet_age = view.findViewById(R.id.tiet_age);
        tiet_age.setText(String.valueOf(cd.getAge()));

        final TextInputEditText tiet_phone = view.findViewById(R.id.tiet_phone);
        tiet_phone.setText(String.valueOf(cd.getPhone()));

        final TextInputEditText tiet_email = view.findViewById(R.id.tiet_email);
        tiet_email.setText(cd.getEmail());

        final Button btn_submit_details = view.findViewById(R.id.btn_submit_details);

        final String[] gender = this.getResources().getStringArray(R.array.gender);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, R.layout.custom_layout_simple_spinner_item,gender);
        genderAdapter.setDropDownViewResource(R.layout.custom_layout_simple_spinner_dropdown);

        final Spinner spinner_gender = view.findViewById(R.id.spinner_gender);
        spinner_gender.setAdapter(genderAdapter);

        int gn = 0;


        for(int i =0; i < gender.length ; i++)
        {
            if(gender[i].equals(cd.getGender()))
            {
                gn = i;
                break;
            }

        }

        spinner_gender.setSelection(gn);


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
                            if(!gender_str.equals("Select gender"))

                                if(validateEmail(email)){

                                    cd.setFirst_name(firstName);
                                    cd.setLast_name(lastName);
                                    cd.setGender(gender_str);
                                    cd.setAge(age);
                                    cd.setPhone(phone);
                                    cd.setEmail(email);

                                    int countUpdatedRows =  databaseHelper.updateData_Contacts(cd);
                                    Log.d("Return Updated Row Id", "Value: " + countUpdatedRows);
                                    setDataInAdapter();
                                    dialog.dismiss();
                                }
                                else
                                    contains_email.setError("Please enter a valid email id");
                            else
                                Toast.makeText(ViewContactsActivity.this, "Please select a gender", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(ViewContactsActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private boolean validateEmail(String email)
    {
        Pattern pattern = Pattern.compile(ConstantClass.EMAIL_PATTERN);

        Matcher matcher = pattern.matcher(email);

        boolean val = matcher.matches();

        return val;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition(); //get position which is swipe


            if (direction == ItemTouchHelper.LEFT) {    //if swipe left

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewContactsActivity.this); //alert for confirm to delete
                builder.setMessage("Are you sure to delete?");    //set message

                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() { //when click on DELETE
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                //        deleteData(position);
                        int id = arrayList.get(position).getId();
                        int deleteRow = databaseHelper.deleteData_Contacts(id);
                        Log.d("Row Deleted","Count: " + deleteRow);

                        if (deleteRow > 0)
                        {
                            arrayList.remove(position);
                            adapter.notifyDataSetChanged();
                        }

                        if(arrayList.size() == 0)
                            setDataInAdapter();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //adapter.notifyItemRemoved(position + 1);
                        adapter.notifyItemRangeChanged(position, adapter.getItemCount());

                    }
                });  //show alert dialog

                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ViewContactsActivity.this, R.color.colorAccent));
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ViewContactsActivity.this, R.color.colorAccent));


            }


        }
    };


    private void onCoachMark(){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if(dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setContentView(R.layout.custom_dialog_instruction);
        dialog.setCanceledOnTouchOutside(true);
        //for dismissing anywhere you touch
        Button button = dialog.findViewById(R.id.btn_inst_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

        loginCount = loginCount + 1;
        databaseHelper.setLoginCount_Registration(accountMobNum, loginCount);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void checkCallPermission(int pos)
    {
        position = pos;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        else
        {
            makeCall();
        }
    }

    private void makeCall()
    {
        String mobileNum = "tel:" + arrayList.get(position).getPhone();
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(mobileNum));
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 1:
                if(grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    makeCall();
                }
                break;

            case 2:
                if(grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    sendSMS();
                }
                break;

        }

    }

    public void checkSmsPermission(int pos)
    {
        position = pos;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 2);
        }
        else
        {
            sendSMS();
        }

    }

    private void sendSMS()
    {
        View view = null;
        final String phNo = String.valueOf(arrayList.get(position).getPhone());

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if(inflater != null)
            view = inflater.inflate(R.layout.customised_sms_layout, null, false);

        final TextInputLayout containsSMSMessage = view.findViewById(R.id.containsSMSMessage);
        final TextInputEditText tiet_myMessage = view.findViewById(R.id.tiet_myMessage);
        Button button = view.findViewById(R.id.btn_send_sms);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myMessage = tiet_myMessage.getText().toString().trim();

                if(!myMessage.isEmpty())
                {

                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phNo, null, myMessage, null, null);

                        dialog.dismiss();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }

                else
                    containsSMSMessage.setError("Please enter some text");
            }
        });

    }

    public void sendEmail(int pos)
    {
        position = pos;



        String emailId = arrayList.get(position).getEmail();

        Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailId});
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent,"Select Email App"));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.imgView_backIcon:
                onBackPressed();
                break;

            case R.id.imgView_infoIcon:
                onCoachMark();
                break;
        }

    }
}
