package com.example.tienbi.filemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tienbi.filemanager.FileManager;
import com.example.tienbi.filemanager.R;
import com.example.tienbi.filemanager.SyncImageLoader;
import com.example.tienbi.filemanager.TFile;

import java.util.List;

public class FileAdapter extends BaseAdapter {

    private FileManager bfm;
    private List<TFile> data;
    private Context cxt;
    private List<TFile> choosedFiles;
    int w;
    private SyncImageLoader syncImageLoader;
    private SyncImageLoader.OnImageLoadListener imageLoadListener;
    private OnListenerCheckBox onListenerCheckBox;

    public void setOnListenerCheckBox(OnListenerCheckBox onListenerCheckBox) {
        this.onListenerCheckBox = onListenerCheckBox;
    }

    public FileAdapter(List<TFile> data, Context cxt, SyncImageLoader syncImageLoader, SyncImageLoader.OnImageLoadListener imageLoadListener) {
        super();
        this.data = data;
        this.cxt = cxt;
        this.syncImageLoader = syncImageLoader;
        this.imageLoadListener = imageLoadListener;
        bfm = FileManager.getInstance();
        choosedFiles = bfm.getChoosedFiles();
        w = cxt.getResources().getDimensionPixelSize(R.dimen.view_36dp);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (null != data)
            return data.size();
        return 0;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        if (null == view) {
            view = LayoutInflater.from(cxt).inflate(R.layout.layout_item_media, null);
        }

        View fileView = view.findViewById(R.id.fileLl);

        view.setTag(pos);
        final TFile bxFile = data.get(pos);
        fileView.setVisibility(View.VISIBLE);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.fileCheckBox);
        ImageView fileType = (ImageView) view.findViewById(R.id.fileType);
        TextView fileName = (TextView) view.findViewById(R.id.fileName);
        TextView fileModifyDate = (TextView) view.findViewById(R.id.fileModifyDate);
        fileName.setText(bxFile.getFileName());
        fileModifyDate.setText(bxFile.getLastModifyTimeStr());
        if (!bxFile.isDir()) {
            TextView fileSize = (TextView) view.findViewById(R.id.fileSize);
            fileSize.setText(bxFile.getFileSizeStr());
            if (bxFile.getMimeType().equals(TFile.MimeType.IMAGE)) {
                fileType.setImageResource(R.drawable.bxfile_file_default_pic);
                if (null != syncImageLoader && null != imageLoadListener)
                    syncImageLoader.loadDiskImage(pos, bxFile.getFilePath(), imageLoadListener);
            } else {
                fileType.setImageResource(bfm.getMimeDrawable(bxFile.getMimeType()));
            }
        } else {
            fileType.setImageResource(R.drawable.bxfile_file_dir);
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onListenerCheckBox.onCheck(pos);
            }
        });
        return view;
    }

    public interface OnListenerCheckBox {
        void onCheck(int pos);
    }
}
