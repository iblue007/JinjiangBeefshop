package com.jinjiang.beefshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.jinjiang.beefshop.R;
import com.jinjiang.beefshop.assistant.ShopToDetailListener;
import com.jinjiang.beefshop.model.ShopProduct;

import java.util.List;

/**
 * Created by caobo on 2016/7/20.
 * 购物车适配器
 */
public class ShopAdapter extends BaseAdapter {

    private ShopToDetailListener shopToDetailListener;

    public void setShopToDetailListener(ShopToDetailListener callBackListener) {
        this.shopToDetailListener = callBackListener;
    }
    private List<ShopProduct> shopProducts;
    private LayoutInflater mInflater;
    public ShopAdapter(Context context, List<ShopProduct> shopProducts) {
        this.shopProducts = shopProducts;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<ShopProduct> data) {
        this.shopProducts = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(shopProducts != null && shopProducts.size() > 0){
            return shopProducts.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return shopProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.trade_widget, null);
            viewHolder = new ViewHolder();
            viewHolder.commodityName = (TextView) convertView.findViewById(R.id.commodityName);
            viewHolder.commodityPrise = (TextView) convertView.findViewById(R.id.commodityPrise);
            viewHolder.commodityNum = (TextView) convertView.findViewById(R.id.commodityNum);
            viewHolder.increase = (TextView)  convertView.findViewById(R.id.increase);
            viewHolder.reduce = (TextView)  convertView.findViewById(R.id.reduce);
            viewHolder.shoppingNum = (TextView)  convertView.findViewById(R.id.shoppingNum);
            viewHolder.increse_addfood = (TextView)  convertView.findViewById(R.id.increase_addfood);
            viewHolder.reduce_addfood = (TextView)  convertView.findViewById(R.id.reduce_addfood);
            viewHolder.addfood_tv = (TextView)  convertView.findViewById(R.id.addfood_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.commodityName.setText(shopProducts.get(position).getGoods());
        viewHolder.commodityPrise.setText(shopProducts.get(position).getPrice());
        viewHolder.commodityNum.setText(1+"");
        viewHolder.shoppingNum.setText(shopProducts.get(position).getNumber()+"");

        viewHolder.addfood_tv.setText(shopProducts.get(position).getAddfoodNum()+"");
        viewHolder.increse_addfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int addFoodNum = shopProducts.get(position).getAddfoodNum();
                addFoodNum++;
                shopProducts.get(position).setAddfoodNum(addFoodNum);
                viewHolder.addfood_tv.setText(shopProducts.get(position).getAddfoodNum()+"");
                if (shopToDetailListener != null) {
                    shopToDetailListener.onUpdateAddFood(shopProducts.get(position), "1");
                }
            }
        });
        viewHolder.reduce_addfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int addFoodNum = shopProducts.get(position).getAddfoodNum();
                addFoodNum--;
                if(addFoodNum < 0){
                    addFoodNum = 0;
                }
                shopProducts.get(position).setAddfoodNum(addFoodNum);
                viewHolder.addfood_tv.setText(shopProducts.get(position).getAddfoodNum()+"");
                if (shopToDetailListener != null) {
                    shopToDetailListener.onUpdateRemoveFood(shopProducts.get(position));
                }
            }
        });

        viewHolder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = shopProducts.get(position).getNumber();
                num++;
                shopProducts.get(position).setNumber(num);
                viewHolder.shoppingNum.setText(shopProducts.get(position).getNumber()+"");
                if (shopToDetailListener != null) {
                    shopToDetailListener.onUpdateDetailList(shopProducts.get(position), "1");
                } else {
                }
            }
        });

        viewHolder.reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = shopProducts.get(position).getNumber();
                if (num > 0) {
                    num--;
                    if(num==0){
                        shopProducts.get(position).setNumber(num);
                        shopToDetailListener.onRemovePriduct(shopProducts.get(position));
                    }else {
                        shopProducts.get(position).setNumber(num);
                        viewHolder.shoppingNum.setText(shopProducts.get(position).getNumber()+"");
                        if (shopToDetailListener != null) {
                            shopToDetailListener.onUpdateDetailList(shopProducts.get(position), "2");
                        } else {
                        }
                    }

                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        /**
         * 购物车商品名称
         */
        public TextView commodityName;
        /**
         * 购物车商品价格
         */
        public TextView commodityPrise;
        /**
         * 购物车商品数量
         */
        public TextView commodityNum;
        /**
         * 增加
         */
        public TextView increase;
        /**
         * 减少
         */
        public TextView reduce;
        /**
         * 商品数目
         */
        public TextView shoppingNum;

        public TextView reduce_addfood;
        public TextView increse_addfood;
        public TextView addfood_tv;
    }
}
