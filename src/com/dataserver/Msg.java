package com.dataserver;

import java.util.ArrayList;
import java.util.List;

/**���ݵ���Ϣ��*/
public class Msg
{
	/**�������͵���Ϣ*/
	public static final int CTRL = 0x01;
	/**info���͵���Ϣ*/
	public static final int INFO = 0x02;
	/**faultcode���͵���Ϣ*/
	public static final int FCODE = 0x04;
	/**��ʾ���͵���Ϣ*/
	//public static final int WARN = 0x08;
	/**other*/
	public static final int OTHER = 0x10;
	
	private String head;
	private String title;
	private int type;
	private List<Line> lines; 

	//=======������========
	public Msg(int type)
	{
		this.type = type;
		this.lines = new ArrayList<Line>();
	}
	public Msg(int type,String title)
	{
		this(type);
		this.title = title;
	}
	public Msg(String head,int type,String title,List<Line> lines)
	{
		this.head = head;
		this.type = type;
		this.title = title;
		this.lines = lines;
	}
	//=======setter getter===========
	public void setHead(String head)
	{
		this.head = head;
	}
	public String getHead()
	{
		return this.head;
	}
	public void setType(int type)
	{
		this.type = type;
	}
	public int getType()
	{
		return this.type;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getTile()
	{
		return this.title;
	}
	//======lines��������õ�============
	public void setLines(List<Line> lines)
	{
		this.lines = lines;
	}
	/**���һ��line*/
	public void add(Line line)
	{
		this.lines.add(line);
	}
	public List<Line> getLines()
	{
		return this.lines;
	}
}
