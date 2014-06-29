package com.dataserver;

import android.util.Log;


/**�����������*/
public class ServerParse
{
	/**���صķ���ͷ���ϵ���ֵ*/
	public static final int SERVER_ADD = 0x40;
	
	/**��������*/
	public static ServerTmp parseServer(byte[] src)
	{
		int srcLength = src.length;
		//������Ϣ��Ҫ�õ���һЩ����,Ĭ���ǿ��ơ�ֹͣ����
		byte[] data = null;
		String text =  "�����������:�Ҳ����÷���!";
		int type = ServerTmp.STOP;
		int msgType = Msg.CTRL;
		ServerTmp tmpData = new ServerTmp();
		//---------------------------
		try
		{
			byte serverHead = (byte) (src[0]-SERVER_ADD);
			switch(serverHead)
			{
			//������
			case 0x19:
				if(src[1]==0x02 && src[2]==0x2C)
				{
					if(srcLength < 4)
					{
						text = "û�й���";
						//type = ServerTmp.STOP;
						msgType = Msg.CTRL;
					}
					else
					{
						data = DataMath.subBytes(src, 3, srcLength-1);
						type = ServerTmp.GOON+ServerTmp.FCODE;
					}
				}break;
			//info,ps.2E 2F��Ҫȷ��һ��
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
			//���ƻ�����ʾ��
			case 0x10:
				if(src[1] == 0x03)
				{
					text = "����������У�Գɹ�";
					Log.d("sp_65", text);
				}break;
			case 0x27:
				if(src[1] == 0x11)
				{
					text = "��ȫ����У�Գɹ�";
				}
				else if(src[1] == 0x12)
				{
					text = "��ȫ����������ȷ";
				}break;
			case 0x31:
				if(src[1]==0x01 && src[2]==0x30 && src[3]==0x02)
				{
					text ="����ѧϰԿ�ף���ȴ�����";
					break;
				}
			//����Ӧ
			case 0x3F:
				byte nCode = src[2];
				switch(nCode)
				{
				case 0x10:
					text = "������������ϣ���Ҫ�򿪳��Ż򽫵��Կ�״�ON����";break;
				case 0x11:
					text = "����֧��";break;
				case 0x12:
					text = "�����ӹ��ܲ�֧��";break;
				case 0x13:
					text = "���񳤶ȴ���";break;
				case 0x22:
					text = "������������ϣ���Ҫ�򿪳��Ż򽫵��Կ�״�ON��";break;
				case 0x24:
					text = "����˳���������";break;
				case 0x31:
					text = "�������ݳ�����Χ";break;
				case 0x33:
					text = "��Ҫ��ȫ��֤";break;
				case 0x35:
					text = "�������";break;
				case 0x36:
					text = "������������������ƣ���ʮ���Ӻ�����";break;
				case 0x37:
					text = "�ȴ�ʱ��Ϊ�ﵽ";break;
				case 0x78:
					text = "�ȴ�";break;
				case 0x7E:
					text = "��ǰģʽ��֧�ִ˷�����ӹ���";break;
				case 0x7F:
					text = "ϵͳ��ʱ,����������";break;
				}break;
			//�����ظ�
			case 0x3E:
				text = "HB";
				break;
			}//end switch
		}//end try
		catch(Exception e)
		{
			text = "���������쳣";
			type = ServerTmp.STOP;
			msgType = Msg.CTRL;
		}
		finally
		{
			//���÷�����Ϣ
			tmpData.setSrc(data);
			tmpData.setType(type);
			Msg msg = null;
			if (!"HB".equals(text))	//�ж��Ƿ���������
			{
				msg = new Msg(null, msgType, text, null);
				Log.d("sp_139", "TITLE:"+msg.getTile()+"TYPE:"+msg.getType());
			}
			tmpData.setMsg(msg);
		}
		return tmpData;
	}
}
