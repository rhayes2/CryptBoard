package prj666.a03.cryptboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


import prj666.a03.cryptboard.ContactBase.Contact;
import prj666.a03.cryptboard.ContactBase.DatabaseHandler;
import prj666.a03.cryptboard.RSAStrings.RSAStrings;
import prj666.a03.cryptboard.TestSteg.Steg;

public class frontEndHelper {

    public static DatabaseHandler db;
    Activity MainAct;
    public static String scanTarget;
    private static frontEndHelper sInstance;
    private List<Contact> Clist;
    private static Thread Worker1;

    public frontEndHelper(DatabaseHandler dbpass, Activity tmp) {
        db = dbpass;
        MainAct = tmp;
        sInstance = this;
        LoaderList();

    }

    public static frontEndHelper getInstance(){return sInstance;}

    public List<Contact> getContacts(){
        return Clist;
    }

    public void LoaderList(){Clist = db.getContactList();}

    /**public void load_List(){
        Thread PerformLoadContacts = new Thread(new Runnable(){
            @Override
            public void run() {
                Clist = db.getContactList();
            }
        });
        PerformLoadContacts.start();
    } **/

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String sendMsg(String name, String msg) throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        Contact tmp = db.getContact(name);
        if (tmp.getName().length()<2)System.out.println("Not Loaded");
        System.out.println("Loading byte[] array X509EncodedKey for Contact: "+ name);
        KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(android.util.Base64.decode(tmp.getContactPubKey(),0));
        RSAPublicKey pubKey = (RSAPublicKey)rsaKeyFac.generatePublic(keySpec);
        String encryptMsg = android.util.Base64.encodeToString(RSAStrings.encryptString(pubKey,msg),0);
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
    public void displayKey(String key, Activity act) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // GenKey and display to UI
        // CALLS TO QR STUFF

        Intent intent = new Intent(act,KeyExchange.class);
        intent.putExtra("Key",key);
        act.startActivityForResult(intent,1);
        System.out.println("Testing Display Key");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decryptMsg(String name, String msg) throws Exception {
        Contact tmp = db.getContact(name);
        if (tmp.getName().length()<2)System.out.println("Not Loaded");
        KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(android.util.Base64.decode(tmp.getMyPrivKey(),0));
        RSAPrivateKey privKey = (RSAPrivateKey)rsaKeyFac.generatePrivate(encodedKeySpec);
        byte [] decrypted = RSAStrings.decryptString(privKey,android.util.Base64.decode(msg.getBytes(),0));
        return new String(decrypted);
    }

    public void saveContact(Contact toSave){
        db.insertContact(toSave);
        LoaderList();
    }

    public void deleteContact(Contact contactToDelete) {
        db.deleteContact(contactToDelete);
    }

    public void updateContact(Contact contactToUpdate) {db.updateContact(contactToUpdate);}

    public void updateName(Contact newContactInfo, String oldName){ db.updateName(newContactInfo, oldName);}

    public Contact getContact(String Name){return db.getContact(Name);}


    public List<String> getNames(){
        Clist = db.getContactList();
        List<String> names = new ArrayList<String>();
        for(Contact x : Clist){
            names.add(x.getName());
        }
        return names;
    }


    public List<String> getNamesAll() {
        Clist = db.getContactList();
        List<String> names = new ArrayList<String>();
        for (Contact x : Clist) {
            names.add(x.getName());
        }
        return names;
    }

    public List<String> getNamesFav() {
        Clist = db.getContactList();
        List<String> names = new ArrayList<String>();
        for (Contact x : Clist) {
            if (x.isFavourite() == true) {
                names.add(x.getName());
            }
        }
        return names;
    }

    public List<String> getNamesLast() {
        Clist = db.getContactList();
        //Collections.reverse(Clist);
        int co = 0;

        List<String> names = new ArrayList<String>();
        for (Contact x : Clist) {
            if (co < 7) {
                names.add(x.getName());
                co++;
            }
        }
        return names;
    }


    public RSAPublicKey getContactsPublicKey(String name){
        Contact tmp = db.getContact(name);
        KeyFactory rsaKeyFac = null;
        RSAPublicKey pubKey = null;

        try {
            rsaKeyFac = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(android.util.Base64.decode(tmp.getContactPubKey(),0));
            pubKey = (RSAPublicKey)rsaKeyFac.generatePublic(keySpec);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return pubKey;
    }
    
    public Contact getPos(int pos){return Clist.get(pos);}

    public void setThread(Thread x){Worker1 = x;}
    public Thread getWorker1(){return Worker1;}


}
