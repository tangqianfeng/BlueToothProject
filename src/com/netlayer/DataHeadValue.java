package com.netlayer;

public class DataHeadValue {
	//数据头长度
	public static int HEAD_LEN = 5;
	//帧起始标志
	public static byte Data_FRAMESTART = (byte) 0xff;
	//ID高位
	public static byte Data_ID_HIGHT = 0x07;
	//ID低位
	public static byte Data_ID_LOW = 0x00;
	
	public static void setControlID(byte ID_HIGHT,byte ID_LOW){
		Data_ID_HIGHT=ID_HIGHT;
		Data_ID_LOW=ID_LOW;
	}
}
