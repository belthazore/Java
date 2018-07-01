import javax.servlet.http.*;
import java.io.IOException;
import java.sql.ResultSet;

public class Registration  extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String CONTENT_TYPE = "text/html;charset=utf-8";
        response.setContentType(CONTENT_TYPE);


        String CURRENT_URL = request.getRequestURL().toString();  // Урл, по которому был переход
        StringBuilder PAGE = new StringBuilder();


        // Page
        PAGE.append("<h3>Регистрация нового пользователя</h3>");

        /*
        1. Первый вызов, без параметров
        2. Такие данные уже есть
                заполнить поля введенными данными + вывести внизу имена конфликтных полей
        3. Успех регистрации
                ?

         */

        //Если это повторный вызов страницы по причине, например "такой логин уже используется"
        //получим ранее введенные данные

        String enteredLogin = request.getParameter("login");
        String enteredPassword = request.getParameter("password");
        String enteredEmail = request.getParameter("email");

        String login = enteredLogin==null ? "" : enteredLogin;
        String email = enteredEmail==null ? "" : enteredEmail;
        String password = enteredPassword==null ? "" : enteredPassword;

        String reg =
            "<form action=\"" + CURRENT_URL + "\">" +
            "Login..........  <input type=\"text\" name=\"login\"    value=\"" + login + "\"><br>" +
            "Password.... <input     type=\"text\" name=\"password\" value=\"" + password + "\"><br>" +
            "Email..........  <input type=\"text\" name=\"email\" value=\"" + email + "\"><br>" +
            "<br>-------------  <input type=\"submit\" value=\"Register\"> -------------</form><br><br>";

        PAGE.append(reg);
        if (!login.equals("") & !password.equals("") & !email.equals("")){
            jdbcPostgres psql = new jdbcPostgres();
            ResultSet rsByLogin = psql.execute("SELECT * FROM clients WHERE login='"+login+"'");
        }

        response.getWriter().println(PAGE); // PRINT PAGE
    }

}