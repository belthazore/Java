import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;



class Log {

//    static String PATH_TO_FOLDER = ".//logs"; //TODO. Сейчас это вызывает java.nio.file.AccessDeniedException
    private static String PATH_TO_FOLDER = "//opt//tomcat//webapps//logs";
    private static String PATH_TO_FILE = null;

    //TODO реализовать ограничение на N-запросов записи в N2-секунд
    // Чтобы не выело место на харде




    static void error(String logMsg){   write("ERROR", logMsg); }
    static void warning(String logMsg){ write("WARN",  logMsg); }
    static void info(String logMsg){    write("INFO",  logMsg); }
    static void transition(String locationClassComment, HttpServletRequest request){
        String URL, reqQueryString;
        reqQueryString = request.getQueryString();
        URL = request.getRequestURL() + (reqQueryString!=null ? ("?" + reqQueryString) : ""); // хак, позволяющий получить полный урл с параметрами если они есть
        String logMsg = locationClassComment + ": " + request.getRemoteAddr() + " | port " + request.getRemotePort() + " | URL " + URL;
        write("INFO",  logMsg); } //записать действие посетителя



    // Запись в лог
    // Пример:
    // [23.07.18 02:15:26] INFO  PSQL. Started success .....
    private static void write(String logTypeUpperCase, String logMsg){ // write("INFO", "PSQL. Started success .....")
        String DateTime,
                logTypeLowerCase,
                logMsgFinal;

        DateTime = new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new Date());
        logTypeLowerCase = logTypeUpperCase.toLowerCase(); // "INFO" >> "info"
        createFILEifNeed(logTypeLowerCase);
        logMsgFinal = "[" + DateTime + "] " + logTypeUpperCase + "  " + logMsg + "\n";

        try {
            Files.write(Paths.get(PATH_TO_FILE),
                    logMsgFinal.getBytes(),
                    StandardOpenOption.APPEND);
        } catch (IOException e) { e.printStackTrace(); }
    }







    //  Создать лог-файл, если такового еще нет
    //  Прим. имени: "error_010118.log"
    private static void createFILEifNeed(String logType){
        String FILE_NAME,
                DDMMYY;
        boolean logFileNotPresent;

        createFOLDERifNeed();

        DDMMYY = new SimpleDateFormat("ddMMyy").format(new Date());
        FILE_NAME = logType + "_" + DDMMYY + ".log"; // "error_010118.log"
        PATH_TO_FILE = PATH_TO_FOLDER + "//" + FILE_NAME;
        logFileNotPresent = ! new File(PATH_TO_FILE).exists();

        try {
            if (logFileNotPresent) Files.createFile(Paths.get(PATH_TO_FILE));
        } catch (IOException e) { e.printStackTrace(); }
    }





    //  Создать лог-папку, если такой еще нет
    private static void createFOLDERifNeed(){
        File path = new File(PATH_TO_FOLDER);

        if(!path.exists()) // создадим папку, если таковой нет
            path.mkdirs();
    }

}
