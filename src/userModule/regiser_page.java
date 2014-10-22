package userModule;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.zuoguangxing.v_verkey.R;
import com.zuoguangxing.v_verkey.R.id;
import com.zuoguangxing.v_verkey.R.layout;

import CommonUse.webapiRestTask;
import CommonUse.webapiRestUtil;
import CommonUse.webapiRestTask.ProgressCallback;
import CommonUse.webapiRestTask.ResponseCallback;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class regiser_page extends Activity
implements ProgressCallback,ResponseCallback{
	ProgressDialog mProgressDia;
	public void onCreate(Bundle savedInstanceState)
	{
		 super.onCreate(savedInstanceState); 
			setContentView(R.layout.user_register_page);
			final EditText et_act= (EditText)findViewById(R.id.regiser_editText_act);
			final EditText et_pvd1= (EditText)findViewById(R.id.regiser_editText_pvd1);
			final EditText et_pvd2= (EditText)findViewById(R.id.regiser_editText_pvd2);
			final EditText et_eml= (EditText)findViewById(R.id.regiser_editText_eml);
			final EditText et_stc= (EditText)findViewById(R.id.regiser_editText_stc);
		Button btn_submit = (Button) findViewById(R.id.register_submit_btn);
		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!et_pvd1.getText().toString().equals(et_pvd2.getText().toString())) 
				{
					AlertDialog alertDialog = new AlertDialog.Builder(
							regiser_page.this)
							.setTitle("input error")
							.setMessage("Enter the password twice inconsistent!"+et_pvd1.getText().toString()+"  "+et_pvd2.getText().toString())
							.setNegativeButton("BACK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									}).create();
					alertDialog.show();
				}
				else
				{
					try
					{
					List<NameValuePair> parameters = new ArrayList<NameValuePair>();
					parameters.add(new BasicNameValuePair("model","1"));
					parameters.add(new BasicNameValuePair("act",et_act.getText().toString()));
					parameters.add(new BasicNameValuePair("pvd",CommonUse.CommonFunctions.MD5(et_pvd1.getText().toString())));
					parameters.add(new BasicNameValuePair("email",et_eml.getText().toString()));
					parameters.add(new BasicNameValuePair("stc",et_stc.getText().toString()));
					webapiRestTask postTask = webapiRestUtil.obtainFormPostTask("http://hk.v-ver.com/api/ApiUser/", parameters);
					postTask.setProgressCallback(regiser_page.this);
					postTask.setResponseCallback(regiser_page.this);
					postTask.execute();
					mProgressDia= ProgressDialog.show(regiser_page.this,"submiting....","Waiting For Results...",true);
					}
					catch(Exception e)
					{
						AlertDialog alertDialog = new AlertDialog.Builder(
								regiser_page.this)
								.setTitle("Registration error")
								.setMessage("Please check the input, or contact the zgx")
								.setNegativeButton("BACK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog, int id) {
												dialog.cancel();
											}
										}).create();
						alertDialog.show();
					}
				}
			}
		});
	}

	@Override
	public void onRequestSuccess(String response) {
		// TODO Auto-generated method stub
		if(mProgressDia!=null){
			mProgressDia.dismiss();
		}
	   	AlertDialog alertDialog=new AlertDialog.Builder(regiser_page.this) .setTitle("Register Success!")
	            .setMessage("Successful registration, return to the login interface£¡") 
	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						startActivity(new Intent(regiser_page.this, User_page.class));
					}
				})
	            .create();
	   	alertDialog.show();
	}
	@Override
	public void onRequestError(Exception error) {
		// TODO Auto-generated method stub
		if(mProgressDia!=null){
			mProgressDia.dismiss();
		}
		AlertDialog alertDialog = new AlertDialog.Builder(
				regiser_page.this)
				.setTitle("Registration error")
				.setMessage("Please check the input, or contact the zgx")
				.setNegativeButton("BACK",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).create();
		alertDialog.show();
	}

	@Override
	public void onProgressUpdate(int progress) {
		// TODO Auto-generated method stub
		if(mProgressDia!=null){
			mProgressDia.dismiss();
		}
	}
}
