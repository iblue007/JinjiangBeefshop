package com.jinjiang.beefshop.view.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinjiang.beefshop.R;
import com.jinjiang.beefshop.sqlite.JjbeafOrderDetail;
import com.jinjiang.beefshop.sqlite.JjbeafSqlBean;
import com.jinjiang.beefshop.util.ScreenUtil;

import java.util.List;

/**购物车item
 * Created by guoqishen on 2016/12/13.
 */
public class OrderHistoryItemView extends RelativeLayout {

    private TextView orderNumber;
    private TextView orderTime;
    private TextView peopleCount;
    private TextView totalPrice;
    private RelativeLayout personRl;
    private LinearLayout orderDetailLL;

    public OrderHistoryItemView(Context context) {
        this(context,null);
    }

    public OrderHistoryItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public OrderHistoryItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initListener();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.view_history_order_item,this);
        personRl = (RelativeLayout) findViewById(R.id.view_person_rl);
        orderNumber = (TextView) findViewById(R.id.view_order_number);
        orderTime = (TextView) findViewById(R.id.view_order_time);
        peopleCount = (TextView) findViewById(R.id.view_order_people_count);
        totalPrice = (TextView) findViewById(R.id.view_order_total_price);
        orderDetailLL = (LinearLayout) findViewById(R.id.view_order_detail_ll);
    }

    private void initListener() {


    }

    public void setData(JjbeafSqlBean jjbeafSqlBean) {
        try {
          //  this.setBackgroundColor(getContext().getResources().getColor(R.color.lockgp_template_main_left_item_bg_ripple));
            orderTime.setText("订单时间："+jjbeafSqlBean.getOrderTime());
            orderNumber.setText("订单号："+jjbeafSqlBean.getOrderNumber());
            peopleCount.setText("用餐人数："+jjbeafSqlBean.getOderPeopleCount());
            totalPrice.setText("总价格："+jjbeafSqlBean.getTotalPrice());
            orderDetailLL.removeAllViews();
            List<JjbeafOrderDetail> jjbeafOrderDetailList = jjbeafSqlBean.getJjbeafOrderDetailList();
            if(jjbeafOrderDetailList != null && jjbeafOrderDetailList.size() > 0){
                for(JjbeafOrderDetail jjbeafOrderDetail : jjbeafOrderDetailList){
                    String foodName = jjbeafOrderDetail.getFoodName();
                    String detail = jjbeafOrderDetail.getDetail();
                    String price = jjbeafOrderDetail.getPrice();
                    LinearLayout linearLayout = new LinearLayout(getContext());
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams layoutParamsTotal = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParamsTotal.topMargin = ScreenUtil.dip2px(getContext(),8);
                    linearLayout.setLayoutParams(layoutParamsTotal);

                    TextView foodNameTv = new TextView(getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ScreenUtil.dip2px(getContext(),25),1.0f);
                    foodNameTv.setLayoutParams(layoutParams);
                    foodNameTv.setText(foodName);

                    TextView detailTv = new TextView(getContext());
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0,  ScreenUtil.dip2px(getContext(),25),1.0f);
                    detailTv.setLayoutParams(layoutParams2);
                    detailTv.setText(detail);

                    TextView priceTv = new TextView(getContext());
                    LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(0,  ScreenUtil.dip2px(getContext(),25),1.0f);
                    priceTv.setLayoutParams(layoutParams3);
                    priceTv.setText(price);

                    linearLayout.addView(foodNameTv);
                    linearLayout.addView(detailTv);
                    linearLayout.addView(priceTv);
                    orderDetailLL.addView(linearLayout);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
