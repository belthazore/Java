import java.text.SimpleDateFormat;
import java.util.*;

public class Nikacakes {

    private StringBuilder table = new StringBuilder();

    public String getTablePage() {


        String body =
                "<body>" +
                        "<p align=\"right\"><input onclick=\"location.href='/project/home?action=exit'\" value=\"Выйти\" type=\"button\"></p>" +

                        "  <div class=\"login-page\">" +
                        "    <div class=\"form\"  float=\"left\">" +
                        "    <a name=\"find_form\"></a>" + // якорь
                        "        <b>Find order</b>" +
                        "        <form action=\"/project/home#find_form\" style=\"width: 300px;margin: auto;\">" +
                        "            <br><br>" +
                        "            <input name=\"order_id\" placeholder=\"order_id\" type=\"text\">" +
                        "            <br><br>" +
                        "         <input name=\"action\" value=\"find\" type=\"hidden\">" +
                        "            <button type=\"submit\" style=\"border-radius: 3px;\">Find</button>" +
                        "        </form>" +
                        getTable() +
                        " </div>" +
                        "</body>";


        return body;
    }


    String getTable(){
        String tableStart, tableEnd;
        tableStart =
                "  <table width=\"70%\" border=\"1\" cellpadding=\"4\">\n" +
                "   <caption>Заказы нах</caption>\n" +
                "   <br>\n" +
                "   <tr>\n" +
                "    <td>#</td>\n" +
                "    <th>Phone</th>\n" +
                "    <th>Created</th>\n" +
                "    <th>Release</th>\n" +
                "    <th>Order body</th>\n" +
                "    <th>Comment</th>\n" +
                "    <th>Status</th>\n";
        tableEnd =
                "   </tr>\n" +
                "  </table>";
        table.append(tableStart);

        Set<String[]> set = new HashSet<>();
        set.add(new String[]{"1", "0951303226", "12/11 17:15", "28/11 10:00", "Test-2 pcs.", "Comment", "Created"});
        set.add(new String[]{"2", "0951303227", "13/11 17:15", "28/11 10:00", "Test-2 pcs.", "Comment", "Created"});
        set.add(new String[]{"3", "0951303228", "14/11 17:15", "28/11 10:00", "Test-2 pcs.", "Comment", "Created"});
        set.add(new String[]{"4", "0951303229", "15/11 17:15", "28/11 10:00", "Test-2 pcs.", "Comment", "Created"});

        for(String[] stArr:set){
            table.append( getTableRowFromArray(stArr) );
        }

        table.append(tableEnd);
        return table.toString();
    }

    /*
    Табличная строка из массива
    Example: getTableRowFromArray(new String[]{"1", "0951303225", "12/11 17:15", "28/11 10:00", "Test-2 pcs.", "Comment", "Created"})
    */
    private String getTableRowFromArray(String[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        for (String cursor : arr) {
            sb.append("<td>");
            sb.append(cursor);
            sb.append("</td>");
        }
//        sb.append("<td><button_mini type=\"submit\" style=\"border-radius: 3px;\">Выполнен</button_mini></td>"); // добавим кнопку для закрытия заказа
        sb.append("<td><button_mini type=\"submit\" style=\"border-radius: 3px;\">Do Relese</button_mini></td>"); // добавим кнопку для закрытия заказа
        sb.append("</tr>");
        return sb.toString();
    }




    // Example: 06-07-2018 22:38:28
    private String getDateTimeNow() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }


    // Switched OFF
    private String doAction(String action, String[] params) {
/*
        // params[] в зависимости от action
        // find:   ["1"]
        // create: ["Merenga: 10 pcs", "0671234567"]
        String result = null;
        jdbcPostgres psql = null;
        try {
            psql = new jdbcPostgres();

            switch (action) {
                case "find":
                    ResultSet rs = psql.executeSelect("SELECT * FROM orders WHERE id='" + Integer.parseInt(params[0]) + "'"); //todo inejction
                    rs.next();
                    String row[]= new String[7];
                    // id, phone, start_date, end_date, order_content, comment, status
                    row[0] = String.valueOf(rs.getInt("id"));
                    row[1] = rs.getString("phone");
                    Date start = new Date(rs.getLong("start_date")*1000); // Создание даты как "new Date();"
                    Date end =   new Date(rs.getLong("end_date")  *1000); // Создание даты как "new Date();"
                    row[2] = new SimpleDateFormat("dd/MM HH:mm").format(start);
                    row[3] = new SimpleDateFormat("dd/MM HH:mm").format(end);
                    row[4] = rs.getString("order_content");
                    row[5] = rs.getString("comment");
                    row[6] = rs.getString("status");
                    result = getTableRowFromArray(row);
                    break;
                case "create":
                    String QueryInsert =
                            "INSERT INTO orders" +
                                    "  (order_id, product, client_phone)" +
                                    "VALUES" +
                                    "  (nextval('orders_order_id_seq'), '" + params[0] + "', '" + params[1] + "')"; // RUTRNUNG
                    psql.executeSelect(QueryInsert); // добавим новую запись. Всегда true, т.к. order_id всегда уникален
                    ResultSet rs2 = psql.executeSelect("SELECT last_value FROM orders_order_id_seq"); // получим последний order_id
                    rs2.next();
                    result = rs2.getString(1) + " | " + params[0] + " | " + params[1];
                    break;
                default:
                    throw new Exception("Wrong action");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (psql != null)
                psql.closeConnection();
        }
        return result;
        */
        return "";
    }
}