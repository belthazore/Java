package hello;

import java.security.SecureRandom;


class FillDb {

    // Запись N-строк в PostgreSQL
    static void start(int count) {
        String tableName = "contacts";
        StringBuilder QUERY = new StringBuilder().append("INSERT INTO " + tableName + "(name) " +
                "VALUES ('" + (getRandString(getRandOneInt()) + " " + getRandString(getRandOneInt())) + "')");

        int countPg = 0;
        while (countPg < count) {
            String fnameLname = getRandString(getRandOneInt()) + " " + getRandString(getRandOneInt());
            QUERY.append(", ('" + fnameLname + "') ");
            countPg++;
        }
        jdbcPostgres.fillDb(QUERY.toString());
    }

    // без цифер 0-3 (!)
    private static int getRandOneInt() {
        String AB = "456789";
        return Integer.parseInt(getRandString(1, AB));
    }

    private static String getRandString(int length) {
//        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        return getRandString(length, AB);
    }

    private static String getRandString(int length, String AB) {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}