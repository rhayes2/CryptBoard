package prj666.a03.cryptboard;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.support.v7.app.ActionBar;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.Toolbar;
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

        import java.security.KeyPair;
        import java.security.NoSuchAlgorithmException;

        import prj666.a03.cryptboard.ContactBase.Contact;
        import prj666.a03.cryptboard.RSAStrings.RSAStrings;

public class Contact_Edit_Details extends AppCompatActivity {

/*   -----------------------------------------------------------------
       Contact_Edit_Details Class
        -----------------------
        - Makes Contact Text Editable
        - Saves New Contact Information
      ------------------------------------------------------------------

        1. User Edits Contact information
        2. Onpress Saves Contact

      ------------------------------------------------------------------
        P.O.I
        
      ----------------------------------------------------------------- 
    */
    
    Contact tmp ;
    EditText name;
    TextView date;
    Button createNewKeyButton;
    Button saveEditButton;

    Boolean changed = false;
    Boolean deleted = false;

    MenuItem favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tmp = (Contact) getIntent().getSerializableExtra("contactToEdit");
        setContentView(R.layout.activity_contact__details__edit);
        Toolbar myToolbar = findViewById(R.id.contact_details_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        Intent fallback = new Intent();
        fallback.putExtra("contact", tmp);
        setResult(RESULT_CANCELED, fallback);


        name = findViewById(R.id.contact_name);
        date = findViewById(R.id.keyGenerationDate);

        createNewKeyButton = findViewById(R.id.keyRefresh);
        saveEditButton = findViewById(R.id.saveContactButton);

        date.setText(tmp.getDateCreated());

        name.setText(tmp.getName());
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 1){
                    saveEditButton.setBackgroundColor(getResources().getColor(R.color.colourRejection));
                    saveEditButton.setEnabled(false);

                } else {
                    saveEditButton.setBackgroundColor(getResources().getColor(R.color.colourConfirmation));
                    saveEditButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        createNewKeyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create new private key code
                // TODO: Send keys to QR Activity
                //       1)add success toast
                KeyPair keytmp = null;
                try {
                    keytmp = RSAStrings.getKeys();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                String tmpPriv = android.util.Base64.encodeToString(keytmp.getPrivate().getEncoded(),0);
                tmp.setMyPrivKey(tmpPriv);
                changed = true;
            }
        });

        saveEditButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (name.getText().toString() != tmp.getName()){
                    changed = true;
                    tmp.setName(name.getText().toString());
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("changedStatus", changed);
                returnIntent.putExtra("updatedContactInfo", tmp);
                returnIntent.putExtra("deleteContact", deleted);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_action_bar, menu);

        favourite = menu.findItem(R.id.action_favourite);
        if(tmp.isFavourite() == true){
            favourite.setIcon(R.drawable.favourite_selected_24dp);
        }
        else {
            favourite.setIcon(R.drawable.favourite_unselected_24dp);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case R.id.action_favourite:
                if(tmp.isFavourite() == true){
                    favourite.setIcon(R.drawable.favourite_unselected_24dp);
                    tmp.setFavourite(false);
                } else if (tmp.isFavourite() == false){
                    favourite.setIcon(R.drawable.favourite_selected_24dp);
                    tmp.setFavourite(true);
                } else {
                    Toast.makeText(this, "Error: nullFav", Toast.LENGTH_SHORT).show();
                }
                return true;

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_delete_contact:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.contactDeletion_confirm)
                        .setTitle(R.string.contactDeletion_title);

                builder.setPositiveButton(R.string.response_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changed = true;
                        deleted = true;

                        Intent deletionIntent = new Intent();
                        deletionIntent.putExtra("changedStatus", changed);
                        deletionIntent.putExtra("updatedContactInfo", tmp);
                        deletionIntent.putExtra("deleteContact", deleted);
                        setResult(Activity.RESULT_OK, deletionIntent);


                    }
                });

                builder.setNegativeButton(R.string.response_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
