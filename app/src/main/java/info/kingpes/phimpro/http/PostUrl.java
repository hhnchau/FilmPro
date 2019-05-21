package info.kingpes.phimpro.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Chau Huynh on 5/20/2019.
 */

public class PostUrl {
    public static String execute(String myUrl, String json) throws IOException, JSONException {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("pro", json);

        InputStream inputStream = null;
        OutputStream outputStream = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(myUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("secret", "qwerty");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            outputStream = conn.getOutputStream();
            outputStream.write(jsonParam.toString().getBytes("UTF-8"));

            inputStream = conn.getInputStream();
            return readInputStream(inputStream);

        } finally {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (conn != null) conn.connect();
        }
    }

    private static String readInputStream(InputStream stream) throws IOException {
        int n;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer)))
            writer.write(buffer, 0, n);
        return writer.toString();
    }
}
