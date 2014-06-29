package com.dataserver.dataparse;

public final class InfoConfig
{
	public static final String DIDDESCRIPTION = "DID描述";
	public static final String BYTESLENGTH = "字节数";
	public static final String BYTERANGE = "字节范围";
	public static final String BITRANGE = "位范围";
	public static final String SUBDATANAME = "子数据名称";
	public static final String UNIT = "单位";
	public static final String CONVERSION = "换算";
	public static final String DATATYPE =  "数据类型";
	public static final String RANGE_ALL = "all";
	/**换算中的需要替换的value（正则）*/
	public static final String CONVERSION_VAULE = "[Vv][Aa][Ll][Uu][Ee]";
	/**map型的换算的分割符｛正则｝*/
	public static final String CONVERSION_SEPARTOR = ":|;";
	/**map型中的范围分割符*/
	public static final String CONVERSION_RANGESEPARTOR = "-";
	/**换算的验证的正则表达式*/
	public static final String CONVERSION_REGEX = 
			"^([0-9A-Fa-f]+(-[0-9A-Fa-f]+)?:[^:]+)(;+[0-9A-Fa-f]+(-[0-9A-Fa-f]+)?:[^:]+)*$";
	public static final String RANGE_SEPARTOR = ",";
	public static final String HEAD = "code";
	//数据类型
	public static final String UNSIGNED = "Unsigned";
	public static final String BOOL = "boolean";
	public static final String ASCII = "ASCII";
	/**一条消息（已去服务）中DIDNUMBER在byte数组中的开始位置*/
	public static final int DIDNUMBER_START = 0;
	/**一条消息（已去服务）中DIDMUBER在byte数组中结束的位置*/
	public static final int DIDNUMBER_END = 1;
	/**一条消息（已去服务）中数据在byte数组中的开始位置*/
	public static final int DATA_START = 2;
}
