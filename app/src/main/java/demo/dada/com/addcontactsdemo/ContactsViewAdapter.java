package demo.dada.com.addcontactsdemo;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DADA on 11-03-2018.
 */

public class ContactsViewAdapter extends RecyclerView.Adapter<ContactsViewAdapter.ViewHolder> {

    private ViewContactsActivity activity;
    private ArrayList<ContactsData> arrayList;

    public ContactsViewAdapter(ViewContactsActivity activity, ArrayList<ContactsData> arrayList)
    {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.customised_contacts_layout, parent, false);

        ViewHolder vH = new ViewHolder(view);
        return vH;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String name = "Name: " +
                      arrayList.get(holder.getAdapterPosition()).getFirst_name() +
                      " " +
                      arrayList.get(holder.getAdapterPosition()).getLast_name();

        String gender = "Gender: " + arrayList.get(holder.getAdapterPosition()).getGender();
        String phone = "Phone: " + String.valueOf(arrayList.get(holder.getAdapterPosition()).getPhone());
        String email = "Email: " + arrayList.get(holder.getAdapterPosition()).getEmail();
        String age =  "Age: " + String.valueOf(arrayList.get(holder.getAdapterPosition()).getAge());

        holder.tv_displayName.setText(name);
        holder.tv_displayGender.setText(gender);
        holder.tv_displayPhone.setText(phone);
        holder.tv_displayEmail.setText(email);
        holder.tv_displayAge.setText(age);

        holder.imgView_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.updateContact(holder.getAdapterPosition());
            }
        });

        holder.imgView_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.deleteData(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_displayName;
        TextView tv_displayGender;
        TextView tv_displayAge;
        TextView tv_displayPhone;
        TextView tv_displayEmail;
        ImageView imgView_edit;
        ImageView imgView_delete;

        ViewHolder(View itemView)
        {
            super(itemView);

            tv_displayName = itemView.findViewById(R.id.tv_displayName);
            tv_displayGender = itemView.findViewById(R.id.tv_displayGender);
            tv_displayAge = itemView.findViewById(R.id.tv_displayAge);
            tv_displayPhone = itemView.findViewById(R.id.tv_displayPhone);
            tv_displayEmail = itemView.findViewById(R.id.tv_displayEmail);
            imgView_edit = itemView.findViewById(R.id.imgView_edit);
            imgView_delete = itemView.findViewById(R.id.imgView_delete);
        }

    }
}
