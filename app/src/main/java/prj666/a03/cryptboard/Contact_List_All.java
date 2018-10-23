package prj666.a03.cryptboard;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import prj666.a03.cryptboard.ContactBase.Contact;

public class Contact_List_All extends ListFragment implements AdapterView.OnItemClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact__list__main, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(getActivity(), android.R.layout.simple_list_item_1, frontEndHelper.getInstance().getContacts());
        setListAdapter(adapter);
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

//public class Contact_List_All extends android.support.v4.app.Fragment {
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//
//        View rootView = inflater.inflate(R.layout.fragment_contact__list__main, container, false);
//        DatabaseHandler c_db = new DatabaseHandler(getActivity());
//        All_c_list = new ArrayAdapter<Contact>(getActivity(), android.R.layout.simple_expandable_list_item_1, c_db.getContactListName());
//        contact_lv.setAdapter(All_c_list);
//
//        return rootView;
//
//    }
//
//    private void loadData() {
//
//        DatabaseHandler c_db = new DatabaseHandler(getActivity());
//        All_c_list = new ArrayAdapter<Contact>(getActivity(), android.R.layout.simple_expandable_list_item_1, c_db.getContactListName());
//        contact_lv.setAdapter(All_c_list);
//    }
//}

//public class Contact_List_All extends android.support.v4.app.Fragment {
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        View rootView = inflater.inflate(R.layout.fragment_contact__list__main, container, false);
//
//        return rootView;
//
//    }
//}

    //
