package demo.dada.com.addcontactsdemo;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DADA on 11-03-2018.
 */

public class ContactsViewAdapter extends RecyclerView.Adapter<ContactsViewAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<ContactsData> arrayList;

    public ContactsViewAdapter(Activity activity, ArrayList<ContactsData> arrayList)
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String name = arrayList.get(holder.getAdapterPosition()).getFirst_name() +
                      arrayList.get(holder.getAdapterPosition()).getLast_name();

        holder.tv_displayName.setText(name);
        holder.tv_displayGender.setText(arrayList.get(holder.getAdapterPosition()).getGender());
        holder.tv_displayPhone.setText(String.valueOf(arrayList.get(holder.getAdapterPosition()).getPhone()));
        holder.tv_displayEmail.setText(arrayList.get(holder.getAdapterPosition()).getEmail());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_displayName;
        TextView tv_displayGender;
        TextView tv_displayPhone;
        TextView tv_displayEmail;

        ViewHolder(View itemView)
        {
            super(itemView);

            tv_displayName = itemView.findViewById(R.id.tv_displayName);
            tv_displayGender = itemView.findViewById(R.id.tv_displayGender);
            tv_displayPhone = itemView.findViewById(R.id.tv_displayPhone);
            tv_displayEmail = itemView.findViewById(R.id.tv_displayEmail);
        }

    }
}
