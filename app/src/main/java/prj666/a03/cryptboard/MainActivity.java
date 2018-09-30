package prj666.a03.cryptboard;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import prj666.a03.ContactBase.Contact;
import prj666.a03.ContactBase.DatabaseHandler;
import prj666.a03.RSAStrings.RSAStrings;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        try {
            runTests();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean runTests() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeySpecException {
        /// TEST BLOCK FOR ACCESSING KEYS

        System.out.println("---------------------------------------------------------------------");
        System.out.println("Testing Load Database:");
        DatabaseHandler db;
        db = DatabaseHandler.getInstance(this);
        if(db.getDatabaseName().length()>1){System.out.println("Loaded Database: "+ db.getDatabaseName());}
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Getting a Key for Storage: ");
        KeyPair MitchKeys, LarryKeys;
        MitchKeys = null;
        LarryKeys = null;
        String x;
        String x2;
        try {
            LarryKeys = RSAStrings.getKeys();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }finally {
            x = new String(Base64.getEncoder().encode(LarryKeys.getPublic().getEncoded()));
            System.out.println("Larry Public Key Generated: "+x);
            x2 = new String(Base64.getEncoder().encode(LarryKeys.getPrivate().getEncoded()));
            System.out.println("Larry Private Key Generated: "+x2);
        }
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Get All Contacts: ");
        List<Contact> clist =  db.getContactList();
        System.out.println(clist);
        System.out.println("---------------------------------------------------------------------");
        //System.out.println("FOR THE SAKE OF THE TEST, KEYS ARE STORED INCORRECTLY");
        //System.out.println("Inserting Contacts: ");
        //Contact tmp = new Contact("Mitch Headburg",Boolean.TRUE,x);
        //Contact tmp2 = new Contact("Larry David",Boolean.FALSE,x2);
        //System.out.println("Inserting Contact: "+ tmp.toString());
        //System.out.println("Inserting Contact: "+ tmp2.toString());
        //db.insertContact(tmp);
        //db.insertContact(tmp2);
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Get All Contacts: ");
        clist =  db.getContactList();
        System.out.println(clist);
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Loading byte[] array X509EncodedKey");
        KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(clist.get(0).getKeyFile()));
        RSAPublicKey pubKey = (RSAPublicKey)rsaKeyFac.generatePublic(keySpec);
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Test Encryption String Mitch -> David");
        byte [] encrypted = RSAStrings.encryptString(pubKey,"Test String HeLLo wOrLd!!");
        byte [] base64Encyption = Base64.getEncoder().encode(encrypted);
        System.out.println("Encrypted Msg: Test String HeLLo wOrLd!!\n" + new String(base64Encyption));
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Loading byte[] array PKCS8Encoded key");
        String testkey = clist.get(1).getKeyFile();
        PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(testkey));
        RSAPrivateKey privKey = (RSAPrivateKey)rsaKeyFac.generatePrivate(encodedKeySpec);;
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Test Decryption String Larry Pubkey");

        encrypted = Base64.getDecoder().decode(base64Encyption);
        byte[] decrypted = new byte[0];
        try {
            decrypted = RSAStrings.decryptString(privKey, encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Orignal msg: "+ new String(decrypted));






        return  true;
    }


}



