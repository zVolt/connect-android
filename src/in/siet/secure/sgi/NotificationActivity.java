package in.siet.secure.sgi;

import in.siet.secure.contants.Constants;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public class NotificationActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Fragment fragment = getFragmentManager().findFragmentByTag(
				FragmentDetailNotification.TAG);
		if (fragment == null) {
			fragment = new FragmentDetailNotification();
		}

		fragment.setArguments(getIntent().getBundleExtra(
				Constants.INTENT_EXTRA.BUNDLE_NAME));

		getFragmentManager()
				.beginTransaction()
				.replace(R.id.notification_fragment_container, fragment,
						FragmentDetailNotification.TAG).commit();
	}
}
