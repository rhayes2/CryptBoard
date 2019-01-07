package prj666.a03.cryptboard;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import prj666.a03.cryptboard.ContactBase.Contact;
import prj666.a03.cryptboard.RSAStrings.RSAStrings;

public class AddContact extends AppCompatActivity {

    /*------------------------------------------------------------------
        Add Contact Class
        -----------------
        - Creates and Saves Contact to Database
        - Starts KeyExchange Activity

      ------------------------------------------------------------------

        1. Create RSAKey Pair for Crontact
        2. Load Key into New Contact and Wait KeyExchange
        3. On KeyExchange Press, Save Name, Start Exchange for Result
        4. On Result Set NewContact's Public Key
        5. On Done Press, Save Contact to Database (future keystorage?)

      ------------------------------------------------------------------
        P.O.I

        L62-80:  Worker Thread to Create Keys, We need a key but only need
                 it before saving / Display, So the join is done before starting
                 the Exchange activity.

        L141:     Worker Thread Join before requiring our key

        L158-159: Save this Contact and Finish the activity
      ----------------------------------------------------------------- 
    */

    Button keyExchange, doneButton;
    EditText contactName;
    TextView keyConfirmation;
    frontEndHelper control;
    CheckBox favFlag;
    public boolean keyset = false;
    public Contact tmpContact = null;
    public String mypub = null;

    Thread LoadKeys = new Thread(new Runnable(){
        @Override
        public void run() {
            KeyPair tmp = null;
            try {
                tmp = RSAStrings.getKeys();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }


            mypub = android.util.Base64.encodeToString(tmp.getPublic().getEncoded(),0);
            String tmpPriv = android.util.Base64.encodeToString(tmp.getPrivate().getEncoded(),0);
            boolean favourite = false;

            //// Load Into a Contact var
            tmpContact = new Contact("404",favourite,tmpPriv, null);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Toolbar toolbar = findViewById(R.id.contact_add_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        keyExchange = findViewById(R.id.keyButton);
        doneButton = findViewById(R.id.saveNewContactButton);
        contactName = findViewById(R.id.contactName);
        keyConfirmation = findViewById(R.id.keyConfirmation);

        LoadKeys.start();

        contactName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 1 &&
                        (keyConfirmation.getText().toString() ==
                                getResources().getString(R.string.keyExchange_positive))){
                    doneButton.setText(R.string.save_contact);
                    doneButton.setBackgroundColor(getResources().getColor(R.color.colourConfirmation));
                    doneButton.setEnabled(true);
                } else {
                    doneButton.setText("Error");
                    doneButton.setBackgroundColor(getResources().getColor(R.color.colourRejection));
                    doneButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }


        });

        keyExchange.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (contactName.getText().length() > 0){
                    control = frontEndHelper.getInstance();
                    try {
                        LoadKeys.join();                           // Joining the LoadKeys worker to ensure we have keys
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tmpContact.setName(contactName.getText().toString());
                    keyset = true;
                    Intent intent = new Intent(AddContact.this,KeyExchange.class);
                    intent.putExtra("Key",mypub);
                    startActivityForResult(intent,1);
                } else {
                    Toast.makeText(AddContact.this, "Contact Name Cannot be Blank", Toast.LENGTH_LONG).show();
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!keyset) {Toast.makeText(AddContact.this, "You have not Created or Exchanged Keys", Toast.LENGTH_SHORT).show();}
                else{
                    control.saveContact(tmpContact);  // Let's Save this Created Contact
                    finish();}
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        if (resultCode == 1){
            keyset=true;
            tmpContact.setContactPubKey(resultIntent.getStringExtra("KEY"));
            keyConfirmation.setText(getResources().getString(R.string.keyExchange_positive));
            if (contactName.getText().length() > 0) {
                doneButton.setText(R.string.save_contact);
                doneButton.setBackgroundColor(getResources().getColor(R.color.colourConfirmation));
                doneButton.setEnabled(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
