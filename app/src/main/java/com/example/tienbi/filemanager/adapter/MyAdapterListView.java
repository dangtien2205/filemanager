package com.example.tienbi.filemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tienbi.filemanager.R;

/**
 * Created by TienBi on 24/09/2016.
 */
public class MyAdapterListView extends BaseAdapter {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String titles[];
    private int icons[];

    private String name;
    private int profile;
    private String email;

    Context context;
    LayoutInflater layoutInflater;

    public MyAdapterListView(Context context, String titles[], int icons[], String name, String email, int profile) {
        this.context = context;
        this.titles=titles;
        this.icons=icons;
        this.name=name;
        this.profile=profile;
        this.email=email;
    }


    @Override
    public int getCount() {
        return titles.length + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater = LayoutInflater.from(context);
        if (position == 0) {
            convertView = layoutInflater.inflate(R.layout.layout_header_navi, parent, false);
            ImageView image = (ImageView)convertView.findViewById(R.id.circleView);
            TextView txtName = (TextView) convertView.findViewById(R.id.name);
            TextView txtEmail = (TextView) convertView.findViewById(R.id.email);
            txtName.setText(name);
            txtEmail.setText(email);
            image.setImageResource(profile);
        }
        else {
            convertView = layoutInflater.inflate(R.layout.layout_item_navi, parent, false);
            ImageView icon = (ImageView)convertView.findViewById(R.id.rowIcon);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.rowText);
            icon.setImageResource(icons[position-1]);
            txtTitle.setText(titles[position-1]);
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}
