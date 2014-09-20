package in.siet.secure.sgi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Utility {
	public static void RaiseToast(Context context,String msg,int len){
		Toast.makeText(context, msg, len==1?Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
	}
	public static void log(String tag,String txt){
		Log.d(tag,txt);
	}
}
