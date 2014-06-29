package com.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

public class IndexPageActivity extends Activity {

	private long firstTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_indexpage);
		ImageButton imageButton = (ImageButton) super
				.findViewById(R.id.linkcar);

		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(IndexPageActivity.this, MainActivity.class);
				IndexPageActivity.this.startActivity(intent);
				IndexPageActivity.this.overridePendingTransition(
						R.anim.forword_push_left_in,
						R.anim.forword_push_left_out);
			}
		});
	}

	//˫���˳�
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) { // ������ΰ���ʱ��������2�룬���˳�
				Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();

				firstTime = secondTime;// ����firstTime
				return true;
			} else { // ���ΰ���С��2��ʱ���˳�Ӧ��
				this.finish();
			}
			break;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.exit(0);
	}
}
