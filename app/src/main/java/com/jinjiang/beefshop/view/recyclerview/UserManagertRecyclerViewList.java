//package com.jinjiang.beefshop.view.recyclerview;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//
//import com.jinjiang.beefshop.R;
//import com.jinjiang.beefshop.sqlite.JjBeafSQLiteOpenHelper;
//import com.jinjiang.beefshop.sqlite.JjbeafSqlBean;
//import com.jinjiang.beefshop.util.RecyclerViewStateUtils;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by xuqunxing on 2016/10/24.
// */
//public class UserManagertRecyclerViewList extends RelativeLayout {
//    /**服务器端一共多少条数据*/
//    private static final int TOTAL_COUNTER = 64;
//    /**每一页展示多少条数据*/
//    private static final int REQUEST_COUNT = 10;
//    /**已经获取到多少条数据了*/
//    private int mCurrentCounter = 0;
//    private DataAdapter mDataAdapter = null;
//    private RecyclerView mRecyclerView;
//    private PreviewHandler mHandler = new PreviewHandler(((Activity)getContext()));
//    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter;
//    private boolean  isLastView = false;
//    private boolean scrollFlag = false;
//    /**当前滑动的状态*/
//    private int currentScrollState = 0;
//    /**
//     * 当前RecyclerView类型
//     */
//    protected EndlessRecyclerOnScrollListener.LayoutManagerType layoutManagerType;
//    /**
//     * 最后一个可见的item的位置
//     */
//    private int lastVisibleItemPosition;
//    /**
//     * 最后一个的位置
//     */
//    private int[] lastPositions;
//    private JjBeafSQLiteOpenHelper dbOpenHelper = new JjBeafSQLiteOpenHelper(getContext(), "JjBeafShop.db", null, 1);
//    private List<JjbeafSqlBean> personList = new ArrayList<>();
//    public final static int TYPE_USER = 300;
//    private int type = 100;
//    private Context context;
//    private Dialog dialog;
//
//    public UserManagertRecyclerViewList(Context context) {
//        this(context,null);
//    }
//    public UserManagertRecyclerViewList(Context context, int type) {
//        super(context);
//        this.type = type;
//        this.context = context;
//        initView();
//        initData();
//    }
//
//    public UserManagertRecyclerViewList(Context context, AttributeSet attrs) {
//        this(context, attrs,0);
//    }
//
//    public UserManagertRecyclerViewList(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        this.context = context;
//        initView();
//        initData();
//    }
//
//    private void initView() {
//        View.inflate(getContext(), R.layout.test,this);
//        mRecyclerView = (RecyclerView) findViewById(R.id.list);
//    }
//
//    private void initData() {
//        List<Person> presonList = getShopCarList();
//        mCurrentCounter = presonList.size();
//
//        mDataAdapter = new DataAdapter(getContext());
//        mDataAdapter.addAll(presonList);
//        //setLayoutManager
//        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mDataAdapter);
//        mRecyclerView.setAdapter(mHeaderAndFooterRecyclerViewAdapter);
//
////        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
////        manager.setSpanSizeLookup(new HeaderSpanSizeLookup((HeaderAndFooterRecyclerViewAdapter) mRecyclerView.getAdapter(), manager.getSpanCount()));
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//    }
//
//    /**
//     * 取数组中最大值
//     *
//     * @param lastPositions
//     * @return
//     */
//    private int findMax(int[] lastPositions) {
//        int max = lastPositions[0];
//        for (int value : lastPositions) {
//            if (value > max) {
//                max = value;
//            }
//        }
//
//        return max;
//    }
//
//    private EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {
//
//        @Override
//        public void hideView() {
//            super.hideView();
//
//        }
//
//        @Override
//        public void showView() {
//            super.showView();
//
//        }
//
//        @Override
//        public void onMoved(int distance) {
//            Log.e("====","====distance:"+distance);
//        }
//
//        @Override
//        public void onLoadNextPage(View view) {
//            super.onLoadNextPage(view);
//
//            LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(mRecyclerView);
//            if(state == LoadingFooter.State.Loading) {
//                Log.d("@Cundong", "the state is Loading, just wait..");
//                return;
//            }
//
//            if (mCurrentCounter < TOTAL_COUNTER) {
//                // loading more
//                RecyclerViewStateUtils.setFooterViewState(((Activity)getContext()), mRecyclerView, REQUEST_COUNT, LoadingFooter.State.Loading, null);
//                requestData();
//            } else {
//                //the end
//                Log.e("====","====TheEnd");
//                RecyclerViewStateUtils.setFooterViewState(((Activity)getContext()), mRecyclerView, REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
//            }
//        }
//    };
//
//    /**
//     * 模拟请求网络
//     */
//    private void requestData() {
//
//        new Thread() {
//
//            @Override
//            public void run() {
//                super.run();
//
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                //模拟一下网络请求失败的情况
////                if(TelephoneUtil.isNetworkAvailable(getContext())) {
//                    mHandler.sendEmptyMessage(-1);
////                } else {
////                    mHandler.sendEmptyMessage(-3);
////                }
//            }
//        }.start();
//    }
//
//    private class PreviewHandler extends Handler {
//
//        private WeakReference<Activity> ref;
//
//        PreviewHandler(Activity activity) {
//            ref = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            final Activity activity = ref.get();
//            if (activity == null || activity.isFinishing()) {
//                return;
//            }
//
//            switch (msg.what) {
//                case -1:
//                    int currentSize = mDataAdapter.getItemCount();
//                    getShopCarList();
//                    addItems(personList);
//                    RecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.Normal);
//                    break;
//                case -2:
//                    notifyDataSetChanged();
//                    break;
//                case -3:
//                    RecyclerViewStateUtils.setFooterViewState(activity, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.NetWorkError, mFooterClick);
//                    break;
//            }
//        }
//    }
//
//    private void addItems(List<Person> shopCarItemList) {
//        mDataAdapter.addAll(shopCarItemList);
//        mCurrentCounter += shopCarItemList.size();
//    }
//
//    private void notifyDataSetChanged() {
//        mHeaderAndFooterRecyclerViewAdapter.notifyDataSetChanged();
//    }
//
//    private OnClickListener mFooterClick = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            RecyclerViewStateUtils.setFooterViewState(((Activity)getContext()), mRecyclerView, REQUEST_COUNT, LoadingFooter.State.Loading, null);
//            requestData();
//        }
//    };
//
//    private class DataAdapter extends RecyclerView.Adapter {
//
//        private LayoutInflater mLayoutInflater;
//        private ArrayList<Person> mDataList = new ArrayList<>();
//
//        public DataAdapter(Context context) {
//            mLayoutInflater = LayoutInflater.from(context);
//        }
//
//        private void addAll(List<Person> shopCarItemList) {
//            int lastIndex = this.mDataList.size();
//            this.mDataList.clear();
//            if (this.mDataList.addAll(shopCarItemList)) {
//                notifyItemRangeInserted(lastIndex, shopCarItemList.size());
//            }
//        }
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return new ViewHolder(mLayoutInflater.inflate(R.layout.sample_item_user, parent, false));
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//
//            Person person = mDataList.get(position);
//            ViewHolder viewHolder = (ViewHolder) holder;
//            viewHolder.shopCarItemView.setData(person);
//        }
//
//        @Override
//        public int getItemCount() {
//            return mDataList.size();
//        }
//
//        private class ViewHolder extends RecyclerView.ViewHolder {
//
//            private UserItemView shopCarItemView;
//
//            public ViewHolder(View itemView) {
//                super(itemView);
//                shopCarItemView = (UserItemView) itemView.findViewById(R.id.lockgp_acitivyt_user_manager);
//                shopCarItemView.setOnClickListener( new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Person person = mDataList.get(RecyclerViewUtils.getAdapterPosition(mRecyclerView, UserManagertRecyclerViewList.DataAdapter.ViewHolder.this));
//                       // Toast.makeText(getContext(), shopCarItemBean.getTitle(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//                shopCarItemView.setOnLongClickListener(new OnLongClickListener() {
//
//                    @Override
//                    public boolean onLongClick(View v) {
//                        dialog = DialogUtil.LockGpTwoBtnDialog(context, "删除", "确定删除选中条目", "取消", "确定", null, null, new OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
//                        }, new OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
////                                    Person person = mDataList.get(RecyclerViewUtils.getAdapterPosition(mRecyclerView, UserManagertRecyclerViewList.DataAdapter.ViewHolder.this));
////                                    SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
////                                    db.delete(DbConstant.PerSon,"_id=?",new String[]{person.getId()+""});
////
////                                    ToastUtils.showText("删除成功");
////
////                                    mDataList.remove(person);
////                                    mHeaderAndFooterRecyclerViewAdapter.notifyItemRemoved(RecyclerViewUtils.getAdapterPosition(mRecyclerView, UserManagertRecyclerViewList.DataAdapter.ViewHolder.this));
//                            }
//                        });
//                        return false;
//                    }
//                });
//            }
//        }
//    }
//
//    public List<Person> getShopCarList(){
//        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
//        String selectTable = null;
//        Cursor cursorShoplist = null;
//        if(type == TYPE_USER){
//            selectTable = DbConstant.PerSon;
//        }
//        cursorShoplist = db.query(selectTable, new String[]{"_id","name","sex","address","tel"},null,
//                null, null, null, null);
//        while(cursorShoplist.moveToNext()){
//            int id = cursorShoplist.getInt(cursorShoplist.getColumnIndex("_id"));
//            String name = cursorShoplist.getString(cursorShoplist.getColumnIndex("name"));
//            String sex = cursorShoplist.getString(cursorShoplist.getColumnIndex("sex"));
//            String address = cursorShoplist.getString(cursorShoplist.getColumnIndex("address"));
//            String tel = cursorShoplist.getString(cursorShoplist.getColumnIndex("tel"));
//
//            Person person = new Person();
//            person.setId(id);
//            person.setName(name);
//            person.setSex(sex);
//            person.setAddress(address);
//            person.setTel(tel);
//            personList.add(person);
//        }
//        return personList;
//    }
//
//    private void saveShopCarData2Sql(String dbListName, String key, int tabId1) {
//        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//        ContentValues DeliveryAddressCv = new ContentValues();
//        DeliveryAddressCv.put(key,0);
//        db.update(dbListName,DeliveryAddressCv,"_id=?", new String[]{tabId1+""});
//        db.close();
//    }
//}
