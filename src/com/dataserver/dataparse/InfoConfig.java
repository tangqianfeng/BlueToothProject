package com.dataserver.dataparse;

public final class InfoConfig
{
	public static final String DIDDESCRIPTION = "DID����";
	public static final String BYTESLENGTH = "�ֽ���";
	public static final String BYTERANGE = "�ֽڷ�Χ";
	public static final String BITRANGE = "λ��Χ";
	public static final String SUBDATANAME = "����������";
	public static final String UNIT = "��λ";
	public static final String CONVERSION = "����";
	public static final String DATATYPE =  "��������";
	public static final String RANGE_ALL = "all";
	/**�����е���Ҫ�滻��value������*/
	public static final String CONVERSION_VAULE = "[Vv][Aa][Ll][Uu][Ee]";
	/**map�͵Ļ���ķָ���������*/
	public static final String CONVERSION_SEPARTOR = ":|;";
	/**map���еķ�Χ�ָ��*/
	public static final String CONVERSION_RANGESEPARTOR = "-";
	/**�������֤��������ʽ*/
	public static final String CONVERSION_REGEX = 
			"^([0-9A-Fa-f]+(-[0-9A-Fa-f]+)?:[^:]+)(;+[0-9A-Fa-f]+(-[0-9A-Fa-f]+)?:[^:]+)*$";
	public static final String RANGE_SEPARTOR = ",";
	public static final String HEAD = "code";
	//��������
	public static final String UNSIGNED = "Unsigned";
	public static final String BOOL = "boolean";
	public static final String ASCII = "ASCII";
	/**һ����Ϣ����ȥ������DIDNUMBER��byte�����еĿ�ʼλ��*/
	public static final int DIDNUMBER_START = 0;
	/**һ����Ϣ����ȥ������DIDMUBER��byte�����н�����λ��*/
	public static final int DIDNUMBER_END = 1;
	/**һ����Ϣ����ȥ������������byte�����еĿ�ʼλ��*/
	public static final int DATA_START = 2;
}
