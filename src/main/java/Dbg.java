import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Dbg extends HttpServlet {

    private static Map <String, Long> HASHMAP = new HashMap<String, Long>();
    private static Map <String, String> hmResult = new HashMap<String, String>(); //ключ= модуль страницы,
    // прим. 'MD5', значение=String с '<br>' тэгами

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
        ACTION_URL = String.valueOf(request.getRequestURL());
        CONTENT_TYPE = "text/html;charset=utf-8";
        response.setContentType(CONTENT_TYPE);

        pageBody.append("<h1>Тест сервер:<br>Amazon EC2 + JavaServlet + Apache Tomcat + Postgres</h1><br><br>");

        pageBody.append("0. Configuration.get(\"db_driver\"): "+Configuration.get("db_driver")+"<br>"+Configuration.err+"<br>");
        pageBody.append("1. Cookies in PSQL:<br>"+Cookies.hmCookieTime.keySet()+"<br><br>");

        if (!Cookies.hmCookieTime.isEmpty()){
            pageBody.append("1.1 Diffs time savings: ");
            Collection<Long> arr = Cookies.hmCookieTime.values();
            for (long timeSave :arr) {
                float diffMinutes = (Cookies.getTimeNow() - timeSave)/60000;
                pageBody.append(timeSave+ ": " + diffMinutes+"<br>");
            }
            pageBody.append("<br><br>");
        }

        pageBody.append("2. Registered users in PSQL:<br>"+Users.registeredUsers.keySet()+"<br><br>");


        Date dateNow = new Date();
        pageBody.append("<getFormAuthorization action=\"" + ACTION_URL + "\">");
        pageBody.append(new SimpleDateFormat("HH:mm:ss").format(dateNow)).append(
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
        //Вариант 1
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