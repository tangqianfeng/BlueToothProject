package com.dataserver;

/**解析server时用到的中间结构*/
public class ServerTmp
{
	/**【保留】还有解析的数据，一般情况下和GOON意思一样*/
	public static final int DATA = 0x01;
	/**继续向下解析，当type为GOON说明这不是一条控制命令或者空数据，src将是需要继续解析的数据*/
	public static final int GOON = 0x02;
	/**停止往下解析，当type为STOP说明这是一条控制命令或者数据为空，msg将被填充作为最后的信息返回*/
	public static final int STOP = 0x04;
	/**info*/
	public static final int INFO = 0x08;
	/**faultcode*/
	public static final int FCODE = 0x10;
	
	/**解析后的通信数据*/
	private byte[] src = null;
	/**如果不需要继续往下解析，msg将作为最后的消息返回*/
	private Msg msg = null;
	/**是否需要继续向下解析的标记*/
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
