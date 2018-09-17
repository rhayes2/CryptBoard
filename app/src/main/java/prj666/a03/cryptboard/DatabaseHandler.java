package prj666.a03.cryptboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


/*
NOTE: THIS CLASS IS NOT COMPLETE
SUBJECT TO CHANGE SOON

 */


public class DatabaseHandler extends SQLiteOpenHelper {
    public DatabaseHandler(Context context) {
        super(context, "ContactList", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db1){
        db1.execSQL("CREATE TABLE contactLog (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, favourite INTEGER(1) NOT NULL DEFAULT 0, keyfile TEXT, dataCreated TEXT NOT NULL DEFAULT CURRENT_DATE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db2, int oldVersion, int newVersion) {
        db2.execSQL("DROP TABLE IF EXISTS contactLog");
        onCreate(db2);
    }

    public void insertContact(Contact newContact) {
        SQLiteDatabase db3 = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", newContact.getName());
        values.put("favourite", newContact.isFavourite());
        values.put("keyfile", newContact.getKeyFile());
        db3.insert("contactLog", null, values);
        db3.close();
    }

    public void updateItem(Contact theContact) {
        SQLiteDatabase db4 = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", theContact.getName());
        values.put("favourite", theContact.isFavourite());
        values.put("keyfile", theContact.getKeyFile());
        db4.update("contactLog", values, "name = ?", new String[] {theContact.getName()});
    }

    public void deleteItem(Contact theContact) {
        SQLiteDatabase db5 = this.getWritableDatabase();
        String delQuery = "DELETE FROM contactLog WHERE name = '" + theContact.getName() + "'";
        db5.execSQL(delQuery);
        db5.close();
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
                String keyFile = cursor.getString(3);
                int id = cursor.getInt(0);
                String date = cursor.getString(4);

                Contact currentContact = new Contact(name, favourite, keyFile, id, date);
                contactList.add(currentContact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db6.close();
        return contactList;
    }


}
