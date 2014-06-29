package com.useroperate;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/*
 * 数据发送   数据链
 */
public class DispatchDataList {
	//数据链
	private List<String> listdata;
	//自选项数据包
	private int[][] switchData ;
	//服务头
	private byte[] head_service=null;
	
	public DispatchDataList(){
		listdata = new ArrayList<String>();
	}
	
	public void clearDispatchDataDataNum(){
		listdata.clear();
	}
	
	//往数据链里加数据
	public void addData(String data){
		listdata.add(data);
	}
	
	public String getData(){
		if(listdata.size()>0){
			Log.d("DispatchDataList-27:",""+listdata.size());
			//发送数据 倒序
			String data = (String) listdata.remove(0);
			return data;
		}
		return null;	
	}
	
	public void setSwitchData(int byteindex,int startbit,int endbit,int data){		
		Log.d("DispatchDataList-40",data+"");
		if(startbit==endbit){
			Log.d("Dispatcher-39","..");
			//数组顺序和字节高低位顺序相反
			switchData[byteindex][7-startbit] = data;	
			return;
		}
		Log.d("Dispatcher-43","..");
		int len = endbit-startbit+1;
		int[] ii= intToBinaryArray(data,len);
		int daozhi = ii.length-1;
		for(int i=0;i<len;i++){
			int hho = startbit+i;
			Log.d("Dispatcher-47",byteindex+"..."+hho+"...."+ii[i]);
			//数组顺序和字节高低位顺序相反 倒置顺序
			switchData[byteindex][7-startbit-i]=ii[daozhi-i];
		}
	}
		
	public void clearSwitch(int datalen){
		head_service=null;
		switchData = new int[datalen][8];
	}
	
	
	//转换switchdata
	public byte[] getByteData(){
		byte[] result = new byte[switchData.length];
		
		for(int row=0;row<switchData.length;row++)
		{
			for(int col=0;col<switchData[row].length;col++)
			{
				result[row] = (byte) (result[row]&0xFF | switchData[row][col]<<(7-col)) ;
			}
		}
		for(int i=0;i<switchData.length;i++){
			Log.d("Dispatcher-71",result[i]+"");
		}
		if(head_service!=null){
			int lenth = head_service.length+result.length;
			byte[] service_result = new byte[lenth];
			for(int i=0;i<head_service.length;i++){
				service_result[i]= head_service[i];
			}
			for(int i=head_service.length;i<service_result.length;i++){
				service_result[i]= result[i-head_service.length];
			}
			return service_result;
		}
		return result;
	}
	
	public  int[] intToBinaryArray(int src,int arrayLength)
	{
		String srcTmp = Integer.toBinaryString(src);
		int[] result = new int[arrayLength];
		int value = arrayLength-srcTmp.length();
		for(int i=0;i<srcTmp.length();i++)
		{
			result[value+i] = Integer.parseInt(srcTmp.substring(i,i+1));
		}
		return result;
	}
	
	public void setServiceHead(byte[] head_service){
		this.head_service =  head_service;
	}

}
