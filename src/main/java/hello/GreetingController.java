package hello;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.*;

import static java.lang.System.out;

@RestController
public class GreetingController {

    private final AtomicLong counter = new AtomicLong();
    private List<Greeting> contactsList = new ArrayList<Greeting>();


    //REST
    @RequestMapping(value = "/rest", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody MyList rest(@RequestParam(value="nameFilter", required=true) String nameFilter) {
        try {
            contactsList.add(new Greeting(nameFilter));
        } catch (Exception e) { e.printStackTrace(); }

        // ---------------------------------------------------------------
        ResultSet rs = jdbcPostgres.select("SELECT * FROM contacts;", new String[]{});
        int foundCount = 0; // Количество найденных
//        String regExpr = "[^A].*";
        String regExpr = nameFilter;
        Pattern pattern = Pattern.compile(regExpr);
        Matcher match;
        try {
            if (rs!=null) {
                while (rs.next() && foundCount < 10) {
                    String clientName = rs.getString(2);
                    match = pattern.matcher(clientName);
                    if (!match.find()) { // Паттерн совпал, найдено имя todo переделать наоборот (ТЗ)
                        System.out.println("true: " + clientName);
                        contactsList.add(new Greeting(clientName));
                        foundCount++;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new MyList(0L, contactsList);
    }

    @RequestMapping(value = "/hello/contacts", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Obj contacts(@RequestParam(value="nameFilter", required=true) String name) {
        contactsList.add( new Greeting(name) );

        System.out.println(Arrays.toString(contactsList.toArray()));
        return new Obj(contactsList, contactsList.size());
    }
}