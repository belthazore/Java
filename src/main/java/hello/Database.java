package hello;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

public class Database {

    static List<Contact> searchByRegExp(String regExp) {
        List<Contact> contactsList = new ArrayList<Contact>();

        jdbcPostgres psql = new jdbcPostgres();
        ResultSet rs = psql.select("SELECT * FROM contacts;", new String[]{});
        int foundCount = 0; // Количество найденных
//        String regExpr = "[^A].*";
        out.println("Received 'regExpr': " + regExp);
        Pattern pattern = Pattern.compile(regExp);
        Matcher match;
        String clientName;
        int clientId;
        try {
            if (rs != null) {
                while (rs.next() && foundCount < 10) {
                    clientName = rs.getString(2);
                    clientId = rs.getInt(1);
                    match = pattern.matcher(clientName);
                    if (!match.find()) { // Паттерн совпал, найдено имя. todo: переделать наоборот как в ТЗ (!)
                        out.println("Id: " + clientId + ". Name: " + clientName);
                        contactsList.add(new Contact(clientId, clientName));
                        foundCount++;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contactsList;
    }
}