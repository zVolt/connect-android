package in.siet.secure.dao;

import in.siet.secure.Util.Attachment;
import in.siet.secure.Util.Notification;
import in.siet.secure.Util.Utility;
import in.siet.secure.sgi.FragmentDetailNotification;
import in.siet.secure.sgi.FragmentNotification;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

public class DbHelper extends SQLiteOpenHelper{
	public static final String TAG="in.siet.secure.dao.DbHelper";
	public static final String DATABASE_NAME="sgi_app.db";
	public static final int DATABASE_VERSION=1;
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
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DbStructure.FileMessageMapTable.COMMAND_DROP);
		db.execSQL(DbStructure.FileNotificationMapTable.COMMAND_DROP);
		db.execSQL(DbStructure.FileTable.COMMAND_DROP);
		db.execSQL(DbStructure.MessageTable.COMMAND_DROP);
		db.execSQL(DbStructure.NotificationTable.COMMAND_DROP);
		db.execSQL(DbStructure.StudentContactsTable.COMMAND_DROP);
		db.execSQL(DbStructure.FcultyContactsTable.COMMAND_DROP);
		onCreate(db);
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
}
