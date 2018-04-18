package com.imart.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.imart.shop.R;
import com.imart.shop.model.ItemMenu;

import java.util.List;

public class AdapterSeller extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<ItemMenu> list;

    public AdapterSeller(Context context, List<ItemMenu> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int location) {
        return list.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.produk_item, null);
        LinearLayout lyt = (LinearLayout) convertView.findViewById(R.id.lytqty);
        lyt.setVisibility(View.GONE);//hilangin button dan quantity
        TextView nama = (TextView) convertView.findViewById(R.id.textproduk);
        TextView harga = (TextView) convertView.findViewById(R.id.textharga);
        ItemMenu item = list.get(position);
        nama.setText(item.getNamaMenu());
        harga.setText("Rp " + item.getHarga());

        return convertView;
    }
}
