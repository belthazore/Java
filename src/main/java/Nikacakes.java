import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

//public class Nikacakes {
//}


public class Nikacakes extends HttpServlet {

    private Map<String, ArrayList<String>> SEARCH_RESULTS = new HashMap<>();


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (Cookies.cookiesAndSavingTime.isEmpty() || !Cookies.haveValidCookie(request)) { // Не валидна или ее вообще нет
            response.sendRedirect("/project/login");
        }

        /*
        Интересный BUG (fixed)
        Суть:
        даже при отработке true в 23 строке этого класса Redirect не происходит(!), выполнение доходит до
        Cookies.updateSavingTimeIfNeed(request) - парадокс!

        0. Убрать if проверку в Cookies USTIN на наличие куки в HashM
        1. redeploy
        2. В БД, 'cooks' ничего нет, в HashM тоже
        3. Переходим 'localhost:8080/project/home'
        4. java.lang.NullPointerException
                Cookies.updateSavingTimeIfNeed(Cookies.java:103)
                Home.doGet(Home.java:29)
        */




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
        if(action.equals("exit")){
            Cookies.deleteFromDbAndHashM(request);
            HttpSession session = request.getSession();
            session.invalidate();
            response.sendRedirect("/project/");
            return;
        }



        Cookies.updateSavingTimeIfNeed(request);

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



        // - - - - - - - - - - -   Find order   - - - - - - - - - - -
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
//                        getHistoryByAction("find") +
//                        "    </div>" +
//                        "    <a name=\"create_form\"></a>" + // якорь
//                        "    <div class=\"form\" float=\"right\">" +
//                        "        <b>Create order</b>" +
//                        "        <form action=\"/project/home#create_form\" style=\"width: 300px;margin: auto;\">" +
//                        "            <br><br>" +
//                        "            <input name=\"product\" placeholder=\"product\" type=\"text\">" +
//                        "            <input name=\"phone_number\" placeholder=\"client phone\" type=\"text\">" +
//                        "            <br><br>" +
//                        "         <input name=\"action\" value=\"create\" type=\"hidden\">" +
//                        "            <button type=\"submit\" style=\"border-radius: 3px;\">Create</button>" +
//                        "        </form>" +
//                        getHistoryByAction("create") +
//                        "    </div>" +
//                        "  </div>" +

                        "  <table width=\"100%\" border=\"1\" cellpadding=\"4\">\n" +
                        "   <caption>Заказы нах</caption>\n" +
                        "   <br>\n" +
                        "   <tr>\n" +
                        "    <td>№</td>\n" +
                        "    <th>Телефон</th>\n" +
                        "    <th>Создан</th>\n" +
                        "    <th>Отдать</th>\n" +
                        "    <th>Тело заказа</th>\n" +
                        "    <th>Коментарий</th>\n" +
                        "    <th>Статус</th>\n" +
                        "    <td></td>\n" +
                        "   </tr>\n" +
                        "   <tr>\n" +
                        "    <td>1</td>\n" +
                        "    <td>0672112508</td>\n" +
                        "    <td>12/11 10:23</td>\n" +
                        "    <td>18/11 18:30</td>\n" +
                        "    <td>Мармелад-400грамм</td>\n" +
                        "    <td>Опаздала скотина на два часа прошлый раз</td>\n" +
                        "    <td>Создан</td>\n" +
//                        "    <td><input onclick=\"location.href='#'\" value=\"Выполнен\" type=\"button\"></td>\n" +
                        "            <td><button_mini type=\"submit\" style=\"border-radius: 3px;\">Выполнен</button_mini></td>" +
                        "   </tr>\n" +
                        "   <tr>\n" +
                        "    <td>2</td>\n" +
                        "    <td>0672112008</td>\n" +
                        "    <td>12/11 12:00</td>\n" +
                        "    <td>25/11 09:00</td>\n" +
                        "    <td>Зефир манго-5, Капкейки-14, макаронс-8</td>\n" +
                        "    <td>Дать три визитки</td>\n" +
                        "    <td>Создан</td>\n" +
//                        "    <td><input onclick=\"location.href='#'\" value=\"Выполнен\" type=\"button\"></td>\n" +
                        "            <td><button_mini type=\"submit\" style=\"border-radius: 3px;\">Выполнен</button_mini></td>" +
                        "   </tr>\n" +
                        "   <tr>\n" +
                        "    <td>3</td>\n" +
                        "    <td>0951303225</td>\n" +
                        "    <td>12/11 17:15</td>\n" +
                        "    <td>28/11 10:00</td>\n" +
                        "    <td>Торт-2,5 кг.</td>\n" +
                        "    <td>нифига</td>\n" +
                        "    <td>Создан</td>\n" +
//                        "    <td><input onclick=\"location.href='#'\" value=\"Выполнен\" type=\"button\"></td>\n" +
                        "            <td><button_mini type=\"submit\" style=\"border-radius: 3px;\">Выполнен</button_mini></td>" +
                        "   </tr>\n" +
                        doAction("find", new String[]{"1"}) +
                        "  </table>" +
                        " </div>" +
                        "</body>";
        PAGE.append(head + body);
        response.getWriter().println(PAGE);
    }

    // Табличная строка из массива
    // Example:
    private String getTableRowFromArray(String []arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        for(String cursor : arr){
            sb.append("<td>");
            sb.append(cursor);
            sb.append("</td>");
        }
        sb.append("<td><button_mini type=\"submit\" style=\"border-radius: 3px;\">Выполнен</button_mini></td>");
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
    }

    // TODO: решить проблему добавления через "link rel"
    private String getStyle() {
        return
                "  <style type=\"text/css\">\n" +
                        ".login-page {\n" +
                        "      width: 1200px;\n" +
                        "      padding: 8% 0 0;\n" +
                        "      margin: auto;\n" +
                        "    }\n" +
                        "    .form {\n" +
                        "      position: relative;\n" +
                        "      z-index: 1;\n" +
                        "      background: #FFFFFF;\n" +
                        "      max-width: 1200px;\n" +
                        "      margin: 0 auto 100px;\n" +
                        "      padding: 45px;\n" +
                        "      text-align: center;\n" +
                        "      box-shadow: 0 0 20px 20px rgba(0, 0, 0, 0.2), 0 5px 5px 0 rgba(0, 0, 0, 0.24);\n" +
                        "    }\n" +
                        "    .form input {\n" +
                        "      font-family: inherit;\n" +
                        "      color: currentColor;\n" +
                        "      outline: 0;\n" +
                        "      background: #f2f2f2;\n" +
                        "      width: 100%;\n" +
                        "      border: 0;\n" +
                        "      margin: 0 0 15px;\n" +
                        "      padding: 15px;\n" +
                        "      box-sizing: border-box;\n" +
                        "      text-align: center;\n" +
                        "      font-size: 14px;\n" +
                        "    }\n" +

                        "    .form button {\n" +
                        "      font-family: \"Roboto\", sans-serif;\n" +
                        "      text-transform: uppercase;\n" +
                        "      outline: 0;\n" +
                        "      background: #4CAF50;\n" +
                        "      width: 100%;\n" +
                        "      border: 0;\n" +
                        "      padding: 15px;\n" +
                        "      color: #FFFFFF;\n" +
                        "      font-size: 14px;\n" +
                        "      -webkit-transition: all 0.3 ease;\n" +
                        "      transition: all 0.3 ease;\n" +
                        "      cursor: pointer;\n" +
                        "    }\n" +
                        "    .form button:hover,.form button:active,.form button:focus {\n" +
                        "      background: #43A047;\n" +
                        "    }\n" +

                        "    .form button_mini {\n" +
                        "      font-family: \"Roboto\", sans-serif;\n" +
                        "      text-transform: uppercase;\n" +
                        "      outline: 0;\n" +
                        "      background: #4CAF50;\n" +
                        "      width: 100%;\n" +
                        "      border: 0;\n" +
                        "      padding: 15px;\n" +
                        "      color: #FFFFFF;\n" +
                        "      font-size: 13px;\n" +
                        "      -webkit-transition: all 0.3 ease;\n" +
                        "      transition: all 0.3 ease;\n" +
                        "      cursor: pointer;\n" +
                        "    }\n" +
                        "    .form button_mini:hover,.form button_mini:active,.form button_mini:focus {\n" +
                        "      background: #43A047;\n" +
                        "    }\n" +

                        "    .form .message {\n" +
                        "      margin: 15px 0 0;\n" +
                        "      color: #b3b3b3;\n" +
                        "      font-size: 12px;\n" +
                        "    }\n" +
                        "    .form .message a {\n" +
                        "      color: #4CAF50;\n" +
                        "      text-decoration: none;\n" +
                        "    }\n" +
                        "    .form .register-form {\n" +
                        "      display: none;\n" +
                        "    }\n" +
                        "    .container {\n" +
                        "      position: relative;\n" +
                        "      z-index: 1;\n" +
                        "      max-width: 300px;\n" +
                        "      margin: 0 auto;\n" +
                        "    }\n" +
                        "    .container:before, .container:after {\n" +
                        "      content: \"\";\n" +
                        "      display: block;\n" +
                        "      clear: both;\n" +
                        "    }\n" +
                        "    .container .info {\n" +
                        "      margin: 50px auto;\n" +
                        "      text-align: center;\n" +
                        "    }\n" +
                        "    .container .info h1 {\n" +
                        "      margin: 0 0 15px;\n" +
                        "      padding: 0;\n" +
                        "      font-size: 36px;\n" +
                        "      font-weight: 300;\n" +
                        "      color: #1a1a1a;\n" +
                        "    }\n" +
                        "    .container .info span {\n" +
                        "      color: #4d4d4d;\n" +
                        "      font-size: 12px;\n" +
                        "    }\n" +
                        "    .container .info span a {\n" +
                        "      color: #000000;\n" +
                        "      text-decoration: none;\n" +
                        "    }\n" +
                        "    .container .info span .fa {\n" +
                        "      color: #EF3B3A;\n" +
                        "    }\n" +
                        "    body {\n" +
                        "      background: linear-gradient(to right, #7EC27F, #004C6F); /* fallback for old browsers */\n" +
                        "      /*background: -moz-linear-gradient(right, #76b852, #8DC26F);*/\n" +
                        "      /*background: -webkit-linear-gradient(right, #76b852, #8DC26F);\n" +
                        "      background: -o-linear-gradient(right, #76b852, #8DC26F);\n" +
                        "      background: linear-gradient(to left, #76b852, #8DC26F);*/\n" +
                        "      font-family: \"Roboto\", sans-serif;\n" +
                        "      -webkit-font-smoothing: antialiased;\n" +
                        "      -moz-osx-font-smoothing: grayscale;      \n" +
                        "    }" +
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

