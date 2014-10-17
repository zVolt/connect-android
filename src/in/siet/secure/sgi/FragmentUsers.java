package in.siet.secure.sgi;

import java.util.ArrayList;

import in.siet.secure.contants.Constants;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FragmentUsers extends Fragment{
	public static final String TAG="FragmentUsers";
	SharedPreferences sharedPreferences=null;
	ArrayList<String> users=new ArrayList<String>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_users, container,	false);
		sharedPreferences=getActivity().getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
		Utility.showProgressDialog(this.getActivity().getApplicationContext());
		fetch_all();
		Utility.hideProgressDialog();
		Utility.log(TAG,"onCreate");
		return rootView;
	}
	
	
	public void show_users(){
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity().getApplicationContext(),R.layout.list_item_users,users);
		ListView listview=(ListView)getActivity().findViewById(R.id.listViewUsers);
		listview.setAdapter(adapter);
		Utility.log(TAG,"show_users");
	}
	
	public void fetch_all(){
		Utility.log(TAG,"fetch_all");
		RequestParams params =new RequestParams();
		params.put(getString(R.string.web_prm_usr),Base64.encodeToString(sharedPreferences.getString(getString(R.string.user_id), null).getBytes(), Base64.DEFAULT));
		params.put(getString(R.string.web_prm_token),sharedPreferences.getString(getString(R.string.acess_token), null).trim());
		params.put(getString(R.string.web_prm_query_id), 1);
		AsyncHttpClient client=new AsyncHttpClient();
		client.get("http://"+Constants.SOCKET+"/SGI_webservice/query/type_resolver",params,new JsonHttpResponseHandler(){
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,JSONArray response) {
				int i=0,len=response.length();
				try{
					for(i=0;i<len;i++){
						users.add(response.getString(i));
					Utility.log("on sucess", users.toString());
					show_users();
					}
				}catch(Exception e){
					Utility.log("fetch_all",e.getLocalizedMessage()+"");
				}
				Utility.log("result",response.toString());
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,JSONObject response) {
				//just in case we receive a object instead of an array
				Utility.log(TAG,"onSocess object"+response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONArray errorResponse) {
				Utility.log("result","failure");
			}
		});
	}
}
