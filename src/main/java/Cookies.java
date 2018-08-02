import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * Менеджер Cookie-s
 * Действия:
 * 1. Автоудаление просроченных кук из БД и HM(время жизни более 15*-ти минут)
 * 2. Создание кукы
 * 3. Расчет временной разницы в секундах и минутах
 **/

class Cookies {




    // Кеш активных кук со временем их сохранения
    static ConcurrentHashMap<String, Long> cookiesAndSavingTime = new ConcurrentHashMap<>();
    // Имя куки в массиве Cookie из HttpServletRequest
    private static String COOKIE_NAME = Configuration.get("cookie_name");

    /* COOKIE_LIFETIME_MIN
    Максимальное время жизни куки при бездействии клиента на сервере */
    private static int COOKIE_LIFETIME_MIN = Integer.valueOf(Configuration.get("cookie_lifetime_min"));

    /* COOKIE_TIME_MIN_FOR_UPDATING
    Цель: снизить нагрузку на Postgres и HashM ( Redis:) )

    Барьерное время, при достижении которого обновляется время сохранения куки в Postgres и HashM
    Если клиент проявил активность (обновление страницы, работа с БД) и с момента выдачи куки
    прошло > 10 мин. время сохранения куки увеличится на 10 мин. */
    private static long COOKIE_TIME_MIN_FOR_UPDATING = 10;


    static {
        jdbcPostgres psql = null;

        // кеширование кук и времени из БД, таблицы cooks
        try {
            psql = new jdbcPostgres();
            ResultSet rsData = psql.executeSelect("SELECT * FROM cooks");
            while (rsData.next()) {
                String cookie = rsData.getString(1);
                long timeSavingCookie = Long.parseLong(rsData.getString(2));
                cookiesAndSavingTime.put(cookie, timeSavingCookie);
            }

            new AutoCleanCooksThread().start(); // старт потока автоочистки кук, время жизни которых > 20 мин.

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (psql != null) psql.closeConnection();
        }
    }


    static Cookie getNewCookie() {
        String cookHashMD5 = Hash.getMD5(String.valueOf(new Date()));
        return new Cookie(COOKIE_NAME, cookHashMD5);
    }


    /*
    сохранить куку (если нет) в HM и PSQL
    todo: update ? Реализовать только insert без on conflict - кейс, когда вход без куки (или ее нет в HashM)
    */
    static void saveOrUpdateCookie(String cookie) {
        long timeNow = getTimeNow();
        cookiesAndSavingTime.put(cookie, timeNow);
        jdbcPostgres.insertCookie(cookie, timeNow);
    }

    /*
    Действие клиента на сервере (обновление страницы)

    Кука была сохранена более 10ти минут назад ?
        да:  добавим к времени +10 мин. , обновим в Postgres и HashM время
        нет: менее 10ти минут, ничего не делаем     */

    static void updateSavingTimeIfNeed(HttpServletRequest request) {

        Cookie cookieValTime; // cookieCookTime: 'c57f21fa46e90738fbd79c3aceb80a1a = 1532900980404L'
        String cookieVal;     // Хеш кука
        long cookieTime;      // Время сохранения
        long timeNow;
        long diffMinutes;


        cookieValTime = getAuthCookieFromRequest(request);

        if (cookieValTime != null && !Cookies.cookiesAndSavingTime.isEmpty()) {
            cookieVal = cookieValTime.getValue();
            cookieTime = cookiesAndSavingTime.get(cookieVal);
            timeNow = getTimeNow();
            diffMinutes = TimeUnit.MILLISECONDS.toMinutes(timeNow - cookieTime); // микросекунды >> минуты

        /*
        if (cookiesAndSavingTime.contains(cookieVal)){
            timeSavingCook = cookiesAndSavingTime.get(cookieVal);
        }
        */

            // DBG todo remove
            Log.info("Cookies.USTIN. Update time: cookie [" + cookieVal + "] diffMinutes " +
                    diffMinutes + " > " + COOKIE_TIME_MIN_FOR_UPDATING + " ?");

            if (diffMinutes > COOKIE_TIME_MIN_FOR_UPDATING) {
                Log.info("Cookies.USTIN. Update time. Old time: " + cookieTime);
                // Увеличим время жизни куки на 10 минут
                cookieTime += TimeUnit.MINUTES.toMillis(COOKIE_TIME_MIN_FOR_UPDATING);
                Log.info("Cookies.USTIN. New time: " + cookieTime + ". Diff: " +
                        COOKIE_TIME_MIN_FOR_UPDATING);

                // обновим сохраненное время в
                // HashM
                cookiesAndSavingTime.put(cookieVal, cookieTime);
                // Postgres
                jdbcPostgres.updateCookieSavingTime(cookieTime, cookieVal);
            }
        }

    }


    private static Cookie getAuthCookieFromRequest(HttpServletRequest request) {
        Cookie[] cookArr = request.getCookies();
        if (cookArr != null) {
            for (Cookie cookieNameVal : cookArr) {
                if (cookieNameVal.getName().equals(COOKIE_NAME)) { //кука 'auth' есть в req & есть в HM
                    return cookieNameVal;
                }
            }
        } return null;
    }

    static boolean haveValidCookie(HttpServletRequest request) {
        Cookie cookieKeyVal = getAuthCookieFromRequest(request); // cookieKeyVal: 'auth = d729cd06701aa419d964747e66248eb0'
        return cookieKeyVal != null && cookiesAndSavingTime.containsKey(cookieKeyVal.getValue());
    }


    static long getTimeNow() { return System.currentTimeMillis(); }


    static class AutoCleanCooksThread extends Thread {
        String cookie;
        long cookieSavingTime;
        int countThreadLoops = 0; // todo rem after dbg

        public void run() {

            while (true) {
                try {
                    Iterator<Map.Entry<String, Long>> hmIterator = cookiesAndSavingTime.entrySet().iterator();
                    while (hmIterator.hasNext()) {
                        Map.Entry<String, Long> hmEntry = hmIterator.next();
                        cookie = hmEntry.getKey();
                        cookieSavingTime = hmEntry.getValue(); // получим время сохранения куки из HM
                        long diffMinutes = TimeUnit.MILLISECONDS.toMinutes((getTimeNow() - cookieSavingTime)); // конвертируем разницу времен в минуты

                        Log.info("ACCThread. Check Cookie [" + cookie + "] " +
                                diffMinutes + " > " + COOKIE_LIFETIME_MIN + " minutes ?");  // TODO: Remove after debug

                        //Удаление если возраст куки более 'COOKIE_LIFETIME_MIN' минут
                        if (diffMinutes > COOKIE_LIFETIME_MIN) {
                            Log.info("ACCThread. Deleting " + cookie);     // TODO: Remove after debug

                            jdbcPostgres.execute("DELETE FROM cooks WHERE cookie=?",
                                    new String[]{cookie}); //ИЗ БД

                            hmIterator.remove(); // из HM
                        }
                    }

                } catch (Exception e) { e.printStackTrace(); }

                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(COOKIE_LIFETIME_MIN));
                } catch (InterruptedException e) { e.printStackTrace(); }
                countThreadLoops++;
            }
        }
    }

    static void deleteFromDbAndHashM(HttpServletRequest request){
        Cookie cookieValTime;
        String cookieVal;

        cookieValTime = getAuthCookieFromRequest(request);
        if (cookieValTime!=null && !cookiesAndSavingTime.isEmpty()){
            cookieVal = cookieValTime.getValue();

            // Из Postgres
            deleteCookieValFromDB(cookieVal);
            // Из HM
            deleteCookieValFromHashM(cookieVal);

            Log.info("Cookies. Session closed by cookieVal: " + cookieVal);
        }
    }

    // Удаление куки из БД
    private static void deleteCookieValFromDB(String cookieVal){
        jdbcPostgres.execute("DELETE FROM cooks WHERE cookie=?",
                new String[]{cookieVal});
    }

    // Удаление куки из кеша HashM
    private static void deleteCookieValFromHashM(String cookieVal){
        cookiesAndSavingTime.remove(cookieVal);
    }


}


// TODO

/*
conn.setAutoCommit( false );
stmt =  conn.createStatement();
stmt.setFetchSize( 50 );
rset = stmt.executeQuery( sql );

while ( rset.next() ) {
...
*/


/*
 IN: card_id1 card_id2 amount

conn.setAutoCommit( false );

 "insert transaction (order_id, status, from_id, to_id, amount) values(?, 'started')" // order_id UNIQUE
 if(error order_id exist){
   "select * from transations where order_id=?"

 }else{

 "select * from cards where c_id=? for update" (card_id1)
 	if card1_amount >= amount{

 	   "select * from cards where c_id=? for update" (card_id2)
 	   Bal1 = card1_amount - amount,
 	   Bal2 = card2_amount + amount,
 	   "update cards set amount=? where c_id=?"  (Bal1, card_id1)
 	   "update cards set amount=? where c_id=?"  (Bal2, card_id1)

     update transactions set status='finished', from_balance=Bal1, to_balance=Bal2 where order_id=?

 	}else{
 	  update transactions set status='error' where order_id=?
 	}
 }

 conn.commit()

*/

//    // Проверка и удаление кук
//    static void asd(){
//        zzz.put("sadsad", 1L);
//        zzz.put("sadsad2", 1L);
//
//        Iterator<Map.Entry<String, Long>> iter = zzz.entrySet().iterator();
//        while(iter.hasNext()){
//            Map.Entry<String, Long> e = iter.next();
//            e.getValue();
//            //iter.remove();
//        }
//    }