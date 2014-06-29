package com.netlayer;

import java.io.IOException;

import android.util.Log;

public class ThreadManger {
	//������Ƶ��
	private int sendtime=3000;
	//�Ƿ����� ��־
	public static boolean flag;
	private static byte[] activate = null;
	private NetSocket ns=null;
	
	public ThreadManger(){
		ns = NetSocket.getInstace(this);
	}
	
	public synchronized void tt(){
		try {
			flag=true;
			super.wait(sendtime);
			if(flag){
				try {
					ns.send(activate,1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("HeartBeat-28","���ʹ���");
					e.printStackTrace();
				}
			}	
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void rr(){
		flag=false;
		super.notify();
	}
	
	public static void setHeartBeatBlood(byte[] blood){
		activate=blood;
	}
}
