package hello;

/*
{
    "id": 1,
    "name":"name", // Фамилия Имя клиента
}
*/

public class Greeting {

    private final String name;


    Greeting(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}