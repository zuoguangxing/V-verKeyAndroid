package com.zuoguangxing.v_verkey;

import java.util.ArrayList;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.CommonParams.Const.ModelName;
import com.baidu.navisdk.CommonParams.NL_Net_Mode;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.mapcontrol.MapParams.Const.LayerMode;
import com.baidu.navisdk.comapi.routeguide.RouteGuideParams.RGLocationMode;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.routeplan.IRouteResultObserver;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.baidu.navisdk.model.NaviDataEngine;
import com.baidu.navisdk.model.RoutePlanModel;
import com.baidu.navisdk.model.datastruct.RoutePlanNode;
import com.baidu.navisdk.ui.routeguide.BNavConfig;
import com.baidu.navisdk.ui.routeguide.BNavigator;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;

import CommonUse.CommonValue;
import CommonUse.LocationDate;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class Route_map_page extends Activity {
	Handler handler=null;
	public static LocationDate LDtagetAdress;
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
	class RouteTd extends Thread
	{
		Boolean flag=true;
		Runnable runnableUi=new Runnable() {
			
			@Override
			public void run() {
				// TODO �Զ����ɵķ������
				route_plan();
			}
		};
		public void run()
		{
			while(flag)
			{
				if(adressLa==0)
				{
					
				}
				else
				{
					flag=false;
					handler.post(runnableUi);
				}
				try {
					sleep(200);
				} catch (InterruptedException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}
			}
		}
	}
	
	private MapGLSurfaceView mMapView = null;  
	private RoutePlanModel mRoutePlanModel = null;  
	private Button KN_btn ;
	private double adressLo;
	private double adressLa;
	GeoCoder mSearch;
	OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {  
	    public void onGetGeoCodeResult(GeoCodeResult result) {  
	        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	            //û�м��������  
	        	adressLo=0;
	        	adressLa=0;
	        }  
	        adressLa=result.getLocation().latitude;
	        adressLo=result.getLocation().longitude;
	        LDtagetAdress.setBdLocation(adressLo, adressLa);
	        //��ȡ���������  
	    }  
	 
	    @Override  
	    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {  
	        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	            //û���ҵ��������  
	        }  
	        //��ȡ������������  
	    }  
	};
	private IRouteResultObserver mRouteResultObserver = new IRouteResultObserver() {  
 
            @Override  
                        public void onRoutePlanYawingSuccess() {  
                // TODO Auto-generated method stub  
 
            }  
 
            @Override  
            public void onRoutePlanYawingFail() {  
                // TODO Auto-generated method stub  
 
            }  
 
            @Override  
            public void onRoutePlanSuccess() {  
                // TODO Auto-generated method stub  
                BNMapController.getInstance().setLayerMode(  
                        LayerMode.MAP_LAYER_MODE_ROUTE_DETAIL);  
                mRoutePlanModel = (RoutePlanModel) NaviDataEngine.getInstance()  
                        .getModel(ModelName.ROUTE_PLAN);  
            }  
 
            @Override  
            public void onRoutePlanFail() {  
                // TODO Auto-generated method stub  
            }  
 
            @Override  
            public void onRoutePlanCanceled() {  
                // TODO Auto-generated method stub  
            }  
 
            @Override  
            public void onRoutePlanStart() {  
                // TODO Auto-generated method stub  
 
            }  
 
        };  
	public void onCreate(Bundle savedInstance) {  
	        super.onCreate(savedInstance); 
			SDKInitializer.initialize(getApplicationContext()); 
	        setContentView(R.layout.route_map_page);
	        BaiduNaviManager.getInstance().  
            initEngine(this, getSdcardDir(), mNaviEngineInitListener, CommonValue.BDKey,null); 
	        adressLa=0;
	        adressLo=0;
	        mMapView = BaiduNaviManager.getInstance().createNMapView(this);
	        KN_btn =(Button) findViewById(R.id.kn_btn);
	        //�о�һ��
	        handler=new Handler();
	        if(LDtagetAdress.gjcLatitude==0)
	        {
	        try
	        {
	        mSearch = GeoCoder.newInstance();
	        mSearch.setOnGetGeoCodeResultListener(listener);
	        mSearch.geocode(new GeoCodeOption()  
	        .city("����")  
	        .address(LDtagetAdress.name));
	        //mSearch.destroy();
	        BNRoutePlaner.getInstance().setRouteResultObserver(mRouteResultObserver);
	        }
	        catch(Exception e)
	        {
	   	    	AlertDialog AlertDialog=new AlertDialog.Builder(Route_map_page.this) .setTitle("����")
	   	                .setMessage(e.toString())  
	   	                .setPositiveButton("ȷ��", null)  
	   	                .show(); 
	        }
	        RouteTd rtd=new RouteTd();
	        rtd.start();
	        }
	        else
	        {
	        	route_plan();
	        }
	//Ψһ���Ǹ����Ȱ�ť
	    KN_btn.setOnClickListener(new OnClickListener() {
			
			@Override
	public void onClick(View arg0) {
				// TODO �Զ����ɵķ������
//				if(adressLa==0)
//				{
//			   	    	AlertDialog AlertDialog=new AlertDialog.Builder(Route_map_page.this) .setTitle("����")
//			   	                .setMessage("λ�û�û�鵽��")  
//			   	                .setPositiveButton("ȷ��", null)  
//			   	                .show(); 
//				}
//				else
//				{
//		   	    	AlertDialog AlertDialog=new AlertDialog.Builder(Route_map_page.this) .setTitle("����")
//		   	                .setMessage(Integer.toString((int)(List_view_page.myLoclo*1E5))+"&&"+Integer.toString((int)(List_view_page.myLocla*1E5)))  
//		   	                .setPositiveButton("ȷ��", null)  
//		   	                .show(); 
//				}
				startNavi(true);
			}
		});
	}
	@Override  
    public void onPause() {  
        super.onPause();  
        BNRoutePlaner.getInstance().setRouteResultObserver(null);  
        ((ViewGroup) (findViewById(R.id.map_content))).removeAllViews();  
        BNMapController.getInstance().onPause();  
    }  
    @Override  
    public void onResume() {  
        super.onResume();  
        ((ViewGroup) (findViewById(R.id.map_content))).addView(mMapView);  
        BNMapController.getInstance().onResume();  
    }
   public void route_plan()
   {
       RoutePlanNode startNode = new RoutePlanNode((int)(MyLocation.myLocationinfo.gjcLatitude*1E5),(int)(MyLocation.myLocationinfo.gjcLongitude*1E5), 
               RoutePlanNode.FROM_MY_POSITION, "�ҵ�λ��", "�ҵ�λ��");    
RoutePlanNode endNode = new RoutePlanNode((int)(LDtagetAdress.gjcLatitude*1E5), (int)(LDtagetAdress.gjcLongitude*1E5),    
               RoutePlanNode.FROM_MAP_POINT,LDtagetAdress.name, LDtagetAdress.name);  
//�����յ���ӵ�nodeList  
ArrayList<RoutePlanNode>  nodeList = new ArrayList<RoutePlanNode>(2);  
nodeList.add(startNode);  
nodeList.add(endNode);  
BNRoutePlaner.getInstance().setObserver(new RoutePlanObserver(this, null));  
//������·��ʽ  
BNRoutePlaner.getInstance().setCalcMode(NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME);  
// ������·����ص�  
BNRoutePlaner.getInstance().setRouteResultObserver(mRouteResultObserver);  
// �������յ㲢��·  
boolean ret = BNRoutePlaner.getInstance().setPointsToCalcRoute(  
               nodeList,NL_Net_Mode.NL_Net_Mode_OnLine);  
if(!ret){  
   Toast.makeText(this, "�滮ʧ��", Toast.LENGTH_SHORT).show();  
}
   }
    private void startNavi(boolean isReal) {  
        if (mRoutePlanModel == null) {  
            Toast.makeText(this, "������·��", Toast.LENGTH_LONG).show();  
            return;  
        }  
        // ��ȡ·�߹滮������  
        RoutePlanNode startNode = mRoutePlanModel.getStartNode();  
        // ��ȡ·�߹滮����յ�  
        RoutePlanNode endNode = mRoutePlanModel.getEndNode();  
        if (null == startNode || null == endNode) {  
            return;  
        }  
        // ��ȡ·�߹滮��·ģʽ  
        int calcMode = BNRoutePlaner.getInstance().getCalcMode();  
        Bundle bundle = new Bundle();  
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_VIEW_MODE,  
                BNavigator.CONFIG_VIEW_MODE_INFLATE_MAP);  
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_CALCROUTE_DONE,  
                BNavigator.CONFIG_CLACROUTE_DONE);  
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_START_X,  
                startNode.getLongitudeE6());  
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_START_Y,  
                startNode.getLatitudeE6());  
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_END_X, endNode.getLongitudeE6());  
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_END_Y, endNode.getLatitudeE6());  
        bundle.putString(BNavConfig.KEY_ROUTEGUIDE_START_NAME,  
                mRoutePlanModel.getStartName(this, false));  
        bundle.putString(BNavConfig.KEY_ROUTEGUIDE_END_NAME,  
                mRoutePlanModel.getEndName(this, false));  
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_CALCROUTE_MODE, calcMode);  
        if (!isReal) {  
            // ģ�⵼��  
            bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_LOCATE_MODE,  
                    RGLocationMode.NE_Locate_Mode_RouteDemoGPS);  
        } else {  
            // GPS ����  
            bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_LOCATE_MODE,  
                    RGLocationMode.NE_Locate_Mode_GPS);  
        }  
     
        Intent intent = new Intent(Route_map_page.this, KNavigation_page.class);  
        intent.putExtras(bundle);  
        startActivity(intent);  
    }
	}

