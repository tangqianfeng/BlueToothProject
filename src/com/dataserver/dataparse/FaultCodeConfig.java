package com.dataserver.dataparse;

public final class FaultCodeConfig
{
		public static final String FAULTCODE_DISPLAY = "显示码";
		public static final String FAULTCODE_DTCMEANING = "DTC含义";
		public static final String FAULTCODE_DESCRIPTION = "描述";
		public static final String FAULTCODE_POSSIBLEFAULTCAUSES = "可能故障原因";
		public static final String FAULTCODE_CORRECTIVEACTION = "维修建议";
		public static final String FAULTCODE_HEAD = "code";
		public static final String FAULTCODE_ROOTNAME = "故障码";
		/**一条故障信息的长度（字节）*/
		public static final int FAULTCODE_LENGTH = 4;
		/**故障码DTCnumber在数据byte数组中的开始位置*/
		public static final int FAULTCODE_DTCNUMBER_START = 0;
		/**故障码DTCnumber结束位置*/
		public static final int FAULTCODE_DTCNUMBER_END = 1;
		/**故障码故障类型位置*/
		public static final int FAULTCODE_FAULTTYPE_INDEX = 2;
		//注：故障码的3x位置是无用的字节
}
