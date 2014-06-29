package com.netlayer;

import java.io.IOException;
import java.io.InputStream;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bluetooth.MainActivity;
import com.dataserver.DataMath;
import com.dataserver.Msg;
import com.dataserver.ServerManager;
import com.useroperate.MainLayer;

public class RecThread extends Thread{

	private InputStream ins=null;
	private Handler handler;
	//�Ƿ���֡
	private boolean newres=true;
	//���ݳ���
	private int datalen=-1;
	private byte[] head_container=new byte[5];
	//��������
	private byte[] container=null;
	//��װ���ݳ���
	private int volume=0;
	//���߱�־
	public static boolean onlineFlag=false;
	
	public RecThread(Handler handler) {
		// TODO Auto-generated constructor stub
		this.handler = handler;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				byte[] buffer = new byte[128]; 
				int len=0;
				//����ͷ
				if(newres){
					init();
					for(int i=0;i<5;i++){
						int head_databyte = ins.read();
						head_container[i]=(byte) head_databyte;
					}
					
					datalen = DataMath.bytesToInt(new byte[]{head_container[3],head_container[4]});
					container = new byte[datalen];
	
				     
					newres=false;
					Log.d("RecThread-57","������"+datalen);
				}
				else if(volume<datalen){
					if((len=ins.read(buffer)) > -1){
						for(int i=0;i<len;i++){
							container[volume]=buffer[i];
							volume++;
						}
						Log.d("RecThread-66","���ݲ���"+volume+".."+datalen);
				     }
				}
				else if(volume==datalen){
					Log.d("RecThread-69","������������");
					fullDataDeal(container);
					newres=true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void fullDataDeal(byte[] temp){
		Log.d("RecThread-81","�յ�����");
		//����֪ͨ
		onlineFlag=true;
		//�������ͷ
		String head = MainLayer.mainlayer.getSendStateContext();
		//���÷����ӿ�  ԭʼ�������͵������
		Msg mdata = ServerManager.dealWith(head, temp, MainActivity.ma);
		if(mdata!=null){
			Log.d("RecThread-81","�յ���������");
			//������������ȡ���ݷ���
			MainLayer.mainlayer.dataSend();
			Message msg = new Message();
			msg.obj = mdata;
			handler.sendMessage(msg);
		}
	}
	
	private void init(){
		head_container=new byte[5];
		datalen=-1;
		container=null;
		volume=0;
	}
	
	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		try {
			ins = MainActivity.btSocket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("RecThread-119","..");
		}
		super.start();
	}	
}
