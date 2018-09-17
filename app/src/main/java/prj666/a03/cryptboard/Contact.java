package prj666.a03.cryptboard;
/*
NOTE: THIS CLASS IS NOT COMPLETE
SUBJECT TO CHANGE

 */

public class Contact {
    private String name;
    private boolean favourite;
    // keyFile contains the name of the file containing public and private key information .it is not the key itself. file extraction code must be implemented.
    private String keyFile;
    private int id;
    private String date;

    // this variable refers to whether or not the contact has been saved in the database for the first time
    //private boolean committed = false;

    // used for NEW contacts being created for the first time
    public Contact(String name, String keyFile) {
        this.name = name;
        this.favourite = false;
        this.keyFile = keyFile;
    }

    // used for fetching EXISTING contacts from the database
    public Contact(String name, Boolean favourite, String keyFile, int id, String date) {
        this.name = name;
        this.favourite = favourite;
        this.keyFile = keyFile;
        this.id = id;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getKeyFile() {
        return keyFile;
    }

    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }

    // more methods to be added:
    // getContactKey
    // saveContact (to database)
    // updateContact( in database)
    // deleteContact (in database)
}
