package com.jinjiang.beefshop.model;

/**
 * Created by caobo on 2016/7/12 0012.
 * 小区商圈商品属性
 */
public class ShopProduct {
    private int id;
    private String price;//单价
    private String goods;//货物名称
    private String createtime;
    private String peopleCount;//用餐人数
    private String orderNumber;//订单排号
    private boolean addfood;//是否显示加料
    private int addfoodNum;//加料个数
    /** * 商品数目*/
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(String peopleCount) {
        this.peopleCount = peopleCount;
    }

    public boolean isAddfood() {
        return addfood;
    }

    public void setAddfood(boolean addfood) {
        this.addfood = addfood;
    }

    public int getAddfoodNum() {
        return addfoodNum;
    }

    public void setAddfoodNum(int addfoodNum) {
        this.addfoodNum = addfoodNum;
    }
}
