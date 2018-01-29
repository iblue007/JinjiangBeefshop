package com.jinjiang.beefshop.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuqunxing on 2017/12/11.
 */

public class secondMenu {

    private String secondMenuStr;
    private List<secondeMenuNameAndPrice> secondeMenuNameAndPrices = new ArrayList<>();
    public String getSecondMenuStr() {
        return secondMenuStr;
    }

    public void setSecondMenuStr(String secondMenuStr) {
        this.secondMenuStr = secondMenuStr;
    }

    public List<secondeMenuNameAndPrice> getSecondeMenuNameAndPrices() {
        return secondeMenuNameAndPrices;
    }

    public void setSecondeMenuNameAndPrices(List<secondeMenuNameAndPrice> secondeMenuNameAndPrices) {
        this.secondeMenuNameAndPrices = secondeMenuNameAndPrices;
    }
}
