package prj666.a03.cryptboard;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.google.zxing.WriterException;

        import prj666.a03.cryptboard.ContactBase.Contact;

public class Contact_Edit_Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Contact tmp = (Contact) getIntent().getSerializableExtra("contact");
        setContentView(R.layout.activity_contact__details__edit);
        TextView name = (TextView)findViewById(R.id.Contact_Deatil_Edit_name);
        TextView date = (TextView)findViewById(R.id.Contact_key_date_Edit);
        ImageView img = (ImageView)findViewById(R.id.Contact_Detail_Edit_Picture);
        Button createNewKeyButton = (Button) findViewById(R.id.CreateNewPrivateKeyButton);
        Button deleteKeyButton = (Button) findViewById(R.id.DeletePrivateKeyButton);
        Button scanNewQRButton = (Button) findViewById(R.id.ScanNewQRButton);
        Button saveEditButton = (Button) findViewById(R.id.SaveEditButton);

        if(tmp.getContactPubKey()!=null){
            try {
                img.setImageBitmap(QRCodeGenerator.encodeAsBitmap(tmp.getContactPubKey()));
            } catch (WriterException e) {
                e.printStackTrace();
            }}

        name.setText(tmp.getName());
        date.setText(tmp.getDateCreated());

        createNewKeyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create new private key code
                // TO DO:
                //       1)  Generate Key
                //       2)  Save key to database
                //       3)  toast?
            }
        });

        deleteKeyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // delete private key code
                // TO DO:
                //
                //     1) "ARE YOU SURE" type popup
                //     2) Delete private key
                //     3) Toast
            }
        });


        scanNewQRButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Scan New QR code code
                // TO DO:
                //       1) launch activity to scan QR
                //       2) replace existing QR
                //       2) return to edit page
            }
        });

        saveEditButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // save edits code
                // TO DO:
                //       1) commit changes to db
                //       3) refresh current activity
            }
        });
    }
}
