package hello;

import java.util.List;

public class Body {

    private List<Contact> contacts;
    private long count;

    Body(List<Contact> contacts, long count) {
        this.contacts = contacts;
        this.count = count;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public long getCount() {
        return count;
    }

}