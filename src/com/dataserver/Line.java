package com.dataserver;

/**һ��Line��ʾһ����Ϣ
 * Line line;
 * line.desһ�����ݵ�����
 * line.value һ��������ʵ��ֵ
 * line.show һ��������ʾ���ַ���*/
public class Line
{
	/**һ�����ݵ�����*/
	public String des;
	/**һ��������ʵ��ֵ*/
	public byte[] value;
	/**һ��������ʾ���ַ���*/
	public String show;
	//==============������================
	/**Ĭ�Ϲ���*/
	public Line()
	{
		this.des = "";
		this.value = null;
		this.show = "";
	}
	/**��ȫ����*/
	public Line(String des,byte[] value,String show)
	{
		this.des = des;
		this.value = value;
		this.show = show;
	}
	/**���ֹ���*/
	public Line(String des,byte[] value)
	{
		this.des = des;
		this.value = value;
	}
	/**���ֹ��졣������faultCode*/
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
