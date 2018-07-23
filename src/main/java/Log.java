import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    public static void main(String[] args){
//        write("INFO", "PSQL. Started success ..... ");
        write("TEST", "new client registered");
    }


    // Пример записи в лог-файле:
    // [23-07-2018 02:15:26] INFO >>   PSQL. Started success .....
    private static void write(String logType, String log){

        String date,
                part_A=logType, // Имя лог-файла: "{part_A}_{part_B}.log"   Прим: "error_01012018.log"
                part_B,
                logFileName;
        // Дата, используется для имени лог-файла и записей в нем
        date = new SimpleDateFormat("ddMMyyyy HH:mm:ss").format(new Date());

        // Создание log_{часть имени лог-файла}.log
        String[] dateAndTime = date.split(" ");
        String DMY = dateAndTime[0], // 01012018
               HMS = dateAndTime[1]; // 01:00:39

        logFileName = null;

        // Сформируем имя лог-файла
        // {имя}.log
        switch (logType){
            case "INFO":
                logFileName = "info_"; break;
            case "ERROR":
                logFileName = "error_"; break;
            case "WARN":
                logFileName = "warning_"; break;
        }
        if (logType!=null){
            logFileName +=date;
        }
        String timeNow = new SimpleDateFormat(date+" HH:mm:ss").format(new Date());
        String forWrite =
                "[" + timeNow + "] " + logType + " >>   " + log + "\n";
//        todo: реализовать логику работы с log-файлами на этапе инициализации (static block) класса,
//        todo: создавать файл если его нет
        try {
            Files.write(Paths.get(
                    "//home//nnm//"+logFileName+".log"), forWrite.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) { e.printStackTrace(); }
    }


}
