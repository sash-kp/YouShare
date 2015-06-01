package com.skpsash.youshare;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyService extends Service{
	private static final String TAG = "MyService";
	String result = "";
	public static int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	HttpEntity resEntity;
	NotificationCompat.Builder builder;
	@Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
 
    @Override
    public void onCreate() {
//        Toast.makeText(this, "MyService Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
    }
 
    @Override
    public void onStart(Intent intent, int startId) {
//        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");    
    }
 
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	while(true)
        {

    		new DoBackgroundTask().execute();
//            Toast.makeText(this, "My Service Started with on startcommand", Toast.LENGTH_LONG).show();
            return START_STICKY;            
        }
    	
    	
 

    }
 
    @Override
    public void onDestroy() {
//        Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
 
    }
    
    private class DoBackgroundTask extends AsyncTask<Void, String, String>{

		@Override
		protected String doInBackground(Void... params) {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                	PackageInfo pInfo = MyService.this.getPackageManager().getPackageInfo(MyService.this.getPackageName(), PackageManager.GET_META_DATA);
                	int versionCodeint = pInfo.versionCode;
                	String versionCode = String.valueOf(versionCodeint);
                	Log.d("VersionCode",versionCode);
                    URL url = new URL("http://www.google.com/");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(15000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                    	 String SetServerString = "";
        	             String urlString = "http://skpsash.site50.net/Versionupdate/versioncode.php";
        	             HttpClient client = new DefaultHttpClient();
        	             HttpPost post = new HttpPost(urlString);
        	             MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        	             /* setting a HttpMultipartMode */
        	             builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        	             builder.addPart("VersionCode", new StringBody(versionCode,ContentType.TEXT_PLAIN));
        	             HttpEntity reqEntity = builder.build();
        	             post.setEntity(reqEntity);
        				 org.apache.http.client.ResponseHandler<String> responseHandler = new org.apache.http.impl.client.BasicResponseHandler();
        				 SetServerString = client.execute(post, responseHandler);
        				 
        				 Log.d("setserverstring",SetServerString);
        				 if(SetServerString.equals("1")){
        					 result = "No update available.";     
//        					 Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        				 }
        				 
        				 if(SetServerString.equals("2")){
        					 result = "Update available.";
        					
        				 }
        				 
                    }
                
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
			
			return result;
		}
		
		@Override
	    protected void onPostExecute(String result) {
			
			if(result.equals("Update available.")){
				sendNotification();
			}
//			else{
//				Toast.makeText(getApplicationContext(), "No update", Toast.LENGTH_LONG).show();
//			}
			Log.d("Result",result);	
			MyService.this.stopSelf();			
//			new Handler().postDelayed(new Runnable() {
//		        public void run() {
//		        	new DoBackgroundTask().execute();
//		        }
//		    }, 10000);
	    }
    	
    }
    
    @SuppressLint("NewApi")
   	private void sendNotification() {
           mNotificationManager = (NotificationManager)
                   this.getSystemService(Context.NOTIFICATION_SERVICE);
           long pattern[]={0,2000,500,2000};	
   		Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
           NotificationCompat.Builder mBuilder =
                   new NotificationCompat.Builder(this)
           .setContentTitle("YouShare Update")
           .setSmallIcon(R.drawable.notify)
           .setContentText("Click here to download now.")
           .setTicker("YouShare Update Available Now.")
           .setWhen(System.currentTimeMillis())
           .setAutoCancel(true)
           .setVibrate(pattern)
           .setSound(uri);
           String url = "http://skpsash.wirenode.mobi";
           Intent intent = new Intent(Intent.ACTION_VIEW);
           intent.setData(Uri.parse(url));
           PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                   intent, 0);
           mBuilder.setContentIntent(contentIntent);
           mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
       }

}
