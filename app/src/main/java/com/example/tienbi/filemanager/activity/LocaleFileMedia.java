package com.example.tienbi.filemanager.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tienbi.filemanager.adapter.FileAdapter;
import com.example.tienbi.filemanager.App;
import com.example.tienbi.filemanager.FileManager;
import com.example.tienbi.filemanager.R;
import com.example.tienbi.filemanager.TFile;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class LocaleFileMedia extends AppCompatActivity implements OnItemClickListener {

    private ListView lv;
    private List<TFile> data;
    private FileAdapter adapter;
    private TextView emptyView;
    private FileManager bfm;
    private boolean varCheck;
    private FloatingActionsMenu multiple_action;
    private List<TFile> choosedFiles;
    private String title;
    private Uri uri;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (1 == msg.what) {
                lv.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter = new FileAdapter(data, LocaleFileMedia.this, null, null);
                adapter.setOnListenerCheckBox(new FileAdapter.OnListenerCheckBox() {
                    @Override
                    public void onCheck(int pos) {
                        if(!varCheck){
                            varCheck = true;
                            multiple_action.setVisibility(View.VISIBLE);
                        }else {
                        }
                        choosedFile(pos);
                        if(choosedFiles.size()==0){
                            varCheck = false;
                            multiple_action.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                lv.setAdapter(adapter);
            } else if (0 == msg.what) {
                lv.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(getString(R.string.curCatagoryNoFiles));
            }
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_media);

        initViews();
        initData();
        initEvents();
    }

    private void initEvents() {
        findViewById(R.id.action_delete2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file;
                for (int i = 0; i < choosedFiles.size(); i++) {
                    file = new File(choosedFiles.get(i).getFilePath());
                    file.delete();
                }
                loadData();
                Toast.makeText(LocaleFileMedia.this, "Delete Success", Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.action_share2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType(title + "/*");
                File imageFileToShare = new File(choosedFiles.get(0).getFilePath());
                Uri uri = Uri.fromFile(imageFileToShare);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share " + title));
            }
        });
    }

    private void initData() {
        // TODO Auto-generated method stub
        Intent intent = getIntent();
        title = intent.getStringExtra("TITLE");
        setTitle(title);
        uri = intent.getData();
        setData();
    }

    private void setData() {
        bfm = FileManager.getInstance();
        loadData();
    }

    private void initViews() {
        // TODO Auto-generated method stub
        TextView curDir = (TextView) findViewById(R.id.curDir);
        curDir.setVisibility(View.GONE);
        lv = (ListView) findViewById(R.id.listView);
        lv.setOnItemClickListener(this);
        emptyView = (TextView) findViewById(R.id.emptyView);
        varCheck = false;
        multiple_action = (FloatingActionsMenu) findViewById(R.id.multiple_actions2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_media);
        setSupportActionBar(toolbar);
        ((TextView) findViewById(R.id.txtTitle)).setText(getIntent().getStringExtra("TITLE") + "");
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,int position, long arg3) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        switch (title) {
            case "Music":
                intent.setDataAndType(Uri.parse("file://" + data.get(position).getFilePath()), "audio/*");
                break;
            case "Video":
                intent.setDataAndType(Uri.parse("file://" + data.get(position).getFilePath()), "video/*");
                break;
        }
        startActivity(intent);
    }

    private void choosedFile(int position) {
        TFile bxfile = data.get(position);
        choosedFiles = bfm.getChoosedFiles();
        if (choosedFiles.contains(bxfile)) {
            choosedFiles.remove(bxfile);
        } else {
            choosedFiles.add(bxfile);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        App app = (App) getApplication();
        app.execRunnable(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                data = bfm.getMediaFiles(LocaleFileMedia.this, uri);
                if (null != data) {
                    Collections.sort(data);
                    handler.sendEmptyMessage(1);
                } else
                    handler.sendEmptyMessage(0);
            }

        });
    }
}
