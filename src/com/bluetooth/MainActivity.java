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
	//��ť���
	public static Button btConnect, btOpen, btIsVisible, btSearch;
	//����������
	public static BluetoothAdapter btAda;
	//UUIDЭ��
	public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	//��������
	public static BluetoothSocket btSocket;
	// ���ڴ洢�������������豸
	public static Vector<String> vc_str = new Vector<String>();;
	// δ���������豸
	public static final int NONE = 1;
	// �������������豸
	public static final int CONNTCTING = 2;
	// �����������豸
	public static final int CONNTCTED = 3;
	// ��ǰ��������״̬
	public static int bluetoothState = NONE;
	// ���������豸���±�����
	public static int deviceIndex;
	//��������  ��δ��
	public static MainActivity ma; 
	public static TextView text ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ma = this;
		//û������ȫ�������ڹ۲쵱ǰ�����Ƿ�򿪵�״̬�仯
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bluetooth);
		//ʵ����ť
		btOpen = (Button) findViewById(R.id.Btn_OpenBt);
		btIsVisible = (Button) findViewById(R.id.Btn_BtIsVisible);
		btSearch = (Button) findViewById(R.id.Btn_SearchDrives);
		btConnect = (Button) findViewById(R.id.Btn_ConnectDrives);
		text = (TextView) findViewById(R.id.textView);
		//Ϊ��ť�󶨼�����
		btOpen.setOnClickListener(this);
		btIsVisible.setOnClickListener(this);
		btSearch.setOnClickListener(this);
		btConnect.setOnClickListener(this);
		//ʵ������������
		btAda = BluetoothAdapter.getDefaultAdapter();
		if (btAda.getState() == BluetoothAdapter.STATE_OFF) {
			btOpen.setText("������");
		} else if (btAda.getState() == BluetoothAdapter.STATE_ON) {
			btOpen.setText("�ر�����");
		}
		// ע��Receiver����ȡ�����豸��صĽ��
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);// Զ���豸���ֶ�����
		intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//Զ���豸�ļ�̬�ı仯������
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//����ɨ�豾��������ģ�ı䶯����
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//״̬�ı䶯��
		registerReceiver(searchDevices, intent);//ע�����
		
		boardAnimation();
		buttongroupAnimation();
	}
	//��������
	private BroadcastReceiver searchDevices = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			//�����豸ʱ��ȡ���豸��MAC��ַ
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String str = device.getName() + "*" + device.getAddress();
				Log.i("2222222222", device.getAddress());
				if (vc_str != null) {
					if (vc_str.size() != 0) {
						for (int j = 0; j < vc_str.size(); j++) {
							// ��ֹ�ظ����
							if (vc_str.elementAt(j).equals(str)) {
								return;
							}
						}
						// ������ӷ��ֵ��豸���ƺ�mac��ַ
						vc_str.addElement(str);
						//���
						text.append("**"+str+"\n");	
					} else {
						vc_str.addElement(str);
						//���
						text.append("**"+str+"\n");				
					}
				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		if (bluetoothState != CONNTCTED) {
			if (v == btOpen) {//��������
				if (btAda.getState() == BluetoothAdapter.STATE_OFF) {
					btAda.enable();
					btOpen.setText("�ر�����");
				} else if (btAda.getState() == BluetoothAdapter.STATE_ON) {
					btAda.disable();
					btOpen.setText("������");
				}
			} else if (v == btIsVisible) {//�����Ƿ�ɼ�
				Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 110);
				//�ڶ��������Ǳ������������ֵ�ʱ�䣬ϵͳĬ�Ϸ�Χ[1-300],������ΧĬ��300��С�ڷ�ΧĬ��120
				startActivity(intent);
			} else if (v == btSearch) {//��������
				text.setText("");
				if (btAda.getState() == BluetoothAdapter.STATE_OFF) {// ���������û��
					Toast.makeText(MainActivity.this, "���ȴ�����", Toast.LENGTH_SHORT).show();
					return;
				}
				vc_str.removeAllElements();
				btAda.startDiscovery();
				Toast.makeText(MainActivity.this, "������������", Toast.LENGTH_SHORT).show();
			} else if (v == btConnect) {
				if (vc_str.size() == 0) {
					Toast.makeText(MainActivity.this, "��ǰû���豸", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent();
					intent.setClass(this, ChoiceDrivesList.class);
					this.startActivity(intent);
				}
			}
		} else {
			Toast.makeText(this, "���������쳣�����������У�", Toast.LENGTH_SHORT).show();
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
	 * dipתpix
	 */
	 private int dipToPx(Context context, float dpValue) {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return (int) (dpValue * scale + 0.5f);  
	}
}
