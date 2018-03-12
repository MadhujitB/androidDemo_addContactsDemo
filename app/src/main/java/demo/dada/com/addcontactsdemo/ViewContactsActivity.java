package demo.dada.com.addcontactsdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class ViewContactsActivity extends AppCompatActivity {

    private RecyclerView recView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private DatabaseHelper databaseHelper;
    private ArrayList<ContactsData> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        recView = findViewById(R.id.recView);
        layoutManager = new LinearLayoutManager(this);
        recView.setLayoutManager(layoutManager);
        recView.setHasFixedSize(true);

        databaseHelper = new DatabaseHelper(this);

        arrayList = databaseHelper.readData_Contacts();

        adapter = new ContactsViewAdapter(ViewContactsActivity.this, arrayList);
        recView.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseHelper.close();
    }

    private void updateContact(int position)
    {
        ContactsData cd = arrayList.get(position);


    }
}
