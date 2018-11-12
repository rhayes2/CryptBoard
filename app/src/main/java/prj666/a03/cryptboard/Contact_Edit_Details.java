package prj666.a03.cryptboard;

        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Color;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.google.zxing.WriterException;

        import prj666.a03.cryptboard.ContactBase.Contact;

public class Contact_Edit_Details extends AppCompatActivity {
    Contact tmp ;
    TextView name;
    String oldname;
    TextView date;
    ImageView img;
    Button createNewKeyButton;
    Button deleteKeyButton;
    Button scanNewQRButton;
    Button saveEditButton;
    Boolean changed = false;

    TextView PrivKeyIndicator;
    TextView PubKeyIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tmp = (Contact) getIntent().getSerializableExtra("contactToEdit");
        setContentView(R.layout.activity_contact__details__edit);
        name = (TextView)findViewById(R.id.Contact_Detail_Edit_name);
        oldname = name.getText().toString();
        date = (TextView)findViewById(R.id.Contact_key_date_Edit);
        img = (ImageView)findViewById(R.id.Contact_Detail_Edit_Picture);

        createNewKeyButton = (Button) findViewById(R.id.CreateNewPrivateKeyButton);
        deleteKeyButton = (Button) findViewById(R.id.DeletePrivateKeyButton);
        scanNewQRButton = (Button) findViewById(R.id.ScanNewQRButton);
        saveEditButton = (Button) findViewById(R.id.SaveEditButton);

        PrivKeyIndicator = (TextView) findViewById(R.id.contact_private_key_Edit);
        PubKeyIndicator = (TextView) findViewById(R.id.Contact_public_key_Edit);

        if(tmp.getContactPubKey()!=null){
            try {
                img.setImageBitmap(QRCodeGenerator.encodeAsBitmap(tmp.getContactPubKey()));
            } catch (WriterException e) {
                e.printStackTrace();
            }}

        name.setText(tmp.getName());
        date.setText(tmp.getDateCreated());
        refreshPrivKeyInfo();
        refreshPubKeyInfo();

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
                //  1) add confirmation dialog
                //  2) add success toast


                tmp.setMyPrivKey(null);
                refreshPrivKeyInfo();
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
                Intent returnIntent = new Intent();
                returnIntent.putExtra("changeStatus", changed);
                returnIntent.putExtra("updatedContactInfo", tmp);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("changeStatus", changed);
        returnIntent.putExtra("updatedContactInfo", tmp);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void refreshPrivKeyInfo(){
        if (tmp.getMyPrivKey() != null) {
            deleteKeyButton.setVisibility(View.VISIBLE);
            PrivKeyIndicator.setTextColor(Color.GREEN);
        }
        else {
            deleteKeyButton.setVisibility(View.INVISIBLE);
            PrivKeyIndicator.setTextColor(Color.RED);
        }
    }

    public void refreshPubKeyInfo(){
        if (tmp.getContactPubKey() != null) {
            PubKeyIndicator.setTextColor(Color.GREEN);
        }
        else {
            PubKeyIndicator.setTextColor(Color.RED);
        }
    }
}
