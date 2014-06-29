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
	 * 单例化
	 */
	public static NetSocket getInstace(ThreadManger thm){
		if( instance == null ) {
            instance = new NetSocket(thm);
        }
        return instance;
    }   
	
	
	/**
	 * 
	 * @param data 发送的数据
	 * @param type 1.心跳包发送   2.普通发送,更新心跳包阻塞时间
	 * @throws IOException
	 */
	public void send(byte[] data,int type) throws IOException{
		if(data.length>0){
			if(type==2){
				thm.rr();
			}
			ops.write(addHead(data));
			Log.d("NetSocket-46","信息发送");
		}
	}
	
	/*
	 * 字节流加头
	 */
	private byte[] addHead(byte[] data){
		byte[] sdata = new byte[data.length+DataHeadValue.HEAD_LEN];
		//加头
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
