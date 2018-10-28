package prj666.a03.cryptboard;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Contact_List_Adapter extends ArrayAdapter<String> {

    private Activity activity;
    String[] contact_list;

    public Contact_List_Adapter(@NonNull Activity context, String[] list) {
        super(context, R.layout.activity_contact__list__main, list);

        contact_list = list;
        activity = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.one_contact,null,false);
        TextView Name = rowView.findViewById(R.id.Contact_Name);
        Name.setText(contact_list[position]);



        return rowView;

    }
}