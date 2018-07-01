import javax.servlet.http.Cookie;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Менеджер Cookie-s
 * Действия:
 * 1. Автоудаление просроченных кук из БД и HM(время жизни более 15*-ти минут)
 * 2. Создание кук
 * ?3. Расчет временной разницы в секундах и минутах
 **/

class Cookies {

    static Map<String, Long> hmCookieTime =
            Collections.synchronizedMap(new HashMap<String, Long>()); // ConcurrentHashMap

//    static ConcurrentHashMap<String, Long> zzz = new ConcurrentHashMap<>(1024);

    static jdbcPostgres psql;

    static {
        //jdbc кеширование в HM кук(Str) и времени(Str) БД, таблицы cooks
        psql = new jdbcPostgres();
        try {
            ResultSet rsData = psql.execute("SELECT * FROM cooks");

            while (rsData.next()) {
                String cook = rsData.getString(1);
                long time = Long.parseLong(rsData.getString(2));
                // out.println(cook + ": " + time);
                hmCookieTime.put(cook, time);
            }


            // старатунь thread

        } catch (Exception e) {
            e.printStackTrace(); // log + стопануть
        } finally {
            psql.closeConnection();
        }
    }

    static Cookie getNewCookie() {
        String cookHashMD5 = Hash.getMD5(String.valueOf(new Date()));
        return new Cookie("cook", cookHashMD5);
    }

    static void saveCookie(Cookie cook) {
        hmCookieTime.put(cook.getValue(), getTimeNow()); //put("DKVBJ3JH2B4JH24JB", "1529225995842")
    }

    // проверка, есть ли кука в HM
    static boolean isAliveCookie(Cookie cook) {
        return hmCookieTime.get(cook.getValue()) != null;
    }

    private static float diffSeconds(long from, long to) {
        return (to - from) / 1000;
    }

    private static float diffMinutes(long from, long to) {
        return (to - from) / 60000;
    }

    private static long getTimeNow() {
        return System.currentTimeMillis();
    }

    private static String remCookieFromDbAndHM(String cookStr) {
        // return тип = String, потому как hm.remove() вернет
        // или long(тип value значения, задаем при создании HM) или null
        return String.valueOf((hmCookieTime.remove(cookStr)));
    }

    class TenMinutesThread implements Runnable {
        public void run() {
//            do {
//                try {
//                    sleep(60000 * 1); //ch to 10
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }while (true);


//                for (Iterator<Map.Entry<String, Long>> iter = zzz.entrySet().iterator(); iter.hasNext();) {
//                    Map.Entry<String, Long> e = iter.next();
//                    e.getValue();
//                    iter.remove();
//                }


            String[] arr = Cookies.hmCookieTime.keySet().toArray(new String[0]); //arr become =["key-0","key-N"]

            for (String s : arr) {
                long timeSaveCook = hmCookieTime.get(s); //берем время куки(long) из HM
                float diffMinutes = (float) (getTimeNow() - timeSaveCook) / 60000;
                //TODO: need realize writing to log
                //Удаление если возраст куки более 10ти минут
                if (diffMinutes > 10) {
                    //из HM
                    hmCookieTime.remove(s);
                    //из PG
                    psql.execute("DELETE FROM cooks WHERE cookie='" + s + "'");
                 }
            }
        }
    }
}


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

