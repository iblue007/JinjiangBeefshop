package com.jinjiang.beefshop.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.GpUtils;
import com.gprinter.service.GpPrintService;
import com.jinjiang.beefshop.R;
import com.jinjiang.beefshop.SettingActivity;
import com.jinjiang.beefshop.adapter.ShopAdapter;
import com.jinjiang.beefshop.adapter.TestSectionedAdapter;
import com.jinjiang.beefshop.assistant.ShopToDetailListener;
import com.jinjiang.beefshop.assistant.onCallBackListener;
import com.jinjiang.beefshop.buletouth.utils.SharedPreferencesUtils;
import com.jinjiang.beefshop.model.FoodInfoBean;
import com.jinjiang.beefshop.model.ProductType;
import com.jinjiang.beefshop.model.ShopProduct;
import com.jinjiang.beefshop.model.secondMenu;
import com.jinjiang.beefshop.model.secondeMenuNameAndPrice;
import com.jinjiang.beefshop.sqlite.JjbeafOrderDetail;
import com.jinjiang.beefshop.sqlite.JjbeafSqlBean;
import com.jinjiang.beefshop.sqlite.SqliteUtil;
import com.jinjiang.beefshop.util.DoubleUtil;
import com.jinjiang.beefshop.util.FileUtil;
import com.jinjiang.beefshop.util.ToastUtil;
import com.jinjiang.beefshop.view.PinnedHeaderListView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by 曹博 on 2016/6/6.
 * 服务商品列表
 */
public class ProductsFragment extends Fragment implements View.OnClickListener, onCallBackListener,ShopToDetailListener {
    private boolean isScroll = true;
    private ListView mainlist;
    private PinnedHeaderListView morelist;
    private TestSectionedAdapter sectionedAdapter;
    /**
     * 保存购物车对象到List
     * TODO:考虑保存购物车缓存
     */
    private List<ShopProduct> productList;
    private int totalprice = 0;//总计金额
    /** * 购物车价格  */
    private TextView shoppingPrise;
    /** * 购物车件数 */
    private TextView shoppingNum;
    /** * 去结算 */
    private TextView settlement;
    /** * 购物车View */
    private FrameLayout cardLayout;
    private LinearLayout cardShopLayout;
    /** * 背景View */
    private View bg_layout;
    /**  * 购物车Logo */
    private ImageView shopping_cart;
    // 动画时间
    private int AnimationDuration = 500;
    // 正在执行的动画数量
    private int number = 0;
    // 是否完成清理
    private boolean isClean = false;
    private FrameLayout animation_viewGroup;
    private TextView defaultText;
    private List<String> strings;
    //父布局
    private RelativeLayout parentLayout;
    private TextView noData;
    /**
     * 分类列表
     */
    private List<ProductType> productCategorizes;
    private List<ShopProduct> shopProductsAll;
    private ListView shoppingListView;
    private ShopAdapter shopAdapter;
    private GpService mGpService = null;
    private PrinterServiceConnection conn = null;
    private RelativeLayout shopping_cart_rl;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    public static final String CURRENT_ORDER_NUM = "current_order_num";
    public static final String CURRENT_TIME = "current_time";
    private FoodInfoBean foodInfoBean;
    private int addFoodPrice = 5;//加料默认一次加5元

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // 用来清除动画后留下的垃圾
                    try {
                        animation_viewGroup.removeAllViews();
                    } catch (Exception e) {

                    }
                    isClean = false;

                    break;
                default:
                    break;
            }
        }
    };
    private TextView reducePeople;
    private TextView increatePeople;
    private TextView peopleNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_classify, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        //连接打印机
        connection();
        // 注册实时状态查询广播
        getContext().registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
        /**
         * 票据模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus()，在打印完成后会接收到
         * action为GpCom.ACTION_DEVICE_STATUS的广播，特别用于连续打印，
         * 可参照该sample中的sendReceiptWithResponse方法与广播中的处理
         **/
        getContext().registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_RECEIPT_RESPONSE));
        /**
         * 标签模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus(RESPONSE_MODE mode)
         * ，在打印完成后会接收到，action为GpCom.ACTION_LABEL_RESPONSE的广播，特别用于连续打印，
         * 可参照该sample中的sendLabelWithResponse方法与广播中的处理
         **/
        getContext().registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_LABEL_RESPONSE));
    }

    public List<ProductType> getData() {
        productCategorizes = new ArrayList<>();
        for (int j = 0; j < foodInfoBean.getSecondMenuList().size(); j++) {
            ProductType productCategorize = new ProductType();
            productCategorize.setType(foodInfoBean.getMenuNameList().get(j));
            shopProductsAll = new ArrayList<>();

            secondMenu secondMenu = foodInfoBean.getSecondMenuList().get(j);
            List<secondeMenuNameAndPrice> secondeMenuNameAndPrices = secondMenu.getSecondeMenuNameAndPrices();
            if(secondeMenuNameAndPrices != null && secondeMenuNameAndPrices.size() > 0){
                for(int k =0;k<secondeMenuNameAndPrices.size();k++){
                    ShopProduct product = new ShopProduct();
                    product.setId(154788 + j + k);
                    product.setGoods(secondeMenuNameAndPrices.get(k).getName());
                    product.setPrice(secondeMenuNameAndPrices.get(k).getPrice()+"");
                    product.setAddfood(secondeMenuNameAndPrices.get(k).isAddfood());
                    shopProductsAll.add(product);
                }
            }
            productCategorize.setProduct(shopProductsAll);
            productCategorizes.add(productCategorize);
        }
        return productCategorizes;
    }

    private void initView() {
        String foodJson = getString(R.string.jjnr_food_json);
        foodInfoBean = FileUtil.parseJson(foodJson);
        getData();
        animation_viewGroup = createAnimLayout();
        noData = (TextView) getView().findViewById(R.id.noData);
        parentLayout = (RelativeLayout)  getView().findViewById(R.id.parentLayout);
        shoppingPrise = (TextView)  getView().findViewById(R.id.shoppingPrise);
        shoppingNum = (TextView) getView().findViewById(R.id.shoppingNum);
        settlement = (TextView)  getView().findViewById(R.id.settlement);
        mainlist = (ListView)  getView().findViewById(R.id.classify_mainlist);
        morelist = (PinnedHeaderListView) getView().findViewById(R.id.classify_morelist);
        shopping_cart_rl = (RelativeLayout)  getView().findViewById(R.id.shopping_cart_rl);
        shopping_cart = (ImageView)  getView().findViewById(R.id.shopping_cart);
        defaultText = (TextView)  getView().findViewById(R.id.defaultText);
//        shoppingList = (LinearLayout) getView().findViewById(R.id.shoppingList);
        shoppingListView = (ListView)  getView().findViewById(R.id.shopproductListView);
        cardLayout = (FrameLayout)  getView().findViewById(R.id.cardLayout);
        cardShopLayout = (LinearLayout)  getView().findViewById(R.id.cardShopLayout);
        bg_layout =  getView().findViewById(R.id.bg_layout);
        reducePeople = (TextView) getView().findViewById(R.id.reduce_people);
        increatePeople = (TextView) getView().findViewById(R.id.increase_people);
        peopleNum = (TextView) getView().findViewById(R.id.people_num);

        reducePeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int peopleNumInt = Integer.parseInt(peopleNum.getText().toString());
                peopleNumInt--;
                if(peopleNumInt <= 1){
                    peopleNumInt = 1;
                }
                peopleNum.setText(peopleNumInt+"");
            }
        });
        increatePeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int peopleNumInt = Integer.parseInt(peopleNum.getText().toString());
                peopleNumInt++;
                if(peopleNumInt <= 1){
                    peopleNumInt = 1;
                }
                peopleNum.setText(peopleNumInt+"");
            }
        });

        initData();
    }

    public void initData(){
        productList = new ArrayList<>();
        strings = new ArrayList<>();
        sectionedAdapter = new TestSectionedAdapter(getActivity(), productCategorizes);

        sectionedAdapter.SetOnSetHolderClickListener(new TestSectionedAdapter.HolderClickListener() {

            @Override
            public void onHolderClick(Drawable drawable, int[] start_location) {
                doAnim(drawable, start_location);
            }

        });

        for(ProductType type :productCategorizes){
            strings.add(type.getType());
        }
        morelist.setAdapter(sectionedAdapter);
        sectionedAdapter.setCallBackListener(this);
        mainlist.setAdapter(new ArrayAdapter<String>(getActivity(),
                R.layout.categorize_item, strings));

        shopAdapter = new ShopAdapter(getActivity(),productList);
        shoppingListView.setAdapter(shopAdapter);
        shopAdapter.setShopToDetailListener(this);

        mainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                isScroll = false;

                for (int i = 0; i < mainlist.getChildCount(); i++) {
                    if (i == position) {
                        mainlist.getChildAt(i).setBackgroundColor(Color.rgb(255, 255, 255));
                    } else {
                        mainlist.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }

                int rightSection = 0;
                for (int i = 0; i < position; i++) {
                    rightSection += sectionedAdapter.getCountForSection(i) + 1;
                }
                morelist.setSelection(rightSection);

            }

        });

        morelist.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (isScroll) {
                    for (int i = 0; i < mainlist.getChildCount(); i++) {

                        if (i == sectionedAdapter
                                .getSectionForPosition(firstVisibleItem)) {
                            mainlist.getChildAt(i).setBackgroundColor(
                                    Color.rgb(255, 255, 255));
                        } else {
                            mainlist.getChildAt(i).setBackgroundColor(
                                    Color.TRANSPARENT);

                        }
                    }

                } else {
                    isScroll = true;
                }
            }
        });



        bg_layout.setOnClickListener(this);
        settlement.setOnClickListener(this);
        shopping_cart_rl.setOnClickListener(this);
    }

    /**
     * 回调函数更新购物车和价格显示状态
     *
     * @param product
     * @param type
     */
    @Override
    public void updateProduct(ShopProduct product, String type) {
        if (type.equals("1")) {
            if(!productList.contains(product)){
                productList.add(product);
            }else {
                for (ShopProduct shopProduct:productList){
                    if(product.getId()==shopProduct.getId()){
                        shopProduct.setNumber(shopProduct.getNumber());
                    }
                }
            }
        } else if (type.equals("2")) {
            if(productList.contains(product)){
                if(product.getNumber()==0){
                    productList.remove(product);
                }else {
                    for (ShopProduct shopProduct:productList){
                        if(product.getId()==shopProduct.getId()){
                            shopProduct.setNumber(shopProduct.getNumber());
                        }
                    }
                }

            }
        }
        shopAdapter.setData(productList);
        setPrise();
    }

    @Override
    public void addFood(ShopProduct product, String type) {
        if (type.equals("1")) {
            if(productList.contains(product)){
                for (ShopProduct shopProduct:productList){
                    if(product.getId()==shopProduct.getId()){
                        shopProduct.setAddfoodNum(shopProduct.getAddfoodNum());
                    }
                }
            }
        } else if (type.equals("2")) {
            if(productList.contains(product)){
                for (ShopProduct shopProduct:productList){
                    if(product.getId()==shopProduct.getId()){
                        shopProduct.setAddfoodNum(shopProduct.getAddfoodNum());
                    }
                }
            }
        }
        shopAdapter.setData(productList);
        setPrise();
    }

    @Override
    public void onUpdateDetailList(ShopProduct product, String type) {
        if (type.equals("1")) {
            for (int i =0;i<productCategorizes.size();i++){
                shopProductsAll = productCategorizes.get(i).getProduct();
                for(ShopProduct shopProduct :shopProductsAll){
                    if(product.getId()==shopProduct.getId()){
                        shopProduct.setNumber(product.getNumber());
                    }
                }
            }
        } else if (type.equals("2")) {
            for (int i =0;i<productCategorizes.size();i++){
                shopProductsAll = productCategorizes.get(i).getProduct();
                for(ShopProduct shopProduct :shopProductsAll){
                    if(product.getId()==shopProduct.getId()){
                        shopProduct.setNumber(product.getNumber());
                    }
                }
            }
        }
        sectionedAdapter.notifyDataSetChanged();
        setPrise();
    }

    @Override
    public void onRemovePriduct(ShopProduct product) {
        for (int i =0;i<productCategorizes.size();i++){
            shopProductsAll = productCategorizes.get(i).getProduct();
            for(ShopProduct shopProduct :shopProductsAll){
                if(product.getId()==shopProduct.getId()){
                    productList.remove(product);
                    shopAdapter.notifyDataSetChanged();
                    shopProduct.setNumber(shopProduct.getNumber());
                }
            }
        }
        sectionedAdapter.notifyDataSetChanged();
        shopAdapter.notifyDataSetChanged();
        setPrise();
    }

    @Override
    public void onUpdateAddFood(ShopProduct product, String type) {
        if (type.equals("1")) {
            for (int i =0;i<productCategorizes.size();i++){
                shopProductsAll = productCategorizes.get(i).getProduct();
                for(ShopProduct shopProduct :shopProductsAll){
                    if(product.getId()==shopProduct.getId()){
                        shopProduct.setOrderNumber(product.getOrderNumber());
                    }
                }
            }
        }
        sectionedAdapter.notifyDataSetChanged();
        shopAdapter.notifyDataSetChanged();
        setPrise();
    }

    @Override
    public void onUpdateRemoveFood(ShopProduct product) {
        for (int i =0;i<productCategorizes.size();i++){
            shopProductsAll = productCategorizes.get(i).getProduct();
            for(ShopProduct shopProduct :shopProductsAll){
                if(product.getId()==shopProduct.getId()){
                    shopProduct.setNumber(shopProduct.getNumber());
                }
            }
        }
        sectionedAdapter.notifyDataSetChanged();
        shopAdapter.notifyDataSetChanged();
        setPrise();
    }

    /**
     * 更新购物车价格
     */
    public void setPrise() {
        double sum = 0;
        int shopNum = 0;
        for (ShopProduct pro : productList) {
//            sum = sum + (pro.getNumber() * Double.parseDouble(pro.getPrice()));
            sum = DoubleUtil.sum(sum, DoubleUtil.mul((double) pro.getNumber(), Double.parseDouble(pro.getPrice())));
            if(pro.getAddfoodNum() > 0){//加料部分
                int addFoodMoney = pro.getAddfoodNum() * addFoodPrice;
                sum = sum + addFoodMoney;
            }
            shopNum = shopNum + pro.getNumber();
        }
        if(shopNum>0){
            shoppingNum.setVisibility(View.VISIBLE);
        }else {
            shoppingNum.setVisibility(View.GONE);
        }
        if(sum>0){
            shoppingPrise.setVisibility(View.VISIBLE);
        }else {
            shoppingPrise.setVisibility(View.GONE);
        }
        shoppingPrise.setText("¥" + " " + (new DecimalFormat("0.00")).format(sum));
        shoppingNum.setText(String.valueOf(shopNum));
    }

    /**购物车是否点开了*/
    public boolean isCarShopVisible() {
        if (cardLayout.getVisibility() == View.VISIBLE) {
            cardLayout.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shopping_cart_rl:
                if (productList.isEmpty() || productList == null) {
                    defaultText.setVisibility(View.VISIBLE);
                } else {
                    defaultText.setVisibility(View.GONE);
                }

                if (cardLayout.getVisibility() == View.GONE) {
                    cardLayout.setVisibility(View.VISIBLE);

                    // 加载动画
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_bottom_in);
                    // 动画开始
                    cardShopLayout.setVisibility(View.VISIBLE);
                    cardShopLayout.startAnimation(animation);
                    bg_layout.setVisibility(View.VISIBLE);

                } else {
                    cardLayout.setVisibility(View.GONE);
                    bg_layout.setVisibility(View.GONE);
                    cardShopLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.settlement:
                if(productList == null || productList.size() <= 0){
                    ToastUtil.showToast(getContext(),"空订单不能下单哦 ~~");
                    return;
                }
                //-----------------测试部门----------------------
//                shopAdapter.setData(null);
//                cardLayout.setVisibility(View.GONE);
//                shoppingPrise.setText("");
//                shoppingPrise.setVisibility(View.GONE);
//                shoppingNum.setText("");
//                shoppingNum.setVisibility(View.GONE);
//                sectionedAdapter.resetOrderNum(true);
//
//                SharedPreferencesUtils.setInt(CURRENT_ORDER_NUM, SharedPreferencesUtils.getInt(CURRENT_ORDER_NUM, 1000) + 1);
//                Log.e("====","====currentOrderNum:"+SharedPreferencesUtils.getInt(CURRENT_ORDER_NUM, 1000));
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//                String currentTime = simpleDateFormat.format(new Date());
//
//
//                JjbeafOrderDetail jjbeafOrderDetail = new JjbeafOrderDetail();
//                jjbeafOrderDetail.setDetail("10*5");
//                jjbeafOrderDetail.setFoodName("牛肉");
//                jjbeafOrderDetail.setPrice(""+23);
//
//                JjbeafOrderDetail jjbeafOrderDetail2 = new JjbeafOrderDetail();
//                jjbeafOrderDetail2.setDetail("15*5");
//                jjbeafOrderDetail2.setFoodName("卤猪蹄");
//                jjbeafOrderDetail2.setPrice(""+26);
//
//                JjbeafOrderDetail jjbeafOrderDetail3 = new JjbeafOrderDetail();
//                jjbeafOrderDetail3.setDetail("10*5");
//                jjbeafOrderDetail3.setFoodName("白饭");
//                jjbeafOrderDetail3.setPrice(""+20);
//
//
//                JjbeafSqlBean jjbeafSqlBean = new JjbeafSqlBean();
//                jjbeafSqlBean.setOderPeopleCount("2");
//                jjbeafSqlBean.setOrderNumber(SharedPreferencesUtils.getInt(CURRENT_ORDER_NUM, 1000)+"");
//                jjbeafSqlBean.setOrderTime(currentTime);
//                jjbeafSqlBean.setTotalPrice("563");
//                jjbeafSqlBean.getJjbeafOrderDetailList().add(jjbeafOrderDetail);
//                jjbeafSqlBean.getJjbeafOrderDetailList().add(jjbeafOrderDetail2);
//                jjbeafSqlBean.getJjbeafOrderDetailList().add(jjbeafOrderDetail3);
//
//                Gson gson = new Gson();
//                String orderJson = gson.toJson(jjbeafSqlBean);
//                SqliteUtil.getInstance().addStudent(getContext(),orderJson);
                //-----------------测试部门----------------------
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//                String currentTime = simpleDateFormat.format(new Date());
//                saveData2Sql(currentTime);
                //productList.clear();
                if (mGpService == null) {
                    Toast.makeText(getContext(),"服务正在开启",Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int type = mGpService.getPrinterCommandType(1);
                        if (type == GpCom.ESC_COMMAND) {
                            mGpService.queryPrinterStatus(1, 1000, REQUEST_PRINT_RECEIPT);
                        } else {
                            Toast.makeText(getContext(), "Printer is not receipt mode", Toast.LENGTH_SHORT).show();
                        }
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }
                break;

            case R.id.bg_layout:
                cardLayout.setVisibility(View.GONE);
                bg_layout.setVisibility(View.GONE);
                cardShopLayout.setVisibility(View.GONE);
                break;
        }
    }



    /**
     * @param
     * @return void
     * @throws
     * @Description: 创建动画层
     */
    private FrameLayout createAnimLayout() {
        ViewGroup rootView = (ViewGroup) getActivity().getWindow().getDecorView();
        FrameLayout animLayout = new FrameLayout(getActivity());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;

    }

    private void doAnim(Drawable drawable, int[] start_location) {
        if (!isClean) {
            setAnim(drawable, start_location);
        } else {
            try {
                animation_viewGroup.removeAllViews();
                isClean = false;
                setAnim(drawable, start_location);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isClean = true;
            }
        }
    }

    /**
     * 动画效果设置
     *
     * @param drawable       将要加入购物车的商品
     * @param start_location 起始位置
     */
    @SuppressLint("NewApi")
    private void setAnim(Drawable drawable, int[] start_location) {
        Animation mScaleAnimation = new ScaleAnimation(1.2f, 0.6f, 1.2f, 0.6f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mScaleAnimation.setFillAfter(true);

        final ImageView iview = new ImageView(getActivity());
        iview.setImageDrawable(drawable);
        final View view = addViewToAnimLayout(animation_viewGroup, iview,
                start_location);


        view.setAlpha(0.6f);

        int[] end_location = new int[2];
        settlement.getLocationInWindow(end_location);

        // 计算位移
        int endX = 0 - start_location[0] + 40;// 动画位移的X坐标
        int endY = end_location[1] - start_location[1];// 动画位移的y坐标
        TranslateAnimation translateAnimationX = new TranslateAnimation(0,
                endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
                0, endY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);


        Animation mRotateAnimation = new RotateAnimation(0, 180,
                Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF,
                0.3f);
        mRotateAnimation.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(mRotateAnimation);
        set.addAnimation(mScaleAnimation);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(500);// 动画的执行时间
        view.startAnimation(set);
        set.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                number++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                number--;
                if (number == 0) {
                    isClean = true;
                    myHandler.sendEmptyMessage(0);
                }

                ObjectAnimator.ofFloat(shopping_cart, "translationY", 0, 4, -2, 0).setDuration(400).start();
                ObjectAnimator.ofFloat(shoppingNum, "translationY", 0, 4, -2, 0).setDuration(400).start();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

    }

    /**
     * @param vg       动画运行的层 这里是frameLayout
     * @param view     要运行动画的View
     * @param location 动画的起始位置
     * @return
     * @deprecated 将要执行动画的view 添加到动画层
     */
    private View addViewToAnimLayout(ViewGroup vg, View view, int[] location) {
        int x = location[0];
        int y = location[1];
        vg.addView(view);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setPadding(5, 5, 5, 5);
        view.setLayoutParams(lp);

        return view;
    }

    /**
     * 内存过低时及时处理动画产生的未处理冗余
     */
    @Override
    public void onLowMemory() {
        isClean = true;
        try {
            animation_viewGroup.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isClean = false;
        super.onLowMemory();
    }

    //下单部分代码
   class PrinterServiceConnection implements ServiceConnection {
       @Override
       public void onServiceDisconnected(ComponentName name) {
           Log.i("ServiceConnection", "onServiceDisconnected() called");
           mGpService = null;
       }
       @Override
       public void onServiceConnected(ComponentName name, IBinder service) {
           mGpService = GpService.Stub.asInterface(service);
       }
   }
    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(getContext(), GpPrintService.class);
        getContext().bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("TAG", action);
            // GpCom.ACTION_DEVICE_REAL_STATUS 为广播的IntentFilter
            if (action.equals(GpCom.ACTION_DEVICE_REAL_STATUS)) {
                // 业务逻辑的请求码，对应哪里查询做什么操作
                int requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);
                // 判断请求码，是则进行业务操作
                if (requestCode == MAIN_QUERY_PRINTER_STATUS) {

                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    String str;
                    if (status == GpCom.STATE_NO_ERR) {
                        str = "打印机正常";
                    } else {
                        str = "打印机 ";
                        if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
                            str += "脱机";
                        }
                        if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
                            str += "缺纸";
                        }
                        if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
                            str += "打印机开盖";
                        }
                        if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
                            str += "打印机出错";
                        }
                        if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
                            str += "查询超时";
                        }
                    }
                    Toast.makeText(getContext(), "打印机：" + 1 + " 状态：" + str, Toast.LENGTH_SHORT).show();
                } else if (requestCode == REQUEST_PRINT_RECEIPT) {
                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    if (status == GpCom.STATE_NO_ERR) {
                        sendReceipt();
                    } else {
//                        Toast.makeText(PrintActivity.this, "query printer status error", Toast.LENGTH_SHORT).show();
                        SharedPreferencesUtils.setDevice(0, "", "");
                        Intent intent1 = new Intent(getContext(), SettingActivity.class);
                        startActivity(intent1);
                    }
                }
            }
        }
    };

    /**输出打印信息**/
    private void sendReceipt() {
        SharedPreferencesUtils.setInt(CURRENT_ORDER_NUM, SharedPreferencesUtils.getInt(CURRENT_ORDER_NUM, 1000) + 1);
        Log.e("====","====currentOrderNum:"+SharedPreferencesUtils.getInt(CURRENT_ORDER_NUM, 1000));

        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        esc.addPrintAndLineFeed();
//        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
        esc.addText("晋江牛肉\n"); // 打印文字
        esc.addPrintAndLineFeed();

//		/* 打印文字 */
//        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
//        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
//        esc.addText("商家\n"); // 打印文字

//        esc.addText("Welcome to use SMARNET printer!\n"); // 打印文字
//		/* 打印繁体中文 需要打印机支持繁体字库 */
//        String message = "佳博智匯票據打印機\n";
//        // esc.addText(message,"BIG5");
//        esc.addText(message, "GB2312");
//        esc.addPrintAndLineFeed();

        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
        //用餐人数
        int peopleCount = Integer.parseInt(peopleNum.getText().toString());
        if(peopleCount == 0){
            peopleCount = 1;
        }
        esc.addText("用餐人数：" + peopleCount);
        esc.addPrintAndLineFeed();
        //当前订单号
        esc.addText("当前订单号：" + SharedPreferencesUtils.getInt(CURRENT_ORDER_NUM, 1000));
        esc.addPrintAndLineFeed();
        //下单时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String currentTime = simpleDateFormat.format(new Date());
        esc.addText("下单时间："+currentTime);
        esc.addPrintAndLineFeed();

		/* 绝对位置 具体详细信息请查看GP58编程手册 */
        esc.addText("商品名");
        esc.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
        esc.addSetAbsolutePrintPosition((short) 6);
        esc.addText("详情");
        esc.addSetAbsolutePrintPosition((short) 10);
        esc.addText("价格");

        esc.addPrintAndLineFeed();
        if(productList != null && productList.size() > 0){
            for(int i=0;i<productList.size();i++){
                ShopProduct shopProduct = productList.get(i);
                if(shopProduct != null){
                    int addfoodNum = shopProduct.getAddfoodNum();
                    int price = 0;
                    String orderDetail = "";
                    if(addfoodNum > 0){
                        orderDetail = shopProduct.getPrice()+"*"+shopProduct.getNumber()+""+"("+"加料数*"+addfoodNum+")";
                        price = (shopProduct.getNumber() * Integer.parseInt(shopProduct.getPrice()))+(addfoodNum * 5);

                    }else {
                        orderDetail = shopProduct.getPrice()+"*"+shopProduct.getNumber()+"";
                        price = shopProduct.getNumber() * Integer.parseInt(shopProduct.getPrice());
                    }
                    esc.addText(shopProduct.getGoods());
                    esc.addSetHorAndVerMotionUnits((byte) 6, (byte) 0);
                    esc.addSetAbsolutePrintPosition((short) 4);
                    esc.addText(orderDetail);
                    esc.addSetAbsolutePrintPosition((short) 10);
                    esc.addText(price+"");
                    esc.addPrintAndLineFeed();
                    // totalprice = totalprice + (shopProduct.getNumber() * Integer.parseInt(shopProduct.getPrice()));
                    totalprice = totalprice + price;
                }
            }
        }

        //总计价格
        esc.addText("总计金额");
        esc.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
        esc.addSetAbsolutePrintPosition((short) 6);
        esc.addText(totalprice+"");
        esc.addSetAbsolutePrintPosition((short) 10);
        esc.addPrintAndLineFeed();

//		/* 打印图片 */
//        esc.addText("Print bitmap!\n"); // 打印文字
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.gprinter);
//        esc.addRastBitImage(b, 384, 0); // 打印图片
//		/* 打印一维条码 */
//        esc.addText("Print code128\n"); // 打印文字
//        esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);//
//        // 设置条码可识别字符位置在条码下方
//        esc.addSetBarcodeHeight((byte) 60); // 设置条码高度为60点
//        esc.addSetBarcodeWidth((byte) 1); // 设置条码单元宽度为1
//        esc.addCODE128(esc.genCodeB("SMARNET")); // 打印Code128码
//        esc.addPrintAndLineFeed();
		/*
		 * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
		 */

//        esc.addText("商家二维码\n"); // 打印文字
//        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31); // 设置纠错等级
//        esc.addSelectSizeOfModuleForQRCode((byte) 6);// 设置qrcode模块大小
//        esc.addStoreQRCodeData("www.baidu.com");// 设置qrcode内容
//        esc.addPrintQRCode();// 打印QRCode
//        esc.addPrintAndLineFeed();

		/* 打印文字 */
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印左对齐
        esc.addText("Completed!\n"); // 打印结束
        // 开钱箱
//        esc.addGeneratePlus(LabelCommand.FOOT.F5, (byte) 255, (byte) 255);
//        esc.addPrintAndFeedLines((byte) 8);
        esc.addPrintAndLineFeed();
        esc.addPrintAndLineFeed();
        esc.addPrintAndLineFeed();

        Vector<Byte> datas = esc.getCommand(); // 发送数据
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs;
        try {
            rs = mGpService.sendEscCommand(1, sss);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(getContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        saveData2Sql(currentTime);
        productList.clear();
        totalprice = 0;
        shopAdapter.setData(null);
        cardLayout.setVisibility(View.GONE);
        shoppingPrise.setText("");
        shoppingPrise.setVisibility(View.GONE);
        shoppingNum.setText("");
        shoppingNum.setVisibility(View.GONE);
        sectionedAdapter.resetOrderNum(true);
        peopleNum.setText(1+"");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mBroadcastReceiver);
        getContext().unregisterReceiver(mBroadcastReceiver);
        getContext().unregisterReceiver(mBroadcastReceiver);
    }


    private void saveData2Sql(String currentTime){
        try {
            if(productList != null && productList.size() > 0){
                JjbeafSqlBean jjbeafSqlBean = new JjbeafSqlBean();
                jjbeafSqlBean.setOderPeopleCount(peopleNum.getText().toString());
                jjbeafSqlBean.setOrderNumber(SharedPreferencesUtils.getInt(CURRENT_ORDER_NUM, 1000)+"");
                jjbeafSqlBean.setOrderTime(currentTime);
                for(ShopProduct shopProduct : productList){
                    int addfoodNum = shopProduct.getAddfoodNum();
                    String goodsName = shopProduct.getGoods();
                    String price = "";
                    String orderDetail = "";
                    if(addfoodNum > 0){
                        orderDetail = shopProduct.getPrice()+"*"+shopProduct.getNumber()+""+"("+"加料*"+addfoodNum+")";
                        price = (shopProduct.getNumber() * Integer.parseInt(shopProduct.getPrice()))+(addfoodNum * 5) +"";
                    }else {
                        orderDetail = shopProduct.getPrice()+"*"+shopProduct.getNumber()+"";
                        price = shopProduct.getNumber() * Integer.parseInt(shopProduct.getPrice())+"";
                    }
                    JjbeafOrderDetail jjbeafOrderDetail = new JjbeafOrderDetail();
                    jjbeafOrderDetail.setDetail(orderDetail);
                    jjbeafOrderDetail.setFoodName(goodsName);
                    jjbeafOrderDetail.setPrice(price);
                    jjbeafSqlBean.getJjbeafOrderDetailList().add(jjbeafOrderDetail);

                }
                jjbeafSqlBean.setTotalPrice(totalprice+"");
                Gson gson = new Gson();
                String orderJson = gson.toJson(jjbeafSqlBean);
                SqliteUtil.getInstance().addStudent(getContext(),orderJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
