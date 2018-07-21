import javax.servlet.http.*;
import java.io.IOException;

// /project/
// 	Gate- проверяет куку
//		есть: проверяем валидность(есть в HM)
//            валидна?
//                да: >>/project/home
//                нет: >>/project/login
//		нет: redirect /login.jsp

public class Gate extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        StringBuilder PAGE = new StringBuilder();
        PAGE.append("<h3>Gate</h3><br>");


        // переход с логином|паролем
        // Получим введенные данные
        String login    = request.getParameter("login");
        String password = request.getParameter("password");

        // Если ничего не введено (null) или пустота - сохраним ее
        boolean loginNotEmpty    = login != null;
        boolean passwordNotEmpty = password != null;



//        Переход с параметрами login/password (!=null)
        if (loginNotEmpty & passwordNotEmpty) {
            if (Users.isRegisteredUser(login, password)) {
                Cookie cook = Cookies.getNewCookie();
                response.addCookie(cook);
                Cookies.saveCookie(cook.getValue()); // сохраним в HM и PSQL новую куку с текущим временем(long)
//              PAGE.append("lp = OK");
                response.sendRedirect("/project/home");
            } else {
//                PAGE.append("lp NOT OK");
                response.sendRedirect("/project/login?badResult=Login+or+password+is+incorrect");
            }
        } else { //Переход без логина и пароля
            if (Cookies.isValidCookie(request)) {
//                PAGE.append("Cookie is valid");
                response.sendRedirect("/project/home");
            } else {
//                PAGE.append("Cookie is NOT valid: "+Cookies.getCookieValue(request));
                response.sendRedirect("/project/login");
            }
        }



    }
}
