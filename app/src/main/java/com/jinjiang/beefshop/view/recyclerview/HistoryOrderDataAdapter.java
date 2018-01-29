package com.jinjiang.beefshop.view.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jinjiang.beefshop.R;
import com.jinjiang.beefshop.sqlite.JjbeafSqlBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuqunxing on 2017/12/18.
 */
public class HistoryOrderDataAdapter extends RecyclerView.Adapter{

    private LayoutInflater mLayoutInflater;
    private ArrayList<JjbeafSqlBean> mDataList = new ArrayList<>();

    public HistoryOrderDataAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void addAll(List<JjbeafSqlBean> shopCarItemList) {
        int lastIndex = this.mDataList.size();
        this.mDataList.clear();
        if (this.mDataList.addAll(shopCarItemList)) {
            notifyItemRangeInserted(lastIndex, shopCarItemList.size());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.sample_item_history_order, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        JjbeafSqlBean person = mDataList.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.shopCarItemView.setData(person);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private OrderHistoryItemView shopCarItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            shopCarItemView = (OrderHistoryItemView) itemView.findViewById(R.id.lockgp_acitivyt_user_manager);
//            shopCarItemView.setOnClickListener( new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Person person = mDataList.get(RecyclerViewUtils.getAdapterPosition(mRecyclerView, UserManagertRecyclerViewList.DataAdapter.ViewHolder.this));
//                    // Toast.makeText(getContext(), shopCarItemBean.getTitle(), Toast.LENGTH_SHORT).show();
//                }
//            });
//            shopCarItemView.setOnLongClickListener(new View.OnLongClickListener() {
//
//                @Override
//                public boolean onLongClick(View v) {
//                    dialog = DialogUtil.LockGpTwoBtnDialog(context, "删除", "确定删除选中条目", "取消", "确定", null, null, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    }, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                            if(account.equals("111111")){
//                                ToastUtils.showText("管理员账号不能删除");
//                            }else {
//                                Person person = mDataList.get(RecyclerViewUtils.getAdapterPosition(mRecyclerView, UserManagertRecyclerViewList.DataAdapter.ViewHolder.this));
//                                SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//                                db.delete(DbConstant.PerSon,"_id=?",new String[]{person.getId()+""});
//
//                                ToastUtils.showText("删除成功");
//                                mDataList.remove(person);
//                                mHeaderAndFooterRecyclerViewAdapter.notifyItemRemoved(RecyclerViewUtils.getAdapterPosition(mRecyclerView, UserManagertRecyclerViewList.DataAdapter.ViewHolder.this));
//                            }
//                        }
//                    });
//                    return false;
//                }
//            });
        }
    }
}