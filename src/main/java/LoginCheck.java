
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LoginCheck extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html;charset=utf-8");
        StringBuilder PAGE = new StringBuilder();


        PAGE.append("<br>is Registered LOGIN!<br>");

        response.getWriter().println(PAGE);

//        String CURRENT_URL = request.getRequestURL().toString();  // Урл, по которому был переход
//        String HOME_PAGE = "http://localhost:8080/project/home";  //TODO:
//
//        if (request.getCookies()!=null)
////            if (AuthorizationChecker.hmCookieTime.get(COOKIE_ARR[0].getValue())!=null)
//            response.sendRedirect(HOME_PAGE); //есть кука в БД, переводим на страницу /home
//
//        // Page
//        PAGE.append("<h4>Вход в личный кабинет</h4>");
//
//
//        String reg =
//            "<form action=\"" + CURRENT_URL + "\">" +
//            "LoginOld..........  <input type=\"text\" name=\"login\"    value=\"" + enteredLogin + "\"><br>" +
//            "Password.... <input     type=\"text\" name=\"password\" value=\"" + enteredPassword + "\"><br>" +
//            "<br>-------------  <input type=\"submit\" value=\"LoginOld\"> -------------</form><br><br>";
//
//        PAGE.append(reg);
//
//        String errMessage = null;
//        boolean loginSuccess = false;
//        boolean loginAndPasswordIsEmpty = true;
//        if (!enteredLogin.equals("") & !enteredPassword.equals("")) //поля не пусты
//            loginAndPasswordIsEmpty=false;
//
//        jdbcPostgres psql = new jdbcPostgres();
//        if (!loginAndPasswordIsEmpty){
//            try {
//                ResultSet rs = psql.execute("SELECT * FROM clients WHERE login='"+enteredLogin+"' AND password='"+enteredPassword+"'");
//                rs.next();
//                rs.getString(1); // Если пользователь не найден, тут случается ошибка "ResultSet..perhap"
//                Cookie cook = Cookies.getNewCookie();
//                Cookies.saveCookie(cook); //TODO: Сохраним в PG нашу новую куку
//                response.addCookie(cook);
//                loginSuccess = true;
//            }catch (Exception e){
//                errMessage = "LoginOld or password wrong<br>(Exception: " + e.getMessage()+")<br>";
//            }
//            finally {
//                psql.closeConnection();
//            }
//        }
//        PAGE.append("<br>DBG: <br>"+ Cookies.hmCookieTime.keySet()+"<br>");
//
//        if (loginSuccess) {
//            response.sendRedirect(HOME_PAGE);
//            //TODO: 1. Присвоить куку 2. Апдейт куки в хеш и БД 3. Редирект в /home
//        }else{
//            if (!loginAndPasswordIsEmpty)
//                PAGE.append(errMessage);
//            response.getWriter().println(PAGE); // PRINT PAGE
//        }
//
//    }
    }
}

