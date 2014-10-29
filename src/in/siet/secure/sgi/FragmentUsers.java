package in.siet.secure.sgi;

import in.siet.secure.Util.FilterOptions;
import in.siet.secure.Util.User;
import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.UsersAdapter;
import in.siet.secure.contants.Constants;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private static ArrayList<User> users=new ArrayList<User>();
	private static UsersAdapter adapter;
	public static ListView listview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		adapter=new UsersAdapter(getActivity(),users);
		sharedPreferences=getActivity().getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
		View rootView = inflater.inflate(R.layout.fragment_users, container,false);
		//Utility.log(TAG,"onCreate"+FilterOptions.USER_TYPE);
		setHasOptionsMenu(true);
		Utility.log(TAG, "onCreateViewCalled");
		listview=(ListView)rootView.findViewById(R.id.listViewUsers);
		listview.setAdapter(adapter);
		listview.setEmptyView(rootView.findViewById(R.id.test_view_empty_list));
		listview.setOnItemClickListener(new ItemClickListener());
		return rootView;
	}
	@Override
	public void onStart(){
		super.onStart();
		load();
	}
	@Override
	public void onResume(){
		super.onResume();
		((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.fragemnt_title_users);
		((MainActivity)getActivity()).getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ic_action_add_user_white));
		//load();
	}
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_users_fragment, menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId()==R.id.action_filter){
			MainActivity.show.show(getFragmentManager(), TAG+"UsersCategoryDialog");
			return true;
		}
		return false;
	}
	public void load(){ //called by filter dialog
		listview.setVisibility(View.GONE);
		Utility.showProgressDialog(getActivity());
		fetch_all();
	}
	
	public static void refresh(){
		if(adapter!=null)
			adapter.notifyDataSetChanged();
		else
			Utility.log(TAG+" refresh()", "adapter is null");
	}
	public static void setData(ArrayList<User> data){
		
		if(adapter==null)
			Utility.log(TAG+" setData()", "adapter is null");
		else{
			adapter.clear();
			adapter.addAll(data);
			
		}
	}
	public void show_users(JSONArray result,int size){
		
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
		RequestParams params =new RequestParams();
		params.put(getString(R.string.web_prm_usr),Base64.encodeToString(sharedPreferences.getString(getString(R.string.user_id), null).getBytes(), Base64.DEFAULT));
		params.put(getString(R.string.web_prm_token),Base64.encodeToString(sharedPreferences.getString(getString(R.string.acess_token), null).trim().getBytes(), Base64.DEFAULT));
		params.put(getString(R.string.web_prm_query_user_type), FilterOptions.STUDENT);
		params.put(getString(R.string.web_prm_query_year), FilterOptions.YEAR);
		params.put(getString(R.string.web_prm_query_department), FilterOptions.DEPARTMENT);
		AsyncHttpClient client=new AsyncHttpClient();
		client.get("http://"+Constants.SOCKET+"/SGI_webservice/query/type_resolver",params,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,JSONArray response) {
					new FillData().execute(response);
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers,JSONObject response) {
				//just in case we receive a object instead of an array
				Utility.log(TAG+" onSucess()",response.toString());
				Utility.hideProgressDialog();
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONArray errorResponse) {
				Utility.log(TAG,"failure");
				Utility.hideProgressDialog();
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,Throwable throwable, JSONObject errorResponse){
				Utility.log(TAG,"failure");
				Utility.hideProgressDialog();
			}
		});
	}
	private class FillData extends AsyncTask<JSONArray, Integer, ArrayList<User>>{

		@Override
		protected ArrayList<User> doInBackground(JSONArray... params) {
			JSONArray values=params[0];
			ArrayList<User> tmpdata=new ArrayList<User>();
			try{
				int size=values.length();
				for(int i=0;i<size;i++){
					JSONObject tmpobj=values.getJSONObject(i);
					User tmpusr;
					if(tmpobj.has(User.YEAR)) //no optimize it we may have mixed users
						tmpusr=new User(tmpobj.getString(User.FIRST_NAME),tmpobj.getString(User.LAST_NAME),tmpobj.getString(User.ID),tmpobj.getString(User.PROFILE_IMAGE).replace("\\/","/"),tmpobj.getString(User.DEPARTMENT),tmpobj.getInt(User.YEAR),tmpobj.getInt(User.SECTION),tmpobj.getInt(User.STATE));
					else
						tmpusr=new User(tmpobj.getString(User.FIRST_NAME),tmpobj.getString(User.LAST_NAME),tmpobj.getString(User.ID),tmpobj.getString(User.PROFILE_IMAGE).replace("\\/","/"),tmpobj.getString(User.DEPARTMENT),tmpobj.getInt(User.STATE),tmpobj.getString(User.MOBILE));
					tmpdata.add(tmpusr);
				}
			}catch(JSONException e){
				Utility.log("error","FillDATA "+e.getLocalizedMessage());
			}
			return tmpdata;
		}
		
		@Override
		 protected void onPostExecute(ArrayList<User> data) {
			setData(data);
			refresh();
			listview.setVisibility(View.VISIBLE);
	        Utility.hideProgressDialog();
	     }
	}
}
