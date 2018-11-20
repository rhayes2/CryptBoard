package prj666.a03.cryptboard;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import prj666.a03.cryptboard.ContactBase.Contact;
import prj666.a03.cryptboard.RSAStrings.RSAStrings;

public class AddContact extends AppCompatActivity {

    Button keyExchange, doneButton;
    EditText contactName;
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

        keyExchange = findViewById(R.id.keyButton);
        doneButton = findViewById(R.id.doneButton);
        contactName = findViewById(R.id.contactName);
        favFlag = findViewById(R.id.checkBox);

        LoadKeys.start();



        keyExchange.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                control = frontEndHelper.getInstance();
                /**try {
                    keyset = true;
                    control.createNewContact(AddContact.this, contactName.getText().toString());

                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } **/
                try {
                    LoadKeys.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tmpContact.setName(contactName.getText().toString());
                tmpContact.setFavourite(favFlag.isChecked());
                control.saveContact(tmpContact);
                keyset = true;
                Intent intent = new Intent(AddContact.this,KeyExchange.class);
                intent.putExtra("Key",mypub);
                startActivityForResult(intent,1);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!keyset) {Toast.makeText(AddContact.this, "You have not Created or Exchanged Keys", Toast.LENGTH_SHORT).show();}
                else{
                    finish();}
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        // Check which request it is that we're responding to
        //Toast.makeText(this, "INMAIN!@@@@ resultCode: " + resultCode,Toast.LENGTH_LONG).show();

        if (resultCode == 1){
            keyset=true;
            tmpContact.setContactPubKey(resultIntent.getStringExtra("KEY"));
            control.updateContact(tmpContact);

        }
    }

}
