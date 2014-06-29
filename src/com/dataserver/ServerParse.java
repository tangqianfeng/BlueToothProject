package com.dataserver;

import android.util.Log;


/**解析服务的类*/
public class ServerParse
{
	/**返回的服务头加上的数值*/
	public static final int SERVER_ADD = 0x40;
	
	/**解析服务*/
	public static ServerTmp parseServer(byte[] src)
	{
		int srcLength = src.length;
		//构造消息需要用到的一些参数,默认是控制、停止解析
		byte[] data = null;
		String text =  "服务解析错误:找不到该服务!";
		int type = ServerTmp.STOP;
		int msgType = Msg.CTRL;
		ServerTmp tmpData = new ServerTmp();
		//---------------------------
		try
		{
			byte serverHead = (byte) (src[0]-SERVER_ADD);
			switch(serverHead)
			{
			//故障码
			case 0x19:
				if(src[1]==0x02 && src[2]==0x2C)
				{
					if(srcLength < 4)
					{
						text = "没有故障";
						//type = ServerTmp.STOP;
						msgType = Msg.CTRL;
					}
					else
					{
						data = DataMath.subBytes(src, 3, srcLength-1);
						type = ServerTmp.GOON+ServerTmp.FCODE;
					}
				}break;
			//info,ps.2E 2F需要确认一下
			case 0x22:
				data = DataMath.subBytes(src, 1, srcLength-1);
				type = ServerTmp.GOON+ServerTmp.INFO;
				break;
			case 0x2E:
				data = DataMath.subBytes(src, 1, srcLength-1);
				type = ServerTmp.GOON+ServerTmp.INFO;
				break;
			case 0x2F:
				if(src[1] == 0x03)
				{
					data = DataMath.subBytes(src, 1, srcLength-1);
					type = ServerTmp.GOON+ServerTmp.INFO;
					break;
				}
			//控制或者提示：
			case 0x10:
				if(src[1] == 0x03)
				{
					text = "防盗控制器校对成功";
					Log.d("sp_65", text);
				}break;
			case 0x27:
				if(src[1] == 0x11)
				{
					text = "安全操作校对成功";
				}
				else if(src[1] == 0x12)
				{
					text = "安全操作密码正确";
				}break;
			case 0x31:
				if(src[1]==0x01 && src[2]==0x30 && src[3]==0x02)
				{
					text ="正在学习钥匙，请等待……";
					break;
				}
			//否定响应
			case 0x3F:
				byte nCode = src[2];
				switch(nCode)
				{
				case 0x10:
					text = "控制器不能诊断（需要打开车门或将点火钥匙打到ON档）";break;
				case 0x11:
					text = "服务不支持";break;
				case 0x12:
					text = "服务子功能不支持";break;
				case 0x13:
					text = "服务长度错误";break;
				case 0x22:
					text = "控制器不能诊断（需要打开车门或将点火钥匙打到ON档";break;
				case 0x24:
					text = "服务顺序请求错误";break;
				case 0x31:
					text = "请求数据超出范围";break;
				case 0x33:
					text = "需要安全验证";break;
				case 0x35:
					text = "密码错误";break;
				case 0x36:
					text = "密码输入次数超过限制，请十分钟后再试";break;
				case 0x37:
					text = "等待时间为达到";break;
				case 0x78:
					text = "等待";break;
				case 0x7E:
					text = "当前模式不支持此服务的子功能";break;
				case 0x7F:
					text = "系统超时,请重新连接";break;
				}break;
			//心跳回复
			case 0x3E:
				text = "HB";
				break;
			}//end switch
		}//end try
		catch(Exception e)
		{
			text = "解析服务异常";
			type = ServerTmp.STOP;
			msgType = Msg.CTRL;
		}
		finally
		{
			//设置返回消息
			tmpData.setSrc(data);
			tmpData.setType(type);
			Msg msg = null;
			if (!"HB".equals(text))	//判断是否是心跳包
			{
				msg = new Msg(null, msgType, text, null);
				Log.d("sp_139", "TITLE:"+msg.getTile()+"TYPE:"+msg.getType());
			}
			tmpData.setMsg(msg);
		}
		return tmpData;
	}
}
