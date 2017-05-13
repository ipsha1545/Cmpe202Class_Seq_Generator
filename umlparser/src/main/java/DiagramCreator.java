

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DiagramCreator {

    public static Boolean pngDiag(String g, String Path) {

        try {
            String urlneeded = "https://yuml.me/diagram/simple/class/draw/" + g
                    + ".png";
            URL urlneed = new URL(urlneeded);
            HttpURLConnection connection = (HttpURLConnection) urlneed.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException(
                        "Failed : The error code is : " + connection.getResponseCode());
            }
            OutputStream outS = new FileOutputStream(new File(Path));
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = connection.getInputStream().read(bytes)) != -1) {
                outS.write(bytes, 0, read);
            }
            outS.close();
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
