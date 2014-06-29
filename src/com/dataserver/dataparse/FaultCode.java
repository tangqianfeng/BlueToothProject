package com.dataserver.dataparse;

import org.dom4j.Document;
import org.dom4j.Element;


import com.dataserver.*;

public class FaultCode
{
	
	public static  Msg parse(Document doc,byte[] src) throws Exception
	{
		Msg msg;
		byte[] faults;
		//�п�����ΪһЩԭ���յ������ݲ�������ȷ�ĸ�ʽ��fL��4���������ĳ��ȣ�������ȥ��ȡ����
		int fL = src.length/FaultCodeConfig.FAULTCODE_LENGTH 
				* FaultCodeConfig.FAULTCODE_LENGTH;
		//���ϵ�����
		int faultsCounts = fL/FaultCodeConfig.FAULTCODE_LENGTH;
		if(src.length%FaultCodeConfig.FAULTCODE_LENGTH != 0)
		{
			//������յ��ĳ��Ȳ���ȷ�Ļ��;����ܵض�
			faults = DataMath.subBytes(src, 0, fL-1);
		}
		else
		{
			faults = src;
		}
		
		msg = new Msg(Msg.FCODE);
		
		Element root = doc.getRootElement();
		for(int i=0;i<faultsCounts;i++)
		{	
			byte[] srcTmp = DataMath.subBytes(faults,
					(i)*FaultCodeConfig.FAULTCODE_LENGTH,(i+1)*FaultCodeConfig.FAULTCODE_LENGTH-1);
			byte[] dTCNumberTmp = DataMath.subBytes(srcTmp, 
					FaultCodeConfig.FAULTCODE_DTCNUMBER_START, FaultCodeConfig.FAULTCODE_DTCNUMBER_END);
			byte faultTypeTmp = DataMath.getByte(srcTmp, FaultCodeConfig.FAULTCODE_FAULTTYPE_INDEX);
			
			/*���ֽ�����ʾ��ʮ��������ת��Ϊ��Ӧ��String�����Ҽ���ͷ��code��������codeFFFF���Է���õ���Ҫ��Ԫ�أ�
			 * ��Ҫע�������������ת���ɴ�д������ڹ������xml�ļ���ʮ������������Ϊ��д*/
			String dTCNumber = FaultCodeConfig.FAULTCODE_HEAD + DataMath.getHexString(dTCNumberTmp).toUpperCase();
			String faultType = FaultCodeConfig.FAULTCODE_HEAD + 
					DataMath.getHexString(faultTypeTmp).toUpperCase();
			Element dTCEle = null ;
			Element typeEle = null;
			String display;
			String dTCMeaning;
			String description;
			String possibleFaultCauses;
			String correctiveAction;
			//����Ҳ���DTCNumber ���� ����Ҳ���typeEle
			if( (dTCEle = root.element(dTCNumber)) == null || 
					(typeEle = dTCEle.element(faultType)) == null)
			{
				display = "Unknown";
				dTCMeaning = "δ�������";
				description = "";
				possibleFaultCauses = "";
				correctiveAction = "";
			}
			else
			{				
				display = dTCEle.attributeValue(FaultCodeConfig.FAULTCODE_DISPLAY);
				dTCMeaning = dTCEle.attributeValue(FaultCodeConfig.FAULTCODE_DTCMEANING);
				description = typeEle.attributeValue(FaultCodeConfig.FAULTCODE_DESCRIPTION);
				possibleFaultCauses = typeEle.attributeValue(FaultCodeConfig.FAULTCODE_POSSIBLEFAULTCAUSES);
				correctiveAction = typeEle.attributeValue(FaultCodeConfig.FAULTCODE_CORRECTIVEACTION);
			}
			
			Line lineA = new Line(FaultCodeConfig.FAULTCODE_DISPLAY, display);
			Line lineB = new Line(FaultCodeConfig.FAULTCODE_DTCMEANING, dTCMeaning);
			Line lineC = new Line(FaultCodeConfig.FAULTCODE_DESCRIPTION, description);
			Line lineD = new Line(FaultCodeConfig.FAULTCODE_POSSIBLEFAULTCAUSES, possibleFaultCauses);
			Line lineE = new Line(FaultCodeConfig.FAULTCODE_CORRECTIVEACTION, correctiveAction);
			
			msg.add(lineA);
			msg.add(lineB);
			msg.add(lineC);
			msg.add(lineD);
			msg.add(lineE);
		}
		return msg;
	}

}
