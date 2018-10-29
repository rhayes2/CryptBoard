package prj666.a03.cryptboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CarrierSelection extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;
    public static final int CAPTURE_IMAGE = 2;

    Button accept, camera, gallery;
    TextView confirm;
    ImageView carrierImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrier_selection);

        Intent parentIntent = getIntent();
        int carrierSelectMode = parentIntent.getIntExtra("MODE", 0);
        Intent intent = new Intent();
        /*
        * TODO: Permissions in manifest, check for valid permissions
        * TODO: Display selected image, offer options to reject and reselect
        * TODO: Handle both of the start activities from the keyboard
        * */

        accept = findViewById(R.id.acceptCarrier);
        camera = findViewById(R.id.recaptureCamera);
        gallery = findViewById(R.id.reselectionFromStorage);
        confirm = findViewById(R.id.carrierConfirmation);
        carrierImage = findViewById(R.id.carrierImage);

        confirm.setText(R.string.carrier_confirmation);
        camera.setText(R.string.carrier_camera_recapture);
        accept.setText(R.string.OK);
        gallery.setText(R.string.carrier_reselect_from_storage);

        //Permissions check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Carrier"), PICK_IMAGE);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent();
                cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE);
            }
        });


        if (carrierSelectMode == 1){ //Camera Capture
            Toast.makeText(this, "Launching Camera", Toast.LENGTH_SHORT).show();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAPTURE_IMAGE);
        } else if (carrierSelectMode == 2){ //Gallery Selection
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Carrier"), PICK_IMAGE);
        } else { //Oops
            Toast.makeText(this, "Criss, quelque chose est casse", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE){
                Toast.makeText(this, "IMAGE SELECTED!", Toast.LENGTH_SHORT).show();
                Uri selectedImage = data.getData();
                carrierImage.setImageURI(selectedImage);
            } else if (requestCode == CAPTURE_IMAGE){
                Toast.makeText(this, "IMAGE CAPTURED!", Toast.LENGTH_SHORT).show();
                Bundle extras = data.getExtras();
                Bitmap capturedImage = (Bitmap) extras.get("data");
                carrierImage.setImageBitmap(capturedImage);
            }
        } else if (resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Capture Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
