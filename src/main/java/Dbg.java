import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class Dbg extends HttpServlet {
    private static Map <String, Long> HASHMAP = new HashMap<String, Long>();
    private Cookie[] COOKIE_ARR;
    private long TIME_NOW_MINUTE = (System.currentTimeMillis()/1000)/60;
    private String COOKIE_CURRENT_STR;
    private String ACTION_URL,
            CONTENT_TYPE,
            LOGIN_ENTERED,
            PASSWORD_ENTERED;
            //LOGIN,
            //PASSWORD,
    private int COOKIE_MAX_AGE = 1800;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        COOKIE_ARR = request.getCookies();
        COOKIE_CURRENT_STR = getCookieStr();

        StringBuilder pageBody = new StringBuilder();
        ACTION_URL = "http://" + request.getLocalAddr() + ":8080/ignis/test";
        CONTENT_TYPE = "text/html;charset=utf-8";
        response.setContentType(CONTENT_TYPE);

        Date dateNow = new Date();
        pageBody.append("<h1>Тест сервер:<br>Amazon EC2 + JavaServlet + Apache Tomcat + Postgres</h1><br><br>");

        pageBody.append("<getFormAuthorization action=\"" + ACTION_URL + "\">");
        pageBody.append(new SimpleDateFormat("HH:mm:ss").format(dateNow)).append(
                "<br>" + new SimpleDateFormat("dd-MM-yyyy").format(dateNow) + "<br><br>");

        // JDBC
        if (validCookie(request)) {
            pageBody.append("SUCCESS LOGINED<br>");

            HASHMAP.put(COOKIE_CURRENT_STR, TIME_NOW_MINUTE);
            //Обновим время в бд если прошло >15 и <30 минут
            String cookFromRequest = String.valueOf(COOKIE_ARR[0].getValue()); //TODO может вызвать Exception
            pageBody.append("<br>1. cookFromRequest: "+cookFromRequest);
            pageBody.append("<br>2. get timeSave by key 'cookie': "+HASHMAP.get(COOKIE_CURRENT_STR));
//          pageBody.append("<br>3. Diff: "+ ((System.currentTimeMillis()/1000/60) - HASHMAP.get(COOKIE_CURRENT_STR)));
            pageBody.append("<br>3. Diff: timeNow - timeSave = "+ ( ((System.currentTimeMillis()/1000/60)+5 ) - HASHMAP.get(COOKIE_CURRENT_STR)) + "<br>");

            try {
                String lg = request.getParameter("login2");
                HASHMAP.put(lg, 23212424L);
                pageBody.append("HASHMAP.get(\"key\"): " + HASHMAP.get(lg) + "<br><br><br>");
            }catch (Exception ignored){
            }

            jdbcPostgres db = new jdbcPostgres();
            ResultSet rs;
            int column = 0;



        } else {
            pageBody.append("NOT LOGINED<br>");
            try {
                if (
                        (
                                (request.getParameter("login").isEmpty() || request.getParameter("password").isEmpty())
                                        ||
                                        (request.getParameter("login") == null || request.getParameter("password") == null)
                        )
                        ) {
                    pageBody.append("<input type=\"text\" name=\"login\" value=\"LoginOld\"><br>");
                    pageBody.append("<input type=\"password\" name=\"password\" value=\"Password\"><br>");
                    pageBody.append("<input type=\"submit\" value=\"LoginOld\">");
                } else {
                    LOGIN_ENTERED = request.getParameter("login");
                    PASSWORD_ENTERED = request.getParameter("password");
                    String stringForHashMD5 = LOGIN_ENTERED + PASSWORD_ENTERED + new Date();
                    String cookHashMD5 = getMD5(stringForHashMD5);


                    Cookie cook = new Cookie("cook", cookHashMD5);
                    cook.setMaxAge(COOKIE_MAX_AGE); // in seconds 30 min
                    response.addCookie(cook);



                    pageBody.append(
                            "login: " + LOGIN_ENTERED +
                                    "<br>password: " + PASSWORD_ENTERED + "<br>" +
                                    stringForHashMD5 + ": " + cookHashMD5
                    );
                }
            } catch (NullPointerException e) { // не удалось выполнить request.getParameter("login").isEmpty()
                pageBody.append("Exception when try check login&password:<br>" + e.toString() + "<br><br>");
                pageBody.append("<input type=\"text\" name=\"login\" value=\"LoginOld\"><br>");
                pageBody.append("<input type=\"password\" name=\"password\" value=\"Password\"><br>");
                pageBody.append("<input type=\"submit\" value=\"LoginOld\">");
            }
        }


        pageBody.append("<br><br>" +
                "<br> <b>AuthorizationChecker income</b>" +
                "<br> -----------------------------------" +
                "<br>" + cookieToString(COOKIE_ARR) +
                "<br> -----------------------------------"
        );

        pageBody.append("<br><br><br>");
        pageBody.append("<h3>MD5</h3>");
        pageBody.append("Get MD5: <input type=\"text\" name=\"input\" value=\"\"><br>");
        pageBody.append("<input type=\"submit\" value=\"Convert to MD5\"><br><br><br>");

        // Попробуем получить текст и хеш из прошлого вызова
        String input = request.getParameter("input");
        pageBody.append(input != null ? (input + " is " + getMD5(input)) : "");

        pageBody.append("<br><br><br><br><br><br>" +
                "<br> <b>REQUEST parameters</b>" +
                "<br> -----------------------------------" +
                "<br> getServletPath(): " + request.getServletPath() +
                "<br> getRequestURI(): " + request.getRequestURI() +
                "<br> getLocalAddr(): " + request.getLocalAddr() +
                "<br> getRequestURL(): " + request.getRequestURL() +
                "<br> -----------------------------------" +
                "<br> <b>REMOTE parameters</b>" +
                "<br>request.getRemoteAddr(): " + request.getRemoteAddr() +
                "<br>request.getRemotePort(): " + request.getRemotePort() +
                "<br>request.getRemoteHost(): " + request.getRemoteHost() +
                "<br> -----------------------------------"
        );
        pageBody.append("validCookie(request): " + validCookie(request));
        pageBody.append("<br> HashMe.getMD5(\"test\"): " + getMD5("test") + "</getFormAuthorization>");
        response.getWriter().println(pageBody);
    }

    private String cookieToString(Cookie[] coo) {
        if (coo != null) {
            StringBuilder sb = new StringBuilder();
            for (Cookie c : coo) {
                sb.append(c.getValue() + "<br>");
            }
            return sb.toString();
        } else
            return "null";

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

    private void cookieSetGenerateAndSave(HttpServletResponse response){
        long timeNowMillisec = System.currentTimeMillis();
        String md5 = getMD5(String.valueOf(timeNowMillisec));
        Cookie cook = new Cookie("cook", md5);
        cook.setMaxAge(1800); // in seconds
        response.addCookie(cook);
        HASHMAP.put(md5, timeNowMillisec);
        // psql insert
    }

    // For enterSuccess users
    private void pageAuthorized(HttpServletRequest request) {
        StringBuilder pageBody = null;
        String ACTION_URL = "http://" + request.getLocalAddr() + ":8080/ignis/test";
        Date dateNow = new Date();
        String date = new SimpleDateFormat("dd-MM-yyyy").format(dateNow);
        String time = new SimpleDateFormat("HH:mm:ss").format(dateNow);
        pageBody.append("<getFormAuthorization action=\"" + ACTION_URL + "\">");
        pageBody.append("<h1>Тест сервер:<br>Amazon EC2 + JavaServlet + Apache Tomcat + Postgres</h1><br>");
        pageBody.append("<br>");
        pageBody.append(time).append("<br>" + date + "<br><br>");

    }


    // Возвращает переданный cookie или null
    private String getCookieStr(){
//        if (COOKIE_ARR.length>0){
//            return COOKIE_ARR[0].getValue();
//        }else
//            return null;
//        return String.valueOf(COOKIE_ARR.length);
        return "huy";
    }

//    private void cookieTimeUpdateIfNeed(HttpServletRequest request) {
//        //текущее время = TimeNow
//
//        if (validCookie(request)) {//если Кука есть, незаэкспайреная
//            HASHMAP.put(COOKIE_CURRENT_STR, TIME_NOW_MINUTE);
//
//            try {Thread.sleepTime(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace(); }
//
//            long COOKIE_SAVED_TIME = HASHMAP.get(COOKIE_CURRENT_STR);
//            out.println();
//        }
//
//
//            getSaveTimeByCookie()
//            diffTimeMinute
//        }
//            TimeSavingCookie = взять время сохранения из HashMap () или из PSQL
//                diffTime = TimeNow - TimeSavingCookie;
//        if(diffTime>14 && diffTime<31)
//            обновить время по этой куке в HM и psql
//        }
//    }



}










        /*
        resp.setContentType("text/html;charset=utf-8");

        PrintWriter pw = resp.getWriter();

        Integer a = 0, b = 0, c = 0;
        Boolean Error = false;

        String param_a = req.getParameter("a");
        String param_b = req.getParameter("b");

        try {
            a = Integer.parseInt(param_a);
            b = Integer.parseInt(param_b);
        }
        catch (NumberFormatException e) {
            Error = true;
        }

        if (Error) {
            pw.println("<h1>ERROR Response<h1/>");
            
        }
        else {
            c = a + b;

            pw.println(c+" TESTTTT");
        }
        */



                /*
        // AuthorizationChecker add
        AuthorizationChecker name = new AuthorizationChecker("name", "nameTest"); //URLEncoder.encode(req.getParameter("name"), "UTF-8"));
        AuthorizationChecker url = new AuthorizationChecker("url", ACTION_URL);
        name.setMaxAge(60*60*24); //
        url.setMaxAge(60*60*24);
        response.addCookie(name);
        response.addCookie(url);
        */

// Date & time
//        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");