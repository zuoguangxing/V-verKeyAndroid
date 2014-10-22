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
			 //���ͱ��������
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
			 //���Ͷ������ļ�
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
					 //��Ҫ�ر�OutputStream
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
			// ����س����б�־�Ŷ��������ݿ�Ľ���
			 writer.write("\r\n");
			 writer.flush();
			
			 //multipart/form-data�Ľ���
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
		 //����������ʾ���޵�����ַ���
		 String boundary = Long.toHexString(System.currentTimeMillis());
		 String charset = Charset.defaultCharset().displayName();
		 
		 try{
			 // ������ԵĻ������������
			 if(mUploadFile != null)
			 {
				 //���Ǳ�����һ����������
				mConnection.setRequestProperty("Content-Type", "multpart/form-data; boundary=" + boundary);
				
				// ����extraԪ���ݵĴ�С
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				writeMultipart(boundary, charset,bos, false);
				byte[] extra = bos.toByteArray();
				int contentLength = extra.length;
				//���ļ���С����length��
				contentLength += mUploadFile.length();
				//������ڱ��壬��������length��
				if(mFormBody != null)
				{
				contentLength += mFormBody.length();
				}
				
				mConnection.setFixedLengthStreamingMode(contentLength);
			 } else if (mFormBody != null){
				 //��������£�ֻ�Ƿ��ͱ�����
				 mConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset="+charset);
				 mConnection.setFixedLengthStreamingMode(mFormBody.length());
			 }
			 //���ǵ�һ�ε���URLConnection, ��������ִ������IO������
			 //openConnection()ִ�еĻ��Ǳ��ز�����
			 mConnection.connect();
			 
			 //������ԵĻ�������һ��POST�������������
			 if(mUploadFile != null){
				 OutputStream out = mConnection.getOutputStream();
				 writeMultipart(boundary, charset, out, true);
			 }else if (mFormBody != null){
				 OutputStream out = mConnection.getOutputStream();
				 writeFormData(charset, out);
			 }
			 // ��ȡ��Ӧ����
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

