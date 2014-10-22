package CommonUse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import CommonUse.webapiRestTask.ProgressCallback;
import android.app.Activity;
import android.content.Context;
import android.text.StaticLayout;

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
    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }
}



