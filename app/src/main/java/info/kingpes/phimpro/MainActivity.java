package info.kingpes.phimpro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.edtApiKey)
    EditText edtApiKey;
    @BindView(R.id.edtPlaylistId)
    EditText edtPlaylistId;
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.lstStatus)
    RecyclerView lstStatus;

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
        List<Status> lst = new ArrayList<>();
        lst.add(new Status("chau", "OK"));
        lst.add(new Status("hoang", "OK"));
        lst.add(new Status("bao", "OK"));
        lst.add(new Status("lan", "OK"));
        ListAdapter adapter = new ListAdapter(lst);
        lstStatus.setAdapter(adapter);

        adapter.setOnItemClick(new ListAdapter.OnItemClick() {
            @Override
            public void onClick(int p) {

            }
        });
    }

    public void onClick(View view) {
        Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
    }
}
