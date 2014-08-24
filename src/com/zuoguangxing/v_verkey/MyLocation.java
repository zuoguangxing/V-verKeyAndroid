package com.zuoguangxing.v_verkey;

import CommonUse.LocationDate;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.nplatform.comapi.basestruct.GeoPoint;

/***
 * 一切状态已MyLocation.Location.getLocType()为准
 * 61为GPS 161为网格 其他都是坏的
 * 对外
 * 一种是静态方法
 * 一种是Bind机制
 * @author wangji
 *
 */
public class MyLocation extends Service {
	public LocationClient mLocationClient = null;
	public IBinder mBinder;
	AutoManagerLocation atl;
	LocationClientOption option;
	public static BDLocationListener myListener = new MyLocationListener();
	@Override
	public void onCreate()
	{
		super.onCreate();
		mBinder = new LocalBinder();
		//定位设置载入模块
		mLocationClient = new LocationClient(getApplicationContext());//声明LocationClient类
	    mLocationClient.registerLocationListener( myListener );    //注册监听函数
	    option = new LocationClientOption();
	    option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
	    option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
	    option.setScanSpan(10000);//设置发起定位请求的间隔时间为10000ms
	    option.setIsNeedAddress(true);//返回的定位结果包含地址信息
	    option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
	    option.setOpenGps(true);
	    myLocationinfo=new LocationDate("我的位置");
	    myLocationinfo=new LocationDate();
	    mLocationClient.setLocOption(option);
//      不准备启用自动管理进程
//	    atl =new AutoManagerLocation();
//	    atl.start();
	    //mLocationClient.start();
	  //定位设置载入模块结束
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		myLocationState=0;
		mLocationClient.stop();
	}
	public void onStop()
	{
		mLocationClient.stop();
		myLocationState=0;
	}
	@Override
    public int  onStartCommand(Intent intent,int flags,int startId)
    {
    	mLocationClient.start();
    	myLocationState=1;
    	return super.onStartCommand(intent, flags, startId);
    }
	//这个地方可以设置位置请求时间
	public void setScanSpan(int arg0 )
	{
		option.setScanSpan(arg0);
		mLocationClient.setLocOption(option);
	}
	/***这里对外提供两种沟通方法
	 * 一种是静态方法
	 * 一种是Bind机制
	 * myLocationState 0代表未开启 1代表服务开启 2网格定位数据  3Gps数据
	 * -1 15s内未获得信号 -3定位程序未启动或者出错 -4 自动进程出错
	 * 其他 定位错误号码 对应的
	 * 61 ： GPS定位结果
*62 ： 扫描整合定位依据失败。此时定位结果无效。
*63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。
*65 ： 定位缓存的结果。
*66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
*67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果
*68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
*161： 表示网络定位结果
*162~167： 服务端定位失败
*502：key参数错误
*505：key不存在或者非法
*601：key服务被开发者自己禁用
*602：key mcode不匹配
*501～700：key验证失败
	 */
	public static Exception e;
	public static MyLocationListener Location =(MyLocationListener)myListener;
	public static int myLocationState=0;
	public static LocationDate myLocationinfo;
    public class LocalBinder extends Binder {  
    	public MyLocation getService() {  
            // 返回Activity所关联的Service对象，这样在Activity里，就可调用Service里的一些公用方法和公用属性  
            return MyLocation.this;  
        }
    }  
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自动生成的方法存根  
		setScanSpan(1010);
		return mBinder;
	}
	@Override
	public void unbindService(ServiceConnection conn) {
		// TODO 自动生成的方法存根
		super.unbindService(conn);
		setScanSpan(10000);
	}
	//对外沟通方面结束
	/**暂时未启用,GPS这玩意是非常耗电的，所以说这东西时间开多了不好，所以我应该有一个管理机制，就是线程检测是否已经获得了GPS位置
	*这里就是搞这么个玩意如果获得GPS定位信息，就暂时把请求速率弄到20s
	*在外部可以改变，而且要实行autoMangger需要获得许可，默认打开,如果获得信号刷新时间改为10s
	*/
	class AutoManagerLocation extends Thread
	{
		Boolean flag=true;
		int count=0;
		@Override
		public void run() {
			// TODO 自动生成的方法存根
			super.run();
			MyLocation.myLocationState=72;
			flag=true;
			while(flag)
			{
				count++;
				if(!(MyLocation.Location==null))
				{
					if(MyLocation.Location.getLocType()==61)
					{
						MyLocation.myLocationState=3;
						//现在状态是获得GPS信号
						setScanSpan(10000);
						flag=false;
					}
					else if(MyLocation.Location.getLocType()==161)
					{
						if (count>15)
						{
						setScanSpan(10000);
						MyLocation.myLocationState=2;
						//现在状态是获得网格定位信号
						flag=false;
						}
				    }
					else
					{		
					if ((count>15)&&(count<60))
						{
						MyLocation.myLocationState=-1;
						//现在状态是出现了在15s内没有获得信号，可能服务器出错。
						}
					else if(count>59)
					{
						MyLocation.myLocationState=MyLocation.Location.getLocType();
						mLocationClient.stop();
						//现在状态是出现了在60s内没有获得信号，请重启服务，弹出错误信息
					}
					}
					
				}
				else
				{
					if(count>15)
					{
					MyLocation.myLocationState=-3;
					//定位程程序未启动或者出错！
					}
				}
				try {
					sleep(1000);
				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					MyLocation.e=e;
					MyLocation.myLocationState=-4;
					//自动管理进程进程出错！
				}
			}
		}
	}
}
//监听类在此！
class MyLocationListener implements BDLocationListener {

	public static double BDLatitude=0;
	public static double BDLongitude=0;
	public float BDRadius=0;
	public float BDdirection=0;
	public int LocType=1180;
	public String adress;
	@Override
	public void onReceiveLocation(BDLocation arg0)
	{
		// TODO 自动生成的方法存根
		BDLatitude=arg0.getLatitude();
		BDLongitude=arg0.getLongitude();
		BDRadius =arg0.getRadius();
		BDdirection=arg0.getDirection();
		LocType=arg0.getLocType();
		adress=arg0.getAddrStr();
		MyLocation.myLocationinfo.setBdLocation(BDLongitude,BDLatitude);
	}
	public float getRadius()
	{
		return BDRadius;
	}
	public double  getLatitude()
	{
		return BDLatitude;
	}
	public double  getLongitude()
	{
		return BDLongitude;
	}
	public float getdirection()
	{
		return BDdirection;
	}
	public String getadress()
	{
		return adress;
	}
	public int getLocType()
	{
		return LocType;
	}
}
