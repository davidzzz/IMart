package com.imart.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imart.shop.R;
import com.imart.shop.model.Cart;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ConfirmAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Cart> list;

    public ConfirmAdapter(Context context, ArrayList<Cart> list) {
        this.context = context;
        this.list = list;
    }

    public ArrayList<Cart> getList() { return list; }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int location) { return list.get(location); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_confirm, null);
        final Cart c = list.get(position);
        DecimalFormat format = new DecimalFormat();
        TextView teks = (TextView) convertView.findViewById(R.id.nama);
        teks.setText(c.getNamaMenu());
        TextView teksHarga = (TextView) convertView.findViewById(R.id.harga);
        teksHarga.setText(format.format(c.getHarga()));
        TextView teksSubtotal = (TextView) convertView.findViewById(R.id.subtotal);
        teksSubtotal.setText(format.format(c.getTotal()));
        TextView teksQty = (TextView) convertView.findViewById(R.id.qty);
        teksQty.setText(String.valueOf(c.getQuantity()));

        return convertView;
    }
}
