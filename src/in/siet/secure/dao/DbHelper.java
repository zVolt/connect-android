package in.siet.secure.dao;

import in.siet.secure.Util.Attachment;
import in.siet.secure.Util.Faculty;
import in.siet.secure.Util.InitialData;
import in.siet.secure.Util.Notification;
import in.siet.secure.Util.Student;
import in.siet.secure.Util.User;
import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

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
	private Intent intent;

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
		db.execSQL(DbStructure.UserMapper.COMMAND_CREATE);

		db.execSQL(DbStructure.Branches.COMMAND_CREATE);
		db.execSQL(DbStructure.Courses.COMMAND_CREATE);
		db.execSQL(DbStructure.Sections.COMMAND_CREATE);
		db.execSQL(DbStructure.Year.COMMAND_CREATE);

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
		db.execSQL(DbStructure.UserMapper.COMMAND_DROP);

		db.execSQL(DbStructure.Branches.COMMAND_DROP);
		db.execSQL(DbStructure.Courses.COMMAND_DROP);
		db.execSQL(DbStructure.Sections.COMMAND_DROP);
		db.execSQL(DbStructure.Year.COMMAND_DROP);

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
		db.execSQL(DbStructure.UserMapper.COMMAND_DROP);

		db.execSQL(DbStructure.UserTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FcultyContactsTable.COMMAND_CREATE);
		db.execSQL(DbStructure.StudentContactsTable.COMMAND_CREATE);
		db.execSQL(DbStructure.NotificationTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FileTable.COMMAND_CREATE);
		db.execSQL(DbStructure.MessageTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FileMessageMapTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FileNotificationMapTable.COMMAND_CREATE);
		db.execSQL(DbStructure.UserInfoTable.COMMAND_CREATE);
		db.execSQL(DbStructure.UserMapper.COMMAND_CREATE);

	}

	/**
	 * this should be done by service
	 * 
	 * @param noti_id
	 */
	public void getFilesOfNotification(int noti_id) {
		new FetchFilesOfNotification().execute(this, noti_id);
	}

	/**
	 * provide users primary key i.e., the value of _id column corresponding to
	 * the login_id provided
	 * 
	 * @param user_id
	 *            user's login id like 'B-11-136' or 'EMP-100' for which we want
	 *            primary key( _id )
	 * @return value of _id column corresponding to user_id(B-11-136 like) or -1
	 *         if not exist
	 */
	public int getUserPk(String user_id) {

		setDb();
		int pk_id = -1;
		if (user_id != null) {
			String[] columns = { DbStructure.UserTable._ID };
			String[] sel_args = { user_id };

			Cursor c = db.query(DbStructure.UserTable.TABLE_NAME, columns,
					DbStructure.UserTable.COLUMN_LOGIN_ID + DbConstants.EQUALS
							+ DbConstants.QUESTION_MARK, sel_args, null, null,
					null);

			if (c.moveToFirst()) {
				int col_index;
				if ((col_index = c.getColumnIndex(DbStructure.UserTable._ID)) > -1)
					pk_id = c.getInt(col_index);
			}
		} else {
			Utility.log(TAG, "user_id null");
		}
		return pk_id;
	}

	/**
	 * fill messages received from server to db this should take ArrayList of
	 * Messages
	 * 
	 * @param messages
	 *            {@link JSONArray} of messages received
	 * @param receiver
	 *            {@link String} login id of user here the user is receiver
	 * @return {@link JSONArray} ids(send by server i.e., these are message pk
	 *         on server) of inserted messages
	 */
	public JSONArray fillMessages(JSONArray messages, String receiver) {

		setDb();
		JSONArray ids = new JSONArray();
		int len = messages.length();
		if (len > 0) {
			StringBuilder query = new StringBuilder(
					"insert into messages(sender,receiver,text,time,is_group_msg) values");
			JSONObject obj;
			int j;
			int receiver_id = getUserPk(receiver);
			String[] args = new String[len * 5];
			try {
				for (int i = 0; i < len; i++) {
					query.append("((select _id from user where login_id=?),?,?,?,?)");
					query.append(DbConstants.COMMA);
					obj = messages.getJSONObject(i);
					j = i * 5;
					args[j] = obj.getString(Constants.JSONKEYS.MESSAGES.SENDER);
					args[j + 1] = String.valueOf(receiver_id);
					args[j + 2] = obj
							.getString(Constants.JSONKEYS.MESSAGES.TEXT);
					args[j + 3] = String.valueOf(obj
							.getLong(Constants.JSONKEYS.MESSAGES.TIME));
					args[j + 4] = String
							.valueOf(obj
									.getInt(Constants.JSONKEYS.MESSAGES.IS_GROUP_MESSAGE));
					ids.put(obj.getInt(Constants.JSONKEYS.MESSAGES.ID));
				}
				query.deleteCharAt(query.length() - 1);
				db.execSQL(query.toString(), args);
			} catch (Exception e) {
				Utility.DEBUG(e);
			}
		}
		return ids;
	}

	/**
	 * 
	 * @param notifications
	 * @param receiver
	 * @return
	 */
	public JSONArray fillNotifications(JSONArray notifications) {
		setDb();
		int len = notifications.length();
		JSONArray ids = new JSONArray();
		if (len > 0) {
			JSONObject notification;
			ContentValues values = new ContentValues();
			long target_id;
			try {
				for (int i = 0; i < len; i++) {
					// insert target
					notification = notifications.getJSONObject(i);
					values.clear();
					values.put(
							DbStructure.UserMapper.COLUMN_COURSE,
							notification
									.getString(Constants.JSONKEYS.NOTIFICATIONS.COURSE));
					values.put(
							DbStructure.UserMapper.COLUMN_BRANCH,
							notification
									.getString(Constants.JSONKEYS.NOTIFICATIONS.BRANCH));
					values.put(DbStructure.UserMapper.COLUMN_YEAR, notification
							.getString(Constants.JSONKEYS.NOTIFICATIONS.YEAR));
					values.put(
							DbStructure.UserMapper.COLUMN_SECTION,
							notification
									.getString(Constants.JSONKEYS.NOTIFICATIONS.SECTION));
					target_id = db.insert(DbStructure.UserMapper.TABLE_NAME,
							null, values);

					// insert new notification and get its pk id
					values.clear();
					values.put(
							DbStructure.NotificationTable.COLUMN_SUBJECT,
							notification
									.getString(Constants.JSONKEYS.NOTIFICATIONS.SUBJECT));
					values.put(
							DbStructure.NotificationTable.COLUMN_TEXT,
							notification
									.getString(Constants.JSONKEYS.NOTIFICATIONS.TEXT));
					values.put(
							DbStructure.NotificationTable.COLUMN_TIME,
							notification
									.getString(Constants.JSONKEYS.NOTIFICATIONS.TIME));
					values.put(DbStructure.NotificationTable.COLUMN_STATE,
							Constants.NOTI_STATE.RECEIVED);
					values.put(
							DbStructure.NotificationTable.COLUMN_SENDER,
							getUserPk(notification
									.getString(Constants.JSONKEYS.NOTIFICATIONS.SENDER)));
					values.put(DbStructure.NotificationTable.COLUMN_TARGET,
							target_id);
					db.insert(DbStructure.NotificationTable.TABLE_NAME, null,
							values);
					ids.put(notification
							.getInt(Constants.JSONKEYS.NOTIFICATIONS.ID));
				}
			} catch (Exception e) {
				Utility.DEBUG(e);
			}
		}
		return ids;
	}

	/**
	 * insert a faculty with full details
	 * 
	 * @param response
	 */
	public void insertUser(JSONObject response) {
		setDb();
		int len;
		try {
			if (response.has(Constants.JSONKEYS.FACULTY)) {
				JSONArray faculties = response
						.getJSONArray(Constants.JSONKEYS.FACULTY);
				JSONObject faculty;
				len = faculties.length();
				if (len > 0) {
					/**
					 * insert faculty into db with their data
					 */
					ContentValues values = new ContentValues();
					long user_pk, branch_id;
					Cursor c;
					for (int i = 0; i < len; i++) {
						faculty = faculties.getJSONObject(i);
						values.clear();
						values.put(DbStructure.UserTable.COLUMN_FNAME, faculty
								.getString(Constants.JSONKEYS.FIRST_NAME));
						values.put(DbStructure.UserTable.COLUMN_LNAME,
								faculty.getString(Constants.JSONKEYS.LAST_NAME));
						values.put(
								DbStructure.UserTable.COLUMN_PROFILE_PIC,
								faculty.getString(Constants.JSONKEYS.PROFILE_IMAGE));
						values.put(DbStructure.UserTable.COLUMN_LOGIN_ID,
								faculty.getString(Constants.JSONKEYS.L_ID));

						user_pk = db.insert(DbStructure.UserTable.TABLE_NAME,
								null, values);
						/**
						 * inserted in user table table
						 */
						values.clear();
						if (user_pk != -1) {
							c = db.rawQuery(
									"select branches._id from branches  where branches.name=?",
									new String[] { faculty
											.getString(Constants.JSONKEYS.BRANCH) });

							branch_id = 0;
							c.moveToFirst();
							if (!c.isAfterLast())
								branch_id = c.getInt(0);
							c.close();

							values.put(
									DbStructure.FcultyContactsTable.COLUMN_BRANCH_ID,
									branch_id);
							values.put(
									DbStructure.FcultyContactsTable.COLUMN_USER_ID,
									user_pk);

							db.insert(
									DbStructure.FcultyContactsTable.TABLE_NAME,
									null, values);
							/**
							 * inserted in faculty table
							 */

							values.clear();

							if (faculty.has(Constants.JSONKEYS.STREET))
								values.put(
										DbStructure.UserInfoTable.COLUMN_STREET,
										faculty.getString(Constants.JSONKEYS.STREET));
							if (faculty.has(Constants.JSONKEYS.CITY))
								values.put(
										DbStructure.UserInfoTable.COLUMN_CITY,
										faculty.getString(Constants.JSONKEYS.CITY));
							if (faculty.has(Constants.JSONKEYS.STATE))
								values.put(
										DbStructure.UserInfoTable.COLUMN_STATE,
										faculty.getString(Constants.JSONKEYS.STATE));
							if (faculty.has(Constants.JSONKEYS.PIN))
								values.put(
										DbStructure.UserInfoTable.COLUMN_PIN,
										faculty.getString(Constants.JSONKEYS.PIN));
							if (faculty.has(Constants.JSONKEYS.P_MOB))
								values.put(
										DbStructure.UserInfoTable.COLUMN_P_MOB,
										faculty.getString(Constants.JSONKEYS.P_MOB));
							if (faculty.has(Constants.JSONKEYS.H_MOB))
								values.put(
										DbStructure.UserInfoTable.COLUMN_H_MOB,
										faculty.getString(Constants.JSONKEYS.H_MOB));
							values.put(
									DbStructure.UserInfoTable.COLUMN_USER_ID,
									user_pk);

							db.insert(DbStructure.UserInfoTable.TABLE_NAME,
									null, values);
							/**
							 * inserted extra details of faculty
							 */
						}
					}
				}
			}
			if (response.has(Constants.JSONKEYS.STUDENT)) {
				/**
				 * don't insert users for now give an option to user
				 */
				Utility.log(TAG, "ignoring new student senders :P");
			}
		} catch (Exception e) {
			Utility.DEBUG(e);

		}
	}

	/**
	 * get user info from server asynchronously add it to database show toast
	 * getting data from server should be moved to service
	 * 
	 * @param user
	 * @param is_student
	 */
	public void getAndAddUser(final User user, final boolean is_student) {
		setDb();
		RequestParams params = new RequestParams();
		Utility.putCredentials(params, spf);
		params.put(Constants.QueryParameters.GET_DETAILS_OF_USER_ID,
				user.user_id);
		params.put(Constants.QueryParameters.USER_TYPE, is_student);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Utility.getBaseURL() + "query/get_user_info", params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							// String query =
							// "insert or ignore into user(f_name,l_name,pic_url,login_id) values(?,?,?,?)";

							ContentValues values = new ContentValues();
							values.put(DbStructure.UserTable.COLUMN_FNAME,
									user.f_name);
							values.put(DbStructure.UserTable.COLUMN_LNAME,
									user.l_name);
							values.put(
									DbStructure.UserTable.COLUMN_PROFILE_PIC,
									user.picUrl);
							values.put(DbStructure.UserTable.COLUMN_LOGIN_ID,
									user.user_id);

							Utility.log(TAG, response.toString()); // use it

							long user_pk = db.insertWithOnConflict(
									DbStructure.UserTable.TABLE_NAME, null,
									values, SQLiteDatabase.CONFLICT_REPLACE);

							if (user_pk != -1) {
								if (is_student) {
									Student s_user = (Student) user;
									String[] args = { s_user.section,
											String.valueOf(s_user.year) };
									Cursor c = db
											.rawQuery(
													"select sections._id from sections join year on year_id=year._id where sections.name=? and year.year=?",
													args);
									c.moveToFirst();
									int section_id = 0;
									if (!c.isAfterLast())
										section_id = c.getInt(0);
									c.close();
									values.clear();
									values.put(
											DbStructure.StudentContactsTable.COLUMN_USER_ID,
											user_pk);
									values.put(
											DbStructure.StudentContactsTable.COLUMN_SECTION_ID,
											section_id);
									values.put(
											DbStructure.StudentContactsTable.COLUMN_ROLL_NO,
											response.getString(Constants.JSONKEYS.ROLL_NO));
									db.insert(
											DbStructure.StudentContactsTable.TABLE_NAME,
											null, values);
									// db.rawQuery(query,args);
								} else {
									// query =
									// "insert into faculty(user_id,branch_id) values(?,(select branches._id from branches join courses on course_id=courses._id where branches.name=? and courses.name=?))";
									Faculty f_user = (Faculty) user;
									String[] args = { f_user.branch };
									Cursor c = db
											.rawQuery(
													"select branches._id from branches  where branches.name=?",
													args);
									long branch_id = 0;
									c.moveToFirst();
									if (!c.isAfterLast())
										branch_id = c.getLong(0);
									c.close();
									values.clear();
									values.put(
											DbStructure.FcultyContactsTable.COLUMN_BRANCH_ID,
											branch_id);
									values.put(
											DbStructure.FcultyContactsTable.COLUMN_USER_ID,
											user_pk);

									long facut_id = db
											.insert(DbStructure.FcultyContactsTable.TABLE_NAME,
													null, values);
									// adding additional details
									if (facut_id != -1) {
										values.clear();
										values.put(
												DbStructure.UserInfoTable.COLUMN_USER_ID,
												facut_id);
										if (response
												.has(Constants.JSONKEYS.STATE))
											values.put(
													DbStructure.UserInfoTable.COLUMN_STATE,
													response.getString(Constants.JSONKEYS.STATE));
										if (response
												.has(Constants.JSONKEYS.CITY))
											values.put(
													DbStructure.UserInfoTable.COLUMN_CITY,
													response.getString(Constants.JSONKEYS.CITY));
										if (response
												.has(Constants.JSONKEYS.STREET))
											values.put(
													DbStructure.UserInfoTable.COLUMN_STREET,
													response.getString(Constants.JSONKEYS.STREET));
										if (response
												.has(Constants.JSONKEYS.P_MOB))
											values.put(
													DbStructure.UserInfoTable.COLUMN_P_MOB,
													response.getString(Constants.JSONKEYS.P_MOB));
										if (response
												.has(Constants.JSONKEYS.H_MOB))
											values.put(
													DbStructure.UserInfoTable.COLUMN_H_MOB,
													response.getString(Constants.JSONKEYS.H_MOB));
										if (response
												.has(Constants.JSONKEYS.PIN))
											values.put(
													DbStructure.UserInfoTable.COLUMN_PIN,
													response.getString(Constants.JSONKEYS.PIN));
										db.insert(
												DbStructure.UserInfoTable.TABLE_NAME,
												null, values);
									}
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
							Utility.DEBUG(e);
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

						Utility.log(TAG,
								"receving JSONArray responce from server requested was JSONObject");
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {

						Utility.log(TAG, " fail to receving found JSONObject "
								+ errorResponse.toString());
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {

						Utility.log(TAG,
								" fail to receving found String and a throwable "
										+ responseString);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONArray errorResponse) {

						Utility.log(TAG,
								" fail to receving found JSONArray and a throwable "
										+ errorResponse.toString());
					}
				});

	}

	public void addInitialData(InitialData idata, String userid) {
		setDb();
		new insertInitialData().execute(idata);

	}

	private void setDb() {
		if (db == null || !db.isOpen())
			db = this.getWritableDatabase();
	}

	public SQLiteDatabase getDb() {
		setDb();
		return db;
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
	public void insertNewNotification(Notification new_noti) {
		setDb();
		new InsertNotification().execute(new_noti);
		// create new notification
	}

	/**
	 * update states of messages and notifications for which we have received
	 * acknowledgment from server i.e., server received them sucessfully
	 * 
	 * @param acks
	 */
	public void receivedAck(JSONObject acks) {
		setDb();
		try {
			int len;
			JSONArray ids;
			// messages
			if (acks.has(Constants.JSONKEYS.MESSAGES.ACK)) {
				ids = acks.getJSONArray(Constants.JSONKEYS.MESSAGES.ACK);

				len = ids.length();
				if (len > 0) {
					StringBuilder query = new StringBuilder();
					ContentValues value = new ContentValues();
					value.put(DbStructure.MessageTable.COLUMN_STATE,
							Constants.MSG_STATE.ACK_RECEIVED);
					query.append("_id IN (");
					String[] args = new String[len];
					for (int i = 0; i < len; i++) {
						query.append(DbConstants.QUESTION_MARK);
						query.append(DbConstants.COMMA);
						args[i] = String.valueOf(ids.getInt(i));
					}
					query.deleteCharAt(query.length() - 1); // delete the last
															// comma
					query.append(DbConstants.BRACES_CLOSE);
					// update messages set state=1 where _id IN(........)
					Utility.log(TAG, query.toString());
					db.update(DbStructure.MessageTable.TABLE_NAME, value,
							query.toString(), args);
					// send broadcast to update message list
				}
			}
			// for notifications
			if (acks.has(Constants.JSONKEYS.NOTIFICATIONS.ACK)) {
				ids = acks.getJSONArray(Constants.JSONKEYS.NOTIFICATIONS.ACK);
				len = ids.length();
				if (len > 0) {
					StringBuilder query = new StringBuilder();
					ContentValues value = new ContentValues();
					value.put(DbStructure.MessageTable.COLUMN_STATE,
							Constants.NOTI_STATE.ACK_RECEIVED);
					query.append(" _id IN (");
					String[] args = new String[len];
					for (int i = 0; i < len; i++) {
						query.append(DbConstants.QUESTION_MARK);
						query.append(DbConstants.COMMA);
						args[i] = String.valueOf(ids.getInt(i));
					}
					query.deleteCharAt(query.length() - 1); // delete the last
															// comma
					query.append(DbConstants.BRACES_CLOSE);
					Utility.log(TAG, query.toString());
					db.update(DbStructure.NotificationTable.TABLE_NAME, value,
							query.toString(), args);
					// send broadcast to update notification list
					intent = new Intent(
							Constants.LOCAL_INTENT_ACTION.RELOAD_NOTIFICATIONS);
					LocalBroadcastManager.getInstance(context).sendBroadcast(
							intent);
				}
			}
			Utility.log(TAG, "done ack");
		} catch (JSONException e) {
			Utility.DEBUG(e);
		}
	}

	/**
	 * Insert notification into database
	 * 
	 * @author Zeeshan Khan
	 * 
	 */
	private static class InsertNotification extends
			AsyncTask<Notification, Void, Void> {

		@Override
		protected Void doInBackground(Notification... params) {
			long target_id, noti_id, file_id;
			ContentValues values = new ContentValues();
			ContentValues values_map = new ContentValues();
			Notification n = params[0];
			Attachment files;
			int len;
			// insert user mapper entry
			values.clear();
			values.put(DbStructure.UserMapper.COLUMN_COURSE, n.course);
			values.put(DbStructure.UserMapper.COLUMN_BRANCH, n.branch);
			values.put(DbStructure.UserMapper.COLUMN_YEAR, n.year);
			values.put(DbStructure.UserMapper.COLUMN_SECTION, n.section);
			target_id = db.insert(DbStructure.UserMapper.TABLE_NAME, null,
					values);
			// insert new notification and get its pk id
			values.clear();
			values.put(DbStructure.NotificationTable.COLUMN_SUBJECT, n.subject);
			values.put(DbStructure.NotificationTable.COLUMN_TEXT, n.text);
			values.put(DbStructure.NotificationTable.COLUMN_TIME, n.time);
			values.put(DbStructure.NotificationTable.COLUMN_STATE,
					Constants.NOTI_STATE.PENDING);
			values.put(DbStructure.NotificationTable.COLUMN_SENDER, n.sid);
			values.put(DbStructure.NotificationTable.COLUMN_TARGET, target_id);
			values.put(DbStructure.NotificationTable.COLUMN_FOR_FACULTY,
					n.for_faculty);
			noti_id = db.insert(DbStructure.NotificationTable.TABLE_NAME, null,
					values);
			// insert files into db here
			/**
			 * 1. pick pk from line above (notification_id for files) 2. insert
			 * new files to files table arraylist of files can be obtained from
			 * n.files :P ullu billui 3. insert name,url,state(check
			 * Constants.STATE docs for refrence),size 4. exit from here :D
			 */

			values.clear();
			len = n.files.size();
			Utility.log(TAG,"lenght :"+len);
			
			for (int i = 0; i < len; i++) {
				files = n.files.get(i);
				values.put(DbStructure.FileTable.COLUMN_URL, files.url);
				values.put(DbStructure.FileTable.COLUMN_NAME, files.name);
				values.put(DbStructure.FileTable.COLUMN_SENDER, n.sid);
				values.put(DbStructure.FileTable.COLUMN_SIZE, files.size);
				values.put(DbStructure.FileTable.COLUMN_STATE, Constants.FILE_STATE.PENDING);
				Utility.log(TAG, files.url);
				Utility.log(TAG, files.name);
				Utility.log(TAG, ""+n.sid);
				Utility.log(TAG, ""+files.size);
				file_id = db.insert(DbStructure.FileTable.TABLE_NAME, null,
						values);

				values.clear();
				values_map.clear();
				values_map.put(
						DbStructure.FileNotificationMapTable.COLUMN_NOTIFICATION_ID,
						noti_id);
				values_map.put(DbStructure.FileNotificationMapTable.COLUMN_FILE_ID,
						file_id);
				db.insert(DbStructure.FileNotificationMapTable.TABLE_NAME,
						null, values_map);
				values_map.clear();
			}
			return null;
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

	/**
	 * should be in service
	 * 
	 * @author Zeeshan Khan
	 * 
	 */
	private class FetchFilesOfNotification extends
			AsyncTask<Object, Integer, ArrayList<Attachment>> {
		int noti_id;

		@Override
		protected ArrayList<Attachment> doInBackground(Object... params) {
			noti_id = (Integer) params[1];
			ArrayList<Attachment> attachments = new ArrayList<Attachment>();
			String[] column = { DbStructure.FileTable.COLUMN_NAME,
					DbStructure.FileTable.COLUMN_STATE,
					DbStructure.FileTable.COLUMN_URL,
					DbStructure.FileTable._ID,
					DbStructure.FileTable.COLUMN_SIZE };
			StringBuilder query = new StringBuilder(
					"select "
							+ column[0]
							+ DbConstants.COMMA
							+ column[1]
							+ DbConstants.COMMA
							+ column[2]
							+ DbConstants.COMMA
							+ column[3]
							+ DbConstants.COMMA
							+ column[4]
							+ " from "
							+ DbStructure.FileTable.TABLE_NAME
							+ " join "
							+ DbStructure.FileNotificationMapTable.TABLE_NAME
							+ " on "
							+ DbStructure.FileNotificationMapTable.COLUMN_FILE_ID
							+ DbConstants.EQUALS
							+ DbStructure.FileTable._ID
							+ " where "
							+ DbStructure.FileNotificationMapTable.COLUMN_NOTIFICATION_ID
							+ DbConstants.EQUALS + noti_id);
			Cursor c = db.rawQuery(query.toString(), null);
			Utility.log(TAG, query.toString());
			c.moveToFirst();
			Attachment tmp;
			while (!c.isAfterLast()) {
				tmp = new Attachment(c.getInt(c
						.getColumnIndexOrThrow(column[3])), c.getString(c
						.getColumnIndexOrThrow(column[0])), c.getInt(c
						.getColumnIndexOrThrow(column[1])), c.getString(c
						.getColumnIndexOrThrow(column[2])), c.getLong(c
						.getColumnIndexOrThrow(column[4])));
				attachments.add(tmp);
				c.moveToNext();
			}

			return attachments;
		}

		@Override
		protected void onPostExecute(ArrayList<Attachment> result) {
			/**
			 * send local broadcast to update list of notifications
			 */
			Intent intent = new Intent(
					Constants.LOCAL_INTENT_ACTION.RELOAD_ATTACHMENTS);
			intent.putExtra(Constants.INTENT_EXTRA.NOTIFICATION_ID, noti_id);
			if (result != null && result.size() > 0) {
				intent.putExtra(Constants.INTENT_EXTRA.HAS_ATTACHMENTS, true);
				intent.putParcelableArrayListExtra(
						Constants.INTENT_EXTRA.ATTACHMENTS_DATA, result);
			} else {
				intent.putExtra(Constants.INTENT_EXTRA.HAS_ATTACHMENTS, false);
			}
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		}
	}

}
