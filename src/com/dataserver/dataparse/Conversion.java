package com.dataserver.dataparse;

import android.annotation.SuppressLint;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**�����࣬��������Ϣ�а�������ת������Ҫ��ʾ��String
 * �������ʽ��MAP����������ʽ����Ϊ���ʽ��������ݿ�����long��ͳһ����long����*/
public class Conversion
{
	private String expression;
	private Stack<BigDecimal> numbers;
	private Stack<Character> chs;
	private Map<Long,String> conversionMap;
	private boolean flag;//ture:���ʽ��;false:map��
	
	/**����*/
	public Conversion(String expression)
	{
		this.expression = expression;
		//�ж������ֻ��㣬��ʱ��������
		if(expression.contains(":"))
		{
			this.flag = false;
		}
		else
		{
			this.flag = true;
			this.numbers = new Stack<BigDecimal>();
			this.chs = new Stack<Character>();
		}
	}
	/**��ֵ�������Ӧ��String*/
	public String conversion(long value)
	{
		if(this.flag)
		{
			String realExpression = this.expression.replaceAll(InfoConfig.CONVERSION_VAULE, ""+value);
			return ""+this.parse(realExpression);
		}
		else
		{
			this.initMap();
			return this.conversionMap.get(value);
		}
	}
	/**��ʼ��conversionMap*/
	public void initMap()
	{
		String[] strMap = this.expression.split(InfoConfig.CONVERSION_SEPARTOR);
		this.conversionMap = new HashMap<Long, String>(strMap.length);
		for(int i =0;i<strMap.length;i+=2)
		{
			if(strMap[i].contains(InfoConfig.CONVERSION_RANGESEPARTOR))
			{
				//��ʱ
				String[] strMapI = strMap[i].split(InfoConfig.CONVERSION_RANGESEPARTOR);
				long rangKeyStart = Long.parseLong(strMapI[0],16);
				long rangKeyEnd = Long.parseLong(strMapI[1],16);
				for(long ii=rangKeyStart;ii<=rangKeyEnd;ii++)
				{
					this.conversionMap.put(ii,strMap[i+1]);
				}
			}
			else
			{
				this.conversionMap.put(Long.parseLong(strMap[i],16),strMap[i+1]);
			}
		}
	}
	/**���ʽ���������ŵ��ַ�������û�����ŵ��ַ�������*/
	public BigDecimal parse(String st)
	{
		int start = 0;
		StringBuffer sts = new StringBuffer(st);
		int end = -1;
		while ((end = sts.indexOf(")")) > 0)
		{
			String s = sts.substring(start, end + 1);
			int first = s.lastIndexOf("(");
			BigDecimal value = caculate(sts.substring(first + 1, end));
			sts.replace(first, end + 1, value.toString());
		}
		return caculate(sts.toString());
	}
	@SuppressLint("UseValueOf")
	public BigDecimal caculate(String st)
	{
		StringBuffer sb = new StringBuffer(st);
		StringBuffer num = new StringBuffer();
		String tem = null;
		char next;
		while (sb.length() > 0)
		{
			tem = sb.substring(0, 1);
			sb.delete(0, 1);
			if (isNum(tem.trim()))
			{
				num.append(tem);
			}
			else
			{
				if (num.length() > 0 && !"".equals(num.toString().trim()))
				{
					BigDecimal bd = new BigDecimal(num.toString().trim());
					numbers.push(bd);
					num.delete(0, num.length());
				}
				if (!chs.isEmpty())
				{
					while (!compare(tem.charAt(0)))
					{
						caculate();
					}
				}
				if (numbers.isEmpty())
				{
					num.append(tem);
				}
				else
				{
					chs.push(new Character(tem.charAt(0)));
				}
				next = sb.charAt(0);
				if (next == '-')
				{
					num.append(next);
					sb.delete(0, 1);
				}
			}
		}
		BigDecimal bd = new BigDecimal(num.toString().trim());
		numbers.push(bd);
		while (!chs.isEmpty())
		{
			caculate();
		}
		return numbers.pop();
	}
	private boolean compare(char str)
	{
		if (chs.empty())
		{
			return true;
		}
		char last = (char) chs.lastElement();
		switch (str)
		{
		case '*':
		{
			if (last == '+' || last == '-')
				return true;
			else
				return false;
		}
		case '/':
		{
			if (last == '+' || last == '-')
				return true;
			else
				return false;
		}
		case '+':
			return false;
		case '-':
			return false;
		}
		return true;
	}
	private void caculate()
	{
		BigDecimal b = numbers.pop();
		BigDecimal a = null;
		a = numbers.pop();
		char ope = chs.pop();
		BigDecimal result = null;
		switch (ope)
		{
		case '+':
			result = a.add(b);
			numbers.push(result);
			break;
		case '-':
			result = a.subtract(b);
			numbers.push(result);
			break;
		case '*':
			result = a.multiply(b);
			numbers.push(result);
			break;
		case '/':
			result = a.divide(b);
			numbers.push(result);
			break;
		}
	}
	private boolean isNum(String num)
	{
		return num.matches("[0-9]");
	}
}
