package com.ushare.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.ushare.FragOrderProses;
import com.ushare.R;
import com.ushare.TabActivity;
import com.ushare.model.ItemOrder;

import java.util.List;

public class OrderAdapter extends BaseAdapter {
    private Context activity;
    private Fragment fragment;
    private LayoutInflater inflater;
    private List<ItemOrder> itemList;
    private String order_id;
    public OrderAdapter(Activity activity, List<ItemOrder> itemList, Fragment fragment) {
        this.activity = activity;
        this.itemList = itemList;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int location) {
        return itemList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_order, null);
        Button btnCancel =(Button)convertView.findViewById(R.id.btnCancel);
        TextView name = (TextView) convertView.findViewById(R.id.textTitle);
        TextView status = (TextView) convertView.findViewById(R.id.txtStatus);
        TextView txtTotal = (TextView) convertView.findViewById(R.id.txtTotal);
        TextView txtTime = (TextView) convertView.findViewById(R.id.txtTime);
        final ItemOrder item = itemList.get(position);
        name.setText(item.getNama()+" ");
        if(item.getStatus().equals("PROCESSED")){
            btnCancel.setText("Accept Delivery");
        }else{
            btnCancel.setText("CANCEL");
        }
        status.setText(item.getStatus());
        txtTotal.setText("Rp "+item.getTotal());
        txtTime.setText(item.getTanggal().replace("-","/"));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order_id = item.getId();
                if (item.getStatus().equals("PROCESSED")) {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                    alert.setTitle(R.string.app_name);
                    alert.setIcon(R.drawable.ic_launcher);
                    alert.setMessage("Accept Delivery");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((FragOrderProses)fragment).Accept(order_id);
                        }
                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

                    alert.show();
                } else {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                    alert.setTitle(R.string.app_name);
                    alert.setIcon(R.drawable.ic_launcher);
                    alert.setMessage("Cancel This Order ??");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((FragOrderProses)fragment).Cancel(order_id);
                        }
                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

                    alert.show();
                }
            }
        });

        return convertView;
    }



}
