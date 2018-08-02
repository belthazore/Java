import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrationUtil extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

//        String CONTENT_TYPE = "text/html;charset=utf-8";
//        response.setContentType(CONTENT_TYPE);
//        StringBuilder PAGE = new StringBuilder();
//        PAGE.append("<h3>Регистрация нового пользователя</h3>");


        // todo при работе с БД реализовать кеширование в HashM чтобы не положили Postgres в Amazon
        // http://localhost:8080/project/registration?login=testLogin&password=testPassword&email=testEmail@gmail.com

        // Получим введенные данные
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        // Если ничего не введено (null) или пустота - сохраним ее
        boolean loginNotEmpty = login != null;
        boolean passwordNotEmpty = password != null;
        boolean emailNotEmpty = email != null;

        // Если регистрация неуспешна - заполним поля старыми данными, чтобы заново не вводить все клиенту,
        // бережем его нервы :)
        String oldParams = "";
        if ( loginNotEmpty | emailNotEmpty ) { // хотя-бы один параметр не null - начнем с артикля
//            oldParams += "&";
            for (String[] param : new String[][]{{"login", login}, {"email", email}}){ // {"password", password} - не используем
                if (param[1]!=null) // параметр не пуст
                    oldParams += "&" + param[0] + "=" + param[1]; // &login=my_login&password=my_password
            }
        }

//        Проверим уникальность login и email
        if (loginNotEmpty & passwordNotEmpty & emailNotEmpty) { // есть все необходимые данные для рег-ции

            if (alreadyExist("login", login)) {
//                PAGE.append("login не уникален");
                response.sendRedirect("/project/registration?result=login+already+exist" + oldParams);
            }else if (alreadyExist("email", email)){
//                PAGE.append("<br>email не уникален");
                response.sendRedirect("/project/registration?result=email+already+exist" + oldParams);

            }else {
                // В БД
                jdbcPostgres.execute( "INSERT INTO users(login, password, email) VALUES (?, ?, ?)", new String[]{login, password, email} );
                // В HashM
                Users.registeredUsers.put(login, password);
                response.sendRedirect("/project/login?result=Registration+success,+try+login&login=" + request.getParameter("login"));
            }

        }


//        response.getWriter().println(PAGE);
    }

    // Проверка существования login | email в таблице 'users'
    private static boolean alreadyExist(String param, String value) {
        boolean result = true;
        if (param.equals("login") | param.equals("email")) {
            jdbcPostgres psql = new jdbcPostgres();
            ResultSet rsLogin = psql.executeSelect("SELECT * FROM users WHERE " + param + " = '" + value + "'");

            if (rsLogin != null) { //пользователь найден
                try {
                    result = rsLogin.next(); // false, если не найдена запись
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    psql.closeConnection();
                }
            }
        }
        return result;
    }

}