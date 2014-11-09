package in.siet.secure.dao;

import in.siet.secure.Util.Attachment;
import in.siet.secure.Util.InitialData;
import in.siet.secure.Util.Notification;
import in.siet.secure.Util.User;
import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.sgi.FragmentDetailNotification;
import in.siet.secure.sgi.FragmentNewNotification;
import in.siet.secure.sgi.FragmentNotification;
import in.siet.secure.sgi.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Base64;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class DbHelper extends SQLiteOpenHelper{
	public static final String TAG="in.siet.secure.dao.DbHelper";
	public static final String DATABASE_NAME="sgi_app.db";
	public static int DATABASE_VERSION=1;
	public static SQLiteDatabase db;
	public static Context context;
	public DbHelper(Context contxt){
		super(contxt,DATABASE_NAME,null,DATABASE_VERSION);
		context=contxt;
	}
	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL(DbStructure.UserTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FcultyContactsTable.COMMAND_CREATE);
		db.execSQL(DbStructure.StudentContactsTable.COMMAND_CREATE);
		db.execSQL(DbStructure.NotificationTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FileTable.COMMAND_CREATE);
		db.execSQL(DbStructure.MessageTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FileMessageMapTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FileNotificationMapTable.COMMAND_CREATE);
		db.execSQL(DbStructure.UserInfoTable.COMMAND_CREATE);
		db.execSQL(DbStructure.BRANCHES.COMMAND_CREATE);
		db.execSQL(DbStructure.COURSES.COMMAND_CREATE);
		db.execSQL(DbStructure.SECTIONS.COMMAND_CREATE);
		db.execSQL(DbStructure.YEAR.COMMAND_CREATE);
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DbStructure.UserTable.COMMAND_DROP);
		db.execSQL(DbStructure.FileMessageMapTable.COMMAND_DROP);
		db.execSQL(DbStructure.FileNotificationMapTable.COMMAND_DROP);
		db.execSQL(DbStructure.FileTable.COMMAND_DROP);
		db.execSQL(DbStructure.MessageTable.COMMAND_DROP);
		db.execSQL(DbStructure.NotificationTable.COMMAND_DROP);
		db.execSQL(DbStructure.StudentContactsTable.COMMAND_DROP);
		db.execSQL(DbStructure.FcultyContactsTable.COMMAND_DROP);
		db.execSQL(DbStructure.UserInfoTable.COMMAND_DROP);
		db.execSQL(DbStructure.BRANCHES.COMMAND_DROP);
		db.execSQL(DbStructure.COURSES.COMMAND_DROP);
		db.execSQL(DbStructure.SECTIONS.COMMAND_DROP);
		db.execSQL(DbStructure.YEAR.COMMAND_DROP);
		onCreate(db);
		DATABASE_VERSION=newVersion;
	}
	public void ClearDb(SQLiteDatabase db){
		db.execSQL(DbStructure.UserTable.COMMAND_DROP);
		db.execSQL(DbStructure.FileMessageMapTable.COMMAND_DROP);
		db.execSQL(DbStructure.FileNotificationMapTable.COMMAND_DROP);
		db.execSQL(DbStructure.FileTable.COMMAND_DROP);
		db.execSQL(DbStructure.MessageTable.COMMAND_DROP);
		db.execSQL(DbStructure.NotificationTable.COMMAND_DROP);
		db.execSQL(DbStructure.StudentContactsTable.COMMAND_DROP);
		db.execSQL(DbStructure.FcultyContactsTable.COMMAND_DROP);
		db.execSQL(DbStructure.UserInfoTable.COMMAND_DROP);
		
		db.execSQL(DbStructure.UserTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FcultyContactsTable.COMMAND_CREATE);
		db.execSQL(DbStructure.StudentContactsTable.COMMAND_CREATE);
		db.execSQL(DbStructure.NotificationTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FileTable.COMMAND_CREATE);
		db.execSQL(DbStructure.MessageTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FileMessageMapTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FileNotificationMapTable.COMMAND_CREATE);
		db.execSQL(DbStructure.UserInfoTable.COMMAND_CREATE);
	}
	public void getNotifications(){
		new FetchNotification().execute(this);
	}
	
	public void getFilesOfNotification(int noti_id) {
		new FetchFilesOfNotification().execute(this,noti_id);
	}
	
	private class FetchNotification extends AsyncTask<DbHelper, Integer, ArrayList<Notification>>{

		@Override
		protected ArrayList<Notification> doInBackground(DbHelper... params) {
			DbHelper This=params[0];
			String[] columns={
					DbStructure.NotificationTable._ID,
					DbStructure.UserTable.COLUMN_PROFILE_PIC,
					DbStructure.NotificationTable.COLUMN_SUBJECT,
					DbStructure.NotificationTable.COLUMN_TEXT,
					DbStructure.NotificationTable.COLUMN_TIME
			};
			Utility.log(TAG,"select "+DbStructure.NotificationTable.TABLE_NAME+DbConstants.DOT+columns[0]+DbConstants.COMMA+columns[1]+DbConstants.COMMA+columns[2]+DbConstants.COMMA+columns[3]+DbConstants.COMMA+columns[4]+" from "+DbStructure.NotificationTable.TABLE_NAME+" join "+DbStructure.UserTable.TABLE_NAME+" on "+DbStructure.NotificationTable.COLUMN_SENDER+"="+DbStructure.UserTable.TABLE_NAME+DbConstants.DOT+DbStructure.UserTable._ID+" order by "+DbStructure.NotificationTable.COLUMN_TIME);
			Cursor c=This.getReadableDatabase().rawQuery("select "+DbStructure.NotificationTable.TABLE_NAME+DbConstants.DOT+columns[0]+DbConstants.COMMA+columns[1]+DbConstants.COMMA+columns[2]+DbConstants.COMMA+columns[3]+DbConstants.COMMA+columns[4]+" from "+DbStructure.NotificationTable.TABLE_NAME+" join "+DbStructure.UserTable.TABLE_NAME+" on "+DbStructure.NotificationTable.COLUMN_SENDER+"="+DbStructure.UserTable.TABLE_NAME+DbConstants.DOT+DbStructure.UserTable._ID+" order by "+DbStructure.NotificationTable.COLUMN_TIME,null);
			c.moveToFirst();
			ArrayList<Notification> notifications=new ArrayList<Notification>();
			while(c.isAfterLast()==false){
				Notification tmpnot=new Notification(c.getInt(c.getColumnIndexOrThrow(columns[0])),
						c.getString(c.getColumnIndexOrThrow(columns[1])),
						c.getString(c.getColumnIndexOrThrow(columns[2])),
						c.getString(c.getColumnIndexOrThrow(columns[3])),
						c.getString(c.getColumnIndexOrThrow(columns[4]))
						);
				notifications.add(tmpnot);
				Utility.log(TAG,tmpnot.subject);
				c.moveToNext();
				synchronized(this){
					try{
						this.wait(100);
					}catch(Exception e){
						
					}
				}
			}
			return notifications;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Notification> data){
			Utility.log(TAG,"we get data"+data.toString());
			FragmentNotification.setData(data);
			FragmentNotification.refresh();
			//FragmentNotification.showList();
		}
		
	}
	
	private class FetchFilesOfNotification extends AsyncTask<Object,Integer,ArrayList<Attachment>>{

		@Override
		protected ArrayList<Attachment> doInBackground(Object... params) {
			DbHelper This=(DbHelper)params[0];
			int noti_id=(Integer)params[1];
			ArrayList<Attachment> attachments=new ArrayList<Attachment>();
			String[] column={
				DbStructure.FileTable.COLUMN_NAME,
				DbStructure.FileTable.COLUMN_STATE,
				DbStructure.FileTable.COLUMN_URL,
				DbStructure.FileTable._ID
			};
			Cursor c=This.getReadableDatabase().rawQuery("select "+column[0]+DbConstants.COMMA+column[1]+DbConstants.COMMA+column[2]+DbConstants.COMMA+column[3]+" from "+DbStructure.FileTable.TABLE_NAME+" join "+DbStructure.FileNotificationMapTable.TABLE_NAME+" on "+DbStructure.FileNotificationMapTable.COLUMN_FILE_ID+"="+DbStructure.FileTable._ID+" where "+DbStructure.FileNotificationMapTable.COLUMN_NOTIFICATION_ID+"="+noti_id+DbConstants.SEMICOLON, null);

			c.moveToFirst();
			Attachment tmp;
			while(c.isAfterLast()==false){
				tmp=new Attachment(c.getInt(c.getColumnIndexOrThrow(column[3])),c.getString(c.getColumnIndexOrThrow(column[0])), c.getInt(c.getColumnIndexOrThrow(column[1])),c.getString(c.getColumnIndexOrThrow(DbStructure.FileTable.COLUMN_URL)));
				attachments.add(tmp);
				c.moveToNext();
				synchronized(this){
					try{
						this.wait(100);
					}catch(Exception e){
						Utility.log(TAG, "cannot wait");
					}
				}
			}
			return attachments;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Attachment> result){
			FragmentDetailNotification.setData(result);
			FragmentDetailNotification.showAttachments();
		}
	}
	
	public void addUser(final User user,final boolean is_student){
		//get user info from server asynchronously
		//add it to db
		//show toast
		if(db==null)
			db=this.getWritableDatabase();
		
		RequestParams params =new RequestParams();
		SharedPreferences spf=context.getSharedPreferences(context.getString(R.string.preference_file_name), Context.MODE_PRIVATE);
		params.put(context.getString(R.string.web_prm_usr),Base64.encodeToString(spf.getString(context.getString(R.string.user_id), null).getBytes(), Base64.DEFAULT));
		params.put(context.getString(R.string.web_prm_token),Base64.encodeToString(spf.getString(context.getString(R.string.acess_token), null).trim().getBytes(), Base64.DEFAULT));
		params.put(context.getString(R.string.web_prm_login_id), user.id);
		params.put(context.getString(R.string.web_prm_query_user_type), is_student);
		AsyncHttpClient client=new AsyncHttpClient();
		client.get("http://"+Constants.SERVER+Constants.COLON+Constants.PORT+"/SGI_webservice/query/get_user_info",params,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,JSONObject response) {
				try{
					String query="insert or ignore into user(f_name,l_name,pic_url,login_id) values(?,?,?,?)";
					
					ContentValues values=new ContentValues();
					values.put(DbStructure.UserTable.COLUMN_FNAME, user.f_name);
					values.put(DbStructure.UserTable.COLUMN_LNAME, user.l_name);
					values.put(DbStructure.UserTable.COLUMN_PROFILE_PIC, user.picUrl);
					Utility.log(TAG,response.toString());
					values.put(DbStructure.UserTable.COLUMN_LOGIN_ID, response.getString(User.USER_ID));
					
					long user_id=db.insertWithOnConflict(DbStructure.UserTable.TABLE_NAME, null,values,SQLiteDatabase.CONFLICT_IGNORE);
					
					
					if(user_id!=-1){
						if(is_student){
							String[] args={
									user.section,
									user.year+"",
									user.dep,
									user.course
									
							};
							Cursor c=db.rawQuery("select sections._id from sections join year on year_id=year._id join branches on branch_id=branches._id join courses on course_id=courses._id where sections.name=? and year.year=? and branches.name=? and courses.name=?", args);
							c.moveToFirst();
							int section_id=0;
							if(!c.isAfterLast())
								section_id=c.getInt(0);
							c.close();
							values.clear();
							values.put(DbStructure.StudentContactsTable.COLUMN_USER_ID, user_id);
							values.put(DbStructure.StudentContactsTable.COLUMN_SECTION_ID, section_id);
							
							db.insert(DbStructure.StudentContactsTable.TABLE_NAME, null, values);
							//db.rawQuery(query,args);
						}
						else{
							query="insert into faculty(user_id,branch_id) values(?,(select branches._id from branches join courses on course_id=courses._id where branches.name=? and courses.name=?))";
							String[] args={
									user.dep,
									user.course
							};
							Cursor c=db.rawQuery("select branches._id from branches join courses on course_id=courses._id where branches.name=? and courses.name=?",args );
							c.moveToFirst();
							int branch_id=0;
							if(!c.isAfterLast())
								branch_id=c.getInt(0);
							c.close();
							values.clear();
							values.put(DbStructure.FcultyContactsTable.COLUMN_BRANCH_ID, branch_id);
							values.put(DbStructure.FcultyContactsTable.COLUMN_USER_ID, user_id);
							db.insert(DbStructure.FcultyContactsTable.TABLE_NAME, null, values);
							//db.rawQuery(query,args);
						}
						Utility.RaiseToast(context, user.f_name+" "+user.l_name+" added Sucessfully", false);
					}
					else{
							Utility.log(TAG,"Error inserting user");
							Utility.RaiseToast(context, user.f_name+" "+user.l_name+" already exist in contacts", false);
					}
					
				}catch(Exception e){
					Utility.log(TAG+"yaha pe error", e.getMessage());
				}
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String responseString) {
				Utility.log(TAG,"receving string responce from server requested was JSONObject");
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				// TODO Auto-generated method stub
				Utility.log(TAG,"receving JSONArray responce from server requested was JSONObject");
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				// TODO Auto-generated method stub
				Utility.log(TAG," fail to receving found JSONObject "+errorResponse.toString());
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				Utility.log(TAG," fail to receving found String and a throwable "+responseString);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				// TODO Auto-generated method stub
				Utility.log(TAG," fail to receving found JSONArray and a throwable "+errorResponse.toString());
			}
		});
		
		
	}
	public void addInitialData(InitialData idata,String userid){
		if(db==null)
			db=this.getWritableDatabase();
		new insertInitialData().execute(idata);
	}
	public void addNewNotification(FragmentNewNotification.ViewHolder holder){
	/*	//get faculty login_id first
		String fid=context.getSharedPreferences(context.getString(R.string.preference_file_name), Context.MODE_PRIVATE).getString(context.getString(R.string.user_id),null);
		Date time=Calendar.getInstance().getTime();
		Notification new_noti=new Notification(holder.subject.getText().toString(),holder.body.getText().toString(),time,fid);
	//	new InsertNotification().execute(new_noti);
			
		//create new notification
		//
		 */
	}
	
	private static class InsertNotification extends AsyncTask<Notification, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Notification... params) {
			String query="insert into "+DbStructure.NotificationTable.TABLE_NAME+"("
					+DbStructure.NotificationTable.COLUMN_SUBJECT+DbConstants.COMMA
					+DbStructure.NotificationTable.COLUMN_TEXT+DbConstants.COMMA
					+DbStructure.NotificationTable.COLUMN_TIME+DbConstants.COMMA
					+DbStructure.NotificationTable.COLUMN_STATE+DbConstants.COMMA
					+DbStructure.NotificationTable.COLUMN_SENDER+DbConstants.COMMA;
			return false;
		}
	}
	
	private static class insertInitialData extends AsyncTask<InitialData, Void, Boolean>{

		@Override
		protected Boolean doInBackground(InitialData... params) {
			InitialData idata=params[0];
			
			db.execSQL(DbStructure.BRANCHES.COMMAND_DROP);
			db.execSQL(DbStructure.COURSES.COMMAND_DROP);
			db.execSQL(DbStructure.SECTIONS.COMMAND_DROP);
			db.execSQL(DbStructure.YEAR.COMMAND_DROP);
			
			db.execSQL(DbStructure.BRANCHES.COMMAND_CREATE);
			db.execSQL(DbStructure.COURSES.COMMAND_CREATE);
			db.execSQL(DbStructure.SECTIONS.COMMAND_CREATE);
			db.execSQL(DbStructure.YEAR.COMMAND_CREATE);
			
			ContentValues values;
			for(InitialData.Courses c:idata.courses){
				values=new ContentValues();
				values.put(DbStructure.COURSES._ID, c.id);
				values.put(DbStructure.COURSES.COLUMN_NAME, c.name);
				values.put(DbStructure.COURSES.COLUMN_DURATION, c.duration);
				db.insert(DbStructure.COURSES.TABLE_NAME, null, values);
			}
			
			for(InitialData.Branches b:idata.branches){
				values=new ContentValues();
				values.put(DbStructure.BRANCHES._ID, b.id);
				values.put(DbStructure.BRANCHES.COLUMN_NAME, b.name);
				values.put(DbStructure.BRANCHES.COLUMN_COURSE_ID, b.course_id);
				db.insert(DbStructure.BRANCHES.TABLE_NAME, null, values);
			}
			
			for(InitialData.Sections s:idata.sections){
				values=new ContentValues();
				values.put(DbStructure.SECTIONS._ID, s.id);
				values.put(DbStructure.SECTIONS.COLUMN_NAME, s.name);
				values.put(DbStructure.SECTIONS.COLUMN_YEAR_ID, s.year_id);
				db.insert(DbStructure.SECTIONS.TABLE_NAME, null, values);
			}
			
			for(InitialData.Year y:idata.years){
				values=new ContentValues();
				values.put(DbStructure.YEAR._ID, y.id);
				values.put(DbStructure.YEAR.COLUMN_BRANCH_ID, y.branch_id);
				values.put(DbStructure.YEAR.COLUMN_YEAR, y.year);
				db.insert(DbStructure.YEAR.TABLE_NAME, null, values);
			}
			
			return null;
		}
		
	}
}
