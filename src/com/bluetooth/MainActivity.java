package com.bluetooth;

import java.util.Vector;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bluetooth.R;


public class MainActivity extends Activity implements OnClickListener {
	//按钮组件
	public static Button btConnect, btOpen, btIsVisible, btSearch;
	//蓝牙适配器
	public static BluetoothAdapter btAda;
	//UUID协议
	public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	//蓝牙连接
	public static BluetoothSocket btSocket;
	// 用于存储搜索到的蓝牙设备
	public static Vector<String> vc_str = new Vector<String>();;
	// 未连接蓝牙设备
	public static final int NONE = 1;
	// 正在连接蓝牙设备
	public static final int CONNTCTING = 2;
	// 已连接蓝牙设备
	public static final int CONNTCTED = 3;
	// 当前蓝牙连接状态
	public static int bluetoothState = NONE;
	// 连接蓝牙设备的下标索引
	public static int deviceIndex;
	//单例本类  暂未用
	public static MainActivity ma; 
	public static TextView text ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ma = this;
		//没有设置全屏，便于观察当前蓝牙是否打开的状态变化
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bluetooth);
		//实例按钮
		btOpen = (Button) findViewById(R.id.Btn_OpenBt);
		btIsVisible = (Button) findViewById(R.id.Btn_BtIsVisible);
		btSearch = (Button) findViewById(R.id.Btn_SearchDrives);
		btConnect = (Button) findViewById(R.id.Btn_ConnectDrives);
		text = (TextView) findViewById(R.id.textView);
		//为按钮绑定监听器
		btOpen.setOnClickListener(this);
		btIsVisible.setOnClickListener(this);
		btSearch.setOnClickListener(this);
		btConnect.setOnClickListener(this);
		//实例蓝牙适配器
		btAda = BluetoothAdapter.getDefaultAdapter();
		if (btAda.getState() == BluetoothAdapter.STATE_OFF) {
			btOpen.setText("打开蓝牙");
		} else if (btAda.getState() == BluetoothAdapter.STATE_ON) {
			btOpen.setText("关闭蓝牙");
		}
		// 注册Receiver来获取蓝牙设备相关的结果
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);// 远程设备发现动作。
		intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//远程设备的键态的变化动作。
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//蓝牙扫描本地适配器模改变动作。
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//状态改变动作
		registerReceiver(searchDevices, intent);//注册接收
		
		boardAnimation();
		buttongroupAnimation();
	}
	//监听动作
	private BroadcastReceiver searchDevices = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			//搜索设备时，取得设备的MAC地址
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String str = device.getName() + "*" + device.getAddress();
				Log.i("2222222222", device.getAddress());
				if (vc_str != null) {
					if (vc_str.size() != 0) {
						for (int j = 0; j < vc_str.size(); j++) {
							// 防止重复添加
							if (vc_str.elementAt(j).equals(str)) {
								return;
							}
						}
						// 容器添加发现的设备名称和mac地址
						vc_str.addElement(str);
						//添加
						text.append("**"+str+"\n");	
					} else {
						vc_str.addElement(str);
						//添加
						text.append("**"+str+"\n");				
					}
				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		if (bluetoothState != CONNTCTED) {
			if (v == btOpen) {//蓝牙开关
				if (btAda.getState() == BluetoothAdapter.STATE_OFF) {
					btAda.enable();
					btOpen.setText("关闭蓝牙");
				} else if (btAda.getState() == BluetoothAdapter.STATE_ON) {
					btAda.disable();
					btOpen.setText("打开蓝牙");
				}
			} else if (v == btIsVisible) {//蓝牙是否可见
				Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 110);
				//第二个参数是本机蓝牙被发现的时间，系统默认范围[1-300],超过范围默认300，小于范围默认120
				startActivity(intent);
			} else if (v == btSearch) {//搜索蓝牙
				text.setText("");
				if (btAda.getState() == BluetoothAdapter.STATE_OFF) {// 如果蓝牙还没打开
					Toast.makeText(MainActivity.this, "请先打开蓝牙", Toast.LENGTH_SHORT).show();
					return;
				}
				vc_str.removeAllElements();
				btAda.startDiscovery();
				Toast.makeText(MainActivity.this, "正在搜索……", Toast.LENGTH_SHORT).show();
			} else if (v == btConnect) {
				if (vc_str.size() == 0) {
					Toast.makeText(MainActivity.this, "当前没有设备", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent();
					intent.setClass(this, ChoiceDrivesList.class);
					this.startActivity(intent);
				}
			}
		} else {
			Toast.makeText(this, "误操作造成异常，请重新运行！", Toast.LENGTH_SHORT).show();
			this.finish();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		this.unregisterReceiver(searchDevices);
		this.finish();
		this.overridePendingTransition(R.anim.back_push_left_in, R.anim.back_push_left_out);	
		System.exit(0);
	}
	
	private void boardAnimation(){
		LinearLayout borad = (LinearLayout) super.findViewById(R.id.borad); 
		int dp = dipToPx(this, 250);
		Animation animation = new TranslateAnimation(0, 0, -dp, 0);
		animation.setDuration(1000);
		borad.startAnimation(animation);
	}
	
	private void buttongroupAnimation(){
		LinearLayout buttongroup = (LinearLayout) super.findViewById(R.id.buttongroup); 
		int dp = dipToPx(this, 400);
		Animation animation = new TranslateAnimation(-dp, 0, 0, 0);
		animation.setDuration(1000);
		buttongroup.startAnimation(animation);
	}
	
	/*
	 * dip转pix
	 */
	 private int dipToPx(Context context, float dpValue) {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return (int) (dpValue * scale + 0.5f);  
	}
}
