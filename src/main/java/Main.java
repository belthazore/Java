import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.lang.System.err;
import static java.lang.System.out;
import static java.lang.Thread.sleep;

// тестирование jdbcPostgres

public class Main {

    private static Map<String, Long> hmCookieTime =
            Collections.synchronizedMap(new HashMap<>());

    private static ConcurrentHashMap<String, Long> hm;


    public static void main(String[] args) {
        try {
//            out.println("\n 1. PGSQL testing Select queryes");
//            ResultSet rsForCC = psql.executeSelect("select * from users"); // для получения количеста колонок
//            out.println("Column count = " + rsForCC.getMetaData().getColumnCount());
//
//            ResultSet rsForRC = psql.executeSelect("select count(*) from users"); // для получения количеста записей
//            rsForRC.next();
//            out.println("Row count = " + rsForRC.getString(1));
//
//            ResultSet rs = psql.executeSelect("SELECT * from users where login='first'");
//            out.println("In search by login \"first\" result rows: " + psql.getRowCount(rs));


            out.println("DONE");
            out.println(hmCookieTime.keySet());


            MyThread mt10 = new MyThread(0, 1000);
//            MyThread mtInfinity = new MyThread(1000);
            new Thread(mt10).start();
//            new Thread(mtInfinity).start();


            long from = getTimeNow();
            out.println(from);
            Thread.sleep(500);
            long to = getTimeNow();
            out.println(to);
            out.println("\nDIFF:\nsec: " + diffSeconds(from, to) + "\nmin: " + diffMinutes(from, to));
            out.println("test: " + diffMinutes(1, 68001));


            //Тест удаления из PSQL
            out.println("\n ____ Тест удаления из PSQL ____");

            String QUERY_DEFAULT_DATA = "INSERT INTO cooks(cookie, time) VALUES " +
                    "('cook_test1', '1529658000001')," +
                    "('cook_test2', '1529658000002')," +
                    "('cook_test3', '1529658000003')";


            // 01. Добавить запись, если уже есть - обновить
            // (Обновление времени куки, если клиент зашел)
            String QUERY_INSERT_ONE = "INSERT INTO cooks(cookie, time) VALUES ('cook_test1', '1529658000001')" +
                    " ON CONFLICT (cookie) DO UPDATE SET time =  '1529658000001'";
//            out.println(QUERY_INSERT_ONE + ":\n" + psql.executeSelect(QUERY_INSERT_ONE));


            // 02. поиск (ХЗ)
            String QUERY_SELECT_ONE = "SELECT * FROM cooks WHERE cookie='cook_test'";
//            out.println(QUERY_SELECT_ONE + ":\n" + psql.getRowCount(rsSelectOne));


            // 03. Удаление
            String QUERY_RM_ONE = "DELETE FROM cooks WHERE cookie='cook_test777'";
//            psql.executeSelect(QUERY_RM_ONE); // "Запрос не вернул результата"
            out.println("\n" + QUERY_RM_ONE + ":\n" + "");


            out.println();
            //Проверка и удаление элементов HM
            //timeNow уже выше определялось
            // До
            /**

             for (String s : arr){
             long cookieSavingTime = hmCookieTime.get(s); //берем время куки(String) из HM >> в long
             long diff = timeNow-cookieSavingTime;
             float diffSec = (float) (diff/1000);
             float diffMin = diffSec/60;
             boolean needRemove = (diffMin>10);
             out.println(s+":"+"\n"+"Diff is: "+diff+"(Sec: "+diffSec+", Min: "+diffMin+").\nNeed remove: "+needRemove+"\n");
             //Удаление
             if (needRemove) {
             //из HM
             hmCookieTime.remove(s);
             //из PG
             psql.executeSelect("DELETE FROM cooks WHERE cookie='"+s+"'");
             }
             }

             // После
             out.println(hmCookieTime.keySet());
             */
//            Logger logger = Logger.getLogger(Main.class.getName());
//            FileHandler fh = new FileHandler("loggerExample.log", false);
//            fh.setLevel(Level.FINE);
//
//            Logger l = Logger.getLogger("");
//            fh.setFormatter(new SimpleFormatter());
//            l.addHandler(fh);
//            l.setLevel(Level.CONFIG);
//            logger.log(Level.INFO, "message 1");
//            logger.log(Level.SEVERE, "message 2");
//            logger.log(Level.FINE, "message 3");

//            LOGGER.addHandler(fh);
//            LOGGER.log( Level.FINE, "processing entries in loop");
//            LOGGER.log( Level.FINE, "processing entries in loop", 14);


//            psql.execute3("DELETE FROM cooks WHERE cookie='$0'", new String[]{"cook2"}); // Не пашет :(



            out.println("\n-- Thread example --");

//            TenMinutesRunnable tmt = new TenMinutesRunnable();
//            Thread thr = new Thread(tmt);
//            thr.start();

//            out.println(Arrays.toString(str[5]));

//            out.println("KS: "+ Cookies.hmCookieTime.keySet());

//            ResultSet rsUser = psql.executeSelect("SELECT * FROM users WHERE login='l' AND password='p'");
//            rsUser.next();
//            out.println("rsUser.getString: " + rsUser.getString(3)); // Если пользователь не найден, тут случается ошибка "ResultSet..perhap"


            out.println("\n\n\n- - - - - - - - - - - - - - - - - - - - - - - - ");
            out.println("- - - - Date formats");
            out.println("- - - - - - - - - - - - - - - - - - - - - - - - ");

            Date dateNow = new Date();
            out.println(dateNow.getTime());
            long l = 1542059563804L;
            Date d2 = new Date(l*1000);
            out.println(d2);
            out.println(new SimpleDateFormat("dd-MM-yyyy").format(dateNow));
            out.println(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(dateNow));


            out.println("\n\n\n- - - - - - - - - - - - - - - - - - - - - - - - ");
            out.println("- - - - HASHMAP Experements");
            out.println("- - - - - - - - - - - - - - - - - - - - - - - - ");

            Map<String, String[]> SEARCH_RESULTS = new HashMap<>(); // K - блок страницы(прим. "Find order"), V - массив String
            // Поиск заказов (Find orders)
            String dateTimeNow = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            SEARCH_RESULTS.put("fo", new String[]{
                    "06-07-2018 21:01:35  1 | Merenga: 10 pcs | 0672112508",
                    "07-07-2018 09:43:08  2 | Merenga: 33 pcs | 0672112008"
            });

            // Создание заказа (Find orders)
            SEARCH_RESULTS.put("create order", new String[]{
                    "06-07-2018 00:48:46   тут всякая дичь из psql",
                    "06-07-2018 14:05:46   тут всякая дичь из psql"
            });


            out.println("keySet(): " + SEARCH_RESULTS.keySet());
            for (String[] strings : SEARCH_RESULTS.values()) {
                out.println("i: " + Arrays.toString(strings));
            }

            out.println("\n\n\n- - - - - - - - - - - - - - - - - - - - - - - - ");
            out.println("- - - - ArrayList<String> Experements");
            out.println("- - - - - - - - - - - - - - - - - - - - - - - - ");

            ArrayList<String> arrListStr = new ArrayList<>();
            arrListStr.add("test_1");
            arrListStr.add("test_2");
            arrListStr.add("test_3");
            arrListStr.size();
            String buff = "";
            for (Object s : arrListStr.toArray()) {
                buff += s;
            }
            out.println("buff: " + buff);
            out.println("arrListStr.toArray(): " + Arrays.toString(arrListStr.toArray()));


            out.println("- - - - - - - - - - - - - - - - - - - - - - - - ");


//            ResultSet rs3 = jdbcPostgres.executeSelect("SELECT * FROM users");
//            try {
//                if (rs3!=null) {
//                    while (rs3.next()) {
//                        out.println(rs3.getString("login")+" | "+rs3.getString("password"));
//                    }
//                }
//            } catch (SQLException e) { e.printStackTrace(); }

//            out.println(Users.registeredUsers.keySet());
//            out.println("Users.isRegisteredUser: " + Users.isRegisteredUser("l","p") );

            out.println("--------------- Properties Ex---------------");
            Properties prop = new Properties();

            try (InputStream input = new FileInputStream(".//conf//db.conf")) {

                // load a properties file
                prop.load(input);

                // get the property value and print it out
                out.println(prop.getProperty("login"));
                out.println(prop.getProperty("password"));

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            out.println("\n\n--------------- Postgres Test ---------------");
//            ResultSet rsUsers = psql.executeSelect("SELECT * FROM users");
//            try {
//                if (rsUsers!=null) {
//                    while (rsUsers.next()) {
//                        out.println(rsUsers.getString("login") + ": " + rsUsers.getString("password"));
//                    }
//                }
//            }
//            catch (Exception e) { e.printStackTrace(); }
//            finally { psql.closeConnection(); }


            out.println("\n\n--------------- Thread and TimeUnit Test ---------------");
            // Запуск:
            MyThread mt = new MyThread(TimeUnit.MINUTES.toMillis(1));
            Thread thread = new Thread(mt);
            thread.setDaemon(true);
//             thread.run();

//            TODO: uncomment
//            out.println(Cookies.hmCookieTime.keySet());


            out.println("\n\n--------------- Users.registeredUsers ---------------");
//            out.println(Users.registeredUsers.keySet()); // TODO: uncomment
//            out.println("Users.isRegisteredUser: "+Users.isRegisteredUser("l", "p"));  // TODO: uncomment


//            -------------

            // get time
            out.println(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));


            out.println("\n\n--------------- Logs ---------------");
            String textForWrite = "192.168.0.1\n" + "192.168.0.2\n";
            try {
                Files.write(Paths.get(
                        "//home//nnm//test.log"), textForWrite.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            File file = new File("//home//nnm//.android");
//            out.println("Are //home//nnm//.android exist? \n" + file.exists() + "\n");
//            out.println("Are home/nnm contains \".android\" folder:\n" + Arrays.asList(new File("//home//nnm//").list()).contains(".android") + "\n");


            out.println("\n\n--------------- Thread Autoclean w iterator ---------------");

            hm = new ConcurrentHashMap<>(1024);

            hm.put("k0", 1532531226359L);
            hm.put("k1", 1532531126359L);


            // для проверки и удаления кук из HM и Postgres
            out.println("UNTIL: " + hm.keySet());

            /*
            // Старт потока авточистки с перезапуском
            AutoCleanCooksThread2 act = new AutoCleanCooksThread2();
            act.setDaemon(true);
            act.run();
            */

            long timeNow = 1532531226359L; // long timeNow = Cookies.getTimeNow();
            out.println(timeNow);
            out.println(timeNow);



            out.println("\n\n--------------- DIR LS Example ---------------");

            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

            File folder = new File(".");
            for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                System.out.println(fileEntry.getName());
            }
            out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

            long unixNow, unixNowPlus7Day, unix7Days;

            unixNow = new Date().getTime();
            unix7Days = TimeUnit.HOURS.toMillis(24*7);
            unixNowPlus7Day = unixNow + unix7Days;

            out.println(System.currentTimeMillis());
            out.println(dateNow.getTime());

            out.println("Now: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(dateNow));
            long nowPlus7D = dateNow.getTime() + unix7Days;
            out.println("No2: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(nowPlus7D));

//            String response = doAction("find", new String[]{"1"});
//            out.println(response);



            jdbcPostgres.execute("SELECT * FROM orders", new String[]{});

            String result = null;
            jdbcPostgres psql = new jdbcPostgres();
            String query = "SELECT * FROM orders WHERE id='1'";
            ResultSet rs = psql.executeSelect(query);
            rs.next();
            out.println(rs.getInt("id"));






        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
//                while (hmIter.hasNext()) {
//        Map.Entry<String, Long> hmEntry = hmIter.next();
//        long cookieSavingTime = hmEntry.getValue(); //берем время сохранения куки из HM
//        float diffMinutes = (float) (Cookies.getTimeNow() - cookieSavingTime) / 60000; // конвертируем разницу времен в минуты
//        out.println("Cook " + hmEntry.getKey() + " age: " +        // TODO: Remove after debug
//                diffMinutes + " minute");
//
//        //Удаление если возраст куки более 'COOKIE_LIFETIME_MIN' минут
//        if (diffMinutes > COOKIE_LIFETIME_MIN) {
//            out.println(diffMinutes + " > " + COOKIE_LIFETIME_MIN); // TODO: Remove after debug
//
//            // из HM
//            hmIter.remove();
//
//            //ИЗ БД
//            //TODO !
//
//        }
//
//        try {
//            Thread.sleep(TimeUnit.MINUTES.toMillis(COOKIE_LIFETIME_MIN));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }



    static class AutoCleanCooksThread2 extends Thread {
        String cookie;
        long cookieSaveTime;
        int COOKIE_LIFETIME_MIN = 1;
        int countThreadStarts = 0;

        public void run() {

            out.println("Thread START ...");
            while (true) {
                try {
                    countThreadStarts++;
                    out.println("start: " + countThreadStarts + " thread");
                    Iterator<Map.Entry<String, Long>> hmIterator = hm.entrySet().iterator();
                    while (hmIterator.hasNext()) {
                        Map.Entry<String, Long> hmEntry = hmIterator.next();
                        cookie = hmEntry.getKey();
                        cookieSaveTime = hmEntry.getValue(); // получим время сохранения куки из HM
                        float diffMinutes = (float) (System.currentTimeMillis() - cookieSaveTime) / 60000; // конвертируем разницу времен в минуты

                        out.println("Cook " + hmEntry.getKey() + " age: " +        // TODO: Remove after debug
                                diffMinutes + " minute");


                        //Удаление если возраст куки более 'COOKIE_LIFETIME_MIN' минут
                        if (diffMinutes > COOKIE_LIFETIME_MIN) {
                            out.println(diffMinutes + " > " + COOKIE_LIFETIME_MIN); // TODO: Remove after debug

                            hmIterator.remove(); // из HM

//                        jdbcPostgres.execute("DELETE FROM cooks WHERE cookie=?", new String[]{cookie}); //ИЗ БД
                        }
                    }

                } catch (Exception e) { e.printStackTrace(); }

                try {
                    out.println("I`m sleeping...");
                    Thread.sleep(60000 * COOKIE_LIFETIME_MIN);
                } catch (InterruptedException e) { e.printStackTrace(); }

                out.println("AFTER: " + hm.keySet());
            }
        }
    }



// Класс-поток, можно указать интервал и число запусков
// Запуск:
// MyThread mt = new MyThread();
// Thread t = new Thread(mt);
// mt.is
static class MyThread implements Runnable {
    long counterGlobal = 0; //общий счетчик итераций
    int limit;
    long sleepTimeMS;
    boolean isInfinity = false;


    //бесконечное выполнение
    MyThread(long sleepTimeMilliseconds) {
        this.sleepTimeMS = sleepTimeMilliseconds;
        this.isInfinity = true;
    }

    //выполняется 'limit'-раз
    MyThread(int limit, int sleepTimeMilliseconds) {
        this.limit = limit;
        this.sleepTimeMS = sleepTimeMilliseconds;
    }

    public long getCounter() {
        return counterGlobal;
    }

    public void run() {
        if (isInfinity) {
            while (true) {
                out.println("[infinity thread] ");
                try {
                    Thread.sleep(sleepTimeMS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                counterGlobal++;
            }
        } else {
            int counter = 0;
            while (counter <= limit) {
                out.println(">> " + counter + " <<");
                try {
                    Thread.sleep(sleepTimeMS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                counter++;
            }
            counterGlobal++;
        }

    }
}

public enum enumTimeExample {

    MILLISECONDS(1),
    SECONDS(1000),
    MINUTES(60000),
    HOURS(360000);
    int time;

    enumTimeExample(int time) {
        this.time = time;
    }

    int get(int factor) {
        return factor * time;
    }
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

static class TenMinutesThread implements Runnable {
    public void run() {
        try {
            while (true) {
                err.println("3sec >>");
                try {
                    sleep(3000); //60000*10 минут 10
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                err.println("3sec <<");

                String[] arr = hmCookieTime.keySet().toArray(new String[0]); //arr become =["key-0","key-N"]
                out.println(hmCookieTime.keySet());
                for (String s : arr) {
                    long timeSaveCook = hmCookieTime.get(s); //берем время куки(long) из HM
                    float diffMinutes = (float) (getTimeNow() - timeSaveCook) / 60000;
                    //TODO: need realize writing to log
                    //Удаление если возраст куки более 10ти минут
                    if (diffMinutes > 10) {
                        out.println("REMOVING: " + s);
                        //из HM
                        hmCookieTime.remove(s);
                        //из PG
//                            psql.executeSelect("DELETE FROM cooks WHERE cookie='" + s + "'");
                    }
                }
                out.println("\n");
                new Main();
            }
        } catch (Exception ignored) {
        } finally {
//                psql.closeConnection();
        }
    }
}

    public static String doAction(String action, String[] params) {
        // params[] в зависимости от action
        // find:   ["1"]
        // create: ["Merenga: 10 pcs", "0671234567"]
        String result = null;
        jdbcPostgres psql = null;
        try {
            psql = new jdbcPostgres();

            switch (action) {
                case "find":
                    String query = "SELECT * FROM orders WHERE id='" + Integer.parseInt(params[0]) + "'";
                    ResultSet rs = psql.executeSelect(query); //todo inejction
                    rs.next();
                    String row[]= new String[7];
                    // id, phone, start_date, end_date, order_content, comment, status
                    row[0] = String.valueOf(rs.getInt("id"));
                    row[1] = rs.getString("phone");
                    Date start = new Date(rs.getLong("start_date")*1000); // Создание даты как "new Date();"
                    Date end =   new Date(rs.getLong("end_date")  *1000); // Создание даты как "new Date();"
                    row[2] = new SimpleDateFormat("dd/MM HH:mm").format(start);
                    row[3] = new SimpleDateFormat("dd/MM HH:mm").format(end);
                    row[4] = rs.getString("order_content");
                    row[5] = rs.getString("comment");
                    row[6] = rs.getString("status");
                    result = getTableRowFromArray(row);
                    break;
                case "create":
                    String QueryInsert =
                            "INSERT INTO orders" +
                                    "  (order_id, product, client_phone)" +
                                    "VALUES" +
                                    "  (nextval('orders_order_id_seq'), '" + params[0] + "', '" + params[1] + "')"; // RUTRNUNG
                    psql.executeSelect(QueryInsert); // добавим новую запись. Всегда true, т.к. order_id всегда уникален
                    ResultSet rs2 = psql.executeSelect("SELECT last_value FROM orders_order_id_seq"); // получим последний order_id
                    rs2.next();
                    result = rs2.getString(1) + " | " + params[0] + " | " + params[1];
                    break;
                default:
                    throw new Exception("Wrong action");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (psql != null)
                psql.closeConnection();
        }
        return result;
    }


    private static String getTableRowFromArray(String[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        for(String cursor : arr){
            sb.append("<td>");
            sb.append(cursor);
            sb.append("</td>");
        }
        sb.append("<td><button_mini type=\"submit\" style=\"border-radius: 3px;\">Выполнен</button_mini></td>");
        sb.append("</tr>");
        return sb.toString();
    }


}