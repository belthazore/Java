import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Менеджер Cookie-s
 * Действия:
 * 1. Автоудаление просроченных кук из БД и HM(время жизни более 15*-ти минут)
 * 2. Создание кукы
 * 3. Расчет временной разницы в секундах и минутах
 **/

class Cookies {



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

    // todo: remove
    // Remove after debug hm ver 2
//    static Map<String, Long> hmCookieTime =
//            Collections.synchronizedMap(new HashMap<String, Long>(1024));

    static ConcurrentHashMap<String, Long> cookiesAndSavingTime = new ConcurrentHashMap<>(1024);
    private static String COOKIE_NAME = Configuration.get("cookie_name");

    // то же время перезапуска потока автоудаления
//    private static int COOKIE_LIFETIME_MIN = Integer.valueOf(Configuration.get("cookie_lifetime_min"));
    private static int COOKIE_LIFETIME_MIN = 1;



    {
        jdbcPostgres psql = null;

        // кеширование кук и времени из БД таблицы cooks,
        try {
            psql= new jdbcPostgres();
            ResultSet rsData = psql.execute("SELECT * FROM cooks");
            while (rsData.next()) {
                String cookie = rsData.getString(1);
                long timeSavingCookie = Long.parseLong(rsData.getString(2)); //todo: integer in pgsql
                cookiesAndSavingTime.put(cookie, timeSavingCookie);
            }

            // todo: rm after dbg
            // Старт автоочистки кук старше 10 минут
//            TenMinutesRunnable tmt = new TenMinutesRunnable();
//            Thread thr = new Thread(tmt);
//            thr.setDaemon(true);
//            thr.start();

            AutoCleanCooksThread acct = new AutoCleanCooksThread();
            acct.start();

        } catch (Exception e) { e.printStackTrace(); }
        finally { if (psql!=null) psql.closeConnection();}
    }


    static Cookie getNewCookie() {
        String cookHashMD5 = Hash.getMD5(String.valueOf(new Date()));
        return new Cookie(COOKIE_NAME, cookHashMD5);
    }

    // сохранить куку (если нет) в HM и PSQL
    static void saveCookie(String cookie) {
        long timeNow = getTimeNow();
        cookiesAndSavingTime.put(cookie, timeNow); //put("DKVBJ3JH2B4JH24JB", "1529225995842")
        jdbcPostgres.execute2(
                "INSERT INTO cooks(cookie, time) VALUES (?, ?) ON CONFLICT (cookie) DO UPDATE SET time = ?",
                new String[]{cookie, String.valueOf(timeNow), String.valueOf(timeNow)});
    }

    static boolean isValidCookie(HttpServletRequest request) { // boolean needUpdate
        boolean result = false;
        Cookie[] cookArr = request.getCookies();
        if (cookArr != null) {
            for (Cookie cook : cookArr) {
                if (cook.getName().equals(COOKIE_NAME)) { //кука authCook есть в req & есть в HM
                    result = cookiesAndSavingTime.containsKey(cook.getValue());
                    break;
                }
            }
        }
        return result;
    }



    static long getTimeNow() {
        return System.currentTimeMillis();
    }



    static class AutoCleanCooksThread extends Thread {
        String cookie;
        long cookieSaveTime;
        int countThreadStarts = 0;

        public void run() {
//            Log.writeInfo("Thread START ..."); // todo NOT WORKED :(

            while (true) {
                try {
                    countThreadStarts++;
//                    Log.writeInfo("start: " + countThreadStarts + " thread");
                    Iterator<Map.Entry<String, Long>> hmIterator = cookiesAndSavingTime.entrySet().iterator();
                    while (hmIterator.hasNext()) {
                        Map.Entry<String, Long> hmEntry = hmIterator.next();
                        cookie = hmEntry.getKey();
                        cookieSaveTime = hmEntry.getValue(); // получим время сохранения куки из HM
                        float diffMinutes = (float) (System.currentTimeMillis() - cookieSaveTime) / 60000; // конвертируем разницу времен в минуты

//                        Log.writeInfo("Cook " + hmEntry.getKey() + " age: " +        // TODO: Remove after debug
//                                diffMinutes + " minute");


                        //Удаление если возраст куки более 'COOKIE_LIFETIME_MIN' минут
                        if (diffMinutes > COOKIE_LIFETIME_MIN) {
//                            Log.writeInfo(diffMinutes + " > " + COOKIE_LIFETIME_MIN); // TODO: Remove after debug

                            hmIterator.remove(); // из HM

//                        jdbcPostgres.execute2("DELETE FROM cooks WHERE cookie=?", new String[]{cookie}); //ИЗ БД
                        }
                    }

                } catch (Exception e) { e.printStackTrace(); }

                try {
//                    Log.writeInfo("I`m sleeping...");
                    Thread.sleep(60000 * COOKIE_LIFETIME_MIN);
                } catch (InterruptedException e) { e.printStackTrace(); }

//                Log.writeInfo("AFTER: " + cookiesAndSavingTime.keySet());
            }
        }
    }

//    class TenMinutesRunnable implements Runnable { //
////                for (Iterator<Map.Entry<String, Long>> iter = zzz.entrySet().iterator(); iter.hasNext();) {
////                    Map.Entry<String, Long> e = iter.next();
////                    e.getValue();
////                    iter.remove();
////                }
//        public void run() {
//            while (true) {
//                try {
//                    String[] arr = hmCookieTime.keySet().toArray(new String[]{}); // Collection become arr =["key-0","key-N"]
//                    long timeNow = getTimeNow();
//                    for (String s : arr) {
//                        long timeSaveCook = hmCookieTime.get(s); //берем время куки(long) из HM
//                        float diffMinutes = (float) (timeNow - timeSaveCook) / 60000; // конвертируем разницу времен в минуты
//                        //TODO: need realize writing to log
//                        //Удаление если возраст куки более 'COOKIE_LIFETIME_MIN' минут
//                        if (diffMinutes > COOKIE_LIFETIME_MIN) {
//
//                            // из HM
//                            hmCookieTime.remove(s);
//
//                            // из PG
//                            jdbcPostgres.execute2("DELETE FROM cooks WHERE cookie=?", new String[]{s});
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        Thread.sleep(60000*COOKIE_LIFETIME_MIN);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }


}

// TODO !
//conn.setAutoCommit( false );
//        stmt =  conn.createStatement();
//        stmt.setFetchSize( 50 );
//        rset = stmt.executeQuery( sql );
//
//        while ( rset.next() ) {
//        ...


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

