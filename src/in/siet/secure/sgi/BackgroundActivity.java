package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;

public class BackgroundActivity extends IntentService {


	//Intent localintent = new Intent(ConstantsBackground.BROADCAST_ACTION).putExtra(ConstantsBackground.EXTENDED_DATA_STATUS,true);
	
	
	public BackgroundActivity() {
		super(BackgroundActivity.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Utility.log("BAckgroundAvtivity", "onHandleIntent");		
		final ResultReceiver receiver=intent.getParcelableExtra("receiver");
		int count=Integer.parseInt(intent.getStringExtra("count"));
		
		Bundle bundle=new Bundle();
		
		receiver.send(count,Bundle.EMPTY);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		count++;
		for(int i = 0 ; i<10 ; i++ ) {
			Utility.RaiseToast(getApplicationContext(), "activity started"+count, 0);
			bundle.putString("inBackground", "inBackgroundActivity");
			receiver.send(count, bundle);
			count++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(i == 5)
				buildNotification();
		}	
		this.stopSelf();
	}
	

	public void buildNotification() {
		NotificationCompat.Builder mBuilder = notificationBuilder(this,
				R.drawable.ic_action_chats_white,
				"New notification", 
				"created a new notification!");		    
		Intent resultIntent = new Intent(this, MainActivity.class);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		
		NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotifyMgr.notify(1, mBuilder.build());

	}

	public NotificationCompat.Builder notificationBuilder(Context context,int icon,String title,String text) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(icon)
	    .setContentTitle(title)
	    .setContentText(text);
		return mBuilder;	
	}
	
	
	
	
}
