import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Вопрос
// 1. jdbcPostgres дергать лучше через статику или через экземплар
// 2. conn.close() как вызывать для всех "нитей"

/**
 * Менеджер Cookie-s
 * Действия:
 * 1. Автоудаление просроченных кук из БД и HM(время жизни более 15*-ти минут)
 * 2. Создание кук
 * ?3. Расчет временной разницы в секундах и минутах
 **/

class Cookies {

//    private static ConcurrentHashMap<String, Long> zzz = new ConcurrentHashMap<>(1024);
////    Iterator<Map.Entry<String, Long>> iter = zzz.entrySet().iterator();
////    iter.hasn... не тянется :(
//
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

    static Map<String, Long> hmCookieTime =
            Collections.synchronizedMap(new HashMap<String, Long>(1024)); // ConcurrentHashMap


//    static String COOKIE_NAME = "cookAuth"; //TODO: rem after dbg
    private static String COOKIE_NAME = Configuration.get("cookie_name");
    private static int COOKIE_LIFETIME_MIN = Integer.valueOf(Configuration.get("cookie_lifetime_min")); // то же время перезапуска потока автоудаления
//    private static int COOKIE_LIFETIME_MIN = 10; // то же время перезапуска потока автоудаления


    {
        jdbcPostgres psql = null;
        //jdbc кеширование в HM кук(Str) и времени(Str) БД, таблицы cooks
        try {
            psql= new jdbcPostgres();
            ResultSet rsData = psql.execute("SELECT * FROM cooks");

            while (rsData.next()) {
                String cook = rsData.getString(1);
                long time = Long.parseLong(rsData.getString(2)); //todo: integer in pgsql
                hmCookieTime.put(cook, time);
            }

            // Старт автоочистки кук старше 10 минут
            //
            // 1. Thread
            // Вешает Tomcat (исправлено)
            //
            TenMinutesThread tmt = new TenMinutesThread();
            Thread thr = new Thread(tmt);
            thr.setDaemon(true);
            thr.start();


            // 2. Executor
            // ВЕШАЕТ CPU
            //
//            ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
//            mExecutor.schedule(getSendRunnable(), COOKIE_LIFETIME_MIN, TimeUnit.MINUTES); //60 секунд перезапуск потока


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
        hmCookieTime.put(cookie, timeNow); //put("DKVBJ3JH2B4JH24JB", "1529225995842")
        new jdbcPostgres().execute2(
                "INSERT INTO cooks(cookie, time) VALUES (?, ?) ON CONFLICT (cookie) DO UPDATE SET time = ?",
                new String[]{cookie, String.valueOf(timeNow), String.valueOf(timeNow)});
    }

    static boolean isValidCookie(HttpServletRequest request) { // boolean needUpdate
        boolean result = false;
        Cookie[] cookArr = request.getCookies();
        if (cookArr != null) {
            for (Cookie cook : cookArr) {
                if (cook.getName().equals(COOKIE_NAME)) { //кука authCook есть в req & есть в HM
                    result = hmCookieTime.containsKey(cook.getValue());
                    break;
                }
            }
        }
        return result;
    }



    static long getTimeNow() {
        return System.currentTimeMillis();
    }


//    Автоочистка просроченных кук и HM и PSQL
//    Вешает CPU
//
    private Runnable getSendRunnable() {
        return new Runnable() {
            public void run() {
                try {
                    while (true) {
                        String[] arr = hmCookieTime.keySet().toArray(new String[]{}); //arr become =["key-0","key-N"]
                        long timeNow = getTimeNow();
                        for (String s : arr) {
                            long timeSaveCook = hmCookieTime.get(s); //берем время куки(long) из HM
                            float diffMinutes = (float) (timeNow - timeSaveCook) / 60000; // конвертируем разницу времен в минуты
                            //TODO: need realize writing to log
                            //Удаление если возраст куки более 10ти минут
                            if (diffMinutes > COOKIE_LIFETIME_MIN) {

                                //из HM
                                hmCookieTime.remove(s);

                                //из PG
                                jdbcPostgres.execute2("DELETE FROM cooks WHERE cookie=?", new String[]{s});
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    class MyThread extends Thread{
        public void run(){
        }
    }

    class TenMinutesThread implements Runnable { //
//                for (Iterator<Map.Entry<String, Long>> iter = zzz.entrySet().iterator(); iter.hasNext();) {
//                    Map.Entry<String, Long> e = iter.next();
//                    e.getValue();
//                    iter.remove();
//                }
        public void run() {
            while (true) {
                try {
                    String[] arr = hmCookieTime.keySet().toArray(new String[]{}); // Collection become arr =["key-0","key-N"]
                    long timeNow = getTimeNow();
                    for (String s : arr) {
                        long timeSaveCook = hmCookieTime.get(s); //берем время куки(long) из HM
                        float diffMinutes = (float) (timeNow - timeSaveCook) / 60000; // конвертируем разницу времен в минуты
                        //TODO: need realize writing to log
                        //Удаление если возраст куки более 'COOKIE_LIFETIME_MIN' минут
                        if (diffMinutes > COOKIE_LIFETIME_MIN) {

                            // из HM
                            hmCookieTime.remove(s);

                            // из PG
                            jdbcPostgres.execute2("DELETE FROM cooks WHERE cookie=?", new String[]{s});
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        Thread.sleep(60000*COOKIE_LIFETIME_MIN);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


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

