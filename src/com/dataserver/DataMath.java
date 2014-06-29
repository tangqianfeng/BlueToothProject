package com.dataserver;

/**dataserver:�������ݴ���ת������ѧ������*/
public class DataMath
{
	/**�õ�byte������ָ��λ�õ�byte����
	 * @param src ԭʼ����
	 * @param start ��ȡ��ʼ����������λ��
	 * @param end ��ȡ��������������λ��*/
	public static byte[] subBytes(byte[] src,int start,int end)
	{
		byte[] result = new byte[end-start+1];
		for(int i=0;i<result.length;i++)
		{
			result[i] = src[start+i];
		}
		return result;
	}
	/**�õ�byte������ָ��λ�õ�byte*/
	public static byte getByte(byte[] src,int index)
	{
		byte result = src[index];
		return result;
	}
	/**����ת��Ϊ����ʾʹ�õ�String*/
	public static String numbersToString(int[] src)
	{
		StringBuffer result = new StringBuffer();
		for(int i=0;i<src.length;i++)
		{
			result.append(src[i]);
		}
		return result.toString();
	}
	/**��byte�е�����ת��Ϊ����������Unsigned����4λ�����������ô�ת����
	 * booleanҲ���ԣ�����������ʲô��ֻҪ����ASCII���ǵ����������*/
	public static int[] getNumbersData(byte[] src)
	{
		int[] result = new int[src.length];
		for(int i=0;i<result.length;i++)
		{
			result[i]=(((int)src[i])&0xFF);
		}
		return result;
	}
	/**byte��������ʾ��һ����ת����int,byte����ĳ��Ȳ��ܳ���4���ֽڣ���������*/
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
	/**������ASCII���byte����ת��Ϊ����ʾ��String*/
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
	/**�õ�byte������ָ����λ��λ������Ϣ�������������Ѿ��ָ�õ�byte����
	 * ��ʱֻ�ܽ�ȡ����ͬһ�ֽ��ڵ�λ���պð����������ֽڵ�λ��������������鷳����Ҳû�б�Ҫ����*/
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
	/**�õ��ֽ���ָ��λ�õ�һλ���ݣ���һ��byte�ĵ�7λ�����һλ����*/ 
	public static byte getBitData(byte src,int index)
	{
		int tmp = src<<7-index;
		byte  result = (byte) ((tmp&0xFF)>>>7);
		return result;
	}
	/**�õ�byte��������ʾ��ʮ�������ַ���*/
	public static String getHexString(byte[] src)
	{
		StringBuffer result= new StringBuffer();
		for(int i =0;i<src.length;i++)
		{
			result.append( Integer.toHexString(src[i]|0xFFFFFF00).substring(6) );
		}
		return result.toString();
	}
	/**�õ�byte����ʾ��ʮ�������ַ���*/
	public static String getHexString(byte src)
	{
		String result = Integer.toHexString(src|0xFFFFFF00).substring(6);
		return result;
	}
	/**�õ�byte��������ʾ��long*/
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
