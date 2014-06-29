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
		//有可能因为一些原因收到的数据不符合正确的格式，fL是4的整数倍的长度，尽量地去读取数据
		int fL = src.length/FaultCodeConfig.FAULTCODE_LENGTH 
				* FaultCodeConfig.FAULTCODE_LENGTH;
		//故障的数量
		int faultsCounts = fL/FaultCodeConfig.FAULTCODE_LENGTH;
		if(src.length%FaultCodeConfig.FAULTCODE_LENGTH != 0)
		{
			//如果接收到的长度不正确的话就尽可能地读
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
			
			/*将字节所表示的十六进制数转换为相应的String，并且加上头“code”，比如codeFFFF，以方便得到需要的元素，
			 * 需要注意的是这里，这里会转换成大写，因此在故障码的xml文件中十六进制数必须为大写*/
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
			//如果找不到DTCNumber 或者 如果找不到typeEle
			if( (dTCEle = root.element(dTCNumber)) == null || 
					(typeEle = dTCEle.element(faultType)) == null)
			{
				display = "Unknown";
				dTCMeaning = "未定义故障";
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
