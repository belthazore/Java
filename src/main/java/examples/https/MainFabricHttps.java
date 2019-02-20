package examples.https;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.System.out;

public class MainFabricHttps {

    public static void main(String... args) {
        HttpsURLConnection httpsURLConnection = HttpsRequester.executePost("https://google.com", "{\"test\":\"fuck\"}");
        try {
            if (httpsURLConnection.getResponseCode() != 405) {
                InputStream is;
                is = httpsURLConnection.getInputStream();
                if (is != null) {
                    StringBuffer resp = new StringBuffer();
                    BufferedReader in = new BufferedReader(new InputStreamReader(is));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) { // Вычитка тела ответа в 'response'
                        resp.append(inputLine);
                    }
                    in.close();
                    out.println(resp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
