package CommonUse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class FileSomething {
	public static boolean writeUserInfo(String filename,String content,Activity nowActivity)
	{
		try {
			FileOutputStream mOutput = nowActivity.openFileOutput(filename, Activity.MODE_PRIVATE);
		     mOutput.write(content.getBytes());
		     mOutput.close();
		     return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	public static String ReadUserInfo(String filename,Activity nowActivity)
	{
		try {
			FileInputStream mInput = nowActivity.openFileInput(filename);
			byte[] data = new byte[128];
			mInput.read(data);
			mInput.close();
		     return new String(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
}
