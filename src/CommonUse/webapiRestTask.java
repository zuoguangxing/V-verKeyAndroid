package CommonUse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;

import android.database.CursorJoiner.Result;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView.FindListener;

public class webapiRestTask extends AsyncTask<Void, Integer, Object> {

	private static final String TAG = "webapiRestTask";
	public interface ResponseCallback{
		public void onRequestSuccess(String response);
		public void onRequestError(Exception error);
	}
	public interface ProgressCallback{
		public void onProgressUpdate(int progress);
	}
	
	private HttpURLConnection mConnection;
	private String mFormBody;
	private File mUploadFile;
	private String mUploadFileName;
	
	private WeakReference<ResponseCallback> mResponseCallback;
	private WeakReference<ProgressCallback> mProgressCallback;
//	@Override
//	protected Result doInBackground(Params... arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	public webapiRestTask(HttpURLConnection connection)
	{
		mConnection = connection;
	}
	public void setFormBody(List<NameValuePair> formData)
	{
		if(formData == null)
		{
			mFormBody = null;
			return;
		}
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<formData.size();i++)
		{
			NameValuePair item = formData.get(i);
			sb.append(URLEncoder.encode(item.getName()));
			sb.append("=");
			sb.append(URLEncoder.encode(item.getValue()));
			if(i!=(formData.size()-1))
			{
				sb.append("&");
			}
		}
		mFormBody=sb.toString();
	}
	public void setUploadFile(File file,String filename)
	{
		mUploadFile =file;
		mUploadFileName = filename;
	}
	 public void setResponseCallback(ResponseCallback callback)
	 {
		 mResponseCallback = new WeakReference<webapiRestTask.ResponseCallback>(callback);
	 }
	 public void setProgressCallback(ProgressCallback callback)
	 {
		 mProgressCallback = new WeakReference<webapiRestTask.ProgressCallback>(callback);
	 }
	 private void writeMultipart(String boundary,String charset,OutputStream output,
			 boolean weiteContent) throws IOException{
		 BufferedWriter writer= null;
		 try
		 {
			 writer=new BufferedWriter(new OutputStreamWriter(output,Charset.forName(charset)),8192);
			 //发送表单数据组件
			 if(mFormBody!=null){
				 writer.write("--"+boundary);
				 writer.write("\r\n");
				 writer.write(
						 "Content-Disposition:form-data; name=\"parameters\"");
				 writer.write("\r\n");
				 writer.write("Content-Type: text/plain; charset="+charset);
				 writer.write("\r\n");
				 writer.write("\r\n");
				 if(weiteContent)
				 {
					 writer.write(mFormBody);
				 }
				 writer.write("\r\n");
				 writer.flush();
			 }
			 //发送二进制文件
			 writer.write("--"+boundary);
			 writer.write("\r\n");
			 writer.write(
					 "Content-Disposition:form-data; name=\""+
			 mUploadFileName+"\"; filename=\""+mUploadFile.getName()+"\"");
			 writer.write("\r\n");
			 writer.write("Content-Type:"+URLConnection.guessContentTypeFromName(mUploadFile.getName()));
			 writer.write("\r\n");
			 writer.write("Content-Transfer-Encoding: binary");
			 writer.write("\r\n");
			 writer.write("\r\n");
			 writer.flush();
			 if(weiteContent)
			 {
				 InputStream input= null;
				 try{
					 input= new FileInputStream(mUploadFile);
					 byte[] buffer = new byte[1024];
					 for(int length=0;(length=input.read(buffer))>0;){
						 output.write(buffer,0,length);
					 }
					 //不要关闭OutputStream
					 output.flush();
				 } catch (IOException e){
					 Log.w(TAG,e);
				 } finally{
					 if(input !=null){
						 try{
							 input.close();
						 } catch (IOException e){
						 }
					 }
				 }
			}
			// 这个回车换行标志着二进制数据块的结束
			 writer.write("\r\n");
			 writer.flush();
			
			 //multipart/form-data的结束
			 writer.write("--"+boundary+"--");
			 writer.write("\r\n");
			 writer.flush();
			} finally{
				if(writer != null){
					writer.close();
				}
			}
	 }
	 
	 private void writeFormData(String charset, OutputStream output) throws IOException{
		 try{
			 output.write(mFormBody.getBytes(charset));
			 output.flush();
		 } finally {
			 if (output != null){
				 output.close();
			 }
		 }
	 }
	 protected Object doInBackground(Void...  params){
		 //生成用来标示界限的随机字符串
		 String boundary = Long.toHexString(System.currentTimeMillis());
		 String charset = Charset.defaultCharset().displayName();
		 
		 try{
			 // 如果可以的话，创建输出流
			 if(mUploadFile != null)
			 {
				 //我们必须做一个复合请求
				mConnection.setRequestProperty("Content-Type", "multpart/form-data; boundary=" + boundary);
				
				// 计算extra元数据的大小
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				writeMultipart(boundary, charset,bos, false);
				byte[] extra = bos.toByteArray();
				int contentLength = extra.length;
				//将文件大小加载length上
				contentLength += mUploadFile.length();
				//如果存在表单体，把它加载length上
				if(mFormBody != null)
				{
				contentLength += mFormBody.length();
				}
				
				mConnection.setFixedLengthStreamingMode(contentLength);
			 } else if (mFormBody != null){
				 //这种情况下，只是发送表单数据
				 mConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset="+charset);
				 mConnection.setFixedLengthStreamingMode(mFormBody.length());
			 }
			 //这是第一次调用URLConnection, 它会真正执行网络IO操作。
			 //openConnection()执行的还是本地操作。
			 mConnection.connect();
			 
			 //如果可以的话（对于一个POST），创建输出流
			 if(mUploadFile != null){
				 OutputStream out = mConnection.getOutputStream();
				 writeMultipart(boundary, charset, out, true);
			 }else if (mFormBody != null){
				 OutputStream out = mConnection.getOutputStream();
				 writeFormData(charset, out);
			 }
			 // 获取响应数据
			 int status = mConnection.getResponseCode();
			 if (status >=300){
				 String message = mConnection.getResponseMessage();
				 return new HttpResponseException(status, message);
			 }
			 InputStream in = mConnection.getInputStream();
			 String encoding = mConnection.getContentEncoding();
			 int contentLength = mConnection.getContentLength();
			 if(encoding==null)
			 {
				 encoding="UTF-8";
			 }
			 BufferedReader reader = new BufferedReader(new InputStreamReader(in,encoding));
			 char[] buffer = new char[4096];
			 
			 StringBuilder sb = new StringBuilder();
			 int downloadedBytes = 0;
			 int len1=0;
			 while ((len1=reader.read(buffer))>0){
				 downloadedBytes +=len1;
				 publishProgress((downloadedBytes*100)/contentLength);
				 sb.append(buffer);
			 }
			return sb.toString();
			 }
		 catch (Exception e){
			 Log.w(TAG, e);
			 return e;
		 }finally{
			 if(mConnection != null)
			 {
				 mConnection.disconnect();
			 }
		 }
		 }
	 @Override
	 protected void onProgressUpdate(Integer... values){
	 if (mProgressCallback != null && mProgressCallback.get() != null){
		mProgressCallback.get().onProgressUpdate(values[0]);
	 }
}
	 @Override
	 protected void onPostExecute(Object result){
		 if (mResponseCallback != null && mResponseCallback.get() != null){
			 if(result instanceof String){
				 mResponseCallback.get().onRequestSuccess((String)result);
			 } else if (result instanceof Exception){
				 mResponseCallback.get().onRequestError((Exception)result);
			 } else {
				 mResponseCallback.get().onRequestError(new IOException("Unknown Error Contacting Host"));
			 }
		 }
	 }


}

