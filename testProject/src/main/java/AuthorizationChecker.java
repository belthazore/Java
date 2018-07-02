import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


// Класс-роутер.
// Проверяет наличие и валидность куки

public class AuthorizationChecker extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //        if (request.getCookies()!=null)
//            response.sendRedirect("http://localhost:8080/project/home"); //TODO: есть кука в БД, переводим на страницу /home

        StringBuilder PAGE = new StringBuilder();
        response.setContentType("text/html;charset=utf-8");

        PAGE.append("Postgres, table cooks: "+ Cookies.hmCookieTime.keySet()+"<br>");
        response.getWriter().println(PAGE);
    }


}