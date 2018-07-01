import java.sql.*;

import static java.lang.System.out;




public class jdbcPostgres {
    //  Database credentials
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_ROOT_URL = "jdbc:postgresql://127.0.0.1:5432/";
    private static final String DB_DEV = "test_igor";
    //    private static final String DB_MASTER = "project";
    private static final String DB_URL = DB_ROOT_URL + DB_DEV;

    // TODO вычитывать из конфига
    private static final String USER = "postgres";
    private static final String PASS = "postgres";


    private Connection connection;
    private Statement statement = null;
    private ResultSet resultSet= null;

    jdbcPostgres() {
        try {
            Class.forName(DB_DRIVER); //TODO: понять что именно тут происходит (Reflection API)
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            //out.println("Connection Failed");
            e.printStackTrace();
        }
    }


    // Выводит в консоль данные только двух колонок
    // TODO универсальным сделать
    public void printResultSet() {
        try {
            int columnCount = resultSet.getMetaData().getColumnCount();
            boolean emptyTable = true;
            while (resultSet.next()) { // пока есть следующие поля
                if (columnCount==0) break;
                emptyTable = false;
                int rowNumber = 1;
                String[] str = new String[10];
                while (rowNumber <= columnCount) { // выводим все записи(строки) таблицы
                    str[rowNumber] = resultSet.getString(rowNumber);
                    rowNumber++;
                }
                out.println(String.format("%-7s %-23s", str[1], str[2]));
            }
            if (emptyTable) {
                out.println("Result: empty table");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    String execute2(String QUERY) {
//        ResultSet execute(String QUERY) {
        try {
            statement.executeQuery(QUERY);
            return "good";
//            return statement.executeQuery(QUERY);
        } catch (Exception e) {
            e.printStackTrace(); //включить для дебага
//            err.println(e.getMessage());
            return e.getMessage()+"huy";
        }
    }

    ResultSet execute(String QUERY) {
        try {
            return statement.executeQuery(QUERY);
        } catch (Exception e) {
//            e.printStackTrace(); //включить для дебага
//            err.println(e.getMessage());
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



    int getRowCount(String tableName) throws Exception{
        try {
            ResultSet rs = statement.executeQuery("SELECT count(*) FROM "+tableName);
            rs.next();
            return Integer.parseInt(rs.getString(1));
        } catch (Exception e) {
            throw new Exception("Error: getRowCount()");
        }
    }

    int getRowCount(ResultSet rs) throws Exception{
        try {
            int count=0;
            while (rs.next()){
                count++;
            }
            return count;
        } catch (Exception e) {
            return -1;
        }
    }



    int getColumnCount(String tableName) throws Exception{
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM "+tableName);
            rs.next();
            return rs.getMetaData().getColumnCount();
        } catch (Exception e) {
            throw new Exception("Error: getColumnCount()");
        }
    }
}

