package prj666.a03.cryptboard;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import prj666.a03.cryptboard.ContactBase.Contact;

public class Contact_List_Quick extends  ListFragment {
    List<String> Contact_Quick_list = null;
    int[] Cont_Image = { R.drawable.star , R.drawable.star ,R.drawable.star , R.drawable.star , R.drawable.star , R.drawable.star , R.drawable.star }; //TODO better implementation


    ArrayList<HashMap<String, String>> data=new ArrayList<HashMap<String,String>>();
    SimpleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loadContact();

        HashMap<String, String> hMap=new HashMap<String, String>();

        //FILL
        for(int i = 0; i< Contact_Quick_list.size(); i++)
        {
            hMap = new HashMap<String, String>();
            hMap.put("Name", Contact_Quick_list.get(i));
            hMap.put("Image", Integer.toString(Cont_Image[0])); //TODO FIX ICON??

            data.add(hMap);

        }

        //key names to pass
        String[] from={"Name","Image"};

        //object that will get the names and images
        int[] to={R.id.Contact_Name,R.id.Contact_Image};

        //array(list later on) adapter
        //Contact_Quick_list = frontEndHelper.getInstance().getNames();
        //ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(getActivity(), android.R.layout.simple_list_item_1, frontEndHelper.getInstance().getContacts());
        setListAdapter(adapter);
        adapter=new SimpleAdapter(getActivity(), data, R.layout.one_contact, from, to);
        setListAdapter(adapter);


        return super.onCreateView(inflater, container, savedInstanceState);



    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Contact_Quick_list = frontEndHelper.getInstance().getNames();
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                // TODO Auto-generated method stub
                //debugging to see if the data is ok
                Toast.makeText(getActivity(), data.get(pos).get("Name"), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void loadContact(){
        if(Contact_Quick_list!=frontEndHelper.getInstance().getNames())Contact_Quick_list=frontEndHelper.getInstance().getNames(); //TODO CleanThis?
    }
}