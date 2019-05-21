package info.kingpes.phimpro;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.kingpes.phimpro.extractor.VideoMeta;
import info.kingpes.phimpro.extractor.YouTubeExtractor;
import info.kingpes.phimpro.extractor.YtFile;
import info.kingpes.phimpro.http.GetUrl;
import info.kingpes.phimpro.http.HandleAsync;
import info.kingpes.phimpro.http.PostUrl;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.edtApiKey)
    EditText edtApiKey;
    @BindView(R.id.edtPlaylistId)
    EditText edtPlaylistId;
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.lstStatus)
    RecyclerView lstStatus;
    @BindView(R.id.btnOK)
    Button btnOk;
    private ListAdapter adapter;

    private static String nextPageToken = "";
    private static String playlistId;
    private static String apiKey;
    private static String category = "ABC";
    private static List<Status> lst = new ArrayList<>();
    private static List<Video> lstVideo = new ArrayList<>();
    private int itag18 = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initList();
    }

    private void initList() {
        lstStatus.setHasFixedSize(true);
        lstStatus.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListAdapter(lst);
        lstStatus.setAdapter(adapter);

        adapter.setOnItemClick(new ListAdapter.OnItemClick() {
            @Override
            public void onClick(int p) {

            }
        });
    }

    public void onClick(View view) {
        playlistId = "PLW3CkIq_CINs4QcnuvDTx2QMllzg9jevQ";//"PL17y3MB-KE4_hOwEOgJBGZmwpkBkA87IE";//edtPlaylistId.getText().toString();
        apiKey = "AIzaSyBnSYRaGovmp9dAwNpy1oQCy_hM0ynXRBU";//edtApiKey.getText().toString();
        btnOk.setVisibility(View.GONE);
        getPlaylist();
    }

    private String getPlaylistLink() {
        return "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + playlistId + "&key=" + apiKey + "&maxResults=50&pageToken=" + nextPageToken;
    }

    private void getPlaylist() {
        new GetTask(this).execute(getPlaylistLink());
    }

    private static class GetTask extends AsyncTask<String, Integer, String> {
        private WeakReference<MainActivity> activityReference;

        GetTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            pd = ProgressDialog.show(activity, "", "Loading", true, false);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                InputStream inputStream = null;
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(strings[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    inputStream = conn.getInputStream();

                    InputStreamReader reader = new InputStreamReader(inputStream, "UTF8");
                    StringWriter writer = new StringWriter();
                    int n;
                    char[] buffer = new char[1024 * 4];
                    while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
                    return writer.toString();
                } finally {
                    if (inputStream != null) inputStream.close();
                    if (conn != null) conn.disconnect();
                }
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            pd.dismiss();

            try {

                JSONObject obj = new JSONObject(result);
                if (!obj.isNull("nextPageToken"))
                    nextPageToken = obj.getString("nextPageToken");

                JSONArray items = new JSONArray(obj.getString("items"));
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    JSONObject snippet = new JSONObject(item.getString("snippet"));
                    String title = snippet.getString("title");
                    String description = snippet.getString("description");
                    JSONObject resourceId = new JSONObject(snippet.getString("resourceId"));
                    String videoId = resourceId.getString("videoId");

                    lstVideo.add(new Video(videoId, title, description));
                }

                if (!nextPageToken.equals("")) {
                    activity.getPlaylist();
                    nextPageToken = "";
                } else {
                    activity.youtubeExtract();
                }

            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
            }
        }
    }

    private void youtubeExtract() {
        tvStatus.setText(("Tổng Cộng: " + lstVideo.size() + " Video"));
        for (final Video video : lstVideo) {
            String youtubeLink = "https://www.youtube.com/watch?v=" + video.getVideoId();
            new YouTubeExtractor(this, new YouTubeExtractor.Callback() {
                @Override
                public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                    if (ytFiles != null && ytFiles.get(itag18).getUrl() != null) {

                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("sn", video.getVideoId());
                            obj.put("name", video.getTitle());
                            obj.put("link", ytFiles.get(itag18).getUrl());
                            obj.put("cate", category);
                            obj.put("des", video.getDescription());
                            new PostTask(new PostTask.Callback() {
                                @Override
                                public void onFinish(int i) {
                                    if (i == 1)
                                        lst.add(new info.kingpes.phimpro.Status(video.getVideoId(), "Ok"));
                                    else
                                        lst.add(new info.kingpes.phimpro.Status(video.getVideoId(), "Fail"));

                                    adapter.notifyDataSetChanged();
                                }
                            }).execute(obj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).extract(youtubeLink, true, true);
        }
    }

    private static class PostTask extends AsyncTask<String, Integer, String> {
        public interface Callback {
            void onFinish(int i);
        }
        private Callback callback;

        PostTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put("pro", strings[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                InputStream inputStream = null;
                OutputStream outputStream = null;
                HttpURLConnection conn = null;
                try {
                    URL url = new URL("http://192.168.1.36:4000/admin/new-product.html");
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

                    InputStreamReader reader = new InputStreamReader(inputStream, "UTF8");
                    StringWriter writer = new StringWriter();
                    int n;
                    char[] buffer = new char[1024 * 4];
                    while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
                    return writer.toString();
                } finally {
                    if (inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                    if (conn != null) conn.connect();
                }
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject obj = new JSONObject(result);
                int insert = obj.getInt("insert");
                if (callback != null) callback.onFinish(insert);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
