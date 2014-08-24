package CommonUse;

import com.baidu.nplatform.comapi.basestruct.GeoPoint;

/***
 * 构造函数第一个数字是1的话，说明坐标是bd09，如果是0，说明坐标是gcj02
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
		// TODO 自动生成的构造函数存根
	}
	public LocationDate(String name)
	{
		this.name=name;
	}
	public LocationDate(String name, int judge, double Longitude ,double Latitude) {
		// TODO 自动生成的构造函数存根
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
