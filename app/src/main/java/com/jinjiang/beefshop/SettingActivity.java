package com.jinjiang.beefshop;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.GpCom;
import com.gprinter.service.GpPrintService;
import com.jinjiang.beefshop.buletouth.BuleToothListAdapter;
import com.jinjiang.beefshop.buletouth.utils.ProgressDialogUtil;
import com.jinjiang.beefshop.buletouth.utils.ResultCode;
import com.jinjiang.beefshop.buletouth.utils.SharedPreferencesUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuqunxing on 2017/12/16.
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener
        ,AdapterView.OnItemClickListener{

    //title
    private LinearLayout title_left_ll;
    private TextView title_middle_tv;

    private Switch buletooth_swtich;
    private TextView buletooth_tv;
    private TextView device_tv;
    private ListView buletooth_list;
    private TextView item_tv;
    private LinearLayout item_rl;
    private RelativeLayout alread_rl;
    private TextView alread_name;
    private TextView alread_id;
    private ImageView alread_iv;
    private TextView alread_tv;

    private BuleToothListAdapter arrayAdapter ;

    private List<String> nameData = new ArrayList<>();
    private List<String> adressData = new ArrayList<>();
    private List<BluetoothDevice> deviceData = new ArrayList<>();

    private BluetoothAdapter bluetoothAdapter;
    private GpService mGpService;
    private PrinterServiceConnection conn = null;
    //是否已经链接的标志
    private boolean flag;
    //已经链接的设备地址
    private String address;

    private int loction = -1;

    private GpCom.ERROR_CODE r;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case ResultCode.STARTSERVER :
                    connect((int)msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        connection();

        initView();

    }

    private void initView(){
        title_left_ll = (LinearLayout)findViewById(R.id.title_left);
        title_left_ll.setOnClickListener(this);
        title_middle_tv = (TextView) findViewById(R.id.title_text_middle);
        title_middle_tv.setVisibility(View.VISIBLE);
        title_middle_tv.setText("设置蓝牙连接");
        buletooth_swtich = (Switch) findViewById(R.id.buletooth_switch);
        buletooth_tv = (TextView) findViewById(R.id.buletooth_tv);
        device_tv = (TextView) findViewById(R.id.buletooth_device_tv);
        buletooth_list = (ListView) findViewById(R.id.buletooth_list);
        item_tv = (TextView) findViewById(R.id.item_buletooth_tv1);
        item_rl = (LinearLayout) findViewById(R.id.item_buletooth_rl);
        alread_rl = (RelativeLayout)findViewById(R.id.bulttooth_alread_rl);
        alread_name = (TextView) findViewById(R.id.bulttooth_alread_name);
        alread_id = (TextView) findViewById(R.id.bulttooth_alread_id);
        alread_iv = (ImageView) findViewById(R.id.bulttooth_alread_iv);
        alread_tv = (TextView) findViewById(R.id.bulttooth_alread_tv);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //初始化
        item_tv.setText("扫描设备");
        item_tv.setVisibility(View.VISIBLE);
        item_tv.setTextColor(Color.RED);
        buletooth_tv.setVisibility(View.VISIBLE);
        device_tv.setVisibility(View.GONE);
        buletooth_list.setVisibility(View.GONE);
        item_rl.setVisibility(View.GONE);
        alread_rl.setVisibility(View.GONE);
        alread_tv.setVisibility(View.GONE);
        flag = (boolean) SharedPreferencesUtils.getDevice("flag", false);
        if(flag){
            if(bluetoothAdapter.isEnabled()){
                buletooth_swtich.setChecked(true);
                buletooth_tv.setVisibility(View.GONE);
                alread_tv.setVisibility(View.VISIBLE);
                alread_rl.setVisibility(View.VISIBLE);
                alread_name.setText((String) SharedPreferencesUtils.getDevice("name", ""));
                alread_id.setText(address = (String)SharedPreferencesUtils.getDevice("address",""));
            }else{
                buletooth_swtich.setChecked(false);
            }
        }else {
            buletooth_swtich.setChecked(false);
        }
        arrayAdapter = new BuleToothListAdapter(SettingActivity.this,
                nameData,adressData);
        buletooth_list.setAdapter(arrayAdapter);
        buletooth_list.setOnItemClickListener(this);//列表的监听
        buletooth_swtich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (mGpService == null) {
                        return;
                    }
                    if (bluetoothAdapter == null) {
                        Toast.makeText(SettingActivity.this,"设备不支持蓝牙",Toast.LENGTH_SHORT).show();
                        buletooth_swtich.setChecked(false);
                    } else {
                        if (!bluetoothAdapter.isEnabled()) {
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            // 设置蓝牙可见性，最多300秒   
                            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                            startActivityForResult(intent, ResultCode.BULETOOTH);
                        }else{
                            //如果蓝牙已经打开，进行操做
                            nameData.clear();
                            adressData.clear();
                            deviceData.clear();
                            buletooth_tv.setVisibility(View.GONE);
                            device_tv.setVisibility(View.VISIBLE);
                            buletooth_list.setVisibility(View.VISIBLE);
                            item_rl.setVisibility(View.VISIBLE);
                            searchBuleTooth();//扫描设备
                        }
                    }
                } else {
                    bluetoothAdapter.disable();//关闭蓝牙
                    buletooth_tv.setVisibility(View.VISIBLE);
                    device_tv.setVisibility(View.GONE);
                    buletooth_list.setVisibility(View.GONE);
                    item_rl.setVisibility(View.GONE);
                    alread_rl.setVisibility(View.GONE);
                    alread_tv.setVisibility(View.GONE);
                    SharedPreferencesUtils.setDevice(0,"","");
                }
            }
        });
        item_rl.setOnClickListener(this);

    }

    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(this, GpPrintService.class);
        this.bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mGpService = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
        }
    }
    /**
     * 扫描设备
     */
    private void searchBuleTooth(){
        // 设置广播信息过滤   
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 注册广播接收器，接收并处理搜索结果   
        registerReceiver(receiver, intentFilter);
        // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去   
        bluetoothAdapter.startDiscovery();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.item_buletooth_rl :
                nameData.clear();
                adressData.clear();
                deviceData.clear();
                loction = -1;
                searchBuleTooth();//扫描设备
//                item_rl.setVisibility(View.GONE);
                break;
            case R.id.title_left :
                finish();
                break;
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean flag = false;
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(nameData.size() == 0) {
                    adressData.add(device.getAddress());
                    deviceData.add(device);
                    if (device.getName() == null){
                        nameData.add("无名称");
                    }else {
                        if (device.getName().equals("")) {
                            nameData.add("无名称");
                        } else {
                            nameData.add(device.getName());
                        }
                    }
                }else{
                    for(int i=0;i<nameData.size();i++){
                        if(adressData.get(i).equals(device.getAddress())){
                            flag = true;
                        }
                    }
                    if(flag){
                        flag = false;
                    }else{
                        adressData.add(device.getAddress());
                        deviceData.add(device);
                        if (device.getName() == null){
                            nameData.add("无名称");
                        }else {
                            if (device.getName().equals("")) {
                                nameData.add("无名称");
                            } else {
                                nameData.add(device.getName());
                            }
                        }
                    }
                }
                //如果有已经链接的设备，则去除设备列表中显示
                if(flag){
                    for(int i=0;i<adressData.size();i++){
                        if(adressData.get(i).equals(address)){
                            adressData.remove(i);
                        }
                    }
                }
                //刷新列表
                arrayAdapter.setData(nameData,adressData);
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int connectState = device.getBondState();
                switch (connectState) {
                    case BluetoothDevice.BOND_NONE:
                        break;
                    case BluetoothDevice.BOND_BONDING:
//                        ProgressDialogUtil.dismiss(BuleToothActivity.this);
//                        ProgressDialogUtil.showLoading(BuleToothActivity.this,"配对中...");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        ProgressDialogUtil.dismiss(SettingActivity.this);
                        ProgressDialogUtil.showLoading(SettingActivity.this, "获取服务...");
                        // 连接
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int rel = 0;
                                try {//使用端口1，4代表模式为蓝牙模式，蓝牙地址，最后默认为0
                                    rel = mGpService.openPort(1,4,adressData.get(loction), 0);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                r = GpCom.ERROR_CODE.values()[rel];
                                int flag = 0;
                                while(r == GpCom.ERROR_CODE.SUCCESS && flag<=5){
                                    try {//使用端口1，4代表模式为蓝牙模式，蓝牙地址，最后默认为0
                                        Thread.sleep(1000);
                                        rel = mGpService.openPort(1,4,adressData.get(loction), 0);
                                        r = GpCom.ERROR_CODE.values()[rel];
                                        flag++;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                Message msg = new Message();
                                msg.what = ResultCode.STARTSERVER;
                                msg.obj = flag;
                                handler.sendMessage(msg);
                            }
                        }).start();
                        break;
                }
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bluetoothAdapter.cancelDiscovery();//停止扫描
        BluetoothDevice device = deviceData.get(position);
        int buleState = device.getBondState();
        loction = position;
        switch(buleState){
            // 未配对      
            case BluetoothDevice.BOND_NONE:
                try {
                    Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                    createBondMethod.invoke(device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            //已配对
            case BluetoothDevice.BOND_BONDED :
                ProgressDialogUtil.showLoading(SettingActivity.this,"获取服务...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int rel = 0;
                        try {//使用端口1，4代表模式为蓝牙模式，蓝牙地址，最后默认为0
                            rel = mGpService.openPort(1,4,adressData.get(loction), 0);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        r = GpCom.ERROR_CODE.values()[rel];
                        int flag = 0;
                        while(r == GpCom.ERROR_CODE.SUCCESS && flag<=5){
                            try {//使用端口1，4代表模式为蓝牙模式，蓝牙地址，最后默认为0
                                Thread.sleep(1000);
                                rel = mGpService.openPort(1,4,adressData.get(loction), 0);
                                r = GpCom.ERROR_CODE.values()[rel];
                                flag++;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Message msg = new Message();
                        msg.what = ResultCode.STARTSERVER;
                        msg.obj = flag;
                        handler.sendMessage(msg);
                    }
                }).start();
                break;
        }
    }

    private Thread thread = new Thread(){
        @Override
        public void run() {
            super.run();

        }
    };


    /**
     * 连接
     */
    private void connect(int flag)  {
//        int rel = 0;
//        try {//使用端口1，4代表模式为蓝牙模式，蓝牙地址，最后默认为0
//            rel = mGpService.openPort(1,4,adressData.get(loction), 0);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        r = GpCom.ERROR_CODE.values()[rel];
        if (flag <= 5) {
            if (r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN) {
                try {
                    //开启成功
                    ProgressDialogUtil.dismiss(SettingActivity.this);
                    Toast.makeText(SettingActivity.this,"成功", Toast.LENGTH_SHORT).show();
                    alread_tv.setVisibility(View.VISIBLE);
                    alread_rl.setVisibility(View.VISIBLE);
                    String name = nameData.get(loction);
                    String address = adressData.get(loction);
                    alread_name.setText(name);
                    alread_id.setText(address);
                    SharedPreferencesUtils.setDevice(1, name, address);//保存数据
                    nameData.remove(loction);
                    adressData.remove(loction);
                    //刷新列表
                    arrayAdapter.setData(nameData, adressData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(SettingActivity.this, GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        }else{
            ProgressDialogUtil.dismiss(SettingActivity.this);
            Toast.makeText(SettingActivity.this,"失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ResultCode.BULETOOTH) {
            if (resultCode == RESULT_OK) {
                nameData.clear();
                adressData.clear();
                deviceData.clear();
                buletooth_tv.setVisibility(View.GONE);
                device_tv.setVisibility(View.VISIBLE);
                buletooth_list.setVisibility(View.VISIBLE);
                item_rl.setVisibility(View.VISIBLE);
                searchBuleTooth();//扫描设备
            } else if (resultCode == RESULT_CANCELED) {
                buletooth_swtich.setChecked(false);
                Toast.makeText(this, "不允许蓝牙开启", Toast.LENGTH_SHORT).show();
            }
        }
    }
}