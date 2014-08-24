package CommonUse;
public class CommonFunctions { 
	public CommonFunctions() {
		// TODO 自动生成的构造函数存根
	}
	static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
	public static int gcj_to_bd(LocationDate gjcxb)
	{
	    double x = gjcxb.gjcLongitude, y = gjcxb.gjcLatitude;
	    double z =Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
	    double theta =Math.atan2(y, x) + 0.000003 *Math.cos(x * x_pi);
	    gjcxb.bdLongitude=z *Math.cos(theta) + 0.0065;
	    gjcxb.bdLatitude=z *Math.sin(theta) + 0.006;
	    return 1;
//	    bd_lon = z * cos(theta) + 0.0065;
//	    bd_lat = z * sin(theta) + 0.006;
	}
	public static int bd_to_gcj(LocationDate bdxb)
	{
		double x = bdxb.bdLongitude - 0.0065, y = bdxb.bdLatitude- 0.006;
	    double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
	    double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
	    bdxb.gjcLongitude = z * Math.cos(theta);
	    bdxb.gjcLatitude = z * Math.sin(theta);
	    return 1;
	}
}
//	void bd_encrypt(double gg_lat, double gg_lon, double &bd_lat, double &bd_lon)
//	{
//	    double x = gg_lon, y = gg_lat;
//	    double z = sqrt(x * x + y * y) + 0.00002 * sin(y * x_pi);
//	    double theta = atan2(y, x) + 0.000003 * cos(x * x_pi);
//	    bd_lon = z * cos(theta) + 0.0065;
//	    bd_lat = z * sin(theta) + 0.006;
//	}
//
//	void bd_decrypt(double bd_lat, double bd_lon, double &gg_lat, double &gg_lon)
//	{
//	    double x = bd_lon - 0.0065, y = bd_lat - 0.006;
//	    double z = sqrt(x * x + y * y) - 0.00002 * sin(y * x_pi);
//	    double theta = atan2(y, x) - 0.000003 * cos(x * x_pi);
//	    gg_lon = z * cos(theta);
//	    gg_lat = z * sin(theta);
//	}


