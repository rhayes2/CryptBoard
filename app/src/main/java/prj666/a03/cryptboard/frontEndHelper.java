package prj666.a03.cryptboard;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import prj666.a03.ContactBase.Contact;
import prj666.a03.ContactBase.DatabaseHandler;
import prj666.a03.RSAStrings.RSAStrings;


public class frontEndHelper {

    public static DatabaseHandler db;

    public frontEndHelper(DatabaseHandler dbpass) {
        db = dbpass;
    }

    public List<Contact> getContacts(){
     return db.getContactList();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNewContact() throws InvalidKeySpecException, NoSuchAlgorithmException {
        ////  CREATE THE KEY FOR SHARE
        KeyPair tmp = null;
        try {
            tmp = RSAStrings.getKeys();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        ///SAVE Keys to String
        String tmpPub = new String(Base64.getEncoder().encode(tmp.getPublic().getEncoded()));
        String tmpPriv = new String(Base64.getEncoder().encode(tmp.getPrivate().getEncoded()));

        //// Get Field Values --- add the field gets
        String name =  "Test Bankers";
        boolean favourite = true;

        //// Load Into a Contact var
        Contact toSave = new Contact(name,favourite,tmpPriv, null);


        displayKey(tmpPub);
        // GET THEIR PublicKey before Saving
        String contactPubKey = scanKey();
        // Calls to Function scanKey()

        // pull key into contact
        toSave.setContactPubKey(contactPubKey);
        displayKey(tmpPub);
        // Save Contact
        db.insertContact(toSave);
        // Display QR
    }

        //MoreCOMINGSOON

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String sendMsg(String name, String msg) throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        Contact tmp = db.getContact(name);
        if (tmp.getName().length()<2)System.out.println("Not Loaded");
        System.out.println("Loading byte[] array X509EncodedKey for Contact: "+ name);
        KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(tmp.getContactPubKey()));
        RSAPublicKey pubKey = (RSAPublicKey)rsaKeyFac.generatePublic(keySpec);
        String encryptMsg = new String(Base64.getEncoder().encode(RSAStrings.encryptString(pubKey,msg)));
        return encryptMsg;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String scanKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String stringKey = null;
        System.out.println("Testing Scan Key");
        // scan key here
        // CALLS TO QR STUFF

        // assuming string

        // **Note, We should implement a keycheck to insure scan went properly
        return stringKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void displayKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // GenKey and display to UI
        // CALLS TO QR STUFF

        System.out.println("Testing Display Key");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decryptMsg(String name, String msg) throws Exception {
        Contact tmp = db.getContact(name);
        if (tmp.getName().length()<2)System.out.println("Not Loaded");
        KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(tmp.getMyPrivKey()));
        RSAPrivateKey privKey = (RSAPrivateKey)rsaKeyFac.generatePrivate(encodedKeySpec);;
        byte [] decrypted = RSAStrings.decryptString(privKey,Base64.getDecoder().decode(msg.getBytes()));
        return new String(decrypted);
    }

}


