package info.kingpes.phimpro.http;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by Chau Huynh on 5/21/2019.
 */

public class HandleAsync extends AsyncTask<String, Void, String> {
    private Callback callback;

    public interface Callback<T> {
        void response(T obj);
    }

    public HandleAsync(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            return GetUrl.execute(strings[0]);
        } catch (IOException e) {
            return "Unable to retrieve data. URL may be invalid.";
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        callback.response(null);
    }
}
