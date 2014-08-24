package com.zuoguangxing.v_verkey;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class About_page extends Activity {
	private Button aboutback_btn;
	private Intent intent;
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_page); 
		aboutback_btn=(Button)findViewById(R.id.aboutback_bt);
		aboutback_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				intent = new Intent(About_page.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}
}
