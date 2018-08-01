import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Dbg extends HttpServlet {

    private static Map <String, Long> HASHMAP = new HashMap<String, Long>();
    private static Map <String, String> hmResult = new HashMap<String, String>(); //ключ= модуль страницы,
    // прим. 'MD5', значение=String с '<br>' тэгами

    static Map<String, Long> debug_old = new HashMap<>(); // Для отладки, сохранение промежуточных результатов
    private Cookie[] COOKIE_ARR;
    private String COOKIE_CURRENT_STR;
    private String ACTION_URL,
            CONTENT_TYPE,
            LOGIN_ENTERED,
            PASSWORD_ENTERED;
            //LOGIN,
            //PASSWORD,
    private int COOKIE_MAX_AGE = 1800;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        long TIME_NOW_MINUTE = (System.currentTimeMillis()/1000)/60;

        long TIME_NOW_MS = System.currentTimeMillis();

        COOKIE_ARR = request.getCookies();
        COOKIE_CURRENT_STR = getCookieStr();

        StringBuilder pageBody = new StringBuilder();
        ACTION_URL = String.valueOf(request.getRequestURL());
        CONTENT_TYPE = "text/html;charset=utf-8";
        response.setContentType(CONTENT_TYPE);

        pageBody.append("<h1>Тест сервер:<br>Amazon EC2 + JavaServlet + Apache Tomcat + Postgres</h1><br><br>");


        pageBody.append("<p align=\"right\"><input onclick=\"location.href='/project/dbg?action=exit2#request_params'\" value=\"Тест action + якорь\" type=\"button\"></p>");
        pageBody.append("<br> 0. Configuration.get(\"db_driver\"): "+Configuration.get("db_driver"));
        pageBody.append("<br> 1. Cookies in HM (Cookies.cookiesAndSavingTime):<br>"+Cookies.cookiesAndSavingTime.keySet()+"<br><br>");

        /*
        Diff
        Cookie / min / sec
        3d28 / 0 / 2300
        3d29 / 0 / 2300
        3d20 / 0 / 2300
        */
        if (!Cookies.cookiesAndSavingTime.isEmpty()){
            pageBody.append("1.1 Diff time savings:<br>Cookie Value / min / sec<br>");
            Collection<Long> arr = Cookies.cookiesAndSavingTime.values();
            for (long timeSave :arr) {
                long diffMil = Cookies.getTimeNow() - timeSave;
                long diffMin = diffMil / 60000;
                pageBody.append(timeSave + " / " + diffMin + " / " + diffMil + "<br>");
            }
            pageBody.append("<br><br>");
        }

        pageBody.append("2. Registered users in PSQL:<br>"+Users.registeredUsers.keySet()+"<br><br>");
        pageBody.append("3. Time back 10 minute is: " + (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(11)) + "<br><br>");


        Date dateNow = new Date();
        pageBody.append("<getFormAuthorization action=\"" + ACTION_URL + "\">");
        pageBody.append(TIME_NOW_MS + "<br>").append(new SimpleDateFormat("HH:mm:ss").format(dateNow)).append(
                "<br>" + new SimpleDateFormat("dd-MM-yyyy").format(dateNow) + "<br><br>");



        pageBody.append("<br><br><br><b>AuthorizationChecker income</b>");

        String cookArrStr = null;
        if (COOKIE_ARR != null) {
            cookArrStr = ""; //очистим от null, т.к циклом будут дописываться строки
            for (Cookie c : COOKIE_ARR) {
                cookArrStr += c.getName()+"="+c.getValue() + "<br>";
            }
        }
        pageBody.append("<br>-----------------------------------<br>" +
                cookArrStr + "<br>-----------------------------------"
        );


        /*
        // String action = request.getParameter("action");
        // Операция выхода из кабинета
        if(action!=null && action.equals("exit")){
            Cookies.deleteFromDbAndHashM(request);
            HttpSession session = request.getSession();
            session.invalidate();
            response.sendRedirect("/project/");
            return;
        }*/


        String MD5=
                "<br><br><br>" +
                "<h3>MD5</h3>" +
                "<br>" +
                "<form action=\"/project/dbg\">" +
                "        <input  type=\"text\"     name=\"input\"    placeholder=\"type here\" /><br>" +
                "        <button type=\"submit\"   style=\"border-radius: 3px;\">Convert</button>" +
                "</form>" +
                "<br>";
        pageBody.append(MD5);


        // Попробуем получить текст и хеш из прошлого вызова
        // Вариант 1
        pageBody.append("Попробуем получить текст и хеш из прошлого вызова<br><br><b>Вариант 1</b><br><br>");
        String inputForMD5Encode = request.getParameter("input");
        pageBody.append(inputForMD5Encode != null ? (inputForMD5Encode + " is " + getMD5(inputForMD5Encode)) : "");

        //Вариант 2 hmResult
        pageBody.append("<br><br><b>Вариант 2 (hmResult)</b><br><br>");
        String resultsMD5 = hmResult.get("MD5");
    
        if (inputForMD5Encode!=null){
            if(!inputForMD5Encode.equals("") & resultsMD5==null){ //пришел запрос с параметром input. Первое добавление, перетрем null в resultsMD5
                resultsMD5="";
            }
            resultsMD5 += inputForMD5Encode + "=" + getMD5(inputForMD5Encode)+"<br>";

            hmResult.put("MD5", resultsMD5);
        }

        pageBody.append(resultsMD5);



        pageBody.append("<br><br><br><br><br><br>" +
                "<a name=\"request_params\"></a>" + //Якорь тест
                "<br> <b>REQUEST parameters</b>" +
                "<br> -----------------------------------" +
                "<br> getServletPath(): " + request.getServletPath() +
                "<br> getRequestURI(): " + request.getRequestURI() +
                "<br> getLocalAddr(): " + request.getLocalAddr() +
                "<br> getRequestURL(): " + request.getRequestURL() +
                "<br> -----------------------------------" +
                "<br> <b>REMOTE parameters</b>" +
                "<br> getRemoteAddr(): " + request.getRemoteAddr() +
                "<br> getRemotePort(): " + request.getRemotePort() +
                "<br> getRemoteHost(): " + request.getRemoteHost() +
                "<br> -----------------------------------" +
                "<br> getQueryString(): " + (request.getQueryString()==null ?
                            "null. Need call page: ../dbg?key=val&key2=val2" : request.getQueryString()) +
                "<br> -----------------------------------"
        );
        pageBody.append("<br> validCookie(request): " + validCookie(request));
        pageBody.append("<br> HashMe.getMD5(\"test\"): " + getMD5("test") + "</getFormAuthorization>");

//        Log.error("jdbcPostgres: test");
//        Log.info("jdbcPostgres: test INFO");









        response.getWriter().println(pageBody);



    }


    private String getMD5(String t) {
        if (t.equals("")) {
            return "empty";
        } else {
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            m.reset();
            m.update(t.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
    }

    private boolean validCookie(HttpServletRequest request) {
        COOKIE_ARR = request.getCookies();
        return COOKIE_ARR != null;
    }



    // Возвращает переданный cookie или null
    private String getCookieStr(){
//        if (COOKIE_ARR.length>0){
//            return COOKIE_ARR[0].getValue();
//        }else
//            return null;
//        return String.valueOf(COOKIE_ARR.length);
        return "test";
    }

}
