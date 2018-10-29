package prj666.a03.cryptboard.ContactBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static DatabaseHandler sInstance;
    public DatabaseHandler(Context context) {
        super(context, "ContactList", null, 13);
    }

    public static synchronized DatabaseHandler getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase db1){
        db1.execSQL("CREATE TABLE contactLog (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, favourite INTEGER(1) NOT NULL DEFAULT 0, myPrivKey TEXT, contactPubKey TEXT, dateCreated TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db2, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db2.execSQL("DROP TABLE IF EXISTS contactLog");
            onCreate(db2);
        }
    }

    public void insertContact(Contact newContact) {
        SQLiteDatabase db3 = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", newContact.getName());
        values.put("favourite", newContact.isFavouriteInt());
        values.put("myPrivKey", newContact.getMyPrivKey());
        values.put("contactPubKey", newContact.getContactPubKey());
        values.put("dateCreated", newContact.getDateCreated());
        db3.insert("contactLog", null, values);
        db3.close();
    }

    public void updateContact(Contact theContact) {
        SQLiteDatabase db4 = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", theContact.getName());
        values.put("favourite", theContact.isFavouriteInt());
        values.put("myPrivKey", theContact.getMyPrivKey());
        values.put("contactPubKey", theContact.getContactPubKey());
        values.put("dateCreated", theContact.getDateCreated());
        db4.update("contactLog", values, "name = ?", new String[] {theContact.getName()});
        db4.close();
    }

    public void deleteContact(Contact theContact) {
        SQLiteDatabase db5 = this.getWritableDatabase();
        String delQuery = "DELETE FROM contactLog WHERE name = '" + theContact.getName() + "'";
        db5.execSQL(delQuery);
        db5.close();
    }

    public Contact getContact(String contactname){
        String selectQuery = "SELECT * FROM contactLog WHERE name = '" + contactname + "'";
        SQLiteDatabase db7 = this.getReadableDatabase();
        Cursor cursor = db7.rawQuery(selectQuery, null);
        Contact contact;
        if(cursor.moveToFirst()) {
            String name = cursor.getString(1);
            Boolean favourite = ( 1 == cursor.getInt(2) );
            String myPrivKey = cursor.getString(3);
            String contactPubKey = cursor.getString(4);
            String date = cursor.getString(5);
            contact = new Contact(name, favourite, myPrivKey, contactPubKey, date );
        }
        else {
            contact = new Contact();
        }

        cursor.close();
        db7.close();
        return contact;

    }

    public List<Contact> getContactList() {
        List<Contact> contactList = new ArrayList<>();
        String selectQuery = "SELECT * FROM contactLog";
        SQLiteDatabase db6 = this.getReadableDatabase();
        Cursor cursor = db6.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1);
                Boolean favourite = ( 1 == cursor.getInt(2) );
                String myPrivKey = cursor.getString(3);
                String contactPubKey = cursor.getString(4);
                String date = cursor.getString(5);
                Contact currentContact = new Contact(name, favourite, myPrivKey, contactPubKey, date);
                contactList.add(currentContact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db6.close();
        return contactList;
    }

    


    //will get all the contact names from the database
    public List<String> getContactListNames() {
        List<String> names = new ArrayList<>();
        String selectQuery = "SELECT * FROM contactLog ORDER BY name";
        SQLiteDatabase db6 = this.getReadableDatabase();
        Cursor cursor = db6.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(1));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db6.close();
        return names;
    }

    //Will get all the names of those contact where favourite is true
    public List<String> getContactListNamesFav() {
        List<String> names = new ArrayList<>();
        String selectQuery = "SELECT * FROM contactLog WHERE favourite = 1 ORDER BY name";
        SQLiteDatabase db6 = this.getReadableDatabase();
        Cursor cursor = db6.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(1));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db6.close();
        return names;
    }

    //will get the last 3 contact added to the contact list
    public List<String> getContactListNamesLast() {
        List<String> names = new ArrayList<>();
        String selectQuery = "SELECT * FROM contactLog ORDER BY id desc LIMIT 3; ";
        SQLiteDatabase db6 = this.getReadableDatabase();
        Cursor cursor = db6.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(1));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db6.close();
        return names;
    }
}
