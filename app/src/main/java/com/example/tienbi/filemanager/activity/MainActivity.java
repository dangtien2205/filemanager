package com.example.tienbi.filemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tienbi.filemanager.adapter.MyAdapterListView;
import com.example.tienbi.filemanager.FileManager;
import com.example.tienbi.filemanager.R;
import com.example.tienbi.filemanager.model.MemorySize;
import com.example.tienbi.filemanager.util.FileUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    String TITLES[] = {"Home","Folder Favorite","Folder History","Cloud Data","Setting","Exit"};
    int ICONS[] = {R.drawable.ic_home_black_24dp,
            R.drawable.ic_star_black_24dp,
            R.drawable.ic_schedule_black_24dp,
            R.drawable.ic_cloud_queue_black_24dp,
            R.drawable.ic_build_black_24dp,
            R.drawable.ic_reply_black_24dp};

    String NAME = "tien bi";
    String EMAIL = "tienbi220595@gmail.com";
    int PROFILE = R.drawable.ic_no_user;

    private String extSdCardPath;
    private Toolbar toolbar;
    private TextView txtInternal;
    private ProgressBar pbInternal;
    private TextView txtExternal;
    private ProgressBar pbExternal;
    private LinearLayout layoutImage;
    private LinearLayout layoutMusic;
    private LinearLayout layoutVideo;
    private LinearLayout layoutDownLoad;

    ListView listView;
    MyAdapterListView myAdapterListView;
    DrawerLayout mDrawer;

    ActionBarDrawerToggle mDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControls();
        addEvents();
    }

    private void addEvents() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawer.closeDrawers();
            }
        });
    }

    private void addControls() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        txtInternal=(TextView)findViewById(R.id.txtInternal);
        txtExternal=(TextView)findViewById(R.id.txtExternal);
        pbInternal=(ProgressBar)findViewById(R.id.pb1);
        pbExternal=(ProgressBar)findViewById(R.id.pb2);
        layoutImage=(LinearLayout)findViewById(R.id.layout_image);
        layoutMusic=(LinearLayout)findViewById(R.id.layout_music);
        layoutVideo=(LinearLayout)findViewById(R.id.layout_video);
        layoutDownLoad=(LinearLayout)findViewById(R.id.layout_download);


        layoutImage.setOnClickListener(this);
        layoutMusic.setOnClickListener(this);
        layoutVideo.setOnClickListener(this);
        layoutDownLoad.setOnClickListener(this);
        (findViewById(R.id.layout_sdcard)).setOnClickListener(this);
        (findViewById(R.id.layout_extsdcard)).setOnClickListener(this);

        ((TextView)findViewById(R.id.numImage)).setText(FileManager.getInstance().getMediaFilesCnt(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)+"");
        ((TextView)findViewById(R.id.numMusic)).setText(FileManager.getInstance().getMediaFilesCnt(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)+"");
        ((TextView)findViewById(R.id.numMovie)).setText(FileManager.getInstance().getMediaFilesCnt(this, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)+"");

        onCreateToolBarMenu();
        memoryCard();
        extSdCardPath = FileUtils.getExtSdCardPath();
    }

    private void memoryCard(){
        double availableInternal = MemorySize.getAvailableInternalMemorySize();
        double totalInternal = MemorySize.getTotalInternalMemorySize();
        pbInternal.setProgress((int)(100-availableInternal/totalInternal*100));
        txtInternal.setText(String.format("%.2f GB",(totalInternal-availableInternal)/1024/1024/1024)+" / "+String.format("%.2f GB",totalInternal/1024/1024/1024));

        double availableExternal=MemorySize.getAvailableExternalMemorySize();
        double totalExternal=MemorySize.getTotalExternalMemorySize();
        //pbExternal.setProgress((int)(100-availableExternal/totalExternal*100));
        //txtExternal.setText(String.format("%.2f GB",(totalExternal-availableExternal)/1024/1024/1024)+" / "+String.format("%.2f GB",totalExternal/1024/1024/1024));
    }

    private void onCreateToolBarMenu(){
        listView=(ListView)findViewById(R.id.listView);
        myAdapterListView = new MyAdapterListView(this,TITLES,ICONS,NAME,EMAIL,PROFILE);
        listView.setAdapter(myAdapterListView);

        mDrawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawer,toolbar,R.string.openDrawer,R.string.closeDrawer){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

        };
        mDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_image:
                Intent intent = new Intent(this,LocaleFileGallery.class);
                intent.putExtra("TITLE","Image");
                startActivity(intent);
                break;
            case R.id.layout_music:
                Intent intent1 = new Intent(this,LocaleFileMedia.class);
                intent1.putExtra("TITLE","Music");
                intent1.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivity(intent1);
                break;
            case R.id.layout_video:
                Intent intent2 = new Intent(this,LocaleFileMedia.class);
                intent2.putExtra("TITLE","Video");
                intent2.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivity(intent2);
                break;
            case R.id.layout_download:
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    Intent intent3 = new Intent(this,LocaleFile.class);
                    intent3.putExtra("TITLE","DownLoad");
                    String base = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator;
                    String downloadPath = base+"download";
                    File file = new File(downloadPath);
                    if(!file.exists())
                        file.mkdir();
                    intent3.putExtra("startPath", downloadPath);
                    startActivity(intent3);
                }else{
                    Toast.makeText(this, getString(R.string.SDCardNotMounted), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layout_sdcard:
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    Intent intent4 = new Intent(this,LocaleFile.class);
                    intent4.putExtra("TITLE", getString(R.string.bxfile_sdcard));
                    intent4.putExtra("startPath", Environment.getExternalStorageDirectory().getAbsolutePath());
                    startActivity(intent4);
                }else{
                    Toast.makeText(this, getString(R.string.SDCardNotMounted), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layout_extsdcard:
                if(extSdCardPath==null){
                    Toast.makeText(this,"No external sd card",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent5 = new Intent(this,LocaleFile.class);
                intent5.putExtra("TITLE", getString(R.string.bxfile_extsdcard));
                intent5.putExtra("startPath", extSdCardPath);
                startActivity(intent5);
                break;
        }
    }
}
