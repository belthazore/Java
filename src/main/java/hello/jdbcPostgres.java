package hello;
import java.sql.*;


class jdbcPostgres {


/*    private static String DB_DRIVER;
    private static String DB_ROOT_URL = Configuration.get("db_root_url");
    private static String DB_DEV = Configuration.get("db_dev");
    private static String DB_URL = DB_ROOT_URL + DB_DEV;
    private static String LOGIN = Configuration.get("login");
    private static String PASSWORD = Configuration.get("password");*/

    private static String DB_DRIVER = "org.postgresql.Driver";
    private static String DB_ROOT_URL = "jdbc:postgresql://127.0.0.1:5432/";
    private static String DB_DEV = "test_igor";
    private static String DB_URL = DB_ROOT_URL + DB_DEV;

    private static String LOGIN = "postgres";
    private static String PASSWORD = "postgres";


    private static Connection connection;
    private Statement statement = null;


    jdbcPostgres() {
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
//            Log.error("jdbcPostgres: " + e.getMessage());
            e.printStackTrace();
        }
        /*
        finally { // после этого ResultSet-ы возвращают null
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        */
    }


    // Создает свой connection и закрывает его в конце
    static void execute(String QUERY, String[] params) {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            PreparedStatement prepStatement = conn.prepareStatement(QUERY);
            int index = 1;
            if (!(params == null)) {
                for (String par : params) {
                    prepStatement.setString(index, par);
                    index++;
                }
            }
            prepStatement.execute();
        } catch (SQLException e) {
//            Log.error(e.getMessage() + "Query: " + QUERY);
            e.printStackTrace();
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    // Добавление 3kk записей в contacts
    static void execute2(String QUERY) {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            PreparedStatement prepStatement = conn.prepareStatement(QUERY);
            prepStatement.execute();
        } catch (SQLException e) {
//            Log.error(e.getMessage() + "Query: " + QUERY);
            e.printStackTrace();
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }


    // Создает свой connection и закрывает его в конце
    static void insertCookie(String cookie, long timeSaving) {
        String QUERY = "INSERT INTO cooks(cookie, time) VALUES (?, ?)";
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            PreparedStatement prepStatement = conn.prepareStatement(QUERY);
            prepStatement.setString(1, cookie);
            prepStatement.setLong(2, timeSaving);
            prepStatement.execute();
        } catch (SQLException e) {
//            Log.error(e.getMessage() + "Query: " + QUERY);
            e.printStackTrace();
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    // todo: подумать как объединить с 'insertCookie'
    static void updateCookieSavingTime(long timeSaving, String cookie) {
        String QUERY = "UPDATE cooks SET time = ? WHERE cookie = ?";
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            PreparedStatement prepStatement = conn.prepareStatement(QUERY);
            prepStatement.setLong(1, timeSaving);
            prepStatement.setString(2, cookie);
            prepStatement.executeQuery();
        } catch (SQLException e) {
//            Log.error(e.getMessage() + "Query: " + QUERY);
            e.printStackTrace();
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    // Заменит insertCookie и updateCookieSavingTime через TreeMap,
    // если реализовать типизацию параметров
    static ResultSet select(String QUERY, String[] params) {
        Connection conn;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            PreparedStatement prepStatement = conn.prepareStatement(QUERY);
            int ind = 1; // порядковый номер элемента в запросе
            for (String par : params) {
                prepStatement.setString(ind, par);
                ind++;
            }
            rs = prepStatement.executeQuery();
        } catch (SQLException e) {
//            Log.error(e.getMessage() + "Query: " + QUERY);
            e.printStackTrace();
        }
        return rs;
    }

    // только для Strings
    ResultSet executeSelect(String QUERY) {
        try {
            return statement.executeQuery(QUERY);
        } catch (Exception e) {
            return null;
        }
    }


    void closeConnection() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

