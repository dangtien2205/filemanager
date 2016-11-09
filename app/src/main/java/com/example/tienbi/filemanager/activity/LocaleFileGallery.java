package com.example.tienbi.filemanager.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tienbi.filemanager.App;
import com.example.tienbi.filemanager.FileManager;
import com.example.tienbi.filemanager.R;
import com.example.tienbi.filemanager.SyncImageLoader;
import com.example.tienbi.filemanager.TFile;
import com.example.tienbi.filemanager.util.Utils;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.util.List;


public class LocaleFileGallery extends AppCompatActivity {

    private String tag = "LocaleFileGallery";
    private GridView gv;
    private MyGVAdapter adapter;
    private List<TFile> data;
    private TextView emptyView;
    private FileManager bfm;

    private List<TFile> choosedFiles;
    private SyncImageLoader syncImageLoader;
    private int gridSize;
    private AbsListView.LayoutParams gridItemParams;
    private boolean varList;
    private FloatingActionsMenu multiple_actions;
    private View checkView1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (1 == msg.what) {
                syncImageLoader = new SyncImageLoader();
                choosedFiles = bfm.getChoosedFiles();
                choosedFiles.clear();
                gridItemParams = new AbsListView.LayoutParams(gridSize, gridSize);
                adapter = new MyGVAdapter();
                gv.setAdapter(adapter);
                gv.setOnScrollListener(adapter.onScrollListener);
            } else if (0 == msg.what) {
                gv.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(getString(R.string.curCatagoryNoFiles));
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_image);

        addControls();
        addEvents();
    }

    private void addControls() {
        bfm = FileManager.getInstance();
        gv = (GridView) findViewById(R.id.gridView);
        emptyView = (TextView) findViewById(R.id.emptyView);
        gridSize = (Utils.getScreenWidth(this) - getResources().getDimensionPixelSize(R.dimen.view_8dp) * 5) / 4;
        varList = false;
        multiple_actions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        checkView1 = findViewById(R.id.checkView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_media);
        setSupportActionBar(toolbar);
        ((TextView)findViewById(R.id.txtTitle)).setText(getIntent().getStringExtra("TITLE") + "");
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addEvents() {
        findViewById(R.id.action_delete1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file;
                for (int i = 0; i < choosedFiles.size(); i++) {
                    file=new File(choosedFiles.get(i).getFilePath());
                    file.delete();
                }
                loadData();
                Toast.makeText(LocaleFileGallery.this,"Delete Success",Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.action_share1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                File imageFileToShare = new File(choosedFiles.get(0).getFilePath());
                Uri uri = Uri.fromFile(imageFileToShare);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share Image!"));
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (null == data) {
            loadData();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        if (null != data)
            data.clear();
        syncImageLoader = null;
        handler = null;
        data = null;
        adapter = null;
        super.onDestroy();
    }

    class MyGVAdapter extends BaseAdapter {

        AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        syncImageLoader.lock();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        loadImage();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        syncImageLoader.lock();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        };

        public void loadImage() {
            int start = gv.getFirstVisiblePosition();
            int end = gv.getLastVisiblePosition();
            if (end >= getCount()) {
                end = getCount() - 1;
            }
            syncImageLoader.setLoadLimit(start, end);
            syncImageLoader.unlock();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (null != data)
                return data.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (null == convertView) {
                convertView = LayoutInflater.from(LocaleFileGallery.this).inflate(R.layout.layout_item_image, null);
            }
            ImageView img = (ImageView) convertView.findViewById(R.id.img);
            img.setImageResource(R.drawable.bxfile_file_default_pic);

            View itemView = convertView.findViewById(R.id.itemView);
            itemView.setLayoutParams(gridItemParams);

            TFile bxfile = data.get(position);
            img.setTag(position);
            syncImageLoader.loadDiskImage(position, bxfile.getFilePath(), imageLoadListener);

            final View checkView = convertView.findViewById(R.id.checkView);

            if (choosedFiles.contains(bxfile)) {
                checkView.setVisibility(View.VISIBLE);
            } else {
                checkView.setVisibility(View.GONE);
            }
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    varList = true;
                    choosedImage(checkView, position);
                    multiple_actions.setVisibility(View.VISIBLE);
                    return false;
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (varList) {
                        choosedImage(checkView, position);
                        if (choosedFiles.size() == 0) {
                            varList = false;
                            multiple_actions.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + data.get(position).getFilePath()), "image/*");
                        startActivity(intent);
                    }
                }
            });
            return convertView;
        }


        SyncImageLoader.OnImageLoadListener imageLoadListener = new SyncImageLoader.OnImageLoadListener() {
            @Override
            public void onImageLoad(Integer t, Drawable drawable) {
                View view = gv.findViewWithTag(t);
                if (view != null) {
                    ImageView iv = (ImageView) view
                            .findViewById(R.id.img);
                    iv.setImageDrawable(drawable);
                } else {
                    Log.i(tag, "View not exists");
                }
            }

            @Override
            public void onError(Integer t) {
                View view = gv.findViewWithTag(t);
                if (view != null) {
                    ImageView iv = (ImageView) view
                            .findViewById(R.id.img);
                    iv.setImageResource(R.drawable.bxfile_file_default_pic);
                } else {
                    Log.i(tag, " onError View not exists");
                }
            }

        };
    }

    private void choosedImage(View checkView, int position) {
        TFile bxfile = data.get(position);
        if (choosedFiles.contains(bxfile)) {
            choosedFiles.remove(bxfile);
            checkView.setVisibility(View.GONE);
        } else {
            choosedFiles.add(bxfile);
            checkView.setVisibility(View.VISIBLE);
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
    private void loadData(){
        App bxApp = (App) getApplication();
        bxApp.execRunnable(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                data = bfm.getMediaFiles(LocaleFileGallery.this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (null != data)
                    handler.sendEmptyMessage(1);
                else
                    handler.sendEmptyMessage(0);
            }

        });
    }
}
