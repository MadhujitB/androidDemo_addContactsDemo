package demo.dada.com.addcontactsdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewContactsActivity extends AppCompatActivity {

    private RecyclerView recView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private DatabaseHelper databaseHelper;
    private ArrayList<ContactsData> arrayList;
    private String gender_str;
    private TextView tv_no_contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);


        tv_no_contacts = findViewById(R.id.tv_no_contacts);
        recView = findViewById(R.id.recView);
        layoutManager = new LinearLayoutManager(this);
        recView.setLayoutManager(layoutManager);
        recView.setHasFixedSize(true);

        databaseHelper = new DatabaseHelper(this);
        arrayList = new ArrayList<ContactsData>();

        setDataInAdapter();
    }

    private void setDataInAdapter()
    {
        arrayList.clear();
        arrayList = databaseHelper.readData_Contacts();

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

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,gender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
                            if(!gender_str.equals("Select gender")) {

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

    public void deleteData(final int position)
    {

        final int id = arrayList.get(position).getId();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Contact !!!");
        builder.setMessage("Are you sure to delete this contact?");


        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int deleteRow = databaseHelper.deleteData_Contacts(id);
                Log.d("Row Deleted","Count: " + deleteRow);

                if (deleteRow > 0)
                {
                    arrayList.remove(position);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
