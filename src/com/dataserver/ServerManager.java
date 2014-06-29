package com.dataserver;

import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dataserver.dataparse.FaultCode;
import com.dataserver.dataparse.Info;

import android.content.Context;

public class ServerManager
{
	public static final String ERROR_UNDEFINE = "未定义异常";
	public static final String ERROR_DATA = "数据异常";
	public static final String ERROR_XML = "配置文件打开异常";
	public static final String ERROR_IO = "I/O异常";
	public static final String INDEX_URL = "lay.xml";
	
	public static Msg dealWith(String head,byte[] src,Context context)
	{
		ServerTmp tmp = ServerParse.parseServer(src);
		Msg result = new Msg(Msg.CTRL);
		try
		{
			if(tmp.getType() == ServerTmp.STOP)
			{
				result = tmp.getMsg();
			}
			else
			{
				//index 的document
				Document indexDoc = getDocument(context, INDEX_URL);
				Element element = null;
				String xmlUrl = null;
				String headTmp = head;
				do
				{
					Object ob = indexDoc.selectObject(headTmp);                      
					element = (Element) ob;
					//xmlType = element.attributeValue("type");
					xmlUrl = element.attributeValue("url");
					int index = headTmp.lastIndexOf('/');
					headTmp = headTmp.substring(0, index);
				}
				while("".equals(xmlUrl) || xmlUrl==null);
				byte[] noSvSrc = tmp.getSrc();
				//根据index和head得到解析数据的doc
				Document doc = getDocument(context, xmlUrl);
				//构造解析类
				if(tmp.getType() == ServerTmp.GOON+ServerTmp.FCODE)
				{

					result = FaultCode.parse(doc, noSvSrc);
				}
				else if(tmp.getType() == ServerTmp.GOON+ServerTmp.INFO)
				{
					result = Info.parse(doc, noSvSrc);
				}
			}
		} catch (DocumentException e)
		{
			result.setType(Msg.CTRL);
			result.setTitle(ERROR_XML);
		} catch (IOException e)
		{
			result.setType(Msg.CTRL);
			result.setTitle(ERROR_IO);
		} catch (Exception e)
		{
			result.setType(Msg.CTRL);
			result.setTitle(ERROR_UNDEFINE);
		}
		finally
		{
			if(result !=null)
				result.setHead(head);
		}
		return result;
	}
	
	/**得到目标路径对应的document，后期实现时应先解密
	 * @throws IOException 
	 * @throws DocumentException */
	public static Document getDocument(Context context,String url) 
			throws DocumentException, IOException
	{
		SAXReader reader = new SAXReader();
		Document doc = null;
		doc = reader.read(context.getAssets().open(url));
		return doc;
	}
}
