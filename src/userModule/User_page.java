package userModule;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;



import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.zuoguangxing.v_verkey.R;
import com.zuoguangxing.v_verkey.R.id;
import com.zuoguangxing.v_verkey.R.layout;

import CommonUse.*;
import CommonUse.webapiRestTask.ProgressCallback;
import CommonUse.webapiRestTask.ResponseCallback;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class User_page extends Activity 
implements ProgressCallback,ResponseCallback
{
	ProgressDialog mProgressDia;
	/*
	 * 这个0的状态表示本地有记录，这个1的状态表示要登录.
	 */
	private static int StateCode=0;
	public void onCreate(Bundle savedInstanceState)
	{
		 super.onCreate(savedInstanceState); 
	        //创建一个线性布局管理器   
//	     LinearLayout layout = new LinearLayout(this);  
//	     layout.setOrientation(LinearLayout.VERTICAL); 
//	     layout.setBackgroundResource(R.drawable.about_back);
//	     setContentView(R.layout.location_page);
		 String userinfo = FileSomething.ReadUserInfo("userInfoData",this);
		 JSONObject person=new JSONObject();
		 //这里进行用户数据的读取
		 if(userinfo==null)
		 {
			 StateCode=1;
		 }
		 else
		 {
			 try 
			 {
			 person =new JSONObject(userinfo);
			 String name =person.getString("Name");
			 if(name==""||name==null)
			 {
				 StateCode=1;
			 }
			 }
			 catch (Exception e)
			 {
				 StateCode=1;
			 }
		 }
		 //开始分配进入哪个界面
		 if(StateCode==1)
		 {
			 createLogin();
		 }
		 else 
		 {
			 createInfo(person);
		 }
	}

	/*
	 *创建信息界面 
	 */
	private Boolean createInfo(JSONObject person)
	{
		setContentView(R.layout.user_info_page);
		TextView textACT =(TextView)findViewById(R.id.info_user_act);
		TextView textID=(TextView)findViewById(R.id.info_user_id);
		TextView textPower=(TextView)findViewById(R.id.info_user_power);
		Button logoutBtn =(Button)findViewById(R.id.info_user_loginout_btn);
		logoutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				

				AlertDialog alertDialog = new AlertDialog.Builder(
						User_page.this)
						.setTitle("Logout")
						.setMessage("Are you sure？")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								deleteFile("userInfoData");
								createLogin();
							}
						})
						.setNegativeButton("BACK",
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog, int id) {
										dialog.cancel();
									}
								}).create();
				alertDialog.show();
			}
		});
		try {
			textACT.setText(person.getString("Name"));
			textID.setText(person.getString("ID"));
			textPower.setText(person.getString("power"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	/*
	 *创建登陆界面
	 */
	private Boolean createLogin()
	{
		setContentView(R.layout.user_login_page);	
		Button sumbit=(Button)findViewById(R.id.login_submit_btn);
		Button regiser=(Button)findViewById(R.id.login_register_btn);
		regiser.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(User_page.this, regiser_page.class));
			}
		});
		sumbit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String act=((EditText)findViewById(R.id.login_editText_act)).getText().toString();
				String pvd=((EditText)findViewById(R.id.login_editText_pvd)).getText().toString();
				pvd= CommonUse.CommonFunctions.MD5(pvd);
					//post请求
					List<NameValuePair> parameters = new ArrayList<NameValuePair>();
					parameters.add(new BasicNameValuePair("model","0"));
					parameters.add(new BasicNameValuePair("act",act));
					parameters.add(new BasicNameValuePair("pvd",pvd));
					webapiRestTask postTask;
					try {
						postTask = webapiRestUtil.obtainFormPostTask("http://hk.v-ver.com/api/ApiUser/", parameters);
						postTask.setProgressCallback(User_page.this);
						postTask.setResponseCallback(User_page.this);
						postTask.execute();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						AlertDialog alertDialog=new AlertDialog.Builder(User_page.this) .setTitle("error")
			            .setMessage("Please check the network Settings")  
			            .create();
			   	         alertDialog.show();
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					AlertDialog alertDialog=new AlertDialog.Builder(User_page.this) .setTitle("")
//				            .setMessage(e.toString())  
//				            .create();
//				   	alertDialog.show();

					//向用户显示进度
					mProgressDia= ProgressDialog.show(User_page.this,"submiting....","Waiting For Results...",true);

				}
		});
		return true;
	}
	@Override
	public void onRequestSuccess(String response) {
		// TODO Auto-generated method stub
		try {
		final JSONObject person;
		if(mProgressDia!=null){
			mProgressDia.dismiss();
		}
		String name="";
		FileSomething.writeUserInfo("userInfoData",response, User_page.this);
		person =new JSONObject(response);
		name =person.getString("Name");
	    StateCode=1;
	   	AlertDialog alertDialog=new AlertDialog.Builder(User_page.this) .setTitle("Connect Success!")
	            .setMessage("Welcome to V-ver!  "+name) 
	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						createInfo(person);
					}
				})
	            .create();
	   	alertDialog.show();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	@Override
	public void onRequestError(Exception error) {
		// TODO Auto-generated method stub
		if(mProgressDia!=null){
			mProgressDia.dismiss();
		}
	   	AlertDialog alertDialog=new AlertDialog.Builder(User_page.this) .setTitle("Login ")
	            .setMessage("Login error！act and pvd。")
	            .create();
	   	alertDialog.show();
	}
	@Override
	public void onProgressUpdate(int progress) {
		// TODO Auto-generated method stub
		if(progress >=0){
			if(mProgressDia!=null){
				mProgressDia.dismiss();
				mProgressDia=null;
			}
		}
	}
}
