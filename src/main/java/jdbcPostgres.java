import java.sql.*;


class jdbcPostgres{
    //  Database settings
////    private static final String DB_DRIVER = "org.postgresql.Driver";
//    private static String DB_DRIVER = Configuration.get("db_driver");
//    private static String DB_ROOT_URL = Configuration.get("db_root_url");
//    private static String DB_DEV = Configuration.get("db_dev");
//
//    private static String DB_URL = DB_ROOT_URL + DB_DEV;
//
//    // TODO вычитывать из конфига
//    private static String LOGIN = Configuration.get("login");
//    private static String PASSWORD = Configuration.get("password");







    private  String DB_DRIVER;
    private  String DB_ROOT_URL;
    private  String DB_DEV;
    private  String DB_URL;
    private  String LOGIN;
    private  String PASSWORD;

    private Connection connection;
    private Statement statement = null;


    jdbcPostgres() {
        DB_DRIVER = Configuration.get("db_driver");
        DB_ROOT_URL = Configuration.get("db_root_url");
        DB_DEV = Configuration.get("db_dev");
        DB_URL = DB_ROOT_URL + DB_DEV;

        LOGIN = Configuration.get("login");
        PASSWORD = Configuration.get("password");

//        DB_DRIVER = "org.postgresql.Driver";
//        DB_ROOT_URL = "jdbc:postgresql://127.0.0.1:5432/";
//        DB_DEV = "test_igor";
//        DB_URL = DB_ROOT_URL + DB_DEV;
//
//        LOGIN = "postgres";
//        PASSWORD = "postgres";

        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
//        finally { // ResultSet-ы возвращают null постоянно
//            Enumeration<Driver> drivers = DriverManager.getDrivers();
//            while (drivers.hasMoreElements()) {
//                Driver driver = drivers.nextElement();
//                try {
//                    DriverManager.deregisterDriver(driver);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }


    static void execute2(String QUERY, String[] params) {
        try {
            PreparedStatement prepStatement = connection.prepareStatement(QUERY);
            int index = 1;
            if (!(params == null)) {
                for (String par : params) {
                    prepStatement.setString(index, par);
                    index++;
                }
            }
            prepStatement.execute();
        } catch (SQLException ignored) {
        }
    }


    ResultSet execute(String QUERY) {
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

        } catch (SQLException ignored) {
        }
    }


    int getRowCount(ResultSet rs) {
        try {
            int count = 0;
            while (rs.next()) {
                count++;
            }
            return count;
        } catch (Exception ignored) {
            return -1;
        }
    }

}

