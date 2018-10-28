package prj666.a03.cryptboard;

import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class AddContact extends AppCompatActivity {

    Button keyExchange, doneButton;
    EditText contactName;
    frontEndHelper control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        keyExchange = findViewById(R.id.keyButton);
        doneButton = findViewById(R.id.doneButton);
        contactName = findViewById(R.id.contactName);

        keyExchange.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                control = frontEndHelper.getInstance();
                System.out.println(control.getContacts());
                //Intent intent = new Intent(AddContact.this, KeyExchange.class);
                //startActivity(intent);
                try {
                    control.createNewContact(AddContact.this, contactName.getText().toString());
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });

//        doneButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent NewContact = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(NewContact, 100);
////                onBackPressed();
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        // Check which request it is that we're responding to
        Toast.makeText(this, "INMAIN!@@@@ resultCode: " + resultCode,Toast.LENGTH_LONG).show();

        if (resultCode == 1){
            control.saveLastKey(resultIntent.getStringExtra("KEY"));
        }
    }

}
