package in.siet.secure.sgi;

import in.siet.secure.Util.User;
import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.UsersAdapter;
import in.siet.secure.contants.Constants;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FragmentUsers extends Fragment{
	public static final String TAG="in.siet.secure.sgi.FragmentUsers";
	SharedPreferences sharedPreferences=null;
	ArrayList<User> users=new ArrayList<User>();
	public int userType;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_users, container,false);
		sharedPreferences=getActivity().getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
		
	//	getActivity().setTitle(getResources().getStringArray(R.array.panel_options)[2]);		
		Utility.log(TAG,"onCreate"+userType);
		load();
		return rootView;
	}
	@Override
	public void onResume(){
		super.onResume();
		Utility.RaiseToast(getActivity(), "I am Users", 0);
	}
	public void load(){
		Utility.showProgressDialog(getActivity());
		fetch_all();
		Utility.hideProgressDialog();
	}
	public void show_users(JSONArray result,int size){
		Utility.log(TAG,result.toString());
		users.clear();
		for(int i=0;i<size;i++){
				try {		
						JSONObject tmpobj=result.getJSONObject(i);
						users.add(new User(tmpobj.getString("name"),tmpobj.getString("profile_img").replace("\\/", "/"),tmpobj.getString("department")));
						Utility.log(TAG,""+tmpobj.getString("name"));
				} catch (Exception e) {
					Utility.log(TAG, e.getLocalizedMessage());
				}
		}
		
		UsersAdapter adapter=new UsersAdapter(getActivity().getApplicationContext(),users);
		ListView listview=(ListView)getActivity().findViewById(R.id.listViewUsers);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new ItemClickListener());
		Utility.log(TAG,"show_users");
	}
	
	class ItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
			//add user to contacts. A db operation
			//(view.findViewById(R.id.ListItemUsersTextViewName)).toString();
			Utility.RaiseToast(getActivity(), ((TextView)(view.findViewById(R.id.ListItemUsersTextViewName))).getText().toString()+" is added to contacts", 0);
		}
		
	}
	
	
	public void fetch_all(){
		Utility.log(TAG,"fetch_all");
		
		RequestParams params =new RequestParams();
		params.put(getString(R.string.web_prm_usr),Base64.encodeToString(sharedPreferences.getString(getString(R.string.user_id), null).getBytes(), Base64.DEFAULT));
		params.put(getString(R.string.web_prm_token),sharedPreferences.getString(getString(R.string.acess_token), null).trim());
		params.put(getString(R.string.web_prm_query_id), userType);
		AsyncHttpClient client=new AsyncHttpClient();
		client.get("http://"+Constants.SOCKET+"/SGI_webservice/query/type_resolver",params,new JsonHttpResponseHandler(){
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,JSONArray response) {
				int len=response.length();
				try{
					Utility.log("on sucess", users.toString());
					show_users(response,len);
				}catch(Exception e){
					Utility.log("fetch_all",e.getLocalizedMessage());
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
				Utility.log(TAG,"failure");
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject errorResponse){
				Utility.log(TAG,"failure");
			}
		});
	}
}
