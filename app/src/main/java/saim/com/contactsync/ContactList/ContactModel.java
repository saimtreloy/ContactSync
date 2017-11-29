package saim.com.contactsync.ContactList;

import android.support.annotation.NonNull;

/**
 * Created by NREL on 11/28/17.
 */

public class ContactModel {
    String name, number;

    public ContactModel(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

}
