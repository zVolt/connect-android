package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

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
			Utility.RaiseToast(getApplicationContext(), "activity started"+count, false);
			bundle.putString("inBackground", "inBackgroundActivity");
			receiver.send(count, bundle);
			count++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(i == 5)
				{
				Utility.AddLines("hiii");				
				Utility.AddLines("hello");
				Utility.buildNotification(this,MainActivity.class,Utility.notification_msg_id,null,null);
				
				}
		}	
		this.stopSelf();
	}
	

	
	
	
	
	
}
