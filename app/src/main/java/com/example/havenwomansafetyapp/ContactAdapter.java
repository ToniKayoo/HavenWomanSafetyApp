package com.example.havenwomansafetyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.havenwomansafetyapp.Contact;
import com.example.havenwomansafetyapp.R;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private Context mContext;
    private ArrayList<Contact> mContacts;

    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
        mContext = context;
        mContacts = contacts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Contact contact = getItem(position);
        
        // Check if an existing view is being reused, or inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);
        }

        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvNumber = convertView.findViewById(R.id.tvNumber);
        TextView tvEmail = convertView.findViewById(R.id.tvEmail);

        // Populate the data into the template view using the data object
        tvName.setText(contact.getName());
        tvNumber.setText(contact.getNumber());
        tvEmail.setText(contact.getEmail());

        // Return the completed view to render on screen
        return convertView;
    }
}
