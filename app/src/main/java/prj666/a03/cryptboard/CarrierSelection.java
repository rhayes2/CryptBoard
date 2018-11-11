package prj666.a03.cryptboard;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import prj666.a03.cryptboard.TestSteg.BitmapEncoder;
import prj666.a03.cryptboard.TestSteg.BitmapHelper;
import prj666.a03.cryptboard.TestSteg.Steg;

public class CarrierSelection extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;
    public static final int CAPTURE_IMAGE = 2;
    String currentPhotoPath;

    Button accept, camera, gallery;
    TextView confirm;
    ImageView carrierImage;
    Spinner SpinnerContact;
    Bitmap SelectedImg;
    String msgForEncryption;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrier_selection);

        Intent parentIntent = getIntent();
        int carrierSelectMode = parentIntent.getIntExtra("MODE", 0);
        msgForEncryption = parentIntent.getStringExtra("Msg");
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
        SpinnerContact = findViewById(R.id.spinner);

        confirm.setText(R.string.carrier_confirmation);
        camera.setText(R.string.carrier_camera_recapture);
        accept.setText(R.string.OK);
        gallery.setText(R.string.carrier_reselect_from_storage);


        List<String> list = frontEndHelper.getInstance().getNames();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerContact.setAdapter(dataAdapter);


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
                try {
                    System.out.println(Steg.withInput(SelectedImg).decode().intoString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Intent cameraIntent = new Intent();
                //cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, CAPTURE_IMAGE);
            }
        });

        accept.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getApplicationContext(), CryptBoard.class);
                setResult(RESULT_OK);
                startActivity(intent);*/
                String x  = (String) SpinnerContact.getSelectedItem();
                String Ecrypted = null;
                try {
                    Ecrypted = frontEndHelper.getInstance().sendMsg(x,msgForEncryption);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                }
                Bitmap tocrypts = SelectedImg;
                Bitmap crypts = null;

                try {
                    crypts = Steg.withInput(tocrypts).encode(Ecrypted).intoBitmap();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                saveToInternalStorage(crypts, "EncodedMsg");
                carrierImage.setImageBitmap(crypts);


                // TODO ADD STEGtoIMG
                finishAffinity();
            }
        });


        if (carrierSelectMode == 1){ //Camera Capture
            Toast.makeText(this, "Launching Camera", Toast.LENGTH_SHORT).show();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex){
                System.err.println(ex);
            }
            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".provider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, CAPTURE_IMAGE);
            } else {
                Toast.makeText(this, "Criss, il n'y a pas un ficher de photo", Toast.LENGTH_SHORT).show();
            }

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
            Uri selectedImage = null;
            if(requestCode == PICK_IMAGE){
                selectedImage = data.getData();
                try {
                    SelectedImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ;
                carrierImage.setImageURI(selectedImage);
                //confirm.setText(selectedImage.toString());
                //Toast.makeText(this, "IMAGE SELECTED! " + selectedImage.toString(), Toast.LENGTH_SHORT).show();
            } else if (requestCode == CAPTURE_IMAGE){
                Bundle extras = data.getExtras();
                Bitmap capturedImage = (Bitmap) extras.get("data");
                carrierImage.setImageBitmap(capturedImage);

                /*Uri capturedImage = data.getData();
                carrierImage.setImageURI(capturedImage);
                Toast.makeText(this, "IMAGE CAPTURED! " + capturedImage.toString(), Toast.LENGTH_SHORT).show();*/
            }
            //Intent intent = new Intent();
            //intent.setData(RESULT_OK, selectedImage);

        } else if (resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Capture Cancelled", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveToInternalStorage(Bitmap bitmapImage, String name){

        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        Integer counter = 0;
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), name+".PNG"); // the File to save ,
        try {
            fOut = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}