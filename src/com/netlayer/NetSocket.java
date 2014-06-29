package com.netlayer;

import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

import com.bluetooth.MainActivity;

public class NetSocket {
	public static OutputStream ops=null;
	private static NetSocket instance=null;
	private ThreadManger thm=null;
	
	private NetSocket(ThreadManger thm){
		try {
			ops = MainActivity.btSocket.getOutputStream();
			this.thm=thm;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * ������
	 */
	public static NetSocket getInstace(ThreadManger thm){
		if( instance == null ) {
            instance = new NetSocket(thm);
        }
        return instance;
    }   
	
	
	/**
	 * 
	 * @param data ���͵�����
	 * @param type 1.����������   2.��ͨ����,��������������ʱ��
	 * @throws IOException
	 */
	public void send(byte[] data,int type) throws IOException{
		if(data.length>0){
			if(type==2){
				thm.rr();
			}
			ops.write(addHead(data));
			Log.d("NetSocket-46","��Ϣ����");
		}
	}
	
	/*
	 * �ֽ�����ͷ
	 */
	private byte[] addHead(byte[] data){
		byte[] sdata = new byte[data.length+DataHeadValue.HEAD_LEN];
		//��ͷ
		sdata[0]= DataHeadValue.Data_FRAMESTART;
		sdata[1]=DataHeadValue.Data_ID_HIGHT;
		sdata[2]=DataHeadValue.Data_ID_LOW;
		sdata[3]=(byte) ((data.length>>8)&0xFF);
		sdata[4]=(byte) (data.length&0xFF);
		for(int i=DataHeadValue.HEAD_LEN;i<data.length+DataHeadValue.HEAD_LEN;i++){
			sdata[i]=data[i-DataHeadValue.HEAD_LEN];
		}
		return sdata;
	}
	
	public void close(){
		try {
			ops.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
