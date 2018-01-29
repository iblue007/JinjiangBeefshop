package com.jinjiang.beefshop.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuqunxing on 2017/12/11.
 */
public class FoodInfoBean {

    private List<String> menuNameList = new ArrayList<>();
    private List<secondMenu> secondMenuList = new ArrayList<>();;

    public List<secondMenu> getSecondMenuList() {
        return secondMenuList;
    }

    public void setSecondMenuList(List<secondMenu> secondMenuList) {
        this.secondMenuList = secondMenuList;
    }

    public List<String> getMenuNameList() {
        return menuNameList;
    }

    public void setMenuNameList(List<String> menuNameList) {
        this.menuNameList = menuNameList;
    }
}


