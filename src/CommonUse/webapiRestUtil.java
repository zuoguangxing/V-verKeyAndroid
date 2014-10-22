package CommonUse;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.NameValuePair;

public class webapiRestUtil {
   public static final webapiRestTask obtainGetTask(String url)
   		throws MalformedURLException,IOException{
	HttpURLConnection connection =(HttpURLConnection)(new URL(url)).openConnection();
	connection.setReadTimeout(10000);
	connection.setConnectTimeout(15000);
	connection.setDoInput(true);
	
	webapiRestTask task =new webapiRestTask(connection);
	return task;
   }
   
   public static final webapiRestTask obtainFormPostTask(String url,List<NameValuePair> formData) throws MalformedURLException,IOException
   {
	   HttpURLConnection connection =(HttpURLConnection)(new URL(url)).openConnection();
		connection.setReadTimeout(10000);
		connection.setConnectTimeout(15000);
		connection.setDoOutput(true);
		
		webapiRestTask task =new webapiRestTask(connection);
		task.setFormBody(formData);
		
		return task;
   }
   public static final webapiRestTask obtainMultipartPostTask(String url,List<NameValuePair> formPart,File file,String filename) throws MalformedURLException,IOException
   {
	   HttpURLConnection connection =(HttpURLConnection)(new URL(url)).openConnection();
		connection.setReadTimeout(10000);
		connection.setConnectTimeout(15000);
		connection.setDoOutput(true);
		
		webapiRestTask task =new webapiRestTask(connection);
		task.setFormBody(formPart);
		task.setUploadFile(file, filename);
		return task;
   }
}
