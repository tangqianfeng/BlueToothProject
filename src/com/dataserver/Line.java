package com.dataserver;

/**一个Line表示一行消息
 * Line line;
 * line.des一条数据的描述
 * line.value 一条数据真实的值
 * line.show 一条数据显示的字符串*/
public class Line
{
	/**一条数据的描述*/
	public String des;
	/**一条数据真实的值*/
	public byte[] value;
	/**一条数据显示的字符串*/
	public String show;
	//==============构造器================
	/**默认构造*/
	public Line()
	{
		this.des = "";
		this.value = null;
		this.show = "";
	}
	/**完全构造*/
	public Line(String des,byte[] value,String show)
	{
		this.des = des;
		this.value = value;
		this.show = show;
	}
	/**部分构造*/
	public Line(String des,byte[] value)
	{
		this.des = des;
		this.value = value;
	}
	/**部分构造。适用于faultCode*/
	public Line(String des,String show)
	{
		this.des = des;
		this.show = show;
	}
	//=======setter getter=============
	public String getDes()
	{
		return des;
	}
	public void setDes(String des)
	{
		this.des = des;
	}
	public byte[] getValue()
	{
		return value;
	}
	public void setValue(byte[] value)
	{
		this.value = value;
	}
	public String getShow()
	{
		return show;
	}
	public void setShow(String show)
	{
		this.show = show;
	}
}
