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
import com.baidu.location.BDNotifyListener;//假如用到位置提醒功能，需要import该类
//如果使用地理围栏功能，需要import如下类
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
	//用handler更新界面
	Handler handler=null;
	 /** 
     * 我的位置图层 
     */ 
	//导航引擎初始化
	private boolean mIsEngineInitSuccess = false;  
	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {  
	        public void engineInitSuccess() {  
	            //导航初始化是异步的，需要一小段时间，以这个标志来识别引擎是否初始化成功，为true时候才能发起导航  
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
//	        //初始化导航引擎  
//	        BaiduNaviManager.getInstance().  
//	            initEngine(this, getSdcardDir(), mNaviEngineInitListener, "我的key",null);  
//	}
	//导航模块
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
	        //初始化导航引擎  
		
	    BaiduNaviManager.getInstance().  
	            initEngine(this, getSdcardDir(), mNaviEngineInitListener, "jw8fKRILuYdTqCgCo3VSBYir",null);
//	        initEngine(this, getSdcardDir(), mNaviEngineInitListener, com.baidu.lbsapi.auth.LBSAuthManagerListener listener) 
	    //绑定服务时间
		mConnection = new ServiceConnection() {  
	        @Override  
	    public void onServiceConnected(ComponentName className, IBinder service) 
	        {// 已经绑定了LocalService，强转IBinder对象，调用方法得到LocalService对象  
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
	     //绑定服务结束
	    //地图模块
	    mBaiduMap = mMapView.getMap();
	    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);  
	    //mBaiduMap.setMyLocationEnabled(false);
        mCurrentMarker = BitmapDescriptorFactory  
		        .fromResource(R.drawable.dw_png);  
        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标） 
		MyLocationConfigeration config = new MyLocationConfigeration(com.baidu.mapapi.map.MyLocationConfigeration.LocationMode.FOLLOWING, true, mCurrentMarker);  
		mBaiduMap.setMyLocationConfigeration(config); 
	    mBaiduMap.setMyLocationEnabled(true);
	    //地图模块结束
	    //自动刷新功能
	    handler=new Handler();
	    autoflash= new MyThread();
	    autoflash.start();
	    //自动刷新功能结束
	    vverbase_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Route_map_page.LDtagetAdress=new LocationDate("V-ver基地",1,116.596587,38.088966);
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
//				// TODO 自动生成的方法存根
////				if(!(mService.Location==null))
////				{
////				if((mService.Location.getLocType()==61)||(mService.Location.getLocType()==65)||(mService.Location.getLocType()==161))
////				{
////				bdtextla.setText(Double.toString(mService.Location.getLatitude()));
////				bdtextlo.setText(Double.toString(mService.Location.getLongitude()));
////				
////				//定位图层开始
////				 // 开启定位图层   
////				 // 构造定位数据  
////			    MyLocationData locData = new MyLocationData.Builder()  
////			        .accuracy(mService.Location.getRadius())  
////			         //此处设置开发者获取到的方向信息，顺时针0-360  
////			        .direction(mService.Location.getdirection()).latitude(mService.Location.getLatitude())  
////			        .longitude(mService.Location.getLongitude()).build();
////			    // 设置定位数据  
////			    mBaiduMap.setMyLocationData(locData); 
////	   	    	AlertDialog AlertDialog=new AlertDialog.Builder(Location_page.this) .setTitle("标题")
////	                    .setMessage(Boolean.toString(mIsEngineInitSuccess))  
////	                    .setPositiveButton("确定", null)  
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
//	   	    			40.05087, 116.30142,"百度大厦",   
//	   	             39.90882, 116.39750,"北京天安门",  
//	   	             NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME,       //算路方式  
//	   	             true ,                                            //真实导航  
//	   	             BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策略  
//	   	             new OnStartNavigationListener() {                //跳转监听  
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
	    //回复
	}
//	protected void onStart()
//	{
//		AlertDialog AlertDialog=new AlertDialog.Builder(Location_page.this) .setTitle("标题")
//                .setMessage(Boolean.toString(mBound))  
//                .setPositiveButton("确定", null)  
//                .show();  
//	}
	 protected void onStop() {  
	        super.onStop();  
	        // 解绑Service，这样可以节约内存  
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
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
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
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
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
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause(); 
        autoflash.flag=false;
        }  
    //内部进程用于刷新定位位置
    class MyThread extends Thread {
   	public boolean flag = true;
   	public Exception xe;
   	MyLocationData locData;
   	DecimalFormat df = new DecimalFormat("#.000000");
   	Runnable runnableUi=new Runnable() {
		
		@Override
		public void run() {
			// TODO 自动生成的方法存根
			mBaiduMap.setMyLocationData(locData); 
			
			bdtextla.setText("纬度："+Double.toString(MyLocation.myLocationinfo.bdLatitude));
			bdtextlo.setText("经度："+Double.toString(MyLocation.myLocationinfo.bdLongitude));
			gjctextla.setText("纬度："+df.format(MyLocation.myLocationinfo.gjcLatitude));
			gjctextlo.setText("经度："+df.format(MyLocation.myLocationinfo.gjcLongitude));
			text_adress.setText("当前地理位置："+MyLocation.Location.getadress());
			if(MyLocation.Location.getLocType()==61)
			{
			text_statetype.setText("当前定位模式为61GPS定位");
			}
			else if(MyLocation.Location.getLocType()==161)
			{
				text_statetype.setText("当前定位模式为161网络定位");
			}
			else
			{
				text_statetype.setText("定位错误");
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
			//定位图层开始
			 // 开启定位图层   
			 // 构造定位数据  
		    locData = new MyLocationData.Builder()  
		        .accuracy(MyLocation.Location.getRadius())  
		         //此处设置开发者获取到的方向信息，顺时针0-360  
		        .direction(MyLocation.Location.getdirection()).latitude(MyLocation.Location.getLatitude())  
		        .longitude(MyLocation.Location.getLongitude()).build();
		    // 设置定位数据  
		    handler.post(runnableUi);
		    sleep(1000);
			}
			}
   	    	else
   	    	{
   	        sleep(500);
   	    	}
   	      } 
   	      catch (Exception e) {//捕获刷新异常 
   	    	  flag=false;
   	    	  xe=e;
   	        return;
   	      }
   	    }
   	  }
   	}
}


