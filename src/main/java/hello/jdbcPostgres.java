package hello;
import java.sql.*;




class jdbcPostgres {

    private String DB_DRIVER = "org.postgresql.Driver";
    private static String DB_ROOT_URL = "jdbc:postgresql://127.0.0.1:5432/";
    private static String DB_DEV = "test_igor";
    private static String DB_URL = DB_ROOT_URL + DB_DEV;

    private static String LOGIN = "postgres";
    private static String PASSWORD = "postgres";


    private static Connection connection;
    private static PreparedStatement prepStatement;
    private static ResultSet resultSet;


    {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    // Поиск контакта по ID
    ResultSet selectById(String QUERY, int id) {
        try {
            prepStatement = getPrepStatement(QUERY);
            prepStatement.setInt(1, id);
            resultSet = prepStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    ResultSet select(String QUERY, String[] params) {
        try {
            prepStatement = getPrepStatement(QUERY);
            int ind = 1; // порядковый номер элемента в запросе
            for (String par : params) {
                prepStatement.setString(ind, par);
                ind++;
            }
            resultSet = prepStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Для добавления 3kk записей в contacts
    static void fillDb(String QUERY) {
        try {
            prepStatement = getPrepStatement(QUERY);
            prepStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }


    private static PreparedStatement getPrepStatement(String QUERY) {
        PreparedStatement prepState = null;
        try {
            connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            prepState = connection.prepareStatement(QUERY);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prepState;
    }


    /*
    Закрыть: connection, prepStatement, resultSet
    */
    static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
            if (prepStatement != null) {
                prepStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}