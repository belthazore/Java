package examples.https;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;

import static java.lang.System.out;

public class HttpsRequester {

    public static HttpsURLConnection executePost(String url, String body) {
        HttpsURLConnection con = null;
        try {
            URL obj = new URL(url);
            con = (HttpsURLConnection) obj.openConnection();

            //Add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream dataOutpStr = new DataOutputStream(con.getOutputStream());

            dataOutpStr.writeBytes(body); // Выполнение запроса (!)
            dataOutpStr.flush();
            dataOutpStr.close();


            out.println("" +
                    "\nRequest >>\n" +
                    "Type: POST" +
                    "\nURL: " + url +
                    "\nParameters : " + body +
                    "\n\nResponse <<\n" +
                    "Code: " + con.getResponseCode() +
                    "\nMessage: " + con.getResponseMessage() +
                    "\n\n"
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
        return con;
    }

}