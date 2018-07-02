import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


// Сюда попадают те, кто успешно авторизовался тут '/login'

//Таблицы проекта:
// orders - покупки
// clients - зарегистрированные пользователи

public class Home extends HttpServlet {
//    String SEARCH_RESULT = "";


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (!Cookies.isValidCookie(request)) { // Не валидна или ее вообще нет
            response.sendRedirect("/project/login");
        }

        // T ODO: 1. добавить проверку куки, если ее нет или она не валидна - редирект на /login
        // T ODO: 2. имя юзера брать с cookie [] по ключу user(? или брать куку, по ней в HM находить user)


        StringBuilder PAGE = new StringBuilder();
        response.setContentType("text/html;charset=utf-8");


        // TODO: решить проблему добавления через "link rel"
        String style =
                "  <style type=\"text/css\">\n" +
                        ".login-page {\n" +
                        "      width: 800px;\n" +
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

        String head =
                "<head>\n" +
                "  <title>Java Tomcat test server | Login</title>\n" +
//                "  <link rel=\"stylesheet\" type=\"text/css\" href=\"/home.css\">" +
                style +
                "</head>\n";
//        PAGE.append(head);

        String body =
                "<body>\n" +
                        "\n" +
                        "  <div class=\"login-page\">\n" +
                        "    <div class=\"form\" >\n" +
                        "        <b>Find order</b>\n" +
                        "        <form action=\"/project/\" style=\"width: 300px;margin: auto;\">\n" +
                        "            <br><br>\n" +
                        "            <input name=\"order_id\" placeholder=\"order_id\" type=\"text\">\n" +
                        "            <br><br>\n" +
                        "            <button type=\"submit\" style=\"border-radius: 3px;\">Find</button>\n" +
                        "        </form>\n" +
                        "    </div>\n" +
                        "    \n" +
                        "    <div class=\"form\">\n" +
                        "        <b>Create Order</b>\n" +
                        "        <form action=\"/project/\" style=\"width: 300px;margin: auto;\">\n" +
                        "            <br><br>\n" +
                        "            <input name=\"order_id\" placeholder=\"order_id\" type=\"text\">\n" +
                        "            <input name=\"product\" placeholder=\"product\" type=\"text\">\n" +
                        "            <input name=\"phone_number\" placeholder=\"client phone\" type=\"text\">\n" +
                        "            <br><br>\n" +
                        "            <button type=\"submit\" style=\"border-radius: 3px;\">Create</button>\n" +
                        "        </form>\n" +
                        "    </div>\n" +
                        "  </div>\n" +
                        "\n" +
                        "</body>";
        PAGE.append(head+body);
        response.getWriter().println(PAGE);
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
            ResultSet rs = psql.execute("SELECT * FROM orders WHERE order_id=" + request.getParameter("order_id"));
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
