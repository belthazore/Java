
class Client {
    // TODO: вычитывать из HashMap-a после static вычитки из PostgreSQL
    static String correctLogin   ="l";
    static String correctPassword="p";


    static boolean isRegisteredClient(String login, String password){
        boolean isRegistered = false;

        if ( !login.equals("") & !password.equals("")   &   login.equals(correctLogin) & password.equals(correctPassword))
            isRegistered = true;

        return isRegistered;
    }

}
