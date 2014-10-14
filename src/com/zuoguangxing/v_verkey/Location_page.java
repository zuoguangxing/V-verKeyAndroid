package com.zuoguangxing.v_verkey;

import java.text.DecimalFormat;
import java.util.concurrent.RunnableFuture;

import javax.net.ssl.SSLEngineResult.HandshakeStatus;

import CommonUse.LocationDate;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;//�����õ�λ�����ѹ��ܣ���Ҫimport����
//���ʹ�õ���Χ�����ܣ���Ҫimport������
import com.baidu.location.BDGeofence;
import com.baidu.location.BDLocationStatusCodes;
import com.baidu.location.GeofenceClient;
import com.baidu.location.GeofenceClient.OnAddBDGeofencesResultListener;
import com.baidu.location.GeofenceClient.OnGeofenceTriggerListener;
import com.baidu.location.GeofenceClient.OnRemoveBDGeofencesResultListener;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.baidu.navisdk.util.common.CoordinateTransformUtil;
import com.baidu.nplatform.comapi.basestruct.GeoPoint;
import com.baidu.nplatform.comapi.basestruct.Point;
import com.zuoguangxing.v_verkey.MyLocation.LocalBinder;

public class Location_page extends Activity {
	MyLocation mService=null;  
	boolean mBound = false;  
//	public LocationClient mLocationClient = null;
//	public BDLocationListener myListener = new MyLocationListener();
//	public MyLocationListener Listener =(MyLocationListener)myListener;
	public Button MapLD_bt;
	public Button vverbase_btn;
	public Button LocData_btn;
	public TextView bdtextla ;
	public TextView bdtextlo ;
	public TextView gjctextla ;
	public TextView gjctextlo ;
	public TextView text_adress ;
	public TextView text_statetype ;
	public MapView mMapView = null;  
	MyThread autoflash;
	BaiduMap  mBaiduMap =null;
	BitmapDescriptor mCurrentMarker;
	//��handler���½���
	Handler handler=null;
	 /** 
     * �ҵ�λ��ͼ�� 
     */ 
	//���������ʼ��
	private boolean mIsEngineInitSuccess = false;  
	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {  
	        public void engineInitSuccess() {  
	            //������ʼ�����첽�ģ���ҪһС��ʱ�䣬�������־��ʶ�������Ƿ��ʼ���ɹ���Ϊtrueʱ����ܷ��𵼺�  
	            mIsEngineInitSuccess = true;  
	        }  
	        public void engineInitStart() {  
	        }  
	 
	        public void engineInitFail() {  
	        }  
	    }; 
	private String getSdcardDir() {  
	        if (Environment.getExternalStorageState().equalsIgnoreCase(  
	                Environment.MEDIA_MOUNTED)) {  
	            return Environment.getExternalStorageDirectory().toString();  
	        }  
	        return null;  
	    }       
//	public void onCreate(Bundle saveInstance) {  
//	        super.onCreate(saveInstance);  
//	        //��ʼ����������  
//	        BaiduNaviManager.getInstance().  
//	            initEngine(this, getSdcardDir(), mNaviEngineInitListener, "�ҵ�key",null);  
//	}
	//����ģ��
	private ServiceConnection mConnection;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.location_page);
		MapLD_bt=(Button)findViewById(R.id.mapLD_bt);
		vverbase_btn=(Button)findViewById(R.id.vverbase_btn);
		LocData_btn=(Button)findViewById(R.id.LocData_btn);
		bdtextla =(TextView)findViewById(R.id.bdlatitude_text);
		bdtextlo =(TextView)findViewById(R.id.bdlongitude_text);
		gjctextla =(TextView)findViewById(R.id.gjclatitude_text);
		gjctextlo =(TextView)findViewById(R.id.gjclongitude_text);
		mMapView = (MapView) findViewById(R.id.bmapView);  
		text_adress =(TextView)findViewById(R.id.adress_text);
		text_statetype =(TextView)findViewById(R.id.state_text);
	        //��ʼ����������  
		
	    BaiduNaviManager.getInstance().  
	            initEngine(this, getSdcardDir(), mNaviEngineInitListener, "jw8fKRILuYdTqCgCo3VSBYir",null);
//	        initEngine(this, getSdcardDir(), mNaviEngineInitListener, com.baidu.lbsapi.auth.LBSAuthManagerListener listener) 
	    //�󶨷���ʱ��
		mConnection = new ServiceConnection() {  
	        @Override  
	    public void onServiceConnected(ComponentName className, IBinder service) 
	        {// �Ѿ�����LocalService��ǿתIBinder���󣬵��÷����õ�LocalService����  
	            LocalBinder binder = (LocalBinder) service;  
	            mService = binder.getService();  
	            mBound = true;  
	        }  
	        @Override  
	    public void onServiceDisconnected(ComponentName arg0) {  
	            mBound = false;  
	        }  
	    };
		 Intent intent = new Intent(Location_page.this, MyLocation.class);  
	     bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
	     //�󶨷������
	    //��ͼģ��
	    mBaiduMap = mMapView.getMap();
	    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);  
	    //mBaiduMap.setMyLocationEnabled(false);
        mCurrentMarker = BitmapDescriptorFactory  
		        .fromResource(R.drawable.dw_png);  
        // ���ö�λͼ������ã���λģʽ���Ƿ���������Ϣ���û��Զ��嶨λͼ�꣩ 
		MyLocationConfigeration config = new MyLocationConfigeration(com.baidu.mapapi.map.MyLocationConfigeration.LocationMode.FOLLOWING, true, mCurrentMarker);  
		mBaiduMap.setMyLocationConfigeration(config); 
	    mBaiduMap.setMyLocationEnabled(true);
	    //��ͼģ�����
	    //�Զ�ˢ�¹���
	    handler=new Handler();
	    autoflash= new MyThread();
	    autoflash.start();
	    //�Զ�ˢ�¹��ܽ���
	    vverbase_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO �Զ����ɵķ������
				Route_map_page.LDtagetAdress=new LocationDate("V-ver����",1,116.596587,38.088966);
	    		Intent intent = new Intent(Location_page.this, Route_map_page.class);
				startActivity(intent);
			}
		});
//	    MapLD_bt1.setOnClickListener(new OnClickListener()
//	    {
//	    	public void onClick(View arg0) {
//	    		Intent intent = new Intent(Location_page.this, RoutePlan_page.class);
//				startActivity(intent);
//	    	}
//	    	}
//	    );
//	    LocData_btn.setOnClickListener(new OnClickListener()
//	    {
//	    	public void onClick(View arg0) {
//	    		Intent intent = new Intent(Location_page.this, vloc_list_page.class);
//				startActivity(intent);
//	    	}
//	    	}
//	    );
//	   MapLD_bt.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				// TODO �Զ����ɵķ������
////				if(!(mService.Location==null))
////				{
////				if((mService.Location.getLocType()==61)||(mService.Location.getLocType()==65)||(mService.Location.getLocType()==161))
////				{
////				bdtextla.setText(Double.toString(mService.Location.getLatitude()));
////				bdtextlo.setText(Double.toString(mService.Location.getLongitude()));
////				
////				//��λͼ�㿪ʼ
////				 // ������λͼ��   
////				 // ���춨λ����  
////			    MyLocationData locData = new MyLocationData.Builder()  
////			        .accuracy(mService.Location.getRadius())  
////			         //�˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360  
////			        .direction(mService.Location.getdirection()).latitude(mService.Location.getLatitude())  
////			        .longitude(mService.Location.getLongitude()).build();
////			    // ���ö�λ����  
////			    mBaiduMap.setMyLocationData(locData); 
////	   	    	AlertDialog AlertDialog=new AlertDialog.Builder(Location_page.this) .setTitle("����")
////	                    .setMessage(Boolean.toString(mIsEngineInitSuccess))  
////	                    .setPositiveButton("ȷ��", null)  
////	                    .show(); 
//				if(mIsEngineInitSuccess)
//				{
//					//com.baidu.nplatform.comapi.basestruct.GeoPoint ptStart;
//					
////			        com.baidu.nplatform.comapi.basestruct.GeoPoint ptGCJStart = 
////			                CoordinateTransformUtil.transferBD09ToGCJ02((int)(ptStart.getLongitudeE6() / 1e6), (int)(ptStart.getLatitudeE6() / 1e6));
////			        com.baidu.nplatform.comapi.basestruct.GeoPoint ptGCJStop = 
////			                CoordinateTransformUtil.transferBD09ToGCJ02(ptStop.getLongitudeE6() / 1e6, ptStop.getLatitudeE6() / 1e6);
//	   	    	BaiduNaviManager.getInstance().launchNavigator(Location_page.this,   
//	   	    			40.05087, 116.30142,"�ٶȴ���",   
//	   	             39.90882, 116.39750,"�����찲��",  
//	   	             NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME,       //��·��ʽ  
//	   	             true ,                                            //��ʵ����  
//	   	             BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //�����߲���  
//	   	             new OnStartNavigationListener() {                //��ת����  
//	   	  
//	   	                 @Override  
//	   	                 public void onJumpToNavigator(Bundle configParams) {  
//	   	                     Intent intent = new Intent(Location_page.this, KNavigation_page.class);  
//	   	                     intent.putExtras(configParams);  
//	   	                     startActivity(intent);  
//	   	                 }  
//	   	                 @Override  
//	   	                 public void onJumpToDownloader() {  
//	   	                 }  
//	   	             });
//				}
////				}
////			}
//			}
//		});
	    //�ظ�
	}
//	protected void onStart()
//	{
//		AlertDialog AlertDialog=new AlertDialog.Builder(Location_page.this) .setTitle("����")
//                .setMessage(Boolean.toString(mBound))  
//                .setPositiveButton("ȷ��", null)  
//                .show();  
//	}
	 protected void onStop() {  
	        super.onStop();  
	        // ���Service���������Խ�Լ�ڴ�  
	        if (mBound) {  
	            unbindService(mConnection);  
	            mBound = false;  
	        }  
	        autoflash.flag=false;
	    }  
	 protected void onStart()
	 {
		 super.onStart();
	   if (mBound==false) {   
	            Intent intent = new Intent(Location_page.this, MyLocation.class);  
	   	     bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
	   	     mBound= true;
	   }
	   autoflash.flag=true;
	 }
	protected void onDestroy() { 
        super.onDestroy();  
        if (mBound) {  
            unbindService(mConnection);  
            mBound = false;
        }
        autoflash.flag=false;
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        if (mBound==false) {   
            Intent intent = new Intent(Location_page.this, MyLocation.class);  
   	     bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
   	     mBound= true;
        }
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume(); 
        autoflash.flag=true;
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        if (mBound) {  
            unbindService(mConnection);  
            mBound = false;  
        }
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause(); 
        autoflash.flag=false;
        }  
    //�ڲ���������ˢ�¶�λλ��
    class MyThread extends Thread {
   	public boolean flag = true;
   	public Exception xe;
   	MyLocationData locData;
   	DecimalFormat df = new DecimalFormat("#.000000");
   	Runnable runnableUi=new Runnable() {
		
		@Override
		public void run() {
			// TODO �Զ����ɵķ������
			mBaiduMap.setMyLocationData(locData); 
			
			bdtextla.setText("γ�ȣ�"+Double.toString(MyLocation.myLocationinfo.bdLatitude));
			bdtextlo.setText("���ȣ�"+Double.toString(MyLocation.myLocationinfo.bdLongitude));
			gjctextla.setText("γ�ȣ�"+df.format(MyLocation.myLocationinfo.gjcLatitude));
			gjctextlo.setText("���ȣ�"+df.format(MyLocation.myLocationinfo.gjcLongitude));
			text_adress.setText("��ǰ����λ�ã�"+MyLocation.Location.getadress());
			if(MyLocation.Location.getLocType()==61)
			{
			text_statetype.setText("��ǰ��λģʽΪ61GPS��λ");
			}
			else if(MyLocation.Location.getLocType()==161)
			{
				text_statetype.setText("��ǰ��λģʽΪ161���綨λ");
			}
			else
			{
				text_statetype.setText("��λ����");
			}
		}
	};
   	  public void run(){
   	    while(flag){
   	      try {
   	    	if(!(mService==null))
			{
			if((MyLocation.Location.getLocType()==61)||(MyLocation.Location.getLocType()==65)||(mService.Location.getLocType()==161))
			{
			//��λͼ�㿪ʼ
			 // ������λͼ��   
			 // ���춨λ����  
		    locData = new MyLocationData.Builder()  
		        .accuracy(MyLocation.Location.getRadius())  
		         //�˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360  
		        .direction(MyLocation.Location.getdirection()).latitude(MyLocation.Location.getLatitude())  
		        .longitude(MyLocation.Location.getLongitude()).build();
		    // ���ö�λ����  
		    handler.post(runnableUi);
		    sleep(1000);
			}
			}
   	    	else
   	    	{
   	        sleep(500);
   	    	}
   	      } 
   	      catch (Exception e) {//����ˢ���쳣 
   	    	  flag=false;
   	    	  xe=e;
   	        return;
   	      }
   	    }
   	  }
   	}
}


