package prj666.a03.cryptboard;

import android.content.Context;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import prj666.a03.cryptboard.TestSteg.Steg;

public class CarrierSelection extends AppCompatActivity {
 /*------------------------------------------------------------------
        Carrier Selection Class
        -----------------------
        - Activity in which user selects media to act as a carrier for encrypted message
        - Starts Encoding
        - Saves File
      ------------------------------------------------------------------

        1. User Selects Media to be Encoded
        2. User Selects Public Key to Encode With
        3. On Encode Press, Encode and Save Image
        4. On SelectFromStorage Press, Reselect another Image
        5. On Done Press, Save Contact to Database (future keystorage?)

      ------------------------------------------------------------------
        P.O.I

        L123-128:  Load Selection Spinner

        L214-231:  Starting worker thread to encode 
        
      ----------------------------------------------------------------- 
    */

    public static final int PICK_IMAGE = 1;
    public static final int CAPTURE_IMAGE = 2;
    public final String APP_TAG = "CryptBoard";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final int COMPRESSION_LEVEL = 50;

    public String photoFileName = "photo.jpg";
    File photoFile;

    String currentPhotoPath;

    Button accept, camera, gallery;
    TextView confirm;
    ImageView carrierImage;
    Spinner SpinnerContact;
    Bitmap SelectedImg;
    String msgForEncryption;
    AutoCompleteTextView SearchContacts;
    ProgressDialog save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrier_selection);

        Intent parentIntent = getIntent();
        int carrierSelectMode = parentIntent.getIntExtra("MODE", 0);
        msgForEncryption = parentIntent.getStringExtra("Msg");
        Intent intent = new Intent();

        accept = findViewById(R.id.acceptCarrier);
        camera = findViewById(R.id.recaptureCamera);
        gallery = findViewById(R.id.reselectionFromStorage);
        confirm = findViewById(R.id.carrierConfirmation);
        carrierImage = findViewById(R.id.carrierImagekey);
        SpinnerContact = findViewById(R.id.spinner);
        SearchContacts = findViewById(R.id.ContactSearchBarCarrier);

        confirm.setText(R.string.carrier_confirmation);

        List<String> list = frontEndHelper.getInstance().getNames();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SearchContacts.setAdapter(dataAdapter);
        SpinnerContact.setAdapter(dataAdapter);

        SpinnerContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedContact = adapterView.getItemAtPosition(i).toString();
                if (selectedContact.length() > 0){
                    accept.setEnabled(true);
                    accept.setBackgroundColor(getResources().getColor(R.color.colourConfirmation));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                accept.setEnabled(false);
                accept.setBackgroundColor(getResources().getColor(R.color.colourRejection));
            }
        });


        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };

        if(!checkAndRequestPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
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

                Intent cameraIntent = new Intent();
                cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                photoFile = getPhotoFileUri(photoFileName);
                Uri fileProvider = FileProvider.getUriForFile(CarrierSelection.this, "prj666.a03.cryptboard.provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                if(cameraIntent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }

            }
        });

        accept.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                // This is a check to see if we're actively processing a worker thread
                if(frontEndHelper.getInstance().getWorker1()!=null){
                    //System.out.println("thread active?-- "+ frontEndHelper.getInstance().getWorker1().isAlive());
                    if(frontEndHelper.getInstance().getWorker1().isAlive()){
                        try {
                            frontEndHelper.getInstance().getWorker1().join();
                            //System.out.println("thread active22?-- "+ frontEndHelper.getInstance().getWorker1().isAlive());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }


                final String x = (String) SpinnerContact.getSelectedItem();
                final Bitmap tocrypts = SelectedImg;
                final String finmsg = msgForEncryption;
                Thread PerformEncoding = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        String inThreadContact = x;
                        String Ecrypted = null;
                        Bitmap inThreadToEncode = tocrypts;
                        String inThreadMsg = finmsg;
                        Bitmap crypts = null;
                        try {
                            Ecrypted = frontEndHelper.getInstance().sendMsg(inThreadContact, inThreadMsg);
                            crypts = Steg.withInput(inThreadToEncode).encode(Ecrypted).intoBitmap();
                            saveToInternalStorage(crypts, "EncodedMsg");
                        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();}
                        }
                    });
                //frontEndHelper.getInstance().setThread(PerformEncoding);
                save = new ProgressDialog(CarrierSelection.this);
                save.setMessage("Processing, Please Wait...");
                save.setTitle("Saving Image");
                save.setIndeterminate(false);
                save.setCancelable(false);
                save.show();
                PerformEncoding.start();
                // TODO ADD STEGtoIMG
                finishAffinity();
            }
        });

    }

    private  boolean checkAndRequestPermissions(Context context, String... permissions) {

        if (context != null && permissions != null){
            for (String permission : permissions){
                if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
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
                carrierImage.setImageURI(selectedImage);

            } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                takenImage.compress(Bitmap.CompressFormat.PNG, COMPRESSION_LEVEL, stream);

                byte[] byteArray = stream.toByteArray();
                Bitmap compressedImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                Uri capturedImage = getImageUri(CarrierSelection.this, compressedImage);

                carrierImage.setImageURI(capturedImage);

                SelectedImg = compressedImage;

            }

        } else if (resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Capture Cancelled", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveToInternalStorage(Bitmap bitmapImage, String name){

        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        Integer counter = 0;
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), name+".PNG"); // the File to save ,
        try {
            fOut = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream

            carrierImage.post(new Runnable() {
                public void run() {
                    save.dismiss();
                }
            });

            MediaScannerConnection.scanFile(this.getApplicationContext(),
                    new String[] { name + ".PNG" }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private Uri getImageUri(Context actContext, Bitmap capImage){
        Bitmap output = Bitmap.createScaledBitmap(capImage , capImage.getWidth(), capImage.getHeight(), true);
        String path = MediaStore.Images.Media.insertImage(actContext.getContentResolver(), output, "Capture", null);
        return Uri.parse(path);
    }

    public File getPhotoFileUri(String filename){
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        File file = new File(mediaStorageDir.getPath() + File.separator + filename);

        return file;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (save != null) {
            save.dismiss();
            save = null;
        }
    }
}
