package com.netlayer;

public class DataHeadValue {
	//����ͷ����
	public static int HEAD_LEN = 5;
	//֡��ʼ��־
	public static byte Data_FRAMESTART = (byte) 0xff;
	//ID��λ
	public static byte Data_ID_HIGHT = 0x07;
	//ID��λ
	public static byte Data_ID_LOW = 0x00;
	
	public static void setControlID(byte ID_HIGHT,byte ID_LOW){
		Data_ID_HIGHT=ID_HIGHT;
		Data_ID_LOW=ID_LOW;
	}
}
