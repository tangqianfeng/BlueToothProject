package com.dataserver.dataparse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.dataserver.DataMath;
import com.dataserver.Line;
import com.dataserver.Msg;

public class Info
{
	
	
	public static Msg parse(Document doc,byte[] src) throws Exception
	{
		Msg msg;
		List<Line> lines;
		List<List<Rule>> lineRules;
		//===��ʼ����������===
		msg = new Msg(Msg.INFO);
		lines = new ArrayList<Line>();
		lineRules = new ArrayList<List<Rule>>();
		
		//�õ�DIDNumber��String��ʾ
		String dIDNO = InfoConfig.HEAD + DataMath.getHexString(
				DataMath.subBytes(src, InfoConfig.DIDNUMBER_START, 
						InfoConfig.DIDNUMBER_END)).toUpperCase();
		//�õ�������Ҫ������byte[]
		byte[] realSrc = DataMath.subBytes(src, InfoConfig.DATA_START, src.length-1);
		Element root = doc.getRootElement();
		Element infoEle = root.element(dIDNO);
		//�õ�DID����
		String dIDDes = infoEle.attributeValue(InfoConfig.DIDDESCRIPTION);
		msg.setTitle(dIDDes);
		String bytesLenStr = infoEle.attributeValue(InfoConfig.BYTESLENGTH);
		int bytesLen = Integer.parseInt(bytesLenStr);
		//�õ�����������ĵ�����
		Iterator<?> lineIter = infoEle.elementIterator();
		while(lineIter.hasNext())
		{
			//�õ���ǰ��line
			Element lineEle = (Element) lineIter.next();
			String subName = lineEle.attributeValue(InfoConfig.SUBDATANAME);
			Iterator<?> ruleIter = lineEle.elementIterator();
			List<Rule> rules = new ArrayList<Rule>();
			while(ruleIter.hasNext())
			{
				//һ��rule����ȡ
				Element ruleEle = (Element) ruleIter.next();
				int bs;
				int be;
				int is;
				int ie;
				String cs = null;
				String un = null;
				String tp = null;
				String byteRange = ruleEle.attributeValue(InfoConfig.BYTERANGE);
				if(InfoConfig.RANGE_ALL.equalsIgnoreCase(byteRange))
				{
					bs = 0;
					be = bytesLen -1;
				}
				else
				{
					String[] tmp = byteRange.split(InfoConfig.RANGE_SEPARTOR);
					bs = Integer.parseInt(tmp[0]);
					be = Integer.parseInt(tmp[1]);
				}
				String bitRange = ruleEle.attributeValue(InfoConfig.BITRANGE);
				if(InfoConfig.RANGE_ALL.equalsIgnoreCase(bitRange))
				{
					is = 0;
					ie = (be+1-bs) * 8 -1;
				}
				else
				{
					String[] tmp = bitRange.split(InfoConfig.RANGE_SEPARTOR);
					is = Integer.parseInt(tmp[0]);
					ie = Integer.parseInt(tmp[1]);
				}
				cs = ruleEle.attributeValue(InfoConfig.CONVERSION);
				un = ruleEle.attributeValue(InfoConfig.UNIT);
				tp = ruleEle.attributeValue(InfoConfig.DATATYPE);
				Rule rule = new Rule();
				rule.bs = bs;
				rule.be = be;
				rule.is = is;
				rule.ie = ie;
				rule.cs = cs;
				rule.un = un;
				rule.tp = tp;
				rules.add(rule);
			}//end һ��line��rule�ı���
			lineRules.add(rules);
			Line line = new Line();
			line.setDes(subName);
			lines.add(line);
		}//end ����line�ı���
		//===��ʼ������===��������һ��List<Line> lines and List<List<Rule>> lineRules
		//===����lineRulesȥ����realSrc��������ŵ�lines��===
		for(int i=0;i<lines.size();i++)
		{
			Line cLine = lines.get(i);
			List<Rule> cRules = lineRules.get(i);
			byte[] value = null;
			String show = "";
			for(Rule cRule:cRules)
			{
				//���xml��Ĺ���Χ������ʵ�ʷ�Χ�������ˡ���֤����ȥ��
				if(cRule.be>=realSrc.length || cRule.bs>=realSrc.length)
					break;
				
				value= DataMath.subBytes(realSrc, cRule.bs, cRule.be);
				value = DataMath.getBitsData(value, cRule.is, cRule.ie);
				
				long valueLong = 0;
				if(InfoConfig.ASCII.equalsIgnoreCase(cRule.tp))
				{
					show +=DataMath.asciiToString(value);
				}
				else //boolean and unsigned
				{
					valueLong = DataMath.bytesToLong(value);
					if( "".equals(cRule.cs)||cRule.cs==null )
					{
						show += valueLong;
					}
					else if(cRule.cs.matches(InfoConfig.CONVERSION_REGEX) 
							|| cRule.cs.contains("value"))
					{
						show += new Conversion(cRule.cs).conversion(valueLong);
					}
					else
					{
						show += valueLong+"(XML����δ����)";
					}
				}
				if(! ("".equals(cRule.un)||cRule.un==null))
				{
					show += cRule.un;
				}
			}
			cLine.setValue(value);
			cLine.setShow(show);
		}
		msg.setLines(lines);
		return msg;
	}
}

//##############################################################
/**�洢��������ݽṹ*/
class Rule
{
	/**�ֽڿ�ʼλ��*/
	public int bs;
	/**�ֽڽ���λ��*/
	public int be;
	/**λ��ʼλ��*/
	public int is;
	/**λ����λ��*/
	public int ie;
	/**�������*/
	public String cs = null;
	/**��λ*/
	public String un = null;
	/**��������*/
	public String tp = null;
}