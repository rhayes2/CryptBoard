package prj666.a03.cryptboard;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import prj666.a03.cryptboard.ContactBase.Contact;
import prj666.a03.cryptboard.ContactBase.DatabaseHandler;
import prj666.a03.cryptboard.TestSteg.Steg;
import prj666.a03.cryptboard.TestSteg.Test;

public class MainActivity extends AppCompatActivity {

    DatabaseHandler db;
    frontEndHelper control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = DatabaseHandler.getInstance(this);
        control = new frontEndHelper(db,this);
        Button settingsButton, keyboardButton;
        TextView step1;
        ImageView tutorial;


        settingsButton = findViewById(R.id.settingsButton);
        keyboardButton = findViewById(R.id.keyboardSelect);
        step1 = findViewById(R.id.textView2);
        //tutorial = findViewById(R.id.tutorialImage);
        String brand = Build.BRAND;


        try {
            Test.runTests();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (brand.contains("sam")){ //Handles Samsung's protectionist bs
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
        else{ //For everyone that isn't a Samsung
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
}


