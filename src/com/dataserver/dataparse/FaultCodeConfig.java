package com.dataserver.dataparse;

public final class FaultCodeConfig
{
		public static final String FAULTCODE_DISPLAY = "��ʾ��";
		public static final String FAULTCODE_DTCMEANING = "DTC����";
		public static final String FAULTCODE_DESCRIPTION = "����";
		public static final String FAULTCODE_POSSIBLEFAULTCAUSES = "���ܹ���ԭ��";
		public static final String FAULTCODE_CORRECTIVEACTION = "ά�޽���";
		public static final String FAULTCODE_HEAD = "code";
		public static final String FAULTCODE_ROOTNAME = "������";
		/**һ��������Ϣ�ĳ��ȣ��ֽڣ�*/
		public static final int FAULTCODE_LENGTH = 4;
		/**������DTCnumber������byte�����еĿ�ʼλ��*/
		public static final int FAULTCODE_DTCNUMBER_START = 0;
		/**������DTCnumber����λ��*/
		public static final int FAULTCODE_DTCNUMBER_END = 1;
		/**�������������λ��*/
		public static final int FAULTCODE_FAULTTYPE_INDEX = 2;
		//ע���������3xλ�������õ��ֽ�
}
