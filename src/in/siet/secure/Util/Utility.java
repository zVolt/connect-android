package in.siet.secure.Util;

import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;
import in.siet.secure.sgi.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class Utility {
	private final static String TAG="in.siet.secure.sgi.Utility";
	private static ProgressDialog progress_dialog;
	
	public static final int notification_msg_id=1;						/* INTEGER VARIABLE TO DEFINE THE ID OF NOTIFICAITON TO MESSAGE */
	public static String notification_msg_text[]=new String[100];		/* STRING TO HOLD THE TEXT OF THE NOTIFICATION TO BE RAISED FOR INCOMING MESSAGE */
	public static Boolean notification_msg_active=false;				/* BOOLEAN VARIABLE TO CHECK WHETHER NOTIFICATION IS ACTIVE IN THE NOTIFICATION DRAWER OR NOT */
	
	public static void RaiseToast(Context context,String msg,boolean for_long_time){
		Toast.makeText(context, msg, for_long_time?Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
	}
	
	public static void log(String tag,String txt){
		Log.d(tag,txt);
	}
	public static void showProgressDialog(Context context){
		try{
			progress_dialog=ProgressDialog.show(context, null, "Please wait..", true, false);
			Utility.log(TAG, "progress is OK");
		}catch(Exception ex){
			log(TAG,"showProgressDialog "+ex.getMessage());
		}
	}
	public static void hideProgressDialog(){
		try{
			progress_dialog.dismiss();
		}catch(Exception ex){
			log(TAG,"hideProgressDialog "+ex.getMessage());
		}
	}
	public static String getUserImage(String id){
		//return 
		return "http://www.secure.siet.in/images/std_photo/"+id.toLowerCase().trim()+".jpg";
	}
	public static String sha1(String input){
		try{
	        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
	        byte[] result = mDigest.digest(input.getBytes());
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < result.length; i++) {
	            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        return sb.toString();
        }
		catch(Exception ex){
			log("sha1", ex.getMessage());
        	return null;
        }
    }
	public static String encode(String str){
		return Base64.encodeToString(str.getBytes(),Base64.DEFAULT);
	}
	public static boolean isConnected(Context context){
		ConnectivityManager cm =
		        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}
	
	 /*
	  * FUNCTION TO BUILD AND RAISE NOTIFICATION IN THE NOTIFICAION DRAWER
	  */
	public static void buildNotification(Context context,Class<?> resultantclass,int id,String title,String text) {
		if(title==null)
			title="New notification";
		if(text==null)
			text="You have a new notification";
		NotificationCompat.Builder mBuilder = NotificationBuilder(context,
				R.drawable.ic_action_chats_white,
				title, 
				text);		    
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle("BigTitle");
		for (int i=0; i < notification_msg_text.length; i++) {
			if(notification_msg_active.equals(null))
				break;
		    inboxStyle.addLine(notification_msg_text[i]);
		}
		mBuilder.setStyle(inboxStyle);
		Intent resultIntent = new Intent(context,resultantclass);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);				
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); 
		mNotifyMgr.notify(id, mBuilder.build());	
		notification_msg_active=true;	 
	}
	/*
	 * FUNCTION TO CREATE NOTIFICATION USING NotificationCompat.Builder 
	 * AND RETURN IT TO buildNotification FUNCTION
	 */
	public static NotificationCompat.Builder NotificationBuilder(Context context,int icon,String title,String text) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(icon)
	    .setContentTitle(title)
	    .setContentText(text)
	    .setDefaults(Notification.DEFAULT_ALL);
		return mBuilder;	
	}
	
	/*
	 * FUNCTION TO ADD TEXT IN THE notification_msg_text FOR 
	 * EXPANDABLE NOTIFICATION TO BE DISPLAYED IN THE NOTOFICATION DRAWER
	 * set the text in the notification according to length of the array
	 * add only 10-15 lines...
	 */
	public static void AddLines(String txt) {
		int i=0;
		while(notification_msg_text[i]!=null){
				i++;
			}
		notification_msg_text[i]=txt;		
	}
	
	/*
	 * FUCTION TO CANCEL THE MESSAGE PRESENT IN THE NOTIFICATION
	 *  DRAWER AND TO CLEAR THE TEXT OF notification_msg_text
	 */
	public static void CancelMessageNotification(Context context) {
		for(int i=0 ; i<100 ; i++)
			notification_msg_text[i]=null;
		notification_msg_active=false;
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.cancel(notification_msg_id);
	}
	
	public static class DownloadFile extends AsyncTask<String, Integer, Boolean>{
		int id;
		@Override
		protected Boolean doInBackground(String... arg0) {
			id=new Integer(arg0[2]);
			try{
			URL url=new URL(arg0[0]);
			String filename=arg0[1];
			URLConnection conection = url.openConnection();
			conection.connect();
			InputStream inputStream = new BufferedInputStream(url.openStream(),1024);
			File file=new File(Constants.pathToApp);
			file.mkdirs();
			OutputStream fileOutput = new FileOutputStream(new File(file,filename));
			byte buffer[] = new byte[1024];
			int bufferLength = 0;
			 while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
	                fileOutput.write(buffer, 0, bufferLength);
	        }
			 fileOutput.close();
			return true;
			}
			catch(Exception e){
				Utility.log(TAG,""+e.getLocalizedMessage());
				return false;
			}
			
		}
		@Override
		protected void onPreExecute() {
			
		}
		@Override 
		protected void onPostExecute(Boolean result){
			if(result){
				Utility.log(TAG, "download done");
				SQLiteDatabase db=new DbHelper(DbHelper.context).getWritableDatabase();
				db.execSQL("update files set state=1 where _id="+id);
				db.close();
			}
			else
				Utility.log(TAG, "download failed");
		}
	}
}
