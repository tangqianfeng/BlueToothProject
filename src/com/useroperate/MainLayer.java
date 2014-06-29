package com.useroperate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bluetooth.MainActivity;
import com.bluetooth.R;
import com.dataserver.DataMath;
import com.dataserver.Line;
import com.dataserver.Msg;
import com.netlayer.DataHeadValue;
import com.netlayer.HeartBeat;
import com.netlayer.NetSocket;
import com.netlayer.RecThread;
import com.netlayer.ThreadManger;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
/**
 * 
 * ���ʹ����Ϊ2�ַ��ʹ���   dataSend()��ns.send()
 * dataSend()Ϊ������ѭ������    ��Ҫ����һ������(������Ϣ)���ж������ݷ��͵Ĵ���    �� type=�ȴ�����oo��ѡ������Ҳ�õ�(����)
 * dataSend()֮�����ns.send()��Ҫ������ѡ�����ݵ��ύ����
 */

public class MainLayer extends Activity {
	// ����������
	private DispatchDataList datalist;
	// ������
	private InputStream in;
	// /xml�ĵ�
	private Document doc;
	// ��������
	private String XML_ROOTCONTEXT = "/������";
	// /��������
	private List<String> sonContext;
	// ���淢������ʱ��������
	private String sendStateContext;
	// ���ֶ���
	private TextView context_area = null;
	private LinearLayout submit_area = null;
	private LinearLayout linear = null;
	private LinearLayout table1 = null;
	private ListView listview = null;
	// ListView����ֵ
	private ArrayList<HashMap<String, String>> list_value = new ArrayList<HashMap<String, String>>();
	// �����߳�
	private RecThread rec = null;
	// ����
	private HeartBeat heartbeat = null;
	private ThreadManger thm = null;
	// ����
	private NetSocket ns = null;
	// back������
	private boolean backEnable = true;
	// ��������֤�Ľ���
	private HashSet<String> checkedContext;
	// ��ǰԪ��
	private Element currentElement;
	// ǰ���ж�
	private boolean forward = true;
	// ����ֹͣ����
	String stopheart_context = null;
	// ��ʱ���߼��
	private Timer timeCheck;
	//������
	public static MainLayer mainlayer;
	//������UI���  
	public boolean accessPermit = true;
	//״ֵ̬
	private int[] stateValue;
	// �߳�ͨ��
	private Handler handler = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mainlayer);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Msg myMsg = (Msg) msg.obj;
				/*
				 * �������Ϣ��ʾ��ʽ
				 */
				if (myMsg.getType() == Msg.INFO && MainLayer.mainlayer.accessPermit==true) {
					Log.d("MainLayer-126","info");
					List<Line> showList = myMsg.getLines();
					cleanRubbishDataOfLines(showList);
					for(int i=0;i<showList.size();i++){
						dealReturnData(myMsg.getHead(),showList.get(i).des,showList.get(i).show);
					}
					setListViewHeight(listview);
				} 
				/*
				 * ���������ʾ��ʽ
				 */
				else if(myMsg.getType() == Msg.FCODE && MainLayer.mainlayer.accessPermit==true){
					Log.d("MainLayer-136","fcode");
					List<Line> showList = myMsg.getLines();
					cleanRubbishDataOfLines(showList);
					Log.d("MainLayer-138",showList.size()+"");
					for(int i=0;i<showList.size();i++){
						RelativeLayout layoutInRow = (RelativeLayout) LayoutInflater.from(MainLayer.this).inflate(R.layout.component_laouytinrow_2, null);
						LinearLayout leftarea = (LinearLayout) layoutInRow.findViewById(R.id.row_left);
						LinearLayout rightarea = (LinearLayout) layoutInRow.findViewById(R.id.row_right);
						
						/*
						 * ��߼����� �ұ�״̬��ʾ
						 */
						TextView text_des = (TextView) LayoutInflater.from(MainLayer.this).inflate(R.layout.component_defaultcodetext, null);
						text_des.setText(showList.get(i).des+":");
						leftarea.addView(text_des);
						TextView text_show = (TextView) LayoutInflater.from(MainLayer.this).inflate(R.layout.component_defaultcodetext, null);
						text_show.setText(showList.get(i).show);
						rightarea.addView(text_show);
						
						LinearLayout.LayoutParams param = new LinearLayout.LayoutParams
						(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
						param.leftMargin=dipToPx(MainLayer.this,10);
						param.rightMargin=dipToPx(MainLayer.this,10);
						if(i%5==0 && i!=0){
							param.topMargin=dipToPx(MainLayer.this,15);
						}
						else{
							param.topMargin=dipToPx(MainLayer.this,0);
						}
						linear.addView(layoutInRow,param);
					}
				}
				/*
				 * ���  
				 * ����ͨ��handler���ա�����
				 * ÿ�������Ӧ��ͬ��UI�������������
				 */
				else if (myMsg.getType() == Msg.CTRL && MainLayer.mainlayer.accessPermit==true) {
					Log.d("MainLayer-172","CTRL");
					String ms = myMsg.getTile();
					String head = myMsg.getHead();
					if ("����������У�Գɹ�".equals(ms)) {
						if (head.equals(getXMLContext())) {
							datalist.clearDispatchDataDataNum(); // ���ݷ��Ͷ������
							cleanUI();// �Ƴ��������
							addComponent();
							checkedContext.add(getXMLContext());
							Toast.makeText(MainLayer.this, "У�Գɹ�",
									Toast.LENGTH_SHORT).show();
						}
					} else if ("����������У��ʧ��".equals(ms) ) {
						// ������-1
						sonContext.remove(sonContext.size() - 1);
						if (head.equals(getXMLContext())) {
							showToast("У��ʧ��");
							invaliDate();
						}
					}
					else if ("��ȫ����У��ʧ��".equals(ms)) {
						// ������-1
						sonContext.remove(sonContext.size() - 1);
						if (head.equals(getXMLContext())) {
							showToast("У��ʧ��");
							invaliDate();
						}
					} else if ("��ȫ����������ȷ".equals(ms)) {
						datalist.clearDispatchDataDataNum(); // ///����ˢ�� ���ݷ��Ͷ������
						cleanUI();// �Ƴ��������
						addComponent();
						checkedContext.add(getXMLContext());
					} else if ("��ȫ�����������".equals(ms)) {
						// ������-1
						sonContext.remove(sonContext.size() - 1);
						if (head.equals(getXMLContext())) {
							showToast("У��ʧ��");
							invaliDate();
						}
					}
					else if("����������".equals(ms)){
						cleanUI();
					}
					else if ("����".equals(ms)) {
						showToast("�ѵ��ߣ������豸�Ƿ���������!");
					}
				}
				/*
				 * �೵�����  ���״̬ (ȷ�������ֵ)
				 */
				else if(myMsg.getType() == Msg.INFO && MainLayer.mainlayer.accessPermit==false){
					accessPermit=true;
					List<Line> list = myMsg.getLines();
					cleanRubbishDataOfLines(list);
					stateValue = new int[list.size()];
					Log.d("MainLayer-233",list.get(0).des+" ");
					for(int i=0;i<list.size();i++){
						stateValue[i]=DataMath.bytesToInt(list.get(i).value) ;
					}
					optionalDeal_called();
				}
			}
		};

		datalist = new DispatchDataList();
		mainlayer = this;
		sonContext = new ArrayList<String>();
		context_area = (TextView) super.findViewById(R.id.context_area);
		submit_area = (LinearLayout) super.findViewById(R.id.submit_area);
		linear = (LinearLayout) super.findViewById(R.id.linear);
		table1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.component_linearlayout, null);
		listview = new ListView(this);
		listview.setDividerHeight(4);
		SimpleAdapter listItemAdapter = new SimpleAdapter(this,list_value,//����Դ   
	            R.layout.component_listview_layout,//ListItem��XMLʵ��  
	            //��̬������ImageItem��Ӧ������          
	            new String[] {"res","show"},   
	            //ImageItem��XML�ļ������һ��ImageView,����TextView ID  
	            new int[] {R.id.res,R.id.show}  
	        );  
		listview.setAdapter(listItemAdapter);
		checkedContext = new HashSet<String>();

		thm = new ThreadManger();
		ns = NetSocket.getInstace(thm);

		/*
		 *  ���������߳�
		 */
		rec = new RecThread(handler);
		rec.start();

		try {
			in = this.getAssets().open("lay.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			doc = new SAXReader().read(in);
			if (doc == null) {
				warnDialog("�����ļ��д�");
			} else {
				invaliDate();
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			warnDialog("�������󣡣���");
		}

	}

	/*
	 * ˢ�½���
	 */
	private void invaliDate() {
		Log.d("Mainlayer-186", "..");
		datalist.clearDispatchDataDataNum(); // ///����ˢ�� ���ݷ��Ͷ������
		//��ս���
		cleanUI();
		
		String context = getXMLContext();
		//�õ�Ŀ��Ԫ��
		Object ob = doc.selectObject(context);
		currentElement = (Element) ob;
		Log.d("Mainlayer-148", this.getXMLContext());
		
		//��ǰλ�� 
		context_area.setText(currentElement.getName());
		
		//��ʼ�����ж�
		isStartHeartBeat();
		//���������ж�
		isEndHeartBeat(context);
		//����id����
		setControlID();

		Attribute type = currentElement.attribute("type");
		String val = null;
		if (type == null || !forward) {
			val = "��ͨ��ת��";
		} else {
			val = type.getValue();
		}
		if (val != null) {
			Log.d("Mainlayer-154", val);
			if (val.equals("�ȴ�����")) {
				Log.d("MainLayer-153", "..");
				waitBlockDeal(1);
			} else if (val.equals("�ȴ�����oo��ѡ����")) {
				waitBlockDeal(2);
			} else if (val.equals("��ѡ��")) {
				Log.d("MainLayer-200", "..");
				optionalDeal();
			} else if (val.equals("��ʱ����")) {
				Log.d("MainLayer-204", "..");
				final ProgressBar pb= (ProgressBar) LayoutInflater.from(this).inflate(R.layout.component_progressbar, null);
				pb.setPadding(0, 5, 0, 20);
				linear.addView(pb);
				Element el = currentElement.element("senddata");
				//�ȴ�������ŷ���
				final Element el_2 = currentElement.element("waitsend");
				sendDeal(el);
				backEnable = false;
				// Ĭ������Ϊ10��
				int blocktime = 10000;
				String value = currentElement.attributeValue("blocktime");
				if (value != null) {
					blocktime = Integer.parseInt(value);
				}
				Timer time = new Timer();
				time.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (el_2 != null) {
							sendDeal(el_2);
						}
						backEnable = true;
					}
				}, blocktime+1000);
				final int sleeptime = blocktime/100;
				new Thread(){
					public void run(){
						int num=0;
						while(num<100){
							try {
								Thread.sleep(sleeptime);
								num++;
								pb.setProgress(num);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						Message msg = new Message();
						Msg myMsg = new Msg(Msg.CTRL, "����������");
						msg.obj = myMsg;
						handler.sendMessage(msg);
					}
				}.start();
			} else if (val.equals("��ͨ��ת��")) {
				Log.d("MainLayer-226", "..");
				addComponent();
			} else if (val.equals("�̶�������")) {
				Log.d("MainLayer-230", "..");
				Element el = currentElement.element("senddata");
				sendDeal(el);
			}else if ("�����".equals(val)) {
				Log.d("MainLayer-387:", "..");
				String service = currentElement.attributeValue("service");
				passwordInput("��������ĺ������", 1, service);
			} 
			else {
				warnDialog("type�����д�");
			}
		}
	}

	/*
	 * ���lines����������
	 */
	private void cleanRubbishDataOfLines(List<Line> lines){
		for(int i=0;i<lines.size();i++){
			String des = lines.get(i).getDes();
			String show = lines.get(i).getShow();
			Log.d("MainLayer-407",des+"----"+show);
			if("0".equals(des.trim()) || "1".equals(des.trim()) || "0".equals(show.trim()) || "1".equals(show.trim())){
				Log.d("MainLayer-409",".....");
				lines.remove(i);
				i--;
			}
		}
	}
	/*
	 *  �Ƴ��������
	 */
	private void cleanUI(){
		linear.removeAllViews();
		submit_area.removeAllViews();
		/*
		 *  ���ListView�������
		 */
		if (!(list_value.isEmpty())) {
			list_value.clear();
			SimpleAdapter a = (SimpleAdapter) listview
					.getAdapter();
			a.notifyDataSetChanged();
		}
	}
	/**
	 * 
	 * @param type
	 *            1���ȴ����� 2���ȴ�����&&��ѡ����
	 */
	private void waitBlockDeal(int type) {
		Attribute checkdata = currentElement.attribute("checkdata");
		datalist.addData(checkdata.getValue());
		if (type == 1) {
			dataSend();
		} else if (type == 2) {
			String service = currentElement.attributeValue("passwordservice");
			passwordInput("��������", type, service);
		}
	}
	
	/*
	 * �ж��Ƿ�Ӧ�ÿ�ʼ����
	 */
	private void isStartHeartBeat(){
		String heartbeatblood = currentElement.attributeValue("heartbeatblood");
		if (forward && heartbeatblood != null) {
			Log.d("MainLayer-216", "����ing");
			onlineCheckStart();
			String[] datagroup = heartbeatblood.split(",");
			int size = datagroup.length;
			byte[] blood = new byte[size];
			for (int i = 0; i < size; i++) {
				blood[i] = (byte) Integer.parseInt(datagroup[i], 16);
			}
			ThreadManger.setHeartBeatBlood(blood);

			heartbeat = new HeartBeat(thm);
			heartbeat.start();

			stopheart_context = XML_ROOTCONTEXT;
			/*
			 * �õ���һ��path ����ֹͣ��path
			 */
			for (int i = 0; i < sonContext.size() - 1; i++) {
				stopheart_context = stopheart_context + "/" + sonContext.get(i);
			}
			Log.d("MainLayer-240", stopheart_context);
		}
	}
	
	/*
	 * �����Ƿ�Ӧ��ֹͣ
	 */
	private void isEndHeartBeat(String context){
		Log.d("MainLayer-244", context + ".." + stopheart_context);
		if (!forward && context.equals(stopheart_context)) {
			Log.d("MainLayer-245", "..");
			stopheart_context = null;
			onlineCheckEnd();
			// �ر�����
			heartbeat.setAliveFalse();
		}
	}
	
	/*
	 * ���ÿ���ID
	 */
	private void setControlID(){
		String value = currentElement.attributeValue("controlID");
		if(forward && value != null){
			String[] datagroup = value.split(",");
			int size = datagroup.length;
			byte[] id = new byte[size];
			for (int i = 0; i < size; i++) {
				id[i] = (byte) Integer.parseInt(datagroup[i], 16);
			}
			DataHeadValue.setControlID(id[0], id[1]);
		}
	}
	/**
	 * ���ݷ��ʹ���
	 * 
	 * @param element
	 *            ����Ԫ��
	 */
	private void sendDeal(Element element) {
		@SuppressWarnings("rawtypes")
		List attlist = element.attributes();
		for (int i = 0; i < attlist.size(); i++) {
			Attribute att = (Attribute) attlist.get(i);
			datalist.addData(att.getValue());
		}
		Log.d("MainLayer-160", +attlist.size() + "");

		// ��������
		dataSend();
	}

	/**
	 * Ϊ����ť����¼�
	 * 
	 * @param tb
	 * @param index
	 *            �ֽ�λ��
	 * @param startbit
	 *            ��ʼλ
	 * @param endbit
	 *            ����λ
	 */
	private void addToggleButtonEvent(ToggleButton tb, final int index,
			final int startbit, final int endbit) {
		tb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ToggleButton tob = (ToggleButton) v;
				if ("������".equals(tob.getText())) {
					datalist.setSwitchData(index + 6, startbit, endbit, 1);
					Log.d("MainLayer-433","������");
				} else if ("������".equals(tob.getText())) {
					datalist.setSwitchData(index + 6, startbit, endbit, 0);
					Log.d("MainLayer-436","������");
				}
				else{
					Log.d("MainLayer-439","�쳣");
				}
			}
		});

	}

	/*
	 * ����������¼�
	 */
	private void addSelectEvent(final int index, final int startbit,
			final int endbit, Spinner spinner) {
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				ArrayAdapterImp ad = (ArrayAdapterImp) arg0.getAdapter();
				int bb = ad.getValue(arg2);
				datalist.setSwitchData(index, startbit, endbit, bb);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	/*
	 * ��ӿ��ذ�ť�¼�
	 */
	private void addSwitchEvent(Button button, final int byteindex,
			final int bitindex) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Button bt = (Button) v;
				String value = (String) bt.getText();
				if ("  ��  ".equals(value)) {
					bt.setText("  �ر�  ");
					datalist.setSwitchData(byteindex, bitindex, bitindex, 1);
					Log.d("MainLayer-300", byteindex + "-----" + bitindex);
				} else if ("  �ر�  ".equals(value)) {
					bt.setText("  ��  ");
					datalist.setSwitchData(byteindex, bitindex, bitindex, 0);
				}
			}
		});
	}

	/*
	 *  ��ѡ��� linearlayout(id:table1)����RelativeLayout��Ȼ����������linearlayout(id��linear)
	 *  table1��ʡ��
	 */
	private void optionalDeal() {
		table1.removeAllViews();
		//�½�����
		String num = currentElement.attributeValue("datalength");
		if (num != null) {
			int datalen = Integer.parseInt(num);
			datalist.clearSwitch(datalen);
		}
		//����״̬ƥ������
		String data_matchState = currentElement.attributeValue("matchState");
		datalist.addData(data_matchState);
		accessPermit=false;
		dataSend();		
	}

	private void optionalDeal_called(){
		Log.d("MainLayer-216", "..");
		//Ĭ��controlΪfalse
		String control = currentElement.attributeValue("control");
		if (control == null) {
			control = "false";
		}
		// ��÷���ͷ
		String service = currentElement.attributeValue("service");
		if (service != null) {
			String[] datagroup = service.split(",");
			int size = datagroup.length;
			byte[] head_byte = new byte[size];
			for (int i = 0; i < size; i++) {
				head_byte[i] = (byte) Integer.parseInt(datagroup[i], 16);
			}
			datalist.setServiceHead(head_byte);
		}

		if (table1 != null) {
			Log.d("MainLayer-201", "..");
			// ///////��ȡ������Ԫ��
			List<?> sl = currentElement.elements();

			// //////////��ť���
			for (int comNum=0;comNum<sl.size();comNum++) {
				Element son_element = (Element) sl.get(comNum);
				// ����ť����
				if ("����ť".equals(son_element.attributeValue("param"))) {
					String value = son_element.getName();
					String bytenum = son_element.attributeValue("byte");
					String bitnum = son_element.attributeValue("bit");
					if (bytenum != null && bitnum != null) {
						int byteindex = Integer.parseInt(bytenum);
						int bitindex = Integer.parseInt(bitnum);
					
						RelativeLayout layoutInRow = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.component_laoutinrow, null);
						// �ı�
						TextView text = (TextView) LayoutInflater.from(this).inflate(R.layout.component_optionaltextview, null);
						text.setText(value);
						//���ı��ŵ����
						LinearLayout leftarea = (LinearLayout) layoutInRow.findViewById(R.id.row_left);
						leftarea.addView(text);
						// ���ذ�ť
						Button button = (Button) LayoutInflater.from(this).inflate(R.layout.component_binarybutton, null);
						/*
						 * ��ť״̬ƥ��
						 */
						if(stateValue[comNum]==0){
							button.setText("  �ر�  ");
						}
						else{
							button.setText("  ��  ");
						}
						addSwitchEvent(button, byteindex, bitindex);
				
						if (control.equals("true")) {
							//�ѿ��ذ�ť�ŵ��м�
							LinearLayout centerarea = (LinearLayout) layoutInRow.findViewById(R.id.row_center);
							centerarea.addView(button);
							// ���ư�ť
							ToggleButton tb = (ToggleButton) LayoutInflater.from(this).inflate(R.layout.component_togglebutton, null);
							tb.setText("������");
							tb.setTextOff("������");
							tb.setTextOn("������");
							addToggleButtonEvent(tb, byteindex, bitindex,
									bitindex);
							//�ѿ���ť�ŵ��ұ�
							LinearLayout rightarea = (LinearLayout) layoutInRow.findViewById(R.id.row_right);
							rightarea.addView(tb);
						}
						else{
							//�ѿ��ذ�ť�ŵ��м�
							LinearLayout rightarea = (LinearLayout) layoutInRow.findViewById(R.id.row_right);
							rightarea.addView(button);
						}
						LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
						param.topMargin=dipToPx(this, 5);
						table1.addView(layoutInRow,param);
						//�»���
//						View divider = LayoutInflater.from(this).inflate(R.layout.component_divider, null);
//						table1.addView(divider);
						Log.d("MainLayer-231", "..");
					} else {
						warnDialog("xml�����ļ������д�!��˶�����byte&&bit��");
						return;
					}
				}
				/*
				 *  ����������
				 */
				else if ("������".equals(son_element.attributeValue("param"))) {
					String value = son_element.getName();
					Log.d("Mianlayer-371", value);
					if (son_element.attributeValue("byte") == null
							|| son_element.attributeValue("startbit") == null
							|| son_element.attributeValue("endbit") == null) {
						warnDialog("�����ļ��д���˶�����byte&&startbit&&endbit��");
						return;
					}
					int index = Integer.parseInt(son_element
							.attributeValue("byte"));
					int startbit = Integer.parseInt(son_element
							.attributeValue("startbit"));
					int endbit = Integer.parseInt(son_element
							.attributeValue("endbit"));
					Log.d("Mianlayer-372", "...");

					RelativeLayout layoutInRow = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.component_laoutinrow, null);
					// �ı�
					TextView text = (TextView) LayoutInflater.from(this).inflate(R.layout.component_optionaltextview, null);
					text.setText(value);
					//���ı��ŵ����
					LinearLayout leftarea = (LinearLayout) layoutInRow.findViewById(R.id.row_left);
					leftarea.addView(text);
					// �����˵�
					@SuppressWarnings("unchecked")
					List<Attribute> li = son_element.elements();
					// ��ʾ
					List<String> showList = new ArrayList<String>();
					// value
					List<Integer> valueList = new ArrayList<Integer>();
					for (Object obs : li) {
						Element op_eleElement = (Element) obs;
						String op_showvalue = op_eleElement
								.attributeValue("name");
						if (op_showvalue == null) {
							op_showvalue = op_eleElement.getName();
						}
						int op_value = Integer.parseInt(op_eleElement
								.attributeValue("value"));
						showList.add(op_showvalue);
						valueList.add(op_value);
					}
					Spinner spinner = buildSpinner(showList, valueList);
					ArrayAdapterImp  adImp = (ArrayAdapterImp)(spinner.getAdapter());
					spinner.setSelection(adImp.getItemPositionByValue(stateValue[comNum]), true);
					addSelectEvent(index, startbit, endbit, spinner);
					if ("true".equals(control)) {
						//���������ŵ��м�
						LinearLayout centerarea = (LinearLayout) layoutInRow.findViewById(R.id.row_center);
						centerarea.addView(spinner);
						ToggleButton tb = (ToggleButton) LayoutInflater.from(this).inflate(R.layout.component_togglebutton, null);
						tb.setText("������");
						tb.setTextOff("������");
						tb.setTextOn("������");
						addToggleButtonEvent(tb, index, startbit, endbit);
						//�ѿ���ť�ŵ��ұ�
						LinearLayout rightarea = (LinearLayout) layoutInRow.findViewById(R.id.row_right);
						rightarea.addView(tb);
					}
					else{
						//���������ŵ��ұ�
						LinearLayout rightarea = (LinearLayout) layoutInRow.findViewById(R.id.row_right);
						rightarea.addView(spinner);
					}
					table1.addView(layoutInRow);
				} 
			}
			linear.addView(table1);
			// �ύ��ť ����ύ����
			Button sur = (Button) LayoutInflater.from(this).inflate(R.layout.component_submitbutton, null);
			sur.setText("�ύ");
			sur.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// ��������
					byte[] bytedata = datalist.getByteData();
					try {
						sendStateContext = getXMLContext();
						if(ns!=null){
							ns.send(bytedata, 2);
							//�������
//							onBackPressed();
							showToast("�ύ�ɹ���");
						}
						else{
							Log.d("MainLayer-746","nulllllll");
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			submit_area.addView(sur);
		}
	}
	
	/*
	 * ��ͨ��ť �¼����
	 */
	private void addEvent(Button button) {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				forward = true;
				Button bt = (Button) v;
				String button_value = (String) bt.getText();
				sonContext.add(button_value);
				invaliDate();
			}
		});
	}

	/*
	 * ������ ��ͨ����
	 */
	private void addComponent() {
		// ///////��ȡ������Ԫ��
		List<?> sl = currentElement.elements();
		// //////////��ť���
		for (Object sob : sl) {
			Element son_element = (Element) sob;
			String value = son_element.getName();
			Button menuButton= (Button) LayoutInflater.from(this).inflate(R.layout.component_skipbutton, null);
			//Button menuButton = new Button(this);
			menuButton.setText(value);
			linear.addView(menuButton);
			Log.d("MainLayer-567", value);
			int dp = dipToPx(this, 80);
			menuButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,dp));
			addEvent(menuButton);
		}
		//����
		if(forward){
			forwordAnimation();
		}
		else{
			backAnimation();
		}
	}

	// У��ʧ�� ����
	private void showToast(String value) {
		// ����
		Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
	}

	/*
	 * ��дback����
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (backEnable) {
			if (sonContext.size() != 0) {
				// ��Ϊ������Ϊ
				forward = false;
				// ������-1
				sonContext.remove(sonContext.size() - 1);
				invaliDate();
			} else {
				// �˳������
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setIcon(android.R.drawable.ic_dialog_info);
				dialog.setTitle("����");
				dialog.setMessage("��ȷ��Ҫ�˳���ǰ���ƣ�");
				dialog.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								MainActivity.bluetoothState = MainActivity.NONE;
								MainActivity.vc_str.removeAllElements(); // ���������豸�б�����
								MainActivity.text.setText("�����������豸");
								try {
									MainActivity.btSocket.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// MainActivity.ma.finish();

								System.exit(0);
							}
						});
				dialog.setNegativeButton("ȡ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				dialog.show();
			}
		} else {
			Toast.makeText(MainLayer.this, "��ȴ�����", Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * ��������ݷ��� ����ӿ�
	 */
	private void dealReturnData(String head, String des,String show) {
		if (list_value.isEmpty()) {
			String context = getXMLContext();
			if (mainlayer != null) {
				if (context.equals(head)) {
					Log.d("MainLayer-911","..");
					linear.addView(listview);
					HashMap<String, String> map = new HashMap<String, String>();  
					map.put("res",des);
					map.put("show",show);
					list_value.add(map);
					//ǿ������UI����  �����̸߳���UI��
					SimpleAdapter a = (SimpleAdapter) listview
							.getAdapter();
					a.notifyDataSetChanged();
				}
			}
		} 
		else {
			if (mainlayer != null) {
				Log.d("MainLayer-277:", "�ش�������ʾ");
				String context = getXMLContext();
				/*
				 * if�ж�   ���˵�����xml������ȱ�ݲ���������������
				 */
				if (context.equals(head)) {
					Log.d("MainLayer-602", "�ش�������ʾ2--" + des);
					HashMap<String, String> map = new HashMap<String, 	String>();  
					map.put("res",des);
					map.put("show",show);
					list_value.add(map);
					SimpleAdapter a = (SimpleAdapter) listview
							.getAdapter();
					a.notifyDataSetChanged();
				}
			}
		}
	}

	/*
	 * ��õ�ǰ������
	 */
	private String getXMLContext() {
		String context = XML_ROOTCONTEXT;
		// ///�õ�Xpath
		for (String soncontext : sonContext) {
			context = context + "/" + soncontext;
		}
		return context;
	}

	public String getSendStateContext() {
		return sendStateContext;
	}

	public void dataSend() {
		String data = datalist.getData();
		// /ת�� ����
		if (data != null) {
			// ���ַ���ת����byte
			String[] datagroup = data.split(",");
			int size = datagroup.length;
			byte[] bytedata = new byte[size];
			for (int i = 0; i < size; i++) {
				bytedata[i] = (byte) Integer.parseInt(datagroup[i], 16);
			}
			try {
				// ���淢��ʱ��������
				sendStateContext = getXMLContext();
				if (ns != null) {
					Log.d("MainLayer-559", "..");
					ns.send(bytedata, 2);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @param text
	 *            ��ʾ�ı�
	 * @param type
	 *            1����ʱ���� 2����ʱ����
	 */
	private void passwordInput(String text, final int type, final String service) {
		final View view = LayoutInflater.from(this).inflate(R.layout.component_edittext, null);

		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle("����������")
				.setMessage(text)
				.setView(view)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						EditText edit = (EditText) view;
						String in = edit.getText().toString();
						if (in.length() == 8) {
							// ���볤�ȹ̶�Ϊ4�ֽ�
							String[] instring = new String[4];
							char[] inchar = in.toCharArray();
							instring[0] = Character.toString(inchar[0])
									+ Character.toString(inchar[1]);
							instring[1] = Character.toString(inchar[2])
									+ Character.toString(inchar[3]);
							instring[2] = Character.toString(inchar[4])
									+ Character.toString(inchar[5]);
							instring[3] = Character.toString(inchar[6])
									+ Character.toString(inchar[7]);
							if (type == 1) {
//								byte[] bytedata = new byte[4];
//								for (int i = 0; i < 4; i++) {
//									bytedata[i] = (byte) Integer.parseInt(
//											instring[i], 16);
//								}
//								// �ӷ���
//								byte[] bytedata2 = null;
//								if (service != null) {
//									String[] datagroup = service.split(",");
//									int size = datagroup.length;
//									byte[] head = new byte[size];
//									for (int i = 0; i < size; i++) {
//										head[i] = (byte) Integer.parseInt(
//												datagroup[i], 16);
//									}
//									bytedata2 = new byte[4 + size];
//									Log.d("MainLayer-743", size + "");
//									// ����
//									for (int i = 0; i < size; i++) {
//										bytedata2[i] = head[i];
//									}
//									// ����
//									for (int i = size; i < 4 + size; i++) {
//										bytedata2[i] = bytedata[i - size];
//									}
//								}
//								try {
//									// ���淢��ʱ��������
//									sendStateContext = getXMLContext();
//									if (ns != null) {
//										Log.d("MainLayer-638", "..");
//										if (bytedata2 != null) {
//											ns.send(bytedata2, 2);
//										} else {
//											ns.send(bytedata, 2);
//										}
//									}
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
								String ts = instring[0] + "," + instring[1]
										+ "," + instring[2] + "," + instring[3];
								// �ӷ���
								if (service != null) {
									ts = service + "," + ts;
								}
								datalist.addData(ts);
								dataSend();
							}
							else if (type == 2) {
								String ts = instring[0] + "," + instring[1]
										+ "," + instring[2] + "," + instring[3];
								//�ӷ���
								if (service != null) {
									ts = service + "," + ts;
								}
								datalist.addData(ts);
								dataSend();
							}

						}
						// ���볤�Ȳ���
						else {
							passwordInput("���볤�Ȳ��ԣ�����������", type, service);
						}
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						// ����
						onBackPressed();
					}
				}).create();
		dialog.show();
	}

	// �����
	private void warnDialog(String text) {
		AlertDialog dialog = new AlertDialog.Builder(this).setTitle("����")
				.setMessage(text)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				}).create();
		dialog.show();
	}

	// ������
	private Spinner buildSpinner(List<String> showList, List<Integer> valueList) {
		Spinner spinner = new Spinner(this);
		spinner.setLayoutParams(new LinearLayout.LayoutParams(220,LinearLayout.LayoutParams.WRAP_CONTENT));
		ArrayAdapterImp adpterimp = new ArrayAdapterImp(this,
				android.R.layout.simple_spinner_item, showList, valueList);
		adpterimp.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
		spinner.setAdapter(adpterimp);
		return spinner;
	}

	public HeartBeat getHeartBeat() {
		return heartbeat;
	}

	private void onlineCheckStart() {
		timeCheck = new Timer();
		timeCheck.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!RecThread.onlineFlag) {
					Message msg = new Message();
					Msg myMsg = new Msg(Msg.CTRL, "����");
					msg.obj = myMsg;
					handler.sendMessage(msg);
				} else {
					RecThread.onlineFlag = false;
				}
			}
		}, 12 * 1000, 12 * 1000);
	}

	private void onlineCheckEnd() {
		timeCheck.cancel();
	}
	
	 private int dipToPx(Context context, float dpValue) {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return (int) (dpValue * scale + 0.5f);  
	}  
	   
	private void forwordAnimation(){
//		int dp = dipToPx(this, 300);
//		Animation animation = new TranslateAnimation(dp, 0, 0, 0);
//		animation.setDuration(350);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.forword_push_left_in);
		linear.startAnimation(animation);
	}
	
	private void backAnimation(){
//		int dp = dipToPx(this, 300);
//		Animation animation = new TranslateAnimation(-dp, 0, 0, 0);
//		animation.setDuration(350);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.back_push_left_in);
		linear.startAnimation(animation);
	}
	
	/*
	 * ListView�߶����� ��������������
	 */
	private void setListViewHeight(ListView lv) {
        ListAdapter la = lv.getAdapter();
        if(null == la) {
            return;
        }
        // calculate height of all items.
        int h = 0;
        final int cnt = la.getCount();
        for(int i=0; i<cnt; i++) {
            View item = la.getView(i, null, lv);
            item.measure(0, 0);
            h += item.getMeasuredHeight();
        }
        // reset ListView height
        ViewGroup.LayoutParams lp = lv.getLayoutParams();
        lp.height = h + (lv.getDividerHeight() * (cnt - 1));
        Display d = this.getWindowManager().getDefaultDisplay();
        if(lp.height<+d.getHeight()){
        	lp.height=d.getHeight()-dipToPx(this, 64);
        }
        lv.setLayoutParams(lp);
    }
}
