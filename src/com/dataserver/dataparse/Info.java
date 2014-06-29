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
		//===初始化解析规则===
		msg = new Msg(Msg.INFO);
		lines = new ArrayList<Line>();
		lineRules = new ArrayList<List<Rule>>();
		
		//得到DIDNumber的String表示
		String dIDNO = InfoConfig.HEAD + DataMath.getHexString(
				DataMath.subBytes(src, InfoConfig.DIDNUMBER_START, 
						InfoConfig.DIDNUMBER_END)).toUpperCase();
		//得到最终需要解析的byte[]
		byte[] realSrc = DataMath.subBytes(src, InfoConfig.DATA_START, src.length-1);
		Element root = doc.getRootElement();
		Element infoEle = root.element(dIDNO);
		//得到DID描述
		String dIDDes = infoEle.attributeValue(InfoConfig.DIDDESCRIPTION);
		msg.setTitle(dIDDes);
		String bytesLenStr = infoEle.attributeValue(InfoConfig.BYTESLENGTH);
		int bytesLen = Integer.parseInt(bytesLenStr);
		//得到所有数据项的迭代器
		Iterator<?> lineIter = infoEle.elementIterator();
		while(lineIter.hasNext())
		{
			//得到当前的line
			Element lineEle = (Element) lineIter.next();
			String subName = lineEle.attributeValue(InfoConfig.SUBDATANAME);
			Iterator<?> ruleIter = lineEle.elementIterator();
			List<Rule> rules = new ArrayList<Rule>();
			while(ruleIter.hasNext())
			{
				//一条rule的提取
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
			}//end 一个line中rule的遍历
			lineRules.add(rules);
			Line line = new Line();
			line.setDes(subName);
			lines.add(line);
		}//end 所有line的遍历
		//===初始化结束===现在有了一个List<Line> lines and List<List<Rule>> lineRules
		//===根据lineRules去解析realSrc并将结果放到lines中===
		for(int i=0;i<lines.size();i++)
		{
			Line cLine = lines.get(i);
			List<Rule> cRules = lineRules.get(i);
			byte[] value = null;
			String show = "";
			for(Rule cRule:cRules)
			{
				//如果xml里的规则范围超过了实际范围，就算了。保证尽量去读
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
						show += valueLong+"(XML错误，未换算)";
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
/**存储规则的数据结构*/
class Rule
{
	/**字节开始位置*/
	public int bs;
	/**字节结束位置*/
	public int be;
	/**位开始位置*/
	public int is;
	/**位结束位置*/
	public int ie;
	/**换算规则*/
	public String cs = null;
	/**单位*/
	public String un = null;
	/**解析类型*/
	public String tp = null;
}