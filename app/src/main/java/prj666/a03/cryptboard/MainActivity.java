package prj666.a03.cryptboard;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import prj666.a03.cryptboard.ContactBase.DatabaseHandler;
import prj666.a03.cryptboard.RSAStrings.RSAStrings;
import prj666.a03.cryptboard.Tests.tester;

public class MainActivity extends AppCompatActivity {

    /*-----------------------------------------------
    *  MainActivity
    * -----------------------------------------------
    *  - Initializes the database on first run
    *  - Provides users with installation instructions
    *  - Provides users with quick access to their device's keyboard settings to enable CryptBoard
    * */

    DatabaseHandler db;
    frontEndHelper control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));



        db = DatabaseHandler.getInstance(this);
        control = new frontEndHelper(db);
        Button settingsButton, keyboardButton;
        TextView step1;


        settingsButton = findViewById(R.id.settingsButton);
        keyboardButton = findViewById(R.id.keyboardSelect);
        step1 = findViewById(R.id.textView2);
        String brand = Build.BRAND;


        tester x = new tester();
        /**
        try {
            Test.runTests();
        } catch (Exception e) {
            e.printStackTrace();
        }

        **/
        System.out.println(control.getNamesAll());
        if(control.getNamesAll().size()==0){
            KeyPair tmp = null;
            try {
                tmp = RSAStrings.getKeys();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }


            String mypub = android.util.Base64.encodeToString(tmp.getPublic().getEncoded(),0);
            String tmpPriv = android.util.Base64.encodeToString(tmp.getPrivate().getEncoded(),0);
            boolean favourite = false;
        }

        /*
        * The method for activating third party keyboards can differ between device manufacturers,
        * depending on any changes that they make to Android's stock UI. This code serves different
        * installation instructions based on known differences in manufacturers' implementations
        * */

        //Samsung
        if (brand.contains("sam")){
            step1.setText(R.string.installation_samsung_1);
            //tutorial.setImageDrawable();
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
                }
            });

            keyboardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                    imeManager.showInputMethodPicker();
                }
            });

        }

        //Stock
        else{
            step1.setText(R.string.installation_standard_1);
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
                }
            });

            keyboardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                    imeManager.showInputMethodPicker();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }
}


