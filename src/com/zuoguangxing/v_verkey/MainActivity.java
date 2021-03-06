package com.zuoguangxing.v_verkey;

import userModule.User_page;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {
	Button about_btn;
	Button connect_btn;
	Button map_btn;
	Button user_btn;
	Intent intent;
	Intent sintent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 //隐藏标题   
	    requestWindowFeature(Window.FEATURE_NO_TITLE);  
	      
	    //定义全屏参数   
	    int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;  
	    //获得窗口对象   
	    Window myWindow = this.getWindow();  
	    //设置Flag标识   
	    myWindow.setFlags(flag, flag);  
		setContentView(R.layout.activity_main);
		about_btn=(Button)findViewById(R.id.about_btn);
		connect_btn=(Button)findViewById(R.id.connect_btn);
		map_btn=(Button)findViewById(R.id.map_btn);
		user_btn=(Button)findViewById(R.id.user_btn);
		sintent =new Intent(MainActivity.this,com.zuoguangxing.v_verkey.MyLocation.class);
		startService(sintent);
		//启动定位服务
		connect_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Intent intent= new Intent();        
			    intent.setAction("android.intent.action.VIEW");    
			    Uri content_url = Uri.parse("http://www.v-ver.com");   
			    intent.setData(content_url);  
			    startActivity(intent);
			}
		});
		about_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				intent = new Intent(MainActivity.this, About_page.class);
				startActivity(intent);
			}
		});
		user_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				intent = new Intent(MainActivity.this, User_page.class);
				startActivity(intent);
			}
		});
		map_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				intent = new Intent(MainActivity.this,Location_page.class);
				startActivity(intent);
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
