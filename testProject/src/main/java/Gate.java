import javax.servlet.http.*;
import java.io.IOException;

// /project/
// 	Gate- проверяет куку
//		есть: проверяем валидность(есть в HM)
//		нет: redirect /login.jsp

public class Gate extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        StringBuilder PAGE = new StringBuilder();
        PAGE.append("<h3>Gate</h3><br>");


        // переход с лоином|паролем
        // Получим введенные данные
        String loginCheck = request.getParameter("login");
        String passwordCheck = request.getParameter("password");

        // Если ничего не введено (null) или пустота - сохраним ее
        String enteredLogin = loginCheck == null ? "" : loginCheck;
        String enteredPassword = passwordCheck == null ? "" : passwordCheck;



        // TODO:
        // 1. Выдадим куку
        // 2. Отправим сюда заново, чтобы произвести Redirect
        if (Client.isRegisteredClient(enteredLogin, enteredPassword)) {
            Cookie cook = Cookies.getNewCookie();
            response.addCookie(cook);
            Cookies.saveCookie(cook.getValue()); // сохраним в HM и PSQL новую куку с текущим временем(long)
            response.sendRedirect("/project/home");
        } else if (loginCheck == null | passwordCheck == null) {

            if (Cookies.isValidCookie(request)) {
                response.sendRedirect("/project/home");
            }else {
                response.sendRedirect("/project/login");
            }

//            // кука есть и она валидна = переход в home
//            Cookie[] cookArr = request.getCookies();
//            if (cookArr != null) {
////            PAGE.append("<br>");
//                for (Cookie cook : cookArr) {
////                PAGE.append("Found: "+cook.getName()+"="+cook.getValue()+"<br>");
//                    if (cook.getName().equals("authCook") & Cookies.hmCookieTime.containsKey(cook.getValue())) { //кука authCook есть в req & есть в HM
////                    PAGE.append("Cook ["+cook.getValue()+"] is valid: ").append(Cookies.hmCookieTime.containsKey(cook.getValue()));
//                        response.sendRedirect("/project/home");
//                        break;
//                    } else { //куки нет, или не валидна = "/project/login"
//                        response.sendRedirect("/project/login");
//                    }
//                }
//            } else { //куки нет, или не валидна = "/project/login"
//                response.sendRedirect("/project/login");
//            }


            PAGE.append("<br>Все куки в PSQL: " + Cookies.hmCookieTime.keySet() + "<br>");


            // Для добавления куки
            // Вызов:
            // http://localhost:8080/project/?cookie=что-то
            if (request.getParameter("cookie") != null) {
                Cookie cook = new Cookie("authCook", "cook-test");
                response.addCookie(cook);
                Cookies.saveCookie(cook.getValue()); // сохраним в HM и PSQL новую куку
                PAGE.append("<br>Cookie success added! Do refresh");
//            response.sendRedirect(String.valueOf(request.getRequestURL()));
            }


            response.getWriter().println(PAGE);
        }
    }
}
