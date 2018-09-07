package hello;
import java.sql.*;

/*
    df
*/



class jdbcPostgres {


    /* Загружаем параметры подключения к БД из конфига
    private static String DB_DRIVER;
    private static String DB_ROOT_URL = Configuration.get("db_root_url");
    private static String DB_DEV = Configuration.get("db_dev");
    private static String DB_URL = DB_ROOT_URL + DB_DEV;
    private static String LOGIN = Configuration.get("login");
    private static String PASSWORD = Configuration.get("password");
    */

    private static String DB_DRIVER = "org.postgresql.Driver";
    private static String DB_ROOT_URL = "jdbc:postgresql://127.0.0.1:5432/";
    private static String DB_DEV = "test_igor";
    private static String DB_URL = DB_ROOT_URL + DB_DEV;

    private static String LOGIN = "postgres";
    private static String PASSWORD = "postgres";


    private static Connection connection;
    private Statement statement = null;


    jdbcPostgres(){
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
//            Log.error("jdbcPostgres: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Поиск контакта по ID
    ResultSet selectById(String QUERY, int id) {
        ResultSet rs = null;
        try {
            PreparedStatement prepStatement = connection.prepareStatement(QUERY);
            prepStatement.setInt(1, id);
            rs = prepStatement.executeQuery();
        } catch (SQLException e) {
//          Log.error(e.getMessage() + "Query: " + QUERY);
            e.printStackTrace();
        }
        return rs;
    }

    // Для добавления 3kk записей в contacts
    static void fillDb(String QUERY) {
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


    ResultSet select(String QUERY, String[] params) {
        Connection conn;
        ResultSet resultSet = null;
        try {
            conn = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            PreparedStatement prepStatement = conn.prepareStatement(QUERY);
            int ind = 1; // порядковый номер элемента в запросе
            for (String par : params) {
                prepStatement.setString(ind, par);
                ind++;
            }
            resultSet = prepStatement.executeQuery();
        } catch (SQLException e) {
//            Log.error(e.getMessage() + "Query: " + QUERY);
            e.printStackTrace();
        }
        return resultSet;
    }


    /*
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
    */


}

