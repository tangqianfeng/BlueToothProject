package com.dataserver;

/**dataserver:进行数据处理及转换的数学方法类*/
public class DataMath
{
	/**得到byte数组中指定位置的byte数组
	 * @param src 原始数组
	 * @param start 截取开始（包含）的位置
	 * @param end 截取结束（包含）的位置*/
	public static byte[] subBytes(byte[] src,int start,int end)
	{
		byte[] result = new byte[end-start+1];
		for(int i=0;i<result.length;i++)
		{
			result[i] = src[start+i];
		}
		return result;
	}
	/**得到byte数组中指定位置的byte*/
	public static byte getByte(byte[] src,int index)
	{
		byte result = src[index];
		return result;
	}
	/**将数转换为供显示使用的String*/
	public static String numbersToString(int[] src)
	{
		StringBuffer result = new StringBuffer();
		for(int i=0;i<src.length;i++)
		{
			result.append(src[i]);
		}
		return result.toString();
	}
	/**将byte中的内容转换为数，不管是Unsigned还是4位的数都可以用此转换，
	 * boolean也可以，反正不管是什么，只要不是ASCII都是当作数处理的*/
	public static int[] getNumbersData(byte[] src)
	{
		int[] result = new int[src.length];
		for(int i=0;i<result.length;i++)
		{
			result[i]=(((int)src[i])&0xFF);
		}
		return result;
	}
	/**byte数组所表示的一个数转换成int,byte数组的长度不能超过4个字节，否则会溢出*/
	public static int bytesToInt(byte[] src)
	{
		int result = 0;
		for(int i=0;i<src.length;i++)
		{
			result = result<<8;
			result = result|((int)src[i]&0xFF);
		}
		return result;
	}
	/**将代表ASCII码的byte数组转换为供显示的String*/
	public static String asciiToString(byte[] src)
	{
		StringBuffer result=new StringBuffer();
		for(int i=0;i<src.length;i++)
		{ 
			char tmp = (char) src[i];
			result.append(tmp);
		}
		return result.toString();
	}
	//=========?
	/**得到byte数组中指定数位的位数据信息，它的输入是已经分割好的byte数组
	 * 暂时只能截取处于同一字节内的位及刚好包含了整个字节的位，其他的情况很麻烦而且也没有必要处理*/
	public static byte[] getBitsData(byte[] src,int start,int end)
	{
		int bitsLength = end-start+1;
		int arrayLength=bitsLength/8;
		if(bitsLength%8!=0)
		{
			arrayLength++;
		}
		byte[] result = new byte[arrayLength];
		if(start%8==0 && end%8==7)
		{
			for(int i=0;i<arrayLength;i++)
			{
				result[i] = src[start/8+i];
			}
		}else if(start/8 == end/8)
		{
			for(int i=0;i<arrayLength;i++)
			{
				int tmp = src[start/8]<<7-end;
				result[i] = (byte) ((tmp&0xFF)>>>(7+start-end));
			}
		}
		return result;
	}
	//=============?
	/**得到字节中指定位置的一位数据，用一个byte的第7位存放这一位数据*/ 
	public static byte getBitData(byte src,int index)
	{
		int tmp = src<<7-index;
		byte  result = (byte) ((tmp&0xFF)>>>7);
		return result;
	}
	/**得到byte数组所表示的十六进制字符串*/
	public static String getHexString(byte[] src)
	{
		StringBuffer result= new StringBuffer();
		for(int i =0;i<src.length;i++)
		{
			result.append( Integer.toHexString(src[i]|0xFFFFFF00).substring(6) );
		}
		return result.toString();
	}
	/**得到byte所表示的十六进制字符串*/
	public static String getHexString(byte src)
	{
		String result = Integer.toHexString(src|0xFFFFFF00).substring(6);
		return result;
	}
	/**得到byte数组所表示的long*/
	public static long bytesToLong(byte[] src)
	{
		long result = 0;
		for(int i=0;i<src.length;i++)
		{
			result = result<<8;
			result = result|((long)src[i]&0xFF);
		}
		return result;
	}
}
