package com.jinjiang.beefshop.sqlite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuqunxing on 2017/12/18.
 */
public class JjbeafSqlBean {
//    _id integer primary key autoincrement,ordername text not null,orderprice integer,ordertime text,ordernumber text,orderpeoplecount text);";
    private int ID;
    private String orderTime;
    private String orderNumber;//订单排号
    private String oderPeopleCount;
    private String totalPrice;
    private List<JjbeafOrderDetail> jjbeafOrderDetailList = new ArrayList<>();

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }


    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOderPeopleCount() {
        return oderPeopleCount;
    }

    public void setOderPeopleCount(String oderPeopleCount) {
        this.oderPeopleCount = oderPeopleCount;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<JjbeafOrderDetail> getJjbeafOrderDetailList() {
        return jjbeafOrderDetailList;
    }

    public void setJjbeafOrderDetailList(List<JjbeafOrderDetail> jjbeafOrderDetailList) {
        this.jjbeafOrderDetailList = jjbeafOrderDetailList;
    }
}
