package com.imart.shop.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.imart.shop.R;
import com.imart.shop.model.DrawerMenuItem;

import java.util.List;

public class DrawerMenuItemAdapter extends ArrayAdapter<DrawerMenuItem> {

    private List<DrawerMenuItem> mItems;
    private Activity activity;
    private DrawerMenuItem objAllBean;
    private int row;

    public DrawerMenuItemAdapter(Activity act, int resource, List<DrawerMenuItem> arrayList) {
        super(act, resource, arrayList);
        this.activity = act;
        this.row = resource;
        this.mItems = arrayList;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(row, null);

            holder = new ViewHolder();
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if ((mItems == null) || ((position + 1) > mItems.size()))
            return view;

        objAllBean = mItems.get(position);

        holder.imgIcon = (ImageView) view.findViewById(R.id.icon);
        holder.tvTitle = (TextView) view.findViewById(R.id.title);
        holder.imgIcon.setImageResource(objAllBean.getIcon());
        holder.tvTitle.setText(objAllBean.getText());

        return view;
    }
    public class ViewHolder {

        public TextView tvTitle;
        public ImageView imgIcon;

    }

}
