package prj666.a03.cryptboard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

public class KeyExchange extends AppCompatActivity {

    Button doneButton, scanButton;
    ImageView qrDisplay;
    String scanResult, publicKey;
    //TextView textContent;
    Bitmap generatedQR;
    Boolean keyScanned = false;

    //@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_exchange);

        final Activity activity = this;

        doneButton = findViewById(R.id.doneButton);
        //scanButton = findViewById(R.id.scanQR);
        qrDisplay = findViewById(R.id.qrDisplay);
        //textContent = findViewById(R.id.textView);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }

        //publicKey = getIntent().getStringExtra("KEY");
        Intent intent = getIntent();
        publicKey = intent.getStringExtra("Key");
        //Converts Strings into QRCodes
        try {
            generatedQR = QRCodeGenerator.encodeAsBitmap(publicKey);
            qrDisplay.setImageBitmap(generatedQR);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        /*scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (keyScanned == true){
                    if (scanResult != null){
                        Intent result = new Intent();
                        result.putExtra("KEY", scanResult);
                        setResult(1, result);
                    } else {                                       // 1 Option result and 1 Flavor result code
                        setResult(404);
                    }
                    finish();
                }
                else {
                    IntentIntegrator integrator = new IntentIntegrator(activity);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    integrator.setPrompt("Scan Contact QR");
                    integrator.setCameraId(0);
                    integrator.initiateScan();
                }

            }
        });
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                //textContent.setText(result.getContents());
                scanResult = result.getContents();
                keyScanned = true;
                doneButton.setText(R.string.save_contact);
                doneButton.setBackgroundColor(getResources().getColor(R.color.colourConfirmation));

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
                setResult(404);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

/*
Zxing QR scanning and generation library (open source)
https://stackoverflow.com/questions/29159104/how-to-integrate-zxing-barcode-scanner-without-installing-the-actual-zxing-app
https://opensource.google.com/projects/zxing
* */
