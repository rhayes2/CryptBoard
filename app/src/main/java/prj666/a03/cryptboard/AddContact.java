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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class AddContact extends AppCompatActivity {

    Button keyExchange, doneButton;
    EditText contactName;
    TextView keyConfirmation;
    frontEndHelper control;

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
                } else {
                    Toast.makeText(AddContact.this, "Contact Name Cannot be Blank", Toast.LENGTH_LONG).show();
                }

            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        if (resultCode == 1){
            control.saveLastKey(resultIntent.getStringExtra("KEY"));
            keyConfirmation.setText(R.string.keyExchange_positive);

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
