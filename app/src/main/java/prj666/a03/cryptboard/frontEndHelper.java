package prj666.a03.cryptboard;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

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
    public void createNewContact(){

        ////  CREATE THE KEY FOR SHARE
        KeyPair tmp = null;
        try {
            tmp = RSAStrings.getKeys();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        ///SAVE PERSONAL KEY
        String tmpPub = new String(Base64.getEncoder().encode(tmp.getPublic().getEncoded()));
        String tmpPriv = new String(Base64.getEncoder().encode(tmp.getPrivate().getEncoded()));

        //// Get Field Values --- add the field gets
        String name =  "Test Bankers";
        boolean favourite = true;

        //// Load Into a Contact var
        Contact toSave = new Contact(name,favourite,tmpPriv,tmpPub);

        //// Insert Into Table

        db.insertContact(toSave);}

        //MoreCOMINGSOON



}
