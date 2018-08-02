import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class Users {
    // TODO: вычитывать из HashMap-a после static вычитки из PostgreSQL
    static Map<String, String> registeredUsers =
            Collections.synchronizedMap(new HashMap<String, String>());

    static{
        jdbcPostgres psql = new jdbcPostgres();
        ResultSet rs = psql.executeSelect("SELECT * FROM users");
        try {
            if (rs!=null) {
                while (rs.next()) {
                    registeredUsers.put(rs.getString("login"), rs.getString("password"));
                }
            }
        }
        catch (SQLException ignored) {}
        finally { psql.closeConnection(); }
    }


    static boolean isRegisteredUser(String login, String password) {
        boolean lpNotEmpty = (!login.equals("") & !password.equals(""));
        return lpNotEmpty & password.equals(registeredUsers.get(login)); // true, если: данные не пусты, найденный пароль по логину совпадает с входящим
    }
}
