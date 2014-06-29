package com.dataserver.dataparse;

import android.annotation.SuppressLint;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**换算类，用来将消息中包含的数转换成需要显示的String
 * 包括表达式和MAP换算两种形式，因为表达式换算的数据可能是long，统一传入long换算*/
public class Conversion
{
	private String expression;
	private Stack<BigDecimal> numbers;
	private Stack<Character> chs;
	private Map<Long,String> conversionMap;
	private boolean flag;//ture:表达式型;false:map型
	
	/**构造*/
	public Conversion(String expression)
	{
		this.expression = expression;
		//判断是哪种换算，暂时这样处理
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
	/**将值换算成相应的String*/
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
	/**初始化conversionMap*/
	public void initMap()
	{
		String[] strMap = this.expression.split(InfoConfig.CONVERSION_SEPARTOR);
		this.conversionMap = new HashMap<Long, String>(strMap.length);
		for(int i =0;i<strMap.length;i+=2)
		{
			if(strMap[i].contains(InfoConfig.CONVERSION_RANGESEPARTOR))
			{
				//暂时
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
	/**表达式：将有括号的字符串换成没有括号的字符串运算*/
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
