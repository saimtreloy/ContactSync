package saim.com.contactsync.ContactList;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Contacts.People;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.Toast;

import java.util.ArrayList;

import saim.com.contactsync.R;

import static android.R.attr.name;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactAdapterHolder> {

    ArrayList<ContactModel> adapterList = new ArrayList<>();

    public ContactAdapter(ArrayList<ContactModel> adapterList) {
        this.adapterList = adapterList;
    }

    @Override
    public ContactAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list, parent, false);
        ContactAdapterHolder contactAdapterHolder = new ContactAdapterHolder(view);
        return contactAdapterHolder;
    }

    @Override
    public void onBindViewHolder(ContactAdapterHolder holder, final int position) {
        holder.txtListNumber.setText(adapterList.get(position).getNumber());
        holder.txtListName.setText(adapterList.get(position).getName());


        holder.imgListCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = adapterList.get(position).getNumber();
                String dial = "tel:" + number;
                v.getContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        });

        holder.imgListMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", adapterList.get(position).getNumber(), null)));
            }
        });

        holder.imgListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(People.NUMBER, adapterList.get(position).getNumber());
                values.put(People.TYPE, Phone.TYPE_CUSTOM);
                values.put(People.NAME, adapterList.get(position).getName());
                Uri dataUri = v.getContext().getContentResolver().insert(People.CONTENT_URI, values);
                Uri updateUri = Uri.withAppendedPath(dataUri, People.Phones.CONTENT_DIRECTORY);
                values.clear();
                values.put(People.Phones.TYPE, People.TYPE_MOBILE);
                values.put(People.NUMBER, adapterList.get(position).getNumber());
                updateUri = v.getContext().getContentResolver().insert(updateUri, values);
                Toast.makeText(v.getContext(), "Contact Added", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }

    public class ContactAdapterHolder extends RecyclerView.ViewHolder {

        TextView txtListName, txtListNumber;
        ImageView imgListCall, imgListMessage, imgListAdd;
        LinearLayout listLayout;

        public ContactAdapterHolder(View itemView) {
            super(itemView);

            txtListName = (TextView) itemView.findViewById(R.id.txtListName);
            txtListNumber = (TextView) itemView.findViewById(R.id.txtListNumber);

            imgListCall = (ImageView) itemView.findViewById(R.id.imgListCall);
            imgListMessage = (ImageView) itemView.findViewById(R.id.imgListMessage);
            imgListAdd = (ImageView) itemView.findViewById(R.id.imgListAdd);

            listLayout = (LinearLayout) itemView.findViewById(R.id.listLayout);

            //listClicked();
        }


        public void listClicked() {

            imgListCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

        }
    }
}
