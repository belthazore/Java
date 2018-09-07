package hello;

import java.util.List;

// Обертка для JSON
/*
{
    "list":[
        {
            "id":1, "val":".."},
            {"id":1, "val":".."}
    ]

}
 */
public class Obj{


    private final String result; // Комментарий результата ("Founded more than 10 contacts")
    private final List<Contact> contacts;

    public Obj(String result, List<Contact> contacts) {
        this.result = result;
        this.contacts = contacts;
    }

    public String getResult() {
        return result;
    }

    public List<Contact> getContacts() {
        return contacts;
    }
}
