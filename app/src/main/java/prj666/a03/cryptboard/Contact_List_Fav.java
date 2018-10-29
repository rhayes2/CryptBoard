package prj666.a03.cryptboard;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
//import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.os.Bundle;
import android.database.Cursor;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import prj666.a03.cryptboard.ContactBase.Contact;


public class Contact_List_Fav extends ListFragment implements AdapterView.OnItemClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact__list__main, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(getActivity(), android.R.layout.simple_list_item_1, frontEndHelper.getInstance().getContacts());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, frontEndHelper.getInstance().getNamesFav());

        adapter.notifyDataSetChanged();
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();

        getListView().setOnItemClickListener(this);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contact clicked = frontEndHelper.getInstance().getPos(position);
        Intent contactDetails = new Intent(getContext(),Contact_Details.class);
        contactDetails.putExtra("contact", clicked);
        startActivity(contactDetails);

    }
}