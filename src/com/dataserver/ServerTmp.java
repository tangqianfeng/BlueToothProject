package com.dataserver;

/**����serverʱ�õ����м�ṹ*/
public class ServerTmp
{
	/**�����������н��������ݣ�һ������º�GOON��˼һ��*/
	public static final int DATA = 0x01;
	/**�������½�������typeΪGOON˵���ⲻ��һ������������߿����ݣ�src������Ҫ��������������*/
	public static final int GOON = 0x02;
	/**ֹͣ���½�������typeΪSTOP˵������һ�����������������Ϊ�գ�msg���������Ϊ������Ϣ����*/
	public static final int STOP = 0x04;
	/**info*/
	public static final int INFO = 0x08;
	/**faultcode*/
	public static final int FCODE = 0x10;
	
	/**�������ͨ������*/
	private byte[] src = null;
	/**�������Ҫ�������½�����msg����Ϊ������Ϣ����*/
	private Msg msg = null;
	/**�Ƿ���Ҫ�������½����ı��*/
	private int type = STOP;
	
	//setter and getter:
	public byte[] getSrc()
	{
		return src;
	}
	public void setSrc(byte[] src)
	{
		this.src = src;
	}
	public Msg getMsg()
	{
		return msg;
	}
	public void setMsg(Msg msg)
	{
		this.msg = msg;
	}
	public int getType()
	{
		return type;
	}
	public void setType(int type)
	{
		this.type = type;
	}
}
