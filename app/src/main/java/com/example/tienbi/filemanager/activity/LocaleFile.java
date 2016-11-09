package com.example.tienbi.filemanager.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tienbi.filemanager.adapter.BottomSheetAdapter;
import com.example.tienbi.filemanager.adapter.FileAdapter;
import com.example.tienbi.filemanager.App;
import com.example.tienbi.filemanager.FileManager;
import com.example.tienbi.filemanager.R;
import com.example.tienbi.filemanager.SyncImageLoader;
import com.example.tienbi.filemanager.TFile;
import com.example.tienbi.filemanager.util.FileUtils;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocaleFile extends AppCompatActivity implements OnItemClickListener, AdapterView.OnItemLongClickListener {
    private String tag = "LocaleFile";
    private File curFile;
    private String startPath;
    private TextView curDir;
    private ListView lv;
    private List<TFile> data;
    private FileAdapter adapter;
    private TextView emptyView;
    private FileManager bfm;
    private boolean varCheck;
    private List<TFile> choosedFiles;

    private SyncImageLoader.OnImageLoadListener imageLoadListener;
    private SyncImageLoader syncImageLoader;
    private AbsListView.OnScrollListener onScrollListener;
    private TFile fileSelection;

    private int firstImageFileIndex;
    private GridView bottomSheet;
    private ArrayAdapter<Integer> bottomSheetAdapter;
    private BottomSheetBehavior sheetBehavior;
    private Integer[] bottomItems = {R.drawable.ic_copy,
            R.drawable.ic_cut,
            R.drawable.ic_like,
            R.drawable.ic_delete,
            R.drawable.ic_select_all};

    private SearchView mSearchView;
    private MenuItem searchMenuItem;
    private FloatingActionButton btnPaste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_media);
        bfm = FileManager.getInstance();
        initViews();
        initData();
        initEvents();
    }

    private void initData() {
        // TODO Auto-generated method stub
        Intent intent = getIntent();
        setTitle(intent.getStringExtra("title"));
        startPath = intent.getStringExtra("startPath");
        if (!FileUtils.isDir(startPath)) {
            startPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        syncImageLoader = new SyncImageLoader();
        imageLoadListener = new SyncImageLoader.OnImageLoadListener() {
            @Override
            public void onImageLoad(Integer t, Drawable drawable) {
                View view = lv.findViewWithTag(t);
                if (view != null) {
                    ImageView iv = (ImageView) view
                            .findViewById(R.id.fileType);
                    iv.setImageDrawable(drawable);
                } else {
                    Log.i(tag, "View not exists");
                }
            }

            @Override
            public void onError(Integer t) {
                View view = lv.findViewWithTag(t);
                if (view != null) {
                    ImageView iv = (ImageView) view
                            .findViewById(R.id.fileType);
                    iv.setImageResource(R.drawable.bxfile_file_unknow);
                } else {
                    Log.i(tag, " onError View not exists");
                }
            }
        };
        onScrollListener = new AbsListView.OnScrollListener() {
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
        setData(startPath);
    }

    private void copyFile(int n) {
        btnPaste.setVisibility(View.VISIBLE);
        App.setVarCopyCut(n);
        App.setSelectionCopy(choosedFiles);
        Toast.makeText(this, "Save ClipBroad", Toast.LENGTH_SHORT).show();
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        varCheck = false;
        unSelectAll();
    }

    private void initEvents() {
        bottomSheet.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        copyFile(0);
                        break;
                    case 1:
                        copyFile(1);
                        break;
                    case 2:
                        break;
                    case 3:
                        File file;
                        boolean result = false;
                        for (int i = 0; i < choosedFiles.size(); i++) {
                            file = new File(choosedFiles.get(i).getFilePath());
                            if (file.isDirectory()) {
                                String[] children = file.list();
                                for (int j = 0; j < children.length; j++) {
                                    new File(file, children[j]).delete();
                                }
                            }
                            result = file.delete();
                        }
                        setData(curDir.getText().toString());
                        if (result)
                            Toast.makeText(LocaleFile.this, "Delete Success", Toast.LENGTH_LONG).show();
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        varCheck = false;
                        unSelectAll();
                        break;
                    case 4:
                        selectAll();
                        break;
                }
            }
        });
        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPaste.setVisibility(View.INVISIBLE);
                List<TFile> list = App.getSelectionCopy();
                for (int i = 0; i < list.size(); i++) {
                    File file = new File(list.get(i).getFilePath());
                    try {
                        copy(file, curDir.getText().toString() + "/" + list.get(i).getFileName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (App.getVarCopyCut() == 1) {
                        if (file.isDirectory()) {
                            String[] children = file.list();
                            for (int j = 0; j < children.length; j++) {
                                new File(file, children[j]).delete();
                            }
                        }
                        file.delete();
                    }
                }
                Toast.makeText(LocaleFile.this, "Copy Success", Toast.LENGTH_SHORT).show();
                App.onClearList();
                setData(curDir.getText().toString());
            }
        });
    }

    public void copy(File src, String urlFileTo) throws IOException {
        File dst = new File(urlFileTo);
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public void loadImage() {
        int start = lv.getFirstVisiblePosition();
        int end = lv.getLastVisiblePosition();
        if (end < firstImageFileIndex) {
            return;
        }
        if (start < firstImageFileIndex)
            start = firstImageFileIndex;
        if (end >= data.size()) {
            end = data.size() - 1;
        }
        syncImageLoader.setLoadLimit(start, end);
        syncImageLoader.unlock();
    }

    private void setData(String dirPath) {
        curDir.setText(dirPath);
        curFile = new File(dirPath);
        File[] childs = curFile.listFiles();
        if (null == childs || 0 == childs.length) {
            lv.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            lv.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            if (null != data)
                data.clear();
            else
                data = new ArrayList<TFile>();
            for (File f : childs) {
                TFile.Builder builder = new TFile.Builder(f.getAbsolutePath());
                TFile bxfile = builder.build();
                if (null != bxfile)
                    data.add(bxfile);
            }
            Collections.sort(data);
            initFirstFileIndex();
            if (null == adapter) {
                syncImageLoader.restore();
                adapter = new FileAdapter(data, this, syncImageLoader, imageLoadListener);
                adapter.setOnListenerCheckBox(new FileAdapter.OnListenerCheckBox() {
                    @Override
                    public void onCheck(int position) {
                        if (!varCheck) {
                            varCheck = true;
                            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                        TFile bxfile = data.get(position);
                        choosedFiles = bfm.getChoosedFiles();
                        if (choosedFiles.contains(bxfile)) {
                            choosedFiles.remove(bxfile);
                        } else {
                            choosedFiles.add(bxfile);
                        }
                        if (choosedFiles.size() == 0) {
                            varCheck = false;
                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }
                });
                lv.setAdapter(adapter);
                lv.setOnScrollListener(onScrollListener);
                loadImage();
            } else {
                syncImageLoader.restore();
                loadImage();
                adapter.notifyDataSetChanged();
                lv.setSelection(0);
            }
        }
    }

    private void initFirstFileIndex() {
        firstImageFileIndex = -1;
        for (int i = 0; i < data.size(); i++) {
            TFile f = data.get(i);
            if (!f.isDir() && f.getMimeType().equals(TFile.MimeType.IMAGE)) {
                firstImageFileIndex = i;
                return;
            }
        }
    }

    private void initViews() {
        // TODO Auto-generated method stub
        varCheck = false;
        curDir = (TextView) findViewById(R.id.curDir);
        lv = (ListView) findViewById(R.id.listView);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
        registerForContextMenu(lv);
        emptyView = (TextView) findViewById(R.id.emptyView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_media);
        setSupportActionBar(toolbar);
        ((TextView) findViewById(R.id.txtTitle)).setText(getIntent().getStringExtra("TITLE") + "");
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bottomSheet = (GridView) findViewById(R.id.bottom_sheet);
        bottomSheetAdapter = new BottomSheetAdapter(this, R.layout.layout_item_grid, bottomItems);
        bottomSheet.setAdapter(bottomSheetAdapter);

        bottomSheet.setTranslationY(getStatusBarHeight());
        sheetBehavior = BottomSheetBehavior.from(bottomSheet);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            boolean first = true;

            @Override
            public void onStateChanged(View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                if (first) {
                    first = false;
                    bottomSheet.setTranslationY(0);
                }
            }
        });
        btnPaste = (FloatingActionButton) findViewById(R.id.btnPaste);
        if (App.getSelectionCopy() != null) {
            btnPaste.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bfm.clear();
        TFile bxfile = data.get(position);
        if (bxfile.isDir()) {
            setData(bxfile.getFilePath());
            ((TextView) findViewById(R.id.txtTitle)).setText(bxfile.getFileName() + "");
        }
        unSelectAll();
    }

    @Override
    public void onBackPressed() {
        bfm.clear();
        if (startPath.equals(curFile.getAbsolutePath())) {
            super.onBackPressed();
        } else {
            ((TextView) findViewById(R.id.txtTitle)).setText(curFile.getParentFile().getName());
            setData(curFile.getParentFile().getAbsolutePath());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                bfm.clear();
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (startPath.equals(curFile.getAbsolutePath())) {
                    finish();
                } else {
                    ((TextView) findViewById(R.id.txtTitle)).setText(curFile.getParentFile().getName());
                    setData(curFile.getParentFile().getAbsolutePath());
                    unSelectAll();
                }
                break;
            case R.id.action_add_folder:
                openDialog();
                break;
            case R.id.action_sort:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        //mSearchView.setOnQueryTextListener(listener);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.contextmenu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuEditName:
                openDialogEditName();
                break;
            case R.id.mnuCopy:
                bfm.clear();
                choosedFiles = bfm.getChoosedFiles();
                choosedFiles.add(fileSelection);
                copyFile(0);
                break;
            case R.id.mnuCut:
                bfm.clear();
                choosedFiles = bfm.getChoosedFiles();
                choosedFiles.add(fileSelection);
                copyFile(1);
                break;
            case R.id.mnuDelete:
                File file = new File(fileSelection.getFilePath());
                if (file.isDirectory()) {
                    String[] children = file.list();
                    for (int j = 0; j < children.length; j++) {
                        new File(file, children[j]).delete();
                    }
                }
                file.delete();
                setData(curDir.getText().toString());
                break;
            case R.id.mnuProperties:
                openDialogProperties();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void openDialog() {
        final EditText edittext = new EditText(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Enter Name New File");
        alert.setTitle("Notification");
        alert.setView(edittext,50,50,50,50);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                File folder = new File(curDir.getText().toString() + "/" + edittext.getText());
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                if (success) {
                    Toast.makeText(LocaleFile.this, "Directory Created", Toast.LENGTH_SHORT).show();
                    setData(curDir.getText().toString());
                } else {
                    Toast.makeText(LocaleFile.this, "Failed - Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void openDialogEditName() {
        final EditText edittext = new EditText(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Name File");
        alert.setView(edittext);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                File from = new File(fileSelection.getFilePath());
                File to;
                if (!from.isDirectory())
                    to = new File(fileSelection.getFilePath().replace(fileSelection.getFileName().substring(0, fileSelection.getFileName().lastIndexOf(".")), edittext.getText().toString()));
                else
                    to = new File(fileSelection.getFilePath().replace(fileSelection.getFileName(), edittext.getText().toString()));
                if (from.exists()) {
                    from.renameTo(to);
                    Toast.makeText(LocaleFile.this, "Edit Name Success", Toast.LENGTH_LONG).show();
                    setData(curDir.getText().toString());
                }
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void openDialogProperties() {
        TextView edittext = new TextView(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Properties");
        String str="Name :"+fileSelection.getFileName()+"\n"+
                   "String Path :"+fileSelection.getFilePath()+"\n"+
                   "Size :"+fileSelection.getFileSizeStr()+"\n"+
                   "Type :"+fileSelection.getMimeType()+"\n"+
                   "Time :"+fileSelection.getLastModifyTimeStr();
        edittext.setText(str);

        alert.setView(edittext,50,50,50,50);

        alert.show();
    }

    private void selectAll() {
        for (int i = 0; i < lv.getChildCount(); i++) {
            View v = lv.getChildAt(i);
            CheckBox cv = (CheckBox) v.findViewById(R.id.fileCheckBox);
            cv.setChecked(true);
        }
        choosedFiles = data;
    }

    private void unSelectAll() {
        for (int i = 0; i < lv.getChildCount(); i++) {
            View v = lv.getChildAt(i);
            CheckBox cv = (CheckBox) v.findViewById(R.id.fileCheckBox);
            cv.setChecked(false);
        }
        bfm.clear();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        fileSelection = data.get(position);
        return false;
    }
}
