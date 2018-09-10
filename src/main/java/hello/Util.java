package hello;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;

/*
    Класс-обработчик данных из БД (получаемых через jdbcPostgres класс)
    Функции:
    - найти контакты не соответствующие маске 'regExp'
    - найти контакт по 'id'
*/

class Util {

    private static jdbcPostgres psql;

    // Найти контакты не соответствующие маске 'regExp'
    static List<Contact> findByRegExp(String regExp) {
        psql = new jdbcPostgres();
        List<Contact> contactsList = new ArrayList<>();

        ResultSet rs = psql.select("SELECT * FROM contacts", new String[]{});
        int foundCount = 0; // Количество найденных контактов
        out.println("Received 'regExpr': " + regExp);
        Pattern pattern = Pattern.compile(regExp);
        Matcher match;

        String clientName;
        int clientId;

        try {
            if (rs != null) {
                while (rs.next() && foundCount < 10) { // не более чем 10 контактов иначе долго "думает" заполняя Body ответа на 3 млн-ах записей
                    clientId = rs.getInt("id");
                    clientName = rs.getString("name");
                    match = pattern.matcher(clientName);
                    if (!match.find()) { // Контакт не соответствует регулярке (ТЗ)
                        out.println("Id: " + clientId + ". Name: " + clientName);
                        contactsList.add(new Contact(clientId, clientName));
                        foundCount++;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            psql.close();
        }

        return contactsList;
    }


    // Найти контакт по 'id'
    static List<Contact> findById(int clientId) {
        psql = new jdbcPostgres();
        String clientName;
        out.println("Received 'id': " + clientId);
        List<Contact> contactsList = new ArrayList<>();
        ResultSet rs = psql.selectById("SELECT * FROM contacts WHERE id = ?", clientId);

        try {
            if (rs != null && rs.next()) {
                clientName = rs.getString("name");
                contactsList.add(new Contact(clientId, clientName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            psql.close();
        }

        return contactsList;
    }

}