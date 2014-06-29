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
 * 发送处理分为2种发送处理   dataSend()、ns.send()
 * dataSend()为数据链循环发送    主要处理一个操作(基本信息)中有多条数据发送的处理    在 type=等待阻塞oo自选数据中也用到(例外)
 * dataSend()之外调用ns.send()主要处理自选项数据的提交发送
 */

public class MainLayer extends Activity {
	// 待发送数据
	private DispatchDataList datalist;
	// 输入流
	private InputStream in;
	// /xml文档
	private Document doc;
	// 父上下文
	private String XML_ROOTCONTEXT = "/主界面";
	// /子上下文
	private List<String> sonContext;
	// 保存发送数据时的上下文
	private String sendStateContext;
	// 布局对象
	private TextView context_area = null;
	private LinearLayout submit_area = null;
	private LinearLayout linear = null;
	private LinearLayout table1 = null;
	private ListView listview = null;
	// ListView参数值
	private ArrayList<HashMap<String, String>> list_value = new ArrayList<HashMap<String, String>>();
	// 接收线程
	private RecThread rec = null;
	// 心跳
	private HeartBeat heartbeat = null;
	private ThreadManger thm = null;
	// 发送
	private NetSocket ns = null;
	// back建可用
	private boolean backEnable = true;
	// 保存已验证的界面
	private HashSet<String> checkedContext;
	// 当前元素
	private Element currentElement;
	// 前进判断
	private boolean forward = true;
	// 心跳停止条件
	String stopheart_context = null;
	// 定时掉线检查
	private Timer timeCheck;
	//单例化
	public static MainLayer mainlayer;
	//进入主UI许可  
	public boolean accessPermit = true;
	//状态值
	private int[] stateValue;
	// 线程通信
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
				 * 类基本信息显示方式
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
				 * 类故障码显示方式
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
						 * 左边加描述 右边状态显示
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
				 * 命令集  
				 * 命令通过handler接收、发送
				 * 每个命令对应不同的UI处理或其他处理
				 */
				else if (myMsg.getType() == Msg.CTRL && MainLayer.mainlayer.accessPermit==true) {
					Log.d("MainLayer-172","CTRL");
					String ms = myMsg.getTile();
					String head = myMsg.getHead();
					if ("防盗控制器校对成功".equals(ms)) {
						if (head.equals(getXMLContext())) {
							datalist.clearDispatchDataDataNum(); // 数据发送队列清空
							cleanUI();// 移除所有组件
							addComponent();
							checkedContext.add(getXMLContext());
							Toast.makeText(MainLayer.this, "校对成功",
									Toast.LENGTH_SHORT).show();
						}
					} else if ("防盗控制器校对失败".equals(ms) ) {
						// 上下文-1
						sonContext.remove(sonContext.size() - 1);
						if (head.equals(getXMLContext())) {
							showToast("校对失败");
							invaliDate();
						}
					}
					else if ("安全操作校对失败".equals(ms)) {
						// 上下文-1
						sonContext.remove(sonContext.size() - 1);
						if (head.equals(getXMLContext())) {
							showToast("校对失败");
							invaliDate();
						}
					} else if ("安全操作密码正确".equals(ms)) {
						datalist.clearDispatchDataDataNum(); // ///界面刷新 数据发送队列清空
						cleanUI();// 移除所有组件
						addComponent();
						checkedContext.add(getXMLContext());
					} else if ("安全操作密码错误".equals(ms)) {
						// 上下文-1
						sonContext.remove(sonContext.size() - 1);
						if (head.equals(getXMLContext())) {
							showToast("校对失败");
							invaliDate();
						}
					}
					else if("清空现有组件".equals(ms)){
						cleanUI();
					}
					else if ("掉线".equals(ms)) {
						showToast("已掉线，请检查设备是否连接正常!");
					}
				}
				/*
				 * 类车辆检测  组件状态 (确定组件的值)
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
		SimpleAdapter listItemAdapter = new SimpleAdapter(this,list_value,//数据源   
	            R.layout.component_listview_layout,//ListItem的XML实现  
	            //动态数组与ImageItem对应的子项          
	            new String[] {"res","show"},   
	            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
	            new int[] {R.id.res,R.id.show}  
	        );  
		listview.setAdapter(listItemAdapter);
		checkedContext = new HashSet<String>();

		thm = new ThreadManger();
		ns = NetSocket.getInstace(thm);

		/*
		 *  开启接收线程
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
				warnDialog("配置文件有错！");
			} else {
				invaliDate();
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			warnDialog("发生错误！！！");
		}

	}

	/*
	 * 刷新界面
	 */
	private void invaliDate() {
		Log.d("Mainlayer-186", "..");
		datalist.clearDispatchDataDataNum(); // ///界面刷新 数据发送队列清空
		//清空界面
		cleanUI();
		
		String context = getXMLContext();
		//得到目标元素
		Object ob = doc.selectObject(context);
		currentElement = (Element) ob;
		Log.d("Mainlayer-148", this.getXMLContext());
		
		//当前位置 
		context_area.setText(currentElement.getName());
		
		//开始心跳判定
		isStartHeartBeat();
		//结束心跳判定
		isEndHeartBeat(context);
		//控制id设置
		setControlID();

		Attribute type = currentElement.attribute("type");
		String val = null;
		if (type == null || !forward) {
			val = "普通跳转项";
		} else {
			val = type.getValue();
		}
		if (val != null) {
			Log.d("Mainlayer-154", val);
			if (val.equals("等待阻塞")) {
				Log.d("MainLayer-153", "..");
				waitBlockDeal(1);
			} else if (val.equals("等待阻塞oo自选数据")) {
				waitBlockDeal(2);
			} else if (val.equals("自选项")) {
				Log.d("MainLayer-200", "..");
				optionalDeal();
			} else if (val.equals("定时阻塞")) {
				Log.d("MainLayer-204", "..");
				final ProgressBar pb= (ProgressBar) LayoutInflater.from(this).inflate(R.layout.component_progressbar, null);
				pb.setPadding(0, 5, 0, 20);
				linear.addView(pb);
				Element el = currentElement.element("senddata");
				//等待结束后才发送
				final Element el_2 = currentElement.element("waitsend");
				sendDeal(el);
				backEnable = false;
				// 默认阻塞为10秒
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
						Msg myMsg = new Msg(Msg.CTRL, "清空现有组件");
						msg.obj = myMsg;
						handler.sendMessage(msg);
					}
				}.start();
			} else if (val.equals("普通跳转项")) {
				Log.d("MainLayer-226", "..");
				addComponent();
			} else if (val.equals("固定数据项")) {
				Log.d("MainLayer-230", "..");
				Element el = currentElement.element("senddata");
				sendDeal(el);
			}else if ("密码框".equals(val)) {
				Log.d("MainLayer-387:", "..");
				String service = currentElement.attributeValue("service");
				passwordInput("请输入更改后的密码", 1, service);
			} 
			else {
				warnDialog("type属性有错");
			}
		}
	}

	/*
	 * 清空lines里无用数据
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
	 *  移除所有组件
	 */
	private void cleanUI(){
		linear.removeAllViews();
		submit_area.removeAllViews();
		/*
		 *  清空ListView里的内容
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
	 *            1，等待阻塞 2，等待阻塞&&自选数据
	 */
	private void waitBlockDeal(int type) {
		Attribute checkdata = currentElement.attribute("checkdata");
		datalist.addData(checkdata.getValue());
		if (type == 1) {
			dataSend();
		} else if (type == 2) {
			String service = currentElement.attributeValue("passwordservice");
			passwordInput("输入密码", type, service);
		}
	}
	
	/*
	 * 判断是否应该开始心跳
	 */
	private void isStartHeartBeat(){
		String heartbeatblood = currentElement.attributeValue("heartbeatblood");
		if (forward && heartbeatblood != null) {
			Log.d("MainLayer-216", "心跳ing");
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
			 * 得到上一级path 心跳停止的path
			 */
			for (int i = 0; i < sonContext.size() - 1; i++) {
				stopheart_context = stopheart_context + "/" + sonContext.get(i);
			}
			Log.d("MainLayer-240", stopheart_context);
		}
	}
	
	/*
	 * 心跳是否应该停止
	 */
	private void isEndHeartBeat(String context){
		Log.d("MainLayer-244", context + ".." + stopheart_context);
		if (!forward && context.equals(stopheart_context)) {
			Log.d("MainLayer-245", "..");
			stopheart_context = null;
			onlineCheckEnd();
			// 关闭心跳
			heartbeat.setAliveFalse();
		}
	}
	
	/*
	 * 设置控制ID
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
	 * 数据发送处理
	 * 
	 * @param element
	 *            数据元素
	 */
	private void sendDeal(Element element) {
		@SuppressWarnings("rawtypes")
		List attlist = element.attributes();
		for (int i = 0; i < attlist.size(); i++) {
			Attribute att = (Attribute) attlist.get(i);
			datalist.addData(att.getValue());
		}
		Log.d("MainLayer-160", +attlist.size() + "");

		// 发送数据
		dataSend();
	}

	/**
	 * 为控制钮添加事件
	 * 
	 * @param tb
	 * @param index
	 *            字节位置
	 * @param startbit
	 *            开始位
	 * @param endbit
	 *            结束位
	 */
	private void addToggleButtonEvent(ToggleButton tb, final int index,
			final int startbit, final int endbit) {
		tb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ToggleButton tob = (ToggleButton) v;
				if ("控制中".equals(tob.getText())) {
					datalist.setSwitchData(index + 6, startbit, endbit, 1);
					Log.d("MainLayer-433","控制中");
				} else if ("不控制".equals(tob.getText())) {
					datalist.setSwitchData(index + 6, startbit, endbit, 0);
					Log.d("MainLayer-436","不控制");
				}
				else{
					Log.d("MainLayer-439","异常");
				}
			}
		});

	}

	/*
	 * 添加下拉单事件
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
	 * 添加开关按钮事件
	 */
	private void addSwitchEvent(Button button, final int byteindex,
			final int bitindex) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Button bt = (Button) v;
				String value = (String) bt.getText();
				if ("  打开  ".equals(value)) {
					bt.setText("  关闭  ");
					datalist.setSwitchData(byteindex, bitindex, bitindex, 1);
					Log.d("MainLayer-300", byteindex + "-----" + bitindex);
				} else if ("  关闭  ".equals(value)) {
					bt.setText("  打开  ");
					datalist.setSwitchData(byteindex, bitindex, bitindex, 0);
				}
			}
		});
	}

	/*
	 *  自选项处理 linearlayout(id:table1)里套RelativeLayout，然后加入最外层linearlayout(id：linear)
	 *  table1可省略
	 */
	private void optionalDeal() {
		table1.removeAllViews();
		//新建容器
		String num = currentElement.attributeValue("datalength");
		if (num != null) {
			int datalen = Integer.parseInt(num);
			datalist.clearSwitch(datalen);
		}
		//发送状态匹配数据
		String data_matchState = currentElement.attributeValue("matchState");
		datalist.addData(data_matchState);
		accessPermit=false;
		dataSend();		
	}

	private void optionalDeal_called(){
		Log.d("MainLayer-216", "..");
		//默认control为false
		String control = currentElement.attributeValue("control");
		if (control == null) {
			control = "false";
		}
		// 获得服务头
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
			// ///////获取所有子元素
			List<?> sl = currentElement.elements();

			// //////////按钮添加
			for (int comNum=0;comNum<sl.size();comNum++) {
				Element son_element = (Element) sl.get(comNum);
				// 开关钮构建
				if ("开关钮".equals(son_element.attributeValue("param"))) {
					String value = son_element.getName();
					String bytenum = son_element.attributeValue("byte");
					String bitnum = son_element.attributeValue("bit");
					if (bytenum != null && bitnum != null) {
						int byteindex = Integer.parseInt(bytenum);
						int bitindex = Integer.parseInt(bitnum);
					
						RelativeLayout layoutInRow = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.component_laoutinrow, null);
						// 文本
						TextView text = (TextView) LayoutInflater.from(this).inflate(R.layout.component_optionaltextview, null);
						text.setText(value);
						//把文本放到左边
						LinearLayout leftarea = (LinearLayout) layoutInRow.findViewById(R.id.row_left);
						leftarea.addView(text);
						// 开关按钮
						Button button = (Button) LayoutInflater.from(this).inflate(R.layout.component_binarybutton, null);
						/*
						 * 按钮状态匹配
						 */
						if(stateValue[comNum]==0){
							button.setText("  关闭  ");
						}
						else{
							button.setText("  打开  ");
						}
						addSwitchEvent(button, byteindex, bitindex);
				
						if (control.equals("true")) {
							//把开关按钮放到中间
							LinearLayout centerarea = (LinearLayout) layoutInRow.findViewById(R.id.row_center);
							centerarea.addView(button);
							// 控制按钮
							ToggleButton tb = (ToggleButton) LayoutInflater.from(this).inflate(R.layout.component_togglebutton, null);
							tb.setText("不控制");
							tb.setTextOff("不控制");
							tb.setTextOn("控制中");
							addToggleButtonEvent(tb, byteindex, bitindex,
									bitindex);
							//把控制钮放到右边
							LinearLayout rightarea = (LinearLayout) layoutInRow.findViewById(R.id.row_right);
							rightarea.addView(tb);
						}
						else{
							//把开关按钮放到中间
							LinearLayout rightarea = (LinearLayout) layoutInRow.findViewById(R.id.row_right);
							rightarea.addView(button);
						}
						LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
						param.topMargin=dipToPx(this, 5);
						table1.addView(layoutInRow,param);
						//下划线
//						View divider = LayoutInflater.from(this).inflate(R.layout.component_divider, null);
//						table1.addView(divider);
						Log.d("MainLayer-231", "..");
					} else {
						warnDialog("xml配置文件属性有错!请核对属性byte&&bit！");
						return;
					}
				}
				/*
				 *  下拉单构建
				 */
				else if ("下拉单".equals(son_element.attributeValue("param"))) {
					String value = son_element.getName();
					Log.d("Mianlayer-371", value);
					if (son_element.attributeValue("byte") == null
							|| son_element.attributeValue("startbit") == null
							|| son_element.attributeValue("endbit") == null) {
						warnDialog("配置文件有错！请核对属性byte&&startbit&&endbit！");
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
					// 文本
					TextView text = (TextView) LayoutInflater.from(this).inflate(R.layout.component_optionaltextview, null);
					text.setText(value);
					//把文本放到左边
					LinearLayout leftarea = (LinearLayout) layoutInRow.findViewById(R.id.row_left);
					leftarea.addView(text);
					// 下拉菜单
					@SuppressWarnings("unchecked")
					List<Attribute> li = son_element.elements();
					// 显示
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
						//把下拉单放到中间
						LinearLayout centerarea = (LinearLayout) layoutInRow.findViewById(R.id.row_center);
						centerarea.addView(spinner);
						ToggleButton tb = (ToggleButton) LayoutInflater.from(this).inflate(R.layout.component_togglebutton, null);
						tb.setText("不控制");
						tb.setTextOff("不控制");
						tb.setTextOn("控制中");
						addToggleButtonEvent(tb, index, startbit, endbit);
						//把控制钮放到右边
						LinearLayout rightarea = (LinearLayout) layoutInRow.findViewById(R.id.row_right);
						rightarea.addView(tb);
					}
					else{
						//把下拉单放到右边
						LinearLayout rightarea = (LinearLayout) layoutInRow.findViewById(R.id.row_right);
						rightarea.addView(spinner);
					}
					table1.addView(layoutInRow);
				} 
			}
			linear.addView(table1);
			// 提交按钮 点击提交数据
			Button sur = (Button) LayoutInflater.from(this).inflate(R.layout.component_submitbutton, null);
			sur.setText("提交");
			sur.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// 发送数据
					byte[] bytedata = datalist.getByteData();
					try {
						sendStateContext = getXMLContext();
						if(ns!=null){
							ns.send(bytedata, 2);
							//界面后退
//							onBackPressed();
							showToast("提交成功！");
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
	 * 普通按钮 事件添加
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
	 * 添加组件 普通处理
	 */
	private void addComponent() {
		// ///////获取所有子元素
		List<?> sl = currentElement.elements();
		// //////////按钮添加
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
		//动画
		if(forward){
			forwordAnimation();
		}
		else{
			backAnimation();
		}
	}

	// 校对失败 界面
	private void showToast(String value) {
		// 提醒
		Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
	}

	/*
	 * 重写back操作
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (backEnable) {
			if (sonContext.size() != 0) {
				// 置为后退行为
				forward = false;
				// 上下文-1
				sonContext.remove(sonContext.size() - 1);
				invaliDate();
			} else {
				// 退出警告框
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setIcon(android.R.drawable.ic_dialog_info);
				dialog.setTitle("警告");
				dialog.setMessage("你确定要退出当前控制？");
				dialog.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								MainActivity.bluetoothState = MainActivity.NONE;
								MainActivity.vc_str.removeAllElements(); // 搜索到的设备列表清零
								MainActivity.text.setText("请重新搜索设备");
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
				dialog.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				dialog.show();
			}
		} else {
			Toast.makeText(MainLayer.this, "请等待……", Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * 处理后数据返回 对外接口
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
					//强制提醒UI更新  （子线程更新UI）
					SimpleAdapter a = (SimpleAdapter) listview
							.getAdapter();
					a.notifyDataSetChanged();
				}
			}
		} 
		else {
			if (mainlayer != null) {
				Log.d("MainLayer-277:", "回传数据显示");
				String context = getXMLContext();
				/*
				 * if判断   过滤掉由于xml生成器缺陷产生的垃圾数据项
				 */
				if (context.equals(head)) {
					Log.d("MainLayer-602", "回传数据显示2--" + des);
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
	 * 获得当前上下文
	 */
	private String getXMLContext() {
		String context = XML_ROOTCONTEXT;
		// ///得到Xpath
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
		// /转换 发送
		if (data != null) {
			// 将字符串转换成byte
			String[] datagroup = data.split(",");
			int size = datagroup.length;
			byte[] bytedata = new byte[size];
			for (int i = 0; i < size; i++) {
				bytedata[i] = (byte) Integer.parseInt(datagroup[i], 16);
			}
			try {
				// 保存发送时的上下文
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
	 *            显示文本
	 * @param type
	 *            1，及时发送 2，延时发送
	 */
	private void passwordInput(String text, final int type, final String service) {
		final View view = LayoutInflater.from(this).inflate(R.layout.component_edittext, null);

		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle("请输入密码")
				.setMessage(text)
				.setView(view)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						EditText edit = (EditText) view;
						String in = edit.getText().toString();
						if (in.length() == 8) {
							// 密码长度固定为4字节
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
//								// 加服务
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
//									// 服务
//									for (int i = 0; i < size; i++) {
//										bytedata2[i] = head[i];
//									}
//									// 数据
//									for (int i = size; i < 4 + size; i++) {
//										bytedata2[i] = bytedata[i - size];
//									}
//								}
//								try {
//									// 保存发送时的上下文
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
								// 加服务
								if (service != null) {
									ts = service + "," + ts;
								}
								datalist.addData(ts);
								dataSend();
							}
							else if (type == 2) {
								String ts = instring[0] + "," + instring[1]
										+ "," + instring[2] + "," + instring[3];
								//加服务
								if (service != null) {
									ts = service + "," + ts;
								}
								datalist.addData(ts);
								dataSend();
							}

						}
						// 密码长度不对
						else {
							passwordInput("密码长度不对，请重新输入", type, service);
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						// 后退
						onBackPressed();
					}
				}).create();
		dialog.show();
	}

	// 警告框
	private void warnDialog(String text) {
		AlertDialog dialog = new AlertDialog.Builder(this).setTitle("警告")
				.setMessage(text)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				}).create();
		dialog.show();
	}

	// 下拉框
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
					Msg myMsg = new Msg(Msg.CTRL, "掉线");
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
	 * ListView高度设置 放弃自身滑动属性
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
