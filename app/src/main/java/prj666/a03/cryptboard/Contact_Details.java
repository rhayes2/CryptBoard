package prj666.a03.cryptboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import prj666.a03.cryptboard.ContactBase.Contact;


public class Contact_Details extends AppCompatActivity {

    /*------------------------------------------------------------------
        Contact Details Class
        -----------------
        - Displays Information about he contact
        - Provides a way to edit Contacts
        - Provides a way to delete Contacts

      ------------------------------------------------------------------

        1. Get Serilizable Extra - Contact
        2. Populate fields with inforation
        3. On press start Edit Contact Activity
        4. On press Delete Contact

      ------------------------------------------------------------------
        P.O.I

      ----------------------------------------------------------------- 
    */

    Contact tmp;
    TextView name;
    TextView date;
    Button editContactButton;
    Button doneContactButton;

    MenuItem fav;

    frontEndHelper frontEndH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact__details);

        Toolbar tool = findViewById(R.id.contact_details_toolbar);
        setSupportActionBar(tool);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        tmp = (Contact) getIntent().getSerializableExtra("contact");

        name = findViewById(R.id.contact_Detail_name);
        date = findViewById(R.id.keyGenerationDate);
        editContactButton = findViewById(R.id.editContactButton);
        doneContactButton = findViewById(R.id.doneContactButton);


        name.setText(tmp.getName());
        date.setText(tmp.getDateCreated());

        editContactButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent editContactIntent = new Intent(Contact_Details.this, Contact_Edit_Details.class);
                editContactIntent.putExtra("contactToEdit", tmp);
                startActivityForResult(editContactIntent, 1);
            }
        });

        doneContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                if ((boolean) data.getSerializableExtra("changedStatus") == true) {
                    if ((boolean) data.getSerializableExtra("deleteContact") == true) {
                        frontEndH = frontEndHelper.getInstance();
                        frontEndH.deleteContact(tmp);
                        finish();
                    }

                    tmp = (Contact) data.getSerializableExtra("updatedContactInfo");

                    frontEndH = frontEndHelper.getInstance();
                    frontEndH.updateName(tmp, name.getText().toString());
                    frontEndH.updateContact(tmp);

                    name.setText(tmp.getName());
                    date.setText(tmp.getDateCreated());

                    Toast.makeText(this, "Contact Updated", Toast.LENGTH_SHORT).show();
                }
                else if ((boolean) data.getSerializableExtra("changedStatus") == false){
                    Toast.makeText(this, "Contact Unchanged", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Error processing change", Toast.LENGTH_SHORT).show();
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "RESULT_CANCELED", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Err: result null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_action_bar, menu);

        fav = menu.findItem(R.id.action_favourite);
        if(tmp.isFavourite() == true){
            fav.setIcon(R.drawable.favourite_selected_24dp);
        }
        else{
            fav.setIcon(R.drawable.favourite_unselected_24dp);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case R.id.action_favourite:
                if (tmp.isFavourite() == true){
                    fav.setIcon(R.drawable.favourite_unselected_24dp);
                    tmp.setFavourite(false);
                } else if (tmp.isFavourite() == false){
                    fav.setIcon(R.drawable.favourite_selected_24dp);
                    tmp.setFavourite(true);
                } else {
                    Toast.makeText(this, "ActBar error", Toast.LENGTH_SHORT).show();
                }
                frontEndH = frontEndHelper.getInstance();
                frontEndH.updateContact(tmp);
                return true;


            case R.id.action_delete_contact:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.contactDeletion_confirm).setTitle(R.string.contactDeletion_title);

                builder.setPositiveButton(R.string.response_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        frontEndH = frontEndHelper.getInstance();
                        frontEndH.deleteContact(tmp);
                        finish();
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
