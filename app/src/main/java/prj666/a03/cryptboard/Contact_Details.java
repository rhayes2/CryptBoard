package prj666.a03.cryptboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import prj666.a03.cryptboard.ContactBase.Contact;

public class Contact_Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Contact tmp = (Contact) getIntent().getSerializableExtra("contact");
        setContentView(R.layout.activity_contact__details);
        TextView name = (TextView)findViewById(R.id.Contact_Deatil_name);
        TextView date = (TextView)findViewById(R.id.Contact_key_date);
        ImageView img = (ImageView)findViewById(R.id.Contact_Detail_Picture);

        if(tmp.getContactPubKey()!=null){
        try {
            img.setImageBitmap(QRCodeGenerator.encodeAsBitmap(tmp.getContactPubKey()));
        } catch (WriterException e) {
            e.printStackTrace();
        }}
        name.setText(tmp.getName());
        date.setText(tmp.getDateCreated());

    }
}
