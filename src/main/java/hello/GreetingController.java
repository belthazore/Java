package hello;

import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

@RestController
public class GreetingController {

    private final AtomicLong counter = new AtomicLong();
    private List<Greeting> contactsList = new ArrayList<Greeting>();


    //REST
    @RequestMapping(value = "/contacts", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    MyList rest(@RequestParam(value = "nameFilter", required = true) String regExpr) {
        try {
            contactsList.add(new Greeting(regExpr));
        } catch (Exception e) { e.printStackTrace(); }

        // ---------------------------------------------------------------
        ResultSet rs = jdbcPostgres.select("SELECT * FROM contacts;", new String[]{});
        int foundCount = 0; // Количество найденных
//        String regExpr = "[^A].*";
        out.println("Received 'regExpr': " + regExpr);
        Pattern pattern = Pattern.compile(regExpr);
        Matcher match;
        try {
            if (rs!=null) {
                while (rs.next() && foundCount < 10) {
                    String clientName = rs.getString(2);
                    match = pattern.matcher(clientName);
                    if (match.find()) { // Паттерн совпал, найдено имя todo переделать наоборот (ТЗ)
                        out.println("true: " + clientName);
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

    @RequestMapping(value = "/rest", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Obj contacts(@RequestParam(value = "nameFilter", required = true) String name) {
        contactsList.add( new Greeting(name) );

        return new Obj(contactsList, contactsList.size());
    }

    @RequestMapping(value = "/fillDb", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Obj fillDb(@RequestParam(value = "count", required = true) int count) {
        contactsList.add(new Greeting(String.valueOf(count)));

        FillDb.start(count);
        return new Obj(contactsList, contactsList.size());
    }
}