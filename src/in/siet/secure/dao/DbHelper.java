package in.siet.secure.dao;

import in.siet.secure.Util.Attachment;
import in.siet.secure.Util.FilterOptions;
import in.siet.secure.Util.InitialData;
import in.siet.secure.Util.Notification;
import in.siet.secure.Util.User;
import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.sgi.FragmentDetailNotification;
import in.siet.secure.sgi.FragmentNotification;

import java.util.ArrayList;
import java.util.Calendar;

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

public class DbHelper extends SQLiteOpenHelper {
	public static final String TAG = "in.siet.secure.dao.DbHelper";
	public static final String DATABASE_NAME = "sgi_app.db";
	public static int DATABASE_VERSION = 1;
	public static SQLiteDatabase db;
	public static Context context;
	public static SharedPreferences spf;

	public DbHelper(Context contxt) {
		super(contxt, DATABASE_NAME, null, DATABASE_VERSION);
		context = contxt;
		spf = context.getSharedPreferences(Constants.pref_file_name,
				Context.MODE_PRIVATE);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DbStructure.UserTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FcultyContactsTable.COMMAND_CREATE);
		db.execSQL(DbStructure.StudentContactsTable.COMMAND_CREATE);
		db.execSQL(DbStructure.NotificationTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FileTable.COMMAND_CREATE);
		db.execSQL(DbStructure.MessageTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FileMessageMapTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FileNotificationMapTable.COMMAND_CREATE);
		db.execSQL(DbStructure.UserInfoTable.COMMAND_CREATE);
		db.execSQL(DbStructure.Branches.COMMAND_CREATE);
		db.execSQL(DbStructure.Courses.COMMAND_CREATE);
		db.execSQL(DbStructure.Sections.COMMAND_CREATE);
		db.execSQL(DbStructure.Year.COMMAND_CREATE);
		db.execSQL(DbStructure.UserMapper.COMMAND_CREATE);
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
		db.execSQL(DbStructure.Branches.COMMAND_DROP);
		db.execSQL(DbStructure.Courses.COMMAND_DROP);
		db.execSQL(DbStructure.Sections.COMMAND_DROP);
		db.execSQL(DbStructure.Year.COMMAND_DROP);
		db.execSQL(DbStructure.UserMapper.COMMAND_DROP);
		onCreate(db);
		DATABASE_VERSION = newVersion;
	}

	public void ClearDb(SQLiteDatabase db) {
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

	public void getNotifications() {
		new FetchNotification().execute(this);
	}

	public void getFilesOfNotification(int noti_id) {
		new FetchFilesOfNotification().execute(this, noti_id);
	}

	private class FetchNotification extends
			AsyncTask<DbHelper, Integer, ArrayList<Notification>> {

		@Override
		protected ArrayList<Notification> doInBackground(DbHelper... params) {
			DbHelper This = params[0];
			String[] columns = { DbStructure.NotificationTable._ID,
					DbStructure.UserTable.COLUMN_PROFILE_PIC,
					DbStructure.NotificationTable.COLUMN_SUBJECT,
					DbStructure.NotificationTable.COLUMN_TEXT,
					DbStructure.NotificationTable.COLUMN_TIME };
			Utility.log(TAG, "select "
					+ DbStructure.NotificationTable.TABLE_NAME
					+ DbConstants.DOT + columns[0] + DbConstants.COMMA
					+ columns[1] + DbConstants.COMMA + columns[2]
					+ DbConstants.COMMA + columns[3] + DbConstants.COMMA
					+ columns[4] + " from "
					+ DbStructure.NotificationTable.TABLE_NAME + " join "
					+ DbStructure.UserTable.TABLE_NAME + " on "
					+ DbStructure.NotificationTable.COLUMN_SENDER + "="
					+ DbStructure.UserTable.TABLE_NAME + DbConstants.DOT
					+ DbStructure.UserTable._ID + " order by "
					+ DbStructure.NotificationTable.COLUMN_TIME);
			Cursor c = This.getReadableDatabase().rawQuery(
					"select " + DbStructure.NotificationTable.TABLE_NAME
							+ DbConstants.DOT + columns[0] + DbConstants.COMMA
							+ columns[1] + DbConstants.COMMA + columns[2]
							+ DbConstants.COMMA + columns[3]
							+ DbConstants.COMMA + columns[4] + " from "
							+ DbStructure.NotificationTable.TABLE_NAME
							+ " join " + DbStructure.UserTable.TABLE_NAME
							+ " on "
							+ DbStructure.NotificationTable.COLUMN_SENDER + "="
							+ DbStructure.UserTable.TABLE_NAME
							+ DbConstants.DOT + DbStructure.UserTable._ID
							+ " order by "
							+ DbStructure.NotificationTable.COLUMN_TIME, null);
			c.moveToFirst();
			ArrayList<Notification> notifications = new ArrayList<Notification>();
			while (c.isAfterLast() == false) {
				Notification tmpnot = new Notification(c.getInt(c
						.getColumnIndexOrThrow(columns[0])), c.getString(c
						.getColumnIndexOrThrow(columns[1])), c.getString(c
						.getColumnIndexOrThrow(columns[2])), c.getString(c
						.getColumnIndexOrThrow(columns[3])), c.getString(c
						.getColumnIndexOrThrow(columns[4])));
				notifications.add(tmpnot);
				Utility.log(TAG, tmpnot.subject);
				c.moveToNext();
				synchronized (this) {
					try {
						this.wait(100);
					} catch (Exception e) {

					}
				}
			}
			return notifications;
		}

		@Override
		protected void onPostExecute(ArrayList<Notification> data) {
			Utility.log(TAG, "we get data" + data.toString());
			FragmentNotification.setData(data);
			FragmentNotification.refresh();
			// FragmentNotification.showList();

		}

	}

	private class FetchFilesOfNotification extends
			AsyncTask<Object, Integer, ArrayList<Attachment>> {

		@Override
		protected ArrayList<Attachment> doInBackground(Object... params) {
			DbHelper This = (DbHelper) params[0];
			int noti_id = (Integer) params[1];
			ArrayList<Attachment> attachments = new ArrayList<Attachment>();
			String[] column = { DbStructure.FileTable.COLUMN_NAME,
					DbStructure.FileTable.COLUMN_STATE,
					DbStructure.FileTable.COLUMN_URL, DbStructure.FileTable._ID };
			Cursor c = This
					.getReadableDatabase()
					.rawQuery(
							"select "
									+ column[0]
									+ DbConstants.COMMA
									+ column[1]
									+ DbConstants.COMMA
									+ column[2]
									+ DbConstants.COMMA
									+ column[3]
									+ " from "
									+ DbStructure.FileTable.TABLE_NAME
									+ " join "
									+ DbStructure.FileNotificationMapTable.TABLE_NAME
									+ " on "
									+ DbStructure.FileNotificationMapTable.COLUMN_FILE_ID
									+ "="
									+ DbStructure.FileTable._ID
									+ " where "
									+ DbStructure.FileNotificationMapTable.COLUMN_NOTIFICATION_ID
									+ "=" + noti_id + DbConstants.SEMICOLON,
							null);

			c.moveToFirst();
			Attachment tmp;
			while (c.isAfterLast() == false) {
				tmp = new Attachment(
						c.getInt(c.getColumnIndexOrThrow(column[3])),
						c.getString(c.getColumnIndexOrThrow(column[0])),
						c.getInt(c.getColumnIndexOrThrow(column[1])),
						c.getString(c
								.getColumnIndexOrThrow(DbStructure.FileTable.COLUMN_URL)));
				attachments.add(tmp);
				c.moveToNext();
				synchronized (this) {
					try {
						this.wait(100);
					} catch (Exception e) {
						Utility.log(TAG, "cannot wait");
					}
				}
			}
			return attachments;
		}

		@Override
		protected void onPostExecute(ArrayList<Attachment> result) {
			FragmentDetailNotification.setData(result);
			FragmentDetailNotification.showAttachments();
		}
	}

	public void fillMessages(JSONArray messages, int receiver_id) {
		if (db == null)
			setDb();
		int len = messages.length();
		String query = "insert into messages(sender,receiver,text,time,is_group_msg) values";
		JSONObject obj;
		int j;
		String[] args = new String[len * 5];
		try {
			for (int i = 0; i < len; i++) {
				query += "((select _id from user where login_id=?),?,?,?,?)"
						+ ((i == len - 1) ? "" : ",");

				obj = messages.getJSONObject(i);
				j = i * 5;
				args[j] = obj.getString(Constants.JSONMEssageKeys.SENDER);
				args[j + 1] = receiver_id + "";// me
				args[j + 2] = obj.getString(Constants.JSONMEssageKeys.TEXT);
				args[j + 3] = (obj.getLong(Constants.JSONMEssageKeys.TIME))
						+ "";
				args[j + 4] = obj
						.getInt(Constants.JSONMEssageKeys.IS_GROUP_MESSAGE)
						+ "";

			}
			db.execSQL(query, args);
			Utility.log(TAG, "done inserting messages \n" + query);
		} catch (Exception e) {
			Utility.log(TAG, "" + e.getMessage());
		}

	}

	public void addUser(final User user, final boolean is_student) {
		// get user info from server asynchronously
		// add it to db
		// show toast
		if (db == null)
			setDb();

		RequestParams params = new RequestParams();
		// SharedPreferences
		// spf=context.getSharedPreferences(Constants.pref_file_name,
		// Context.MODE_PRIVATE);
		params.put(Constants.QueryParameters.USERNAME, Base64.encodeToString(
				spf.getString(Constants.PreferenceKeys.user_id, null)
						.getBytes(), Base64.DEFAULT));
		params.put(
				Constants.QueryParameters.TOKEN,
				Base64.encodeToString(
						spf.getString(Constants.PreferenceKeys.token, null)
								.trim().getBytes(), Base64.DEFAULT));
		params.put(Constants.QueryParameters.LOGIN_ID, user.id);
		params.put(Constants.QueryParameters.USER_TYPE, is_student);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://" + Constants.SERVER + Constants.COLON
				+ Constants.PORT + "/SGI_webservice/query/get_user_info",
				params, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							String query = "insert or ignore into user(f_name,l_name,pic_url,login_id) values(?,?,?,?)";

							ContentValues values = new ContentValues();
							values.put(DbStructure.UserTable.COLUMN_FNAME,
									user.f_name);
							values.put(DbStructure.UserTable.COLUMN_LNAME,
									user.l_name);
							values.put(
									DbStructure.UserTable.COLUMN_PROFILE_PIC,
									user.picUrl);
							Utility.log(TAG, response.toString());
							values.put(
									DbStructure.UserTable.COLUMN_LOGIN_ID,
									response.getString(Constants.JSONKeys.USER_ID));

							long user_id = db.insertWithOnConflict(
									DbStructure.UserTable.TABLE_NAME, null,
									values, SQLiteDatabase.CONFLICT_IGNORE);

							if (user_id != -1) {
								if (is_student) {
									String[] args = { user.section,
											user.year + "", user.dep,
											user.course

									};
									Cursor c = db
											.rawQuery(
													"select sections._id from sections join year on year_id=year._id join branches on branch_id=branches._id join courses on course_id=courses._id where sections.name=? and year.year=? and branches.name=? and courses.name=?",
													args);
									c.moveToFirst();
									int section_id = 0;
									if (!c.isAfterLast())
										section_id = c.getInt(0);
									c.close();
									values.clear();
									values.put(
											DbStructure.StudentContactsTable.COLUMN_USER_ID,
											user_id);
									values.put(
											DbStructure.StudentContactsTable.COLUMN_SECTION_ID,
											section_id);

									db.insert(
											DbStructure.StudentContactsTable.TABLE_NAME,
											null, values);
									// db.rawQuery(query,args);
								} else {
									query = "insert into faculty(user_id,branch_id) values(?,(select branches._id from branches join courses on course_id=courses._id where branches.name=? and courses.name=?))";
									String[] args = { user.dep, user.course };
									Cursor c = db
											.rawQuery(
													"select branches._id from branches join courses on course_id=courses._id where branches.name=? and courses.name=?",
													args);
									c.moveToFirst();
									int branch_id = 0;
									if (!c.isAfterLast())
										branch_id = c.getInt(0);
									c.close();
									values.clear();
									values.put(
											DbStructure.FcultyContactsTable.COLUMN_BRANCH_ID,
											branch_id);
									values.put(
											DbStructure.FcultyContactsTable.COLUMN_USER_ID,
											user_id);
									db.insert(
											DbStructure.FcultyContactsTable.TABLE_NAME,
											null, values);
									// db.rawQuery(query,args);
								}
								Utility.RaiseToast(context, user.f_name + " "
										+ user.l_name + " added Sucessfully",
										false);
							} else {
								Utility.log(TAG, "Error inserting user");
								Utility.RaiseToast(context, user.f_name + " "
										+ user.l_name
										+ " already exist in contacts", false);
							}

						} catch (Exception e) {
							Utility.log(TAG + "yaha pe error", e.getMessage());
						}
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							String responseString) {
						Utility.log(TAG,
								"receving string responce from server requested was JSONObject");
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						// TODO Auto-generated method stub
						Utility.log(TAG,
								"receving JSONArray responce from server requested was JSONObject");
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						Utility.log(TAG, " fail to receving found JSONObject "
								+ errorResponse.toString());
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						Utility.log(TAG,
								" fail to receving found String and a throwable "
										+ responseString);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONArray errorResponse) {
						// TODO Auto-generated method stub
						Utility.log(TAG,
								" fail to receving found JSONArray and a throwable "
										+ errorResponse.toString());
					}
				});

	}

	public void addInitialData(InitialData idata, String userid) {
		if (db == null)
			setDb();
		new insertInitialData().execute(idata);// add initial data

	}

	private void setDb() {
		db = this.getWritableDatabase();
	}

	/**
	 * Insert new notification to database fetches the target from
	 * FilterOptionsStatic class members
	 * 
	 * @param sub
	 *            Subject of the notification
	 * @param body
	 *            Content of the notification
	 */
	public void addNewNotification(String sub, String body) {
		// get faculty login_id first
		String course, branch, section;
		int year;
		// data copied in case use change it suddenly
		course = FilterOptions.COURSE;
		branch = FilterOptions.BRANCH;
		section = FilterOptions.SECTION;
		year = FilterOptions.YEAR;

		String fid = context.getSharedPreferences(Constants.pref_file_name,
				Context.MODE_PRIVATE).getString(
				Constants.PreferenceKeys.user_id, null);
		Long time = Calendar.getInstance().getTimeInMillis();
		Notification new_noti = new Notification(sub, body, time, fid, course,
				branch, section, year);
		if (db == null)
			setDb();
		new InsertNotification().execute(new_noti);
		// create new notification
	}

	private static class InsertNotification extends
			AsyncTask<Notification, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Notification... params) {
			Notification n = params[0];
			long target_id;
			// insert target
			ContentValues values = new ContentValues();
			values.put(DbStructure.UserMapper.COLUMN_COURSE, n.course);
			values.put(DbStructure.UserMapper.COLUMN_BRANCH, n.branch);
			values.put(DbStructure.UserMapper.COLUMN_YEAR, n.year);
			values.put(DbStructure.UserMapper.COLUMN_SECTION, n.section);
			target_id = db.insert(DbStructure.UserMapper.TABLE_NAME, null,
					values);
			// insert new notification
			values = new ContentValues();
			values.put(DbStructure.NotificationTable.COLUMN_SUBJECT, n.subject);
			values.put(DbStructure.NotificationTable.COLUMN_TEXT, n.text);
			values.put(DbStructure.NotificationTable.COLUMN_TIME, n.time);
			values.put(DbStructure.NotificationTable.COLUMN_STATE,
					Notification.STATE.CREATED);
			values.put(DbStructure.NotificationTable.COLUMN_SENDER, n.sid);
			values.put(DbStructure.NotificationTable.COLUMN_TARGET, target_id);
			db.insert(DbStructure.NotificationTable.TABLE_NAME, null, values);
			return true;
		}
	}

	/**
	 * Inserts courses, branches, year and section data passed, and fill
	 * user_mapper table accordingly
	 * 
	 * @author Zeeshan Khan
	 * 
	 */
	private static class insertInitialData extends
			AsyncTask<InitialData, Void, Boolean> {

		@Override
		protected Boolean doInBackground(InitialData... params) {
			InitialData idata = params[0];
			// drop tables not safe yet
			db.execSQL(DbStructure.Branches.COMMAND_DROP);
			db.execSQL(DbStructure.Courses.COMMAND_DROP);
			db.execSQL(DbStructure.Sections.COMMAND_DROP);
			db.execSQL(DbStructure.Year.COMMAND_DROP);

			db.execSQL(DbStructure.Branches.COMMAND_CREATE);
			db.execSQL(DbStructure.Courses.COMMAND_CREATE);
			db.execSQL(DbStructure.Sections.COMMAND_CREATE);
			db.execSQL(DbStructure.Year.COMMAND_CREATE);
			// adding branches and
			ContentValues values;
			for (InitialData.Courses c : idata.courses) {
				values = new ContentValues();
				values.put(DbStructure.Courses._ID, c.id);
				values.put(DbStructure.Courses.COLUMN_NAME, c.name);
				values.put(DbStructure.Courses.COLUMN_DURATION, c.duration);
				db.insert(DbStructure.Courses.TABLE_NAME, null, values);
			}

			for (InitialData.Branches b : idata.branches) {
				values = new ContentValues();
				values.put(DbStructure.Branches._ID, b.id);
				values.put(DbStructure.Branches.COLUMN_NAME, b.name);
				values.put(DbStructure.Branches.COLUMN_COURSE_ID, b.course_id);
				db.insert(DbStructure.Branches.TABLE_NAME, null, values);
			}

			for (InitialData.Sections s : idata.sections) {
				values = new ContentValues();
				values.put(DbStructure.Sections._ID, s.id);
				values.put(DbStructure.Sections.COLUMN_NAME, s.name);
				values.put(DbStructure.Sections.COLUMN_YEAR_ID, s.year_id);
				db.insert(DbStructure.Sections.TABLE_NAME, null, values);
			}

			for (InitialData.Year y : idata.years) {
				values = new ContentValues();
				values.put(DbStructure.Year._ID, y.id);
				values.put(DbStructure.Year.COLUMN_BRANCH_ID, y.branch_id);
				values.put(DbStructure.Year.COLUMN_YEAR, y.year);
				db.insert(DbStructure.Year.TABLE_NAME, null, values);
			}
			return null;
		}
	}
}
