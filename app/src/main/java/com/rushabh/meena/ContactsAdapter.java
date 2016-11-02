package com.rushabh.meena;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rushabh on 02/11/16.
 */

public class ContactsAdapter extends RecyclerViewCustomAdapter<ContactView> {
    LayoutInflater inflater;
    Context context;

    int columnIndexForName=1;
    int getColumnIndexForNumber=2;
    public ContactsAdapter(Cursor cursor, Context context) {
        super(cursor);
        this.context=context;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public ContactView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.item_contact_view,parent,false);

        ContactView contactView=new ContactView(view);

        return contactView;
    }

    @Override
    protected void onBindViewHolder(ContactView holder, Cursor cursor) {

        String name=cursor.getString(columnIndexForName);
        String phoneNumber=cursor.getString(getColumnIndexForNumber);
        holder.tvInitial.setText(name.substring(0,1));
        holder.tvMobileNumber.setText(phoneNumber);
        holder.tvName.setText(name);
    }


}

class ContactView extends RecyclerView.ViewHolder{
    @BindView(R.id.tv_initials)
    TextView tvInitial;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_mobile_number)
    TextView tvMobileNumber;
    public ContactView(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
