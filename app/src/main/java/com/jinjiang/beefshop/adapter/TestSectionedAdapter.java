package com.jinjiang.beefshop.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinjiang.beefshop.R;
import com.jinjiang.beefshop.assistant.onCallBackListener;
import com.jinjiang.beefshop.model.ProductType;
import com.jinjiang.beefshop.model.ShopProduct;

import java.util.List;

public class TestSectionedAdapter extends SectionedBaseAdapter {

	List<ProductType> pruductCagests;
    private HolderClickListener mHolderClickListener;
    private Context context;
    private LayoutInflater mInflater;
    private boolean isResetOrderNum = false;

    private onCallBackListener callBackListener;

    public void setCallBackListener(onCallBackListener callBackListener) {
        this.callBackListener = callBackListener;
    }


    public TestSectionedAdapter(Context context, List<ProductType> pruductCagests){
		this.context = context;
		this.pruductCagests = pruductCagests;
		mInflater = LayoutInflater.from(context);
	}

    public void setData(List<ProductType> pruductCagests) {
        this.pruductCagests = pruductCagests;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int section, int position) {
        return pruductCagests.get(section).getProduct().get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return position;
    }

    @Override
    public int getSectionCount() {
        if(pruductCagests != null && pruductCagests.size() > 0){
            return pruductCagests.size();
        }
        return  0;
    }

    @Override
    public int getCountForSection(int section) {
        return pruductCagests.get(section).getProduct().size();
    }

    @Override
    public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.product_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.prise = (TextView) convertView.findViewById(R.id.prise);
            viewHolder.increase = (TextView) convertView.findViewById(R.id.increase);
            viewHolder.reduce = (TextView) convertView.findViewById(R.id.reduce);
            viewHolder.shoppingNum = (TextView) convertView.findViewById(R.id.shoppingNum);
            viewHolder.addFoodLL = (LinearLayout) convertView.findViewById(R.id.add_food_ll);
            viewHolder.reduce_addfood = (TextView) convertView.findViewById(R.id.reduce_addfood);
            viewHolder.increase_addfood = (TextView) convertView.findViewById(R.id.increase_addfood);
            viewHolder.addfood_num = (TextView) convertView.findViewById(R.id.addfood_num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ShopProduct product = pruductCagests.get(section).getProduct().get(position);
        viewHolder.name.setText(product.getGoods());
        viewHolder.prise.setText(String.valueOf(product.getPrice()));
        viewHolder.shoppingNum.setText(String.valueOf(product.getNumber()));

        if(isResetOrderNum){
            viewHolder.shoppingNum.setText(0+"");
            product.setNumber(0);
        }

        if(product.getNumber() > 0){
            viewHolder.addFoodLL.setVisibility(View.VISIBLE);
            viewHolder.addfood_num.setText(product.getAddfoodNum()+"");
        }else {
            viewHolder.addFoodLL.setVisibility(View.GONE);
        }
       viewHolder.increase_addfood.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               int addfoodNum = product.getAddfoodNum();
               addfoodNum++;
               product.setAddfoodNum(addfoodNum);
               viewHolder.addfood_num.setText(product.getAddfoodNum()+"");
               if (callBackListener != null) {
                   callBackListener.addFood(product, "1");
               }
           }
       });
        viewHolder.reduce_addfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int addfoodNum = product.getAddfoodNum();
                addfoodNum--;
                if(addfoodNum < 0){
                    addfoodNum = 0;
                }
                product.setAddfoodNum(addfoodNum);
                viewHolder.addfood_num.setText(product.getAddfoodNum()+"");
                if (callBackListener != null) {
                    callBackListener.addFood(product, "2");
                }
            }
        });

        viewHolder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isResetOrderNum = false;
                int num = product.getNumber();
                num++;
                product.setNumber(num);
                if(product.isAddfood() && num > 0){
                    viewHolder.addFoodLL.setVisibility(View.VISIBLE);
                }
                viewHolder.shoppingNum.setText(product.getNumber()+"");
                if (callBackListener != null) {
                    callBackListener.updateProduct(product, "1");
                } else {
                }
                if(mHolderClickListener!=null){
                    int[] start_location = new int[2];
                    viewHolder.shoppingNum.getLocationInWindow(start_location);//获取点击商品图片的位置
                    Drawable drawable = context.getResources().getDrawable(R.drawable.adddetail);//复制一个新的商品图标
                    //TODO:解决方案，先监听到左边ListView的Item中，然后在开始动画添加
                    mHolderClickListener.onHolderClick(drawable, start_location);
                }
            }
        });
        viewHolder.reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = product.getNumber();
                if (num > 0) {
                    num--;
                    product.setNumber(num);
                    if(num == 0){
                        viewHolder.addFoodLL.setVisibility(View.GONE);
                    }
                    viewHolder.shoppingNum.setText(product.getNumber()+"");
                    if (callBackListener != null) {
                        callBackListener.updateProduct(product, "2");
                    } else {
                    }
                }
            }
        });

        viewHolder.shoppingNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
                    int shoppingNum = Integer.parseInt(viewHolder.shoppingNum.getText().toString());
                }
            }
        });

        return convertView;
    }

    public void resetOrderNum(boolean isResetOrderNum1) {
        isResetOrderNum = isResetOrderNum1;
        notifyDataSetChanged();
    }

    class ViewHolder {
        /**
         * 商品名称
         */
        public TextView name;
        /**
         * 商品价格
         */
        public TextView prise;
        /**
         * 增加
         */
        public TextView increase;
        /**
         * 商品数目
         */
        public TextView shoppingNum;
        /**
         * 减少
         */
        public TextView reduce;
        /**加料LL*/
        public LinearLayout addFoodLL;
        public TextView reduce_addfood;
        public TextView increase_addfood;
        public TextView addfood_num;
    }

    public void SetOnSetHolderClickListener(HolderClickListener holderClickListener){
        this.mHolderClickListener = holderClickListener;
    }
    public interface HolderClickListener{
        public void onHolderClick(Drawable drawable, int[] start_location);
    }
    

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inflator.inflate(R.layout.header_item, null);
        } else {
            layout = (LinearLayout) convertView;
        }
        layout.setClickable(false);
        ((TextView) layout.findViewById(R.id.textItem)).setText(pruductCagests.get(section).getType());
        return layout;
    }

}
