package prj666.a03.cryptboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
        Button showQRButton = (Button) findViewById(R.id.showQRButton);
        Button editContactButton = (Button) findViewById(R.id.editContactButton);
        Button deleteContactButton = (Button) findViewById(R.id.deleteContactButton);

        if(tmp.getContactPubKey()!=null){
        try {
            img.setImageBitmap(QRCodeGenerator.encodeAsBitmap(tmp.getContactPubKey()));
        } catch (WriterException e) {
            e.printStackTrace();
        }}
        name.setText(tmp.getName());
        date.setText(tmp.getDateCreated());


        showQRButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // show QR code
                // TO DO:
                //       1) launch activity to show QR
            }
        });

        editContactButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // edit contact activity
                // TO DO:
                //       1) launch edit contact activity
                //       2) make sure this activity refreshes when coming back
            }
        });

        deleteContactButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // delete contact code
                // TO DO:
                //       1) "are you sure" type popup
                //       2) delete contact from database
                //       3) return to previous activity
            }
        });

    }
}
