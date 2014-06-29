package com.bluetooth;

import java.io.IOException;
import java.util.UUID;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.bluetooth.R;
import com.useroperate.MainLayer;


public class ChoiceDrivesList extends Activity {
	//���������豸������
	private String[] names;
	//��ʾ
	private Toast toast;
	//�Ի�����ʾ��ǰ�������������豸
	private AlertDialog.Builder dialog;

	public ChoiceDrivesList() {
		names = new String[MainActivity.vc_str.size()];
		for (int i = 0; i < MainActivity.vc_str.size(); i++) {
			names[i] = MainActivity.vc_str.elementAt(i);
		}
	}
	public void DisplayToast(String str, int type) {
		try {
			toast = null;
			if (type == 0) {
				toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
			} else {
				toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
			}
			toast.setGravity(Gravity.TOP, 0, 220);
			toast.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new AlertDialog.Builder(ChoiceDrivesList.this);
		dialog.setIcon(android.R.drawable.btn_dialog);
		dialog.setSingleChoiceItems(names, 0, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				MainActivity.deviceIndex = which;
			}
		}).setIcon(R.drawable.logo).setPositiveButton("����", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				DisplayToast("���������豸��" + MainActivity.vc_str.elementAt(MainActivity.deviceIndex), 1);
				MainActivity.bluetoothState = MainActivity.CONNTCTING;
				connecting();
			}
			
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				finish();
			}
		}).setTitle("��ѡ�������豸!");
		dialog.show();
	}
	
	private void connecting(){
		//ȡ���ɼ�
		MainActivity.btAda.cancelDiscovery();
		String str = MainActivity.vc_str.elementAt(MainActivity.deviceIndex);
		String[] values = str.split("\\*");//����split�Ĳ����ǲ���������ʽ����
		// *�� ֮ǰһ����*�� ֮�� һ��
		String address = values[1];
		Log.i("11111111111", address);
		UUID uuid = UUID.fromString(MainActivity.SPP_UUID);//�����ձ�֧��SPPЭ��
		//ʵ�������豸
		BluetoothDevice btDevice = MainActivity.btAda.getRemoteDevice(address);
		try {
			MainActivity.btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
			MainActivity.btSocket.connect();
		} catch (IOException e) {
			Toast.makeText(MainActivity.ma, "�޷����Ӵ��豸!", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		}
		
		MainActivity.bluetoothState = MainActivity.CONNTCTED;
		Intent intent = new Intent();
		intent.setClass(ChoiceDrivesList.this,MainLayer.class);
		this.startActivity(intent);
		finish();
	}
}