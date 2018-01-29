package com.jinjiang.beefshop.util;

import android.content.Context;

import com.jinjiang.beefshop.model.FoodInfoBean;
import com.jinjiang.beefshop.model.secondMenu;
import com.jinjiang.beefshop.model.secondeMenuNameAndPrice;
import com.jinjiang.beefshop.sqlite.JjbeafOrderDetail;
import com.jinjiang.beefshop.sqlite.JjbeafSqlBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by xuqunxing on 2017/12/11.
 */

public class FileUtil {

//    public static File saveFileFromBytes(AppBean bean) {
//        String path = null;
//        JSONObject totalObj= null;
//        path = SystemConst.BASE_DIR + "/apps/"+"download4Upload/";
//        String saveName = path + bean.identifier+".txt";
//        try {
//            File file = new File(path);
//            if (!file.exists()) {
//                try {
//                    file.mkdirs();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            File dir = new File(saveName);
//            if (dir.exists()) {
//                FileUtil.deleteFile(dir.toString());
//            }
//            try {
//                dir.createNewFile();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            long apkId = bean.apkId;
//            long appId = bean.appId;
//            String pkgName = bean.identifier;
//            String appName  = bean.name;
//            String versionCode = bean.versionCode;
//            String recommendId = bean.recommendId;
//            int source = bean.source;
//            String channelId = bean.channelId;
//            String dataAnalysisId = bean.dataAnalysisId;
//            String interfaceName = bean.interfaceName;
//            int sourceId = bean.sourceId;
//            JSONArray jsonArray = new JSONArray();
//            totalObj = new JSONObject();
//            JSONObject object=new JSONObject();
//            object.put("apkId",apkId);
//            object.put("appId",appId);
//            object.put("pkgName",pkgName);
//            object.put("appName",appName);
//            object.put("versionCode",versionCode);
//            object.put("recommendId",recommendId);
//            object.put("source",source);
//            object.put("channelId",channelId);
//            object.put("dataAnalysisId",dataAnalysisId);
//            object.put("interfaceName",interfaceName);
//            object.put("sourceId",sourceId);
//            jsonArray.put(object);
//            totalObj.put("appDatail",jsonArray);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        byte[] b=totalObj.toString().getBytes();
//        BufferedOutputStream stream = null;
//        File file = null;
//        try {
//            file = new File(saveName);
//            FileOutputStream fstream = new FileOutputStream(file);
//            stream = new BufferedOutputStream(fstream);
//            stream.write(b);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (stream != null) {
//                try {
//                    stream.close();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        }
//        return file;
//    }

    public static String readLocalJson(Context context, String fileName){
        String localJson = null;
        try {
            File file=new File(fileName);
            FileInputStream inputStream= new FileInputStream(file);                  //创建输入流
            ByteArrayOutputStream outStream=new ByteArrayOutputStream();             //缓存输出流
            byte[] buffer =new byte[1024];                                           //创建字节数组
            int len=0;
            while((len=inputStream.read(buffer))!=-1){                              //循环读取数据并且将数据写入到缓存输出流中
                outStream.write(buffer, 0, len);
            }
            localJson = new String(outStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localJson;
    }

    public static FoodInfoBean parseJson(String json){
        FoodInfoBean foodInfoBean = new FoodInfoBean();
        try {
            JSONObject responseObj = new JSONObject(json);
            JSONArray firstmenuArr = responseObj.optJSONArray("firstmenu");
            if(firstmenuArr != null){
                for(int k =0 ;k<firstmenuArr.length();k++){
                    JSONObject jsonObject = firstmenuArr.getJSONObject(k);
                    String menuName = jsonObject.optString("menuname");
                    foodInfoBean.getMenuNameList().add(menuName);
                }
            }

            JSONArray listJson = responseObj.optJSONArray("foodinfos");
            if (listJson != null) {
                for (int i = 0; i < listJson.length(); i++) {
                    secondMenu secondMenu = new secondMenu();
                    JSONObject item = listJson.getJSONObject(i);
                    String secondmenu = item.getString("sencondMenuName");
                    secondMenu.setSecondMenuStr(secondmenu);
                    JSONArray jsonArray = item.optJSONArray("secondmenu");
                    if(jsonArray != null){
                        for(int j=0;j<jsonArray.length();j++){
                            JSONObject jsonObj = jsonArray.getJSONObject(j);
                            String foodName = jsonObj.optString("foodname");
                            int footprice = jsonObj.optInt("footprice");
                            boolean addfood = jsonObj.optBoolean("addfood");
                            secondeMenuNameAndPrice secondeMenuNameAndPrice = new secondeMenuNameAndPrice();
                            secondeMenuNameAndPrice.setName(foodName);
                            secondeMenuNameAndPrice.setPrice(footprice);
                            secondeMenuNameAndPrice.setAddfood(addfood);
                            secondMenu.getSecondeMenuNameAndPrices().add(secondeMenuNameAndPrice);
                        }
                    }
                    foodInfoBean.getSecondMenuList().add(secondMenu);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  foodInfoBean;
    }

    public static JjbeafSqlBean parseHistoryOrderJson(String json){
        JjbeafSqlBean foodInfoBean = new JjbeafSqlBean();
        try {
            JSONObject responseObj = new JSONObject(json);
            JSONArray firstmenuArr = responseObj.optJSONArray("jjbeafOrderDetailList");
            if(firstmenuArr != null){
                for(int k =0 ;k<firstmenuArr.length();k++){
                    JjbeafOrderDetail jjbeafOrderDetail = new JjbeafOrderDetail();
                    JSONObject jsonObject = firstmenuArr.getJSONObject(k);
                    String detail = jsonObject.optString("detail");
                    String foodName = jsonObject.optString("foodName");
                    String price = jsonObject.optString("price");
                    jjbeafOrderDetail.setDetail(detail);
                    jjbeafOrderDetail.setFoodName(foodName);
                    jjbeafOrderDetail.setPrice(price);
                    foodInfoBean.getJjbeafOrderDetailList().add(jjbeafOrderDetail);
                }
            }

            String oderPeopleCount = responseObj.optString("oderPeopleCount");
            String orderNumber = responseObj.optString("orderNumber");
            String orderTime = responseObj.optString("orderTime");
            String totalPrice = responseObj.optString("totalPrice");
            foodInfoBean.setOderPeopleCount(oderPeopleCount);
            foodInfoBean.setOrderNumber(orderNumber);
            foodInfoBean.setOrderTime(orderTime);
            foodInfoBean.setTotalPrice(totalPrice);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  foodInfoBean;
    }

}
