package in.siet.secure.sgi;

import java.security.MessageDigest;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Utility {
	private final static String TAG="in.siet.secure.sgi.Utility";
	private static ProgressDialog progress_dialog;
	public static void RaiseToast(Context context,String msg,int len){
		Toast.makeText(context, msg, len==1?Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
	}
	public static void log(String tag,String txt){
		Log.d(tag,txt);
	}
	public static void showProgressDialog(Context context){
		try{
			progress_dialog=ProgressDialog.show(context, null, "Please wait..", true, false);
		}catch(Exception ex){
			log(TAG,"showProgressDialog "+ex.getMessage());
		}
	}
	public static void hideProgressDialog(){
		try{
			progress_dialog.dismiss();
		}catch(Exception ex){
			log(TAG,"showProgressDialog "+ex.getMessage());
		}
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
}
