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

public class MyList {

    private final long key;
    private final List<Greeting> valList;

    public MyList(long key, List<Greeting> valList) {
        this.key = key;
        this.valList = valList;
    }

    public long getKey() {
        return key;
    }

    public List<Greeting> getValList() {
        return valList;
    }
}
