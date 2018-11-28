import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;


public class Nikacakes extends HttpServlet {

    private Map<String, ArrayList<String>> SEARCH_RESULTS = new HashMap<>();


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // todo: RAD
//        if (Cookies.cookiesAndSavingTime.isEmpty() || !Cookies.haveValidCookie(request)) { // Не валидна или ее вообще нет
//            response.sendRedirect("/project/login");
//        }


        String action, orderId, product, phoneNumber; // проверенные параметры (!"" | "")
        String actionNeedCheck, orderIdNeedCheck, productNeedCheck, phoneNumberNeedCheck; // параметры для проверки, могут быть == null


        actionNeedCheck = request.getParameter("action");
        orderIdNeedCheck = request.getParameter("order_id");

        productNeedCheck = request.getParameter("product");
        phoneNumberNeedCheck = request.getParameter("phone_number");

        // если так не делать, то в кейсе 'action==null' блок
        // 'if ( action!=null & (action.equals("find") | action.equals("create") ){}'
        // дает NullPointerException =(
        action = (actionNeedCheck == null ? "" : actionNeedCheck);
        orderId = (orderIdNeedCheck == null ? "" : orderIdNeedCheck);

        product = (productNeedCheck == null ? "" : productNeedCheck);
        phoneNumber = (phoneNumberNeedCheck == null ? "" : phoneNumberNeedCheck);


        // Операция выхода из кабинета
        // todo забиндить под 'a href'
        if (action.equals("exit")) {
            Cookies.deleteFromDbAndHashM(request);
            HttpSession session = request.getSession();
            session.invalidate();
            response.sendRedirect("/project/");
            return;
        }


//        Cookies.updateSavingTimeIfNeed(request);

        // Запишем успешную авторизацию
        Log.transition("Home. Logined with cookie success", request);


        StringBuilder PAGE = new StringBuilder();
        response.setContentType("text/html;charset=utf-8");

        String head =
                "<head>" +
                        "  <title>Java Tomcat test server | Login</title>" +
                        getStyle() +
                        "</head>";


        // выполнение действий и сохранение их результатов в историю HashMap
        // TODO: сохранять по логину пользователя историю индивидуально
        if (action.equals("find") | action.equals("create")) {
            String actionResult = doAction(action, (action.equals("find") ? new String[]{orderId} : new String[]{product, phoneNumber}));
            if (actionResult != null && SEARCH_RESULTS.containsKey(action)) {
                SEARCH_RESULTS.get(action).add("[" + getDateTimeNow() + "]   " + actionResult); // добавим результат запроса в историю

            } else {//еще нет такого 'action', добавим в HashM. Прим. '"find", ArrayList<S> {"07-07-2018 14:47:39 4 | Macaroni: 45 pcs | 0671234567"}'
                ArrayList<String> tempArrList = new ArrayList<>();
                tempArrList.add("[" + getDateTimeNow() + "]   " + actionResult);
                SEARCH_RESULTS.put(action, tempArrList);
            }
        }


        String body =
                "<body>" +

                        "<div>" +
                        "    <ul class=\"horizontal\">" +
                        "        <li><a href=\"#home\"  >Главная</a></li>" +
                        "        <li><a href=\"#change\">Изменить</a></li>" +
                        "        <li><a href=\"#search\">Поиск   </a></li>" +
                        "        <li style=\"float: right;\"><a href=\"#\">Выйти</a></li>" +
                        "" +
                        "        <!-- 		  <li style=\"float:right\"><a class=\"active\" href=\"#about\">О Нас</a></li> -->" +
                        "    </ul>" +
                        "</div>" +
                        "" +
                        "<br><br><br>" +
                        "<div class=\"block\">" +
                        "    <a name=\"home\">" +
                        "        <b class=\"title\">Мои заказы</b>" +
                        "        <br><br><br>" +
                        "        <table width=\"100%\" border=\"1\" cellpadding=\"4\">" +
                        "            <tbody>" +
                        "            <tr>" +
                        "                <th>№</th>" +
                        "                <th>Телефон</th>" +
                        "                <th>Создан</th>" +
                        "                <th>Отдать</th>" +
                        "                <th>Тело заказа</th>" +
                        "                <th>Коментарий</th>" +
                        "                <th>Статус</th>" +
                        "                <th align=\"right\">Дейс</th>" +
                        "                <th align=\"left\">твие</th>" +
                        "            </tr>" +
                        doAction("findAllCreated", new String[]{}) +
                        "            </tbody>" +
                        "        </table>" +
                        "        <br><br><br>" +
                        "</div>" +
                        "" +
                        "" +
                        "" +
                        "<div class=\"block\">" +
                        "    <a name=\"add\"/>" +
                        "    <b class=\"title\">Добавить</b>" +
                        "    <br><br><br>" +
                        "    <table width=\"100%\" border=\"1\" cellpadding=\"4\">" +
                        "        <tbody>" +
                        "        <tr>" +
                        "            <th>№</th>" +
                        "            <th>Телефон</th>" +
                        "            <th>Создан</th>" +
                        "            <th>Отдать</th>" +
                        "            <th>Тело заказа</th>" +
                        "            <th>Коментарий</th>" +
                        "            <th>Статус</th>" +
                        "            <td align=\"center\" rowspan=\"2\"><input onclick=\"location.href=/project/n\" value=\"Добавить\" type=\"button\" class=\"button2\"></td>" +
                        "        </tr>" +
                        "        <tr>" +
                        "            <td><input type=\"text\" name=\"id\" 		 value=\"1\">			 </td>" +
                        "            <td><input type=\"text\" name=\"phone\" 	 value=\"0672112508\"> </td>" +
                        "            <td><input type=\"text\" name=\"start_date\" value=\"5.12\">		 </td>" +
                        "            <td><input type=\"text\" name=\"end_date\" 	 value=\"15.12\">		 </td>" +
                        "            <td><textarea type=\"text\" name=\"body\" 	 value=\"Зефир манго-5, Капкейки-14, макаронс-8\" align=\"left\"></textarea></td>" +
                        "            <td><textarea type=\"text\" name=\"comment\" value=\"Коментарий\" align=\"left\"></textarea></td>" +
                        "            <td><input type=\"text\" name=\"status\" 	 value=\"Создан\">			 </td>" +
                        "" +
                        "        </tr>" +
                        "        </tbody>" +
                        "    </table>" +
                        "</div>" +
                        "" +
                        "" +
                        "" +
                        "<div class=\"block\">" +
                        "    <a name=\"search\">" +
                        "        <b class=\"title\">Найти</b>" +
                        "        <br><br><br>" +
                        "        <table width=\"1200\" border=\"1\" cellpadding=\"4\">" +
                        "            <tbody>" +
                        "            <tr>" +
                        "                <th>№ Заказа</th>" +
                        "                <th>Телефон</th>" +
                        "                <th>Создан</th>" +
                        "                <th>Отдать</th>" +
                        "                <th>Коментарий</th>" +
                        "                <th>Статус</th>" +
                        "                <td align=\"center\" rowspan=\"2\"><input onclick=\"location.href=/project/n\" value=\"Найти\" type=\"button\" class=\"button1\"></td>" +
                        "                <!-- <td align=\"center\" rowspan=\"2\"><a href=\"http://localhost:8080/project/n#\">Найти</a></td> -->" +
                        "            </tr>" +
                        "            <tr style=\"height: 50px;\">" +
                        "                <td><input type=\"text\" name=\"id\" value=\"1\"></td>" +
                        "                <td><input type=\"text\" name=\"phone\" value=\"0672112508\"></td>" +
                        "                <td><input type=\"text\" name=\"start_date\" value=\"5.12\"></td>" +
                        "                <td><input type=\"text\" name=\"end_date\" value=\"15.12\"></td>" +
                        "                <td><input type=\"text\" name=\"comment\" value=\"comment\"></td>" +
                        "                <td><input type=\"text\" name=\"status\" value=\"\"></td>" +
                        "            </tr>" +
                        "            </tbody>" +
                        "        </table>" +
                        "</div>" +
                        "" +
                        "" +
                        "" +
                        "<div class=\"block\">" +
                        "    <a name=\"change\"/>" +
                        "    <b class=\"title\">Изменить</b>" +
                        "    <br><br><br>" +
                        "    <table width=\"100%\" border=\"1\" cellpadding=\"4\">" +
                        "        <tbody>" +
                        "        <tr>" +
                        "            <th>№</th>" +
                        "            <th>Телефон</th>" +
                        "            <th>Создан</th>" +
                        "            <th>Отдать</th>" +
                        "            <th>Тело заказа</th>" +
                        "            <th>Коментарий</th>" +
                        "            <th>Статус</th>" +
                        "            <td align=\"center\" rowspan=\"2\"><input onclick=\"location.href=/project/n\" value=\"Сохранить\" type=\"button\" class=\"button2\"></td>" +
                        "        </tr>" +
                        "        <tr>" +
                        "            <td><input type=\"text\" name=\"id\" value=\"1\"> </td>" +
                        "            <td><input type=\"text\" name=\"phone\" value=\"0672112508\"> </td>" +
                        "            <td><input type=\"text\" name=\"start_date\" value=\"5.12\"></td>" +
                        "            <td><input type=\"text\" name=\"end_date\" value=\"15.12\"></td>" +
                        "            <td><textarea type=\"text\" name=\"body\" value=\"Зефир манго-5, Капкейки-14, макаронс-8\" align=\"left\"></textarea></td>" +
                        "            <td><textarea type=\"text\" name=\"comment\" value=\"Коментарий\" align=\"left\"></textarea></td>" +
                        "            <td><input type=\"text\" name=\"status\" value=\"Создан\"></td>" +
                        "" +
                        "            <!-- 					<td align=\"center\"><a href=\"http://localhost:8080/project/n#\">Изменить</a></td>" +
                        "                                <td align=\"center\"><a href=\"http://localhost:8080/project/n#\">Выполнить</a></td> -->" +
                        "        </tr>" +
                        "        </tbody>" +
                        "    </table>" +
                        "</div>" +
                        "</body>";
        PAGE.append(head + body);
        response.getWriter().println(PAGE);
    }


    String s = // todo: remove me
            "            <tr>" +
                    "                <td>1</td>" +
                    "                <td>0672112508</td>" +
                    "                <td>5.12</td>" +
                    "                <td>10.12</td>" +
                    "                <td align=\"left\">Зефир манго-5, Капкейки-14, макаронс-8</td>" +
                    "                <td align=\"left\">на 07:30 бля, муж снова будет ругаться</td>" +
                    "                <td>Создан</td>" +
                    "                <td align=\"center\"><input style=\"width: 98; height: 27;\" onclick=\"location.href=/project/n\" value=\"Изменить\" type=\"button\"></td>" +
                    "                <td align=\"center\"><input style=\"width: 98; height: 27;\" onclick=\"location.href=/project/n\" value=\"Выполнить\" type=\"button\"></td>" +
                    "            </tr>";

    // Табличная строка из массива
    // Example:
    private String getTableRowFromArray(String[] arr, String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        for (int c = 0; c < arr.length; c++) {
            if (c == 4 | c == 5) { // Для тела заказа и коментария выравнивание "влево"
                sb.append("<td align=\"left\">");
            } else {
                sb.append("<td>");
            }
            sb.append(arr[c]);
            sb.append("</td>");
        }
        if (tableName.equals("All")) { // Для каждой таблицы свои кнопки
            sb.append("<td align=\"center\"><input style=\"width: 98; height: 27;\" onclick=\"location.href=/project/n\" value=\"Изменить\" type=\"button\"></td>");
            sb.append("<td align=\"center\"><input style=\"width: 98; height: 27;\" onclick=\"location.href=/project/n\" value=\"Выполнить\" type=\"button\"></td>");
        }
        sb.append("</tr>");
        return sb.toString();
    }


    // Example: 06-07-2018 22:38:28
    private String getDateTimeNow() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }

    private String doAction(String action, String[] params) {
        // params[] в зависимости от action
        // find:   ["1"]
        // create: ["Merenga: 10 pcs", "0671234567"]
        StringBuilder result = new StringBuilder();
        jdbcPostgres psql = null;
        try {
            psql = new jdbcPostgres();
            switch (action) {
                case "findAllCreated":
                    ResultSet rs0 = psql.executeSelect("SELECT * FROM orders WHERE status = 'Создан'"); //todo inejction
                    while (rs0.next()) {
                        String rowArr[] = new String[7];
                        // id, phone, start_date, end_date, order_content, comment, status
                        rowArr[0] = String.valueOf(rs0.getInt("id"));
                        rowArr[1] = rs0.getString("phone");
                        Date start = new Date(rs0.getLong("start_date") * 1000); // Создание даты как "new Date();"
                        Date end = new Date(rs0.getLong("end_date") * 1000); // Создание даты как "new Date();"
                        rowArr[2] = new SimpleDateFormat("dd.MM").format(start);
                        rowArr[3] = new SimpleDateFormat("dd.MM").format(end);
                        rowArr[4] = rs0.getString("order_content");
                        rowArr[5] = rs0.getString("comment");
                        rowArr[6] = rs0.getString("status");
                        result.append(getTableRowFromArray(rowArr, "All"));
                    }
                    break;
                case "find":
                    ResultSet rs = psql.executeSelect("SELECT * FROM orders WHERE id='" + Integer.parseInt(params[0]) + "'"); //todo inejction
                    rs.next();
                    String row[] = new String[7];
                    // id, phone, start_date, end_date, order_content, comment, status
                    row[0] = String.valueOf(rs.getInt("id"));
                    row[1] = rs.getString("phone");
                    Date start = new Date(rs.getLong("start_date") * 1000); // Создание даты как "new Date();"
                    Date end = new Date(rs.getLong("end_date") * 1000); // Создание даты как "new Date();"
                    row[2] = new SimpleDateFormat("dd.MM").format(start);
                    row[3] = new SimpleDateFormat("dd.MM").format(end);
                    row[4] = rs.getString("order_content");
                    row[5] = rs.getString("comment");
                    row[6] = rs.getString("status");
                    result = new StringBuilder(getTableRowFromArray(row, "All"));
                    break;
                case "add":
                    String QueryInsert =
                            "INSERT INTO orders" +
                                    "  (order_id, product, client_phone)" +
                                    "VALUES" +
                                    "  (nextval('orders_order_id_seq'), '" + params[0] + "', '" + params[1] + "')"; // RUTRNUNG
                    psql.executeSelect(QueryInsert); // добавим новую запись. Всегда true, т.к. order_id всегда уникален
                    ResultSet rs2 = psql.executeSelect("SELECT last_value FROM orders_order_id_seq"); // получим последний order_id
                    rs2.next();
                    result = new StringBuilder(rs2.getString(1) + " | " + params[0] + " | " + params[1]);
                    String query = "" +
                    "INSERT INTO orders(phone, start_date, end_date, order_content, comment, status)" +
                        "VALUES" +
                            "('0672112008', 1542059302829, 1542059402829, 'Зефир-5, макаронс-25', 'Коментарий опять', 'Создан')";
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
        return result.toString();
    }

    // TODO: решить проблему добавления через "link rel"
    private String getStyle() {
        return
                "  <style type=\"text/css\">\n" +
                        ".login-page {\n" +
                        "  width: 1200px;\n" +
                        "  padding: 8% 0 0;\n" +
                        "  margin: auto;\n" +
                        "}\n" +
                        ".title {\n" +
                        "  font-size: x-large;\n" +
                        "}\n" +
                        ".block {\n" +
                        "  display: block;\n" +
                        "  /*position: relative;*/\n" +
                        "  z-index: 1;\n" +
                        "  background: #FFFFFF;\n" +
                        "  width: 1200px;\n" +
                        "  margin: 0 auto 70px;\n" +
                        "  padding: 25px;\n" +
                        "  text-align: center;\n" +
                        "  box-shadow: 0 0 20px 20px rgba(0, 0, 0, 0.2), 0 5px 5px 0 rgba(0, 0, 0, 0.24);\n" +
                        "}\n" +
                        ".form {\n" +
                        "  display: block;\n" +
                        "  position: relative;\n" +
                        "  z-index: 1;\n" +
                        "  background: #FFFFFF;\n" +
                        "  width: 1200px;\n" +
                        "  margin: 0 auto 100px;\n" +
                        "  padding: 45px;\n" +
                        "  text-align: center;\n" +
                        "  box-shadow: 0 0 20px 20px rgba(0, 0, 0, 0.2), 0 5px 5px 0 rgba(0, 0, 0, 0.24);\n" +
                        "}\n" +
                        ".form input {\n" +
                        "  font-family: inherit;\n" +
                        "  /*font-size: 17px;*/\n" +
                        "\n" +
                        "  font-size: medium;\n" +
                        "  font-weight: normal;\n" +
                        "  font-style: normal;\n" +
                        "  font-variant: normal;\n" +
                        "\n" +
                        "  color: currentColor;\n" +
                        "  outline: 0;\n" +
                        "  background: #f2f2f2;\n" +
                        "  width: 100%;\n" +
                        "  border: 0;\n" +
                        "  margin: 0 0 15px;\n" +
                        "  padding: 15px;\n" +
                        "  box-sizing: border-box;\n" +
                        "  text-align: center;\n" +
                        "}\n" +
                        "\n" +
                        ".form button {\n" +
                        "  font-family: \"Roboto\", sans-serif;\n" +
                        "  text-transform: uppercase;\n" +
                        "  outline: 0;\n" +
                        "  background: #4CAF50;\n" +
                        "  width: 100%;\n" +
                        "  border: 0;\n" +
                        "  padding: 15px;\n" +
                        "  color: #FFFFFF;\n" +
                        "  font-size: 14px;\n" +
                        "  -webkit-transition: all 0.3 ease;\n" +
                        "  transition: all 0.3 ease;\n" +
                        "  cursor: pointer;\n" +
                        "}\n" +
                        ".form button:hover, .form button:active, .form button:focus {\n" +
                        "  background: #43A047;\n" +
                        "}\n" +
                        ".form button_mini {\n" +
                        "  font-family: \"Roboto\", sans-serif;\n" +
                        "  text-transform: uppercase;\n" +
                        "  outline: 0;\n" +
                        "  background: #4CAF50;\n" +
                        "  width: 100%;\n" +
                        "  border: 0;\n" +
                        "  padding: 15px;\n" +
                        "  color: #FFFFFF;\n" +
                        "  font-size: 13px;\n" +
                        "  -webkit-transition: all 0.3 ease;\n" +
                        "  transition: all 0.3 ease;\n" +
                        "  cursor: pointer;\n" +
                        "}\n" +
                        ".form button_mini:hover,.form button_mini:active,.form button_mini:focus {\n" +
                        "  background: #43A047;\n" +
                        "}\n" +
                        ".form .message {\n" +
                        "  margin: 15px 0 0;\n" +
                        "  color: #b3b3b3;\n" +
                        "  font-size: 12px;\n" +
                        "}\n" +
                        ".form .message a {\n" +
                        "  color: #4CAF50;\n" +
                        "  text-decoration: none;\n" +
                        "}\n" +
                        ".form .register-form {\n" +
                        "  display: none;\n" +
                        "}\n" +
                        ".container {\n" +
                        "  position: relative;\n" +
                        "  z-index: 1;\n" +
                        "  max-width: 300px;\n" +
                        "  margin: 0 auto;\n" +
                        "}\n" +
                        ".container:before, .container:after {\n" +
                        "  content: \"\";\n" +
                        "  display: block;\n" +
                        "  clear: both;\n" +
                        "}\n" +
                        ".container .info {\n" +
                        "  margin: 50px auto;\n" +
                        "  text-align: center;\n" +
                        "}\n" +
                        ".container .info h1 {\n" +
                        "  margin: 0 0 15px;\n" +
                        "  padding: 0;\n" +
                        "  font-size: 36px;\n" +
                        "  font-weight: 300;\n" +
                        "  color: #1a1a1a;\n" +
                        "}\n" +
                        ".container .info span {\n" +
                        "  color: #4d4d4d;\n" +
                        "  font-size: 12px;\n" +
                        "}\n" +
                        ".container .info span a {\n" +
                        "  color: #000000;\n" +
                        "  text-decoration: none;\n" +
                        "}\n" +
                        ".container .info span .fa {\n" +
                        "  color: #EF3B3A;\n" +
                        "}\n" +
                        "table {\n" +
                        "\twhite-space: normal;\n" +
                        "\tline-height: normal;\n" +
                        "\twidth: normal;\n" +
                        "\tfont-weight: normal;\n" +
                        "\tfont-size: medium;\n" +
                        "\tfont-style: normal;\n" +
                        "\tcolor: -internal-quirk-inherit;\n" +
                        "\tfont-variant: normal;\n" +
                        "}\n" +
                        "table tbody {\n" +
                        "  text-align: center;\n" +
                        "}\n" +
                        "table th {\n" +
                        "  background-color: #dce6dd;\n" +
                        "}\n" +
                        "table td {\n" +
                        "  padding: 2 2 2 2;\n" +
                        "}\n" +
                        "input[type=button1] {\n" +
                        "  width: 100;\n" +
                        "  height: 78;\n" +
                        "}\n" +
                        "input[class=button2] {\n" +
                        "  width: 100%;\n" +
                        "  height: 78;\n" +
                        "}\n" +
                        "textarea {\n" +
                        "  height: 50;\n" +
                        "}\n" +
                        "input {\n" +
                        "  width: 100%;\n" +
                        "  height: 50;\n" +
                        "  font-size: medium;\n" +
                        "  font-weight: normal;\n" +
                        "  font-style: normal;\n" +
                        "  font-variant: normal;\n" +
                        "  text-align: center;\n" +
                        "  box-sizing: auto;\n" +
                        "  /*width: auto;*/\n" +
                        "  /*word-spacing: normal;*/\n" +
                        "}\n" +
                        "body {\n" +
                        "  background: linear-gradient(to right, #7EC27F, #004C6F); /* fallback for old browsers */\n" +
                        "  /*background: -moz-linear-gradient(right, #76b852, #8DC26F);*/\n" +
                        "  /*background: -webkit-linear-gradient(right, #76b852, #8DC26F);\n" +
                        "  background: -o-linear-gradient(right, #76b852, #8DC26F);\n" +
                        "  background: linear-gradient(to left, #76b852, #8DC26F);*/\n" +
                        "  font-family: \"Roboto\", sans-serif;\n" +
                        "  -webkit-font-smoothing: antialiased;\n" +
                        "  -moz-osx-font-smoothing: grayscale;\n" +
                        "}\n" +
                        "\n" +
                        "/*ul {\n" +
                        "  display: block;\n" +
                        "  position: fixed;\n" +
                        "  list-style-type: none;\n" +
                        "  top: 0;\n" +
                        "  margin: 0;\n" +
                        "  padding: 0;\n" +
                        "  width: 70px;\n" +
                        "}*/\n" +
                        "ul.horizontal {\n" +
                        "\tposition: fixed;\n" +
                        "\tlist-style-type: none;\n" +
                        "    margin: 0;\n" +
                        "    padding: 0;\n" +
                        "    overflow: hidden;\n" +
                        "    background-color: #333;\n" +
                        "}\n" +
                        "ul.horizontal li {\n" +
                        "    float: left;\n" +
                        "}\n" +
                        "ul.horizontal li a {\n" +
                        "    display: inline-block;\n" +
                        "    color: white;\n" +
                        "    text-align: center;\n" +
                        "    padding: 14px 16px;\n" +
                        "    text-decoration: none;\n" +
                        "}\n" +
                        "ul.horizontal li a.active {\n" +
                        "    background-color: #4CAF50;\n" +
                        "}\n" +
                        "li {\n" +
                        "    display: list-item;\n" +
                        "    text-align: -webkit-match-parent;\n" +
                        "}\n" +
                        "li a {\n" +
                        "    display: block;\n" +
                        "}\n" +
                        "li a:active {\n" +
                        "    color: #4CAF50;\n" +
                        "}\n" +
                        "li a:hover {\n" +
                        "    background-color: #4CAF50;\n" +
                        "}\n" +
                        "li:last-child {\n" +
                        "  border-right: none;\n" +
                        "}" +
                        "  </style>\n";
    }
}

/*
        Cookie[] COOKIE_ARR = request.getCookies();
        if (COOKIE_ARR==null) {
            title = "Вы перешли сюда зайцем.<br>Нужно допилить CookieCheck !";
            //TODO: redirect to /login
        } else
            title = "Добро пожаловать<>" + COOKIE_ARR[0].getValue();

        PAGE.append(Cookies.printHm()+"<br>");


        String body =
                "<h3>" + title + "</h3>" +
                        "<p>Вы перешли сюда по адресу: " + request.getRequestURL() + "</p><br><br>" +
                        // Форма поиска заказов по "order+id"
                        "<p>Форма поиска заказов по 'order_id'</p><br>" +

                        "<p>1. Поиск заказа</p>" +
                        "<form action=\"" + request.getRequestURL() + "\">" +
                        "№ Заказа <input type=\"text\" name=\"order_id\" value=\"\"><br>" +
                        "<br>-------------  <input type=\"submit\" value=\"Найти\"> -------------</form><br><br>" +

                        "<p>2. Добавление заказа</p>" +
                        "<form action=\"" + request.getRequestURL() + "\">" +
                        "№ Заказа <input type=\"text\" name=\"order_id\" value=\"\"><br>" + //TODO: номер заказа должен сам генериться
                        "Продукт <input type=\"text\" name=\"product\" value=\"\"><br>" +
                        "№ Телефона <input type=\"text\" name=\"phone\" value=\"\"><br>" +
                        "<br>-------------  <input type=\"submit\" value=\"Добавить\"> -------------</form><br><br>"


                ;

        jdbcPostgres psql = new jdbcPostgres();
        try {
            ResultSet rs = psql.executeSelect("SELECT * FROM orders WHERE order_id=" + request.getParameter("order_id"));
            rs.next();
            String s1 = rs.getString(1); // тут палает если ничего не найдег ос ошибкой "ResultSet..perhap"
            // не упало, очистим SB SEARCH_RESULT
//            SEARCH_RESULT = s1 + "<br>"; //TODO
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally{
            psql.closeConnection();
        }


        PAGE.append(body);
//        PAGE.append(SEARCH_RESULT);

        response.getWriter().println(PAGE);
    }
 */

