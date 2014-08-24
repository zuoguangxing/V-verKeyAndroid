package CommonUse;

import com.baidu.nplatform.comapi.basestruct.GeoPoint;

/***
 * ���캯����һ��������1�Ļ���˵��������bd09�������0��˵��������gcj02
 * @author wangji
 *
 */
public class LocationDate {
	public String name="null";
	public double gjcLongitude=0;
	public double gjcLatitude=0;
	public double bdLongitude=0;
	public double bdLatitude=0;
	public LocationDate() {
		// TODO �Զ����ɵĹ��캯�����
	}
	public LocationDate(String name)
	{
		this.name=name;
	}
	public LocationDate(String name, int judge, double Longitude ,double Latitude) {
		// TODO �Զ����ɵĹ��캯�����
		this.name=name;
		if(judge ==0)
		{
		this.gjcLongitude=Longitude;
		this.gjcLatitude=Latitude;
		CommonFunctions.gcj_to_bd(this);
		}
		else if (judge==1)
		{
		this.bdLatitude=Latitude;
		this.bdLongitude=Longitude;
		CommonFunctions.bd_to_gcj(this);
		}
	}
	public void setBdLocation(double Longitude ,double Latitude)
	{
		this.bdLatitude=Latitude;
		this.bdLongitude=Longitude;
		CommonFunctions.bd_to_gcj(this);
	}
}
