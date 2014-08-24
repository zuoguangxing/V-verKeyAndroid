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
 * һ��״̬��MyLocation.Location.getLocType()Ϊ׼
 * 61ΪGPS 161Ϊ���� �������ǻ���
 * ����
 * һ���Ǿ�̬����
 * һ����Bind����
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
		//��λ��������ģ��
		mLocationClient = new LocationClient(getApplicationContext());//����LocationClient��
	    mLocationClient.registerLocationListener( myListener );    //ע���������
	    option = new LocationClientOption();
	    option.setLocationMode(LocationMode.Hight_Accuracy);//���ö�λģʽ
	    option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
	    option.setScanSpan(10000);//���÷���λ����ļ��ʱ��Ϊ10000ms
	    option.setIsNeedAddress(true);//���صĶ�λ���������ַ��Ϣ
	    option.setNeedDeviceDirect(true);//���صĶ�λ��������ֻ���ͷ�ķ���
	    option.setOpenGps(true);
	    myLocationinfo=new LocationDate("�ҵ�λ��");
	    myLocationinfo=new LocationDate();
	    mLocationClient.setLocOption(option);
//      ��׼�������Զ��������
//	    atl =new AutoManagerLocation();
//	    atl.start();
	    //mLocationClient.start();
	  //��λ��������ģ�����
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
	//����ط���������λ������ʱ��
	public void setScanSpan(int arg0 )
	{
		option.setScanSpan(arg0);
		mLocationClient.setLocOption(option);
	}
	/***��������ṩ���ֹ�ͨ����
	 * һ���Ǿ�̬����
	 * һ����Bind����
	 * myLocationState 0����δ���� 1��������� 2����λ����  3Gps����
	 * -1 15s��δ����ź� -3��λ����δ�������߳��� -4 �Զ����̳���
	 * ���� ��λ������� ��Ӧ��
	 * 61 �� GPS��λ���
*62 �� ɨ�����϶�λ����ʧ�ܡ���ʱ��λ�����Ч��
*63 �� �����쳣��û�гɹ���������������󡣴�ʱ��λ�����Ч��
*65 �� ��λ����Ľ����
*66 �� ���߶�λ�����ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ��
*67 �� ���߶�λʧ�ܡ�ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ��
*68 �� ��������ʧ��ʱ�����ұ������߶�λʱ��Ӧ�ķ��ؽ��
*161�� ��ʾ���綨λ���
*162~167�� ����˶�λʧ��
*502��key��������
*505��key�����ڻ��߷Ƿ�
*601��key���񱻿������Լ�����
*602��key mcode��ƥ��
*501��700��key��֤ʧ��
	 */
	public static Exception e;
	public static MyLocationListener Location =(MyLocationListener)myListener;
	public static int myLocationState=0;
	public static LocationDate myLocationinfo;
    public class LocalBinder extends Binder {  
    	public MyLocation getService() {  
            // ����Activity��������Service����������Activity��Ϳɵ���Service���һЩ���÷����͹�������  
            return MyLocation.this;  
        }
    }  
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO �Զ����ɵķ������  
		setScanSpan(1010);
		return mBinder;
	}
	@Override
	public void unbindService(ServiceConnection conn) {
		// TODO �Զ����ɵķ������
		super.unbindService(conn);
		setScanSpan(10000);
	}
	//���⹵ͨ�������
	/**��ʱδ����,GPS�������Ƿǳ��ĵ�ģ�����˵�ⶫ��ʱ�俪���˲��ã�������Ӧ����һ��������ƣ������̼߳���Ƿ��Ѿ������GPSλ��
	*������Ǹ���ô������������GPS��λ��Ϣ������ʱ����������Ū��20s
	*���ⲿ���Ըı䣬����Ҫʵ��autoMangger��Ҫ�����ɣ�Ĭ�ϴ�,�������ź�ˢ��ʱ���Ϊ10s
	*/
	class AutoManagerLocation extends Thread
	{
		Boolean flag=true;
		int count=0;
		@Override
		public void run() {
			// TODO �Զ����ɵķ������
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
						//����״̬�ǻ��GPS�ź�
						setScanSpan(10000);
						flag=false;
					}
					else if(MyLocation.Location.getLocType()==161)
					{
						if (count>15)
						{
						setScanSpan(10000);
						MyLocation.myLocationState=2;
						//����״̬�ǻ������λ�ź�
						flag=false;
						}
				    }
					else
					{		
					if ((count>15)&&(count<60))
						{
						MyLocation.myLocationState=-1;
						//����״̬�ǳ�������15s��û�л���źţ����ܷ���������
						}
					else if(count>59)
					{
						MyLocation.myLocationState=MyLocation.Location.getLocType();
						mLocationClient.stop();
						//����״̬�ǳ�������60s��û�л���źţ����������񣬵���������Ϣ
					}
					}
					
				}
				else
				{
					if(count>15)
					{
					MyLocation.myLocationState=-3;
					//��λ�̳���δ�������߳���
					}
				}
				try {
					sleep(1000);
				} catch (Exception e) {
					// TODO �Զ����ɵ� catch ��
					MyLocation.e=e;
					MyLocation.myLocationState=-4;
					//�Զ�������̽��̳���
				}
			}
		}
	}
}
//�������ڴˣ�
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
		// TODO �Զ����ɵķ������
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
