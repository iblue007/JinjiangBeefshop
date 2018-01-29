package com.jinjiang.beefshop.buletouth;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jinjiang.beefshop.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HLSY-Android on 2016/11/5.
 */
public class BuleToothListAdapter extends BaseAdapter {


    private List<String> nameData = new ArrayList<>();
    private List<String> addressData = new ArrayList<>();
    private Context context;
    private int flag = -1;

    public BuleToothListAdapter(Context context, List<String> nameData
            , List<String> addressData) {
        this.context = context;
        this.addressData = addressData;
        this.nameData = nameData;

    }

    public void setFlag(int flag){
        this.flag = flag;
        notifyDataSetChanged();
    }
    public void setData(List<String> nameData, List<String> addressData){
        this.nameData = nameData;
        this.addressData = addressData;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return nameData.size();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holdder holdder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_buletoothlist,null);
            holdder = new Holdder();
            convertView.setTag(holdder);
        }else{
            holdder = (Holdder) convertView.getTag();
        }

        holdder.iv = (ImageView) convertView.findViewById(R.id.item_buletooth_iv);
        holdder.tv = (TextView) convertView.findViewById(R.id.item_buletooth_tv);
        holdder.name = (TextView) convertView.findViewById(R.id.item_buletooth_name);

        holdder.tv.setText(addressData.get(position));
        holdder.tv.setVisibility(View.VISIBLE);
        holdder.name.setText(nameData.get(position));
        holdder.name.setVisibility(View.VISIBLE);
        if(flag == position){
            holdder.iv.setVisibility(View.VISIBLE);
        }else{
            holdder.iv.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }

    class Holdder{
        TextView name;
        TextView tv;
        ImageView iv;
    }

}
