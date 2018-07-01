import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


// Сюда попадают те, кто успешно авторизовался тут '/login'

//Таблицы проекта:
// orders - покупки
// clients - зарегистрированные пользователи

public class Home extends HttpServlet {
//    String SEARCH_RESULT = "";


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder PAGE = new StringBuilder();
        String CONTENT_TYPE = "text/html;charset=utf-8";
        response.setContentType(CONTENT_TYPE);

        // TODO: 1. добавить проверку куки, если ее нет или ей ПЫЗДА - редирект на /login
        // TODO: 2. имя юзера брать с cookie [] по ключу user(? или брать куку, по ней в HM находить user)

        String title;

        Cookie[] COOKIE_ARR = request.getCookies();
        if (COOKIE_ARR==null) {
            title = "Вы перешли сюда зайцем.<br>Нужно допилить CookieCheck !";
            //TODO: redirect to /login
        } else
            title = "Добро пожаловать, " + COOKIE_ARR[0].getValue();

        PAGE.append("Postgres, table cooks: "+Cookies.hmCookieTime.keySet()+"<br>");


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
}
