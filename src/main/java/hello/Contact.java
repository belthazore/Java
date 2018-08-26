package hello;

/*
{
    "id": 1,
    "name":"name", // Фамилия Имя клиента
}
*/

public class Contact {
    private int id;
    private String name;

    Contact(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}