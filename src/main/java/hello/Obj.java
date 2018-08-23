package hello;

import java.util.List;

public class Obj {

    private List<Greeting> contacts;
    private long count;

    Obj(List<Greeting> contacts, long count) {
        this.contacts = contacts;
        this.count = count;
    }

    public List<Greeting> getContacts() {
        return contacts;
    }
    public long getCount() {
        return count;
    }

}