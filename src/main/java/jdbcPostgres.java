import java.sql.*;


class jdbcPostgres {
    //  Database credentials
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_ROOT_URL = "jdbc:postgresql://127.0.0.1:5432/";
    private static final String DB_DEV = "test_igor";
    //    private static final String DB_MASTER = "project";
    private static final String DB_URL = DB_ROOT_URL + DB_DEV;

    // TODO вычитывать из конфига
    final String USER = "postgres";
    final String PASS = "postgres";


    Connection connection;
    Statement statement = null;


    jdbcPostgres() {
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
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


    void execute2(String QUERY, String[] params) {
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

