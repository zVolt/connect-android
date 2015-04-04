package in.siet.secure.Util;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

public class MyJsonHttpResponseHandler extends JsonHttpResponseHandler {
	private static String TAG = "in.siet.secure.Util.MyJsonHttpResponseHandler";
	private static String RESPONSE = " response:";
	private static String SUCCESS = "success:";
	private static String FAILURE = " failure:";
	private static String THROWABLE = " throwable:";
	
	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
		commonTask();
		Utility.log(TAG, SUCCESS + statusCode + RESPONSE + response);
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
		commonTask();
		Utility.log(TAG, SUCCESS + statusCode + RESPONSE + response);
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers,
			String responseString) {
		commonTask();
		Utility.log(TAG, SUCCESS + statusCode + RESPONSE + responseString);
	}

	@Override
	public void onFailure(int statusCode, Header[] headers,
			String responseString, Throwable throwable) {
		commonTask();
		Utility.log(TAG, FAILURE + statusCode + THROWABLE + throwable.getLocalizedMessage() +RESPONSE + responseString);
	}

	public void onFailure(int statusCode, Header[] headers,
			Throwable throwable, JSONArray errorResponse) {
		commonTask();
		Utility.log(TAG, FAILURE + statusCode + THROWABLE + throwable.getLocalizedMessage() +RESPONSE + errorResponse);
	}

	@Override
	public void onFailure(int statusCode, Header[] headers,
			Throwable throwable, JSONObject errorResponse) {
		commonTask();
		Utility.log(TAG, FAILURE + statusCode + THROWABLE + throwable.getLocalizedMessage() +RESPONSE + errorResponse);
	}

	/**
	 * define the task that you want to do in all success and failure methods
	 * that are not overridden by you
	 */
	public void commonTask() {
	}
}
