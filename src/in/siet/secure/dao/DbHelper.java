package in.siet.secure.dao;

import in.siet.secure.Util.Attachment;
import in.siet.secure.Util.Faculty;
import in.siet.secure.Util.FacultyFull;
import in.siet.secure.Util.InitialData;
import in.siet.secure.Util.Message;
import in.siet.secure.Util.MyJsonHttpResponseHandler;
import in.siet.secure.Util.Notification;
import in.siet.secure.Util.Student;
import in.siet.secure.Util.StudentFull;
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
import com.loopj.android.http.RequestParams;

public class DbHelper extends SQLiteOpenHelper {
	public static final String TAG = "in.siet.secure.dao.DbHelper";
	public static final String DATABASE_NAME = "sgi_app.db";
	public static int DATABASE_VERSION = 1;
	public static SQLiteDatabase db;
	private Context context;
	public static SharedPreferences spf;

	// private Intent intent;

	public DbHelper(Context contxt) {
		super(contxt, DATABASE_NAME, null, DATABASE_VERSION);
		context = contxt;
		spf = context.getSharedPreferences(Constants.PREF_FILE_NAME,
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

	/**
	 * clear all data including course details
	 */
	public void hardReset() {
		setDb();
		onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION);
	}

	public void clearCourseData() {
		setDb();
		db.execSQL(DbStructure.Branches.COMMAND_DROP);
		db.execSQL(DbStructure.Courses.COMMAND_DROP);
		db.execSQL(DbStructure.Sections.COMMAND_DROP);
		db.execSQL(DbStructure.Year.COMMAND_DROP);

		db.execSQL(DbStructure.Branches.COMMAND_CREATE);
		db.execSQL(DbStructure.Courses.COMMAND_CREATE);
		db.execSQL(DbStructure.Sections.COMMAND_CREATE);
		db.execSQL(DbStructure.Year.COMMAND_CREATE);
	}

	/**
	 * clear only user data courses details will be stored
	 */
	public void clearUserData() {
		setDb();
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
	 * @param not_id
	 */
	public void getFilesOfNotification(long not_id) {
		new FetchFilesOfNotification(not_id).execute();
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
	 * called on non UI thread
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
					"insert into messages(sender,receiver,text,time,is_group_msg,state) values");
			JSONObject obj;
			int j;
			String receiver_id = String.valueOf(getUserPk(receiver));
			String state_received = String
					.valueOf(Constants.MSG_STATE.RECEIVED);
			String[] args = new String[len * 6];

			try {
				for (int i = 0; i < len; i++) {
					query.append("((select _id from user where login_id=?),?,?,?,?,?)");
					query.append(DbConstants.COMMA);
					obj = messages.getJSONObject(i);
					j = i * 6;
					args[j] = obj.getString(Constants.JSONKEYS.MESSAGES.SENDER);
					args[j + 1] = receiver_id;
					args[j + 2] = Utility.decode(obj
							.getString(Constants.JSONKEYS.MESSAGES.TEXT));
					args[j + 3] = String.valueOf(obj
							.getLong(Constants.JSONKEYS.MESSAGES.TIME));
					args[j + 4] = String
							.valueOf(obj
									.getInt(Constants.JSONKEYS.MESSAGES.IS_GROUP_MESSAGE));
					args[j + 5] = state_received;
					ids.put(obj.getInt(Constants.JSONKEYS.MESSAGES.ID));

				}
				query.deleteCharAt(query.length() - 1);
				db.execSQL(query.toString(), args);

				// update user table for each msg

				db.execSQL("update user set last_msg_on=(select time from messages where sender=user._id or receiver=user._id order by time desc limit 1)");

				sendBroadcast(Constants.LOCAL_INTENT_ACTION.RELOAD_MESSAGES);
				sendBroadcast(Constants.LOCAL_INTENT_ACTION.RELOAD_CONTACTS);
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
		int len_files = -1;
		JSONObject file;
		JSONArray files;
		long noti_id, file_id;
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
					noti_id = db.insert(
							DbStructure.NotificationTable.TABLE_NAME, null,
							values);
					ids.put(notification
							.getInt(Constants.JSONKEYS.NOTIFICATIONS.ID));
					files = notification
							.getJSONArray(Constants.JSONKEYS.NOTIFICATIONS.ATTACHMENTS);
					len_files = files.length();
					for (int j = 0; j < len_files; j++) {
						file = files.getJSONObject(j);
						values.clear();
						String path = file
								.getString(Constants.JSONKEYS.FILES.URL);
						values.put(
								DbStructure.FileTable.COLUMN_SENDER,
								getUserPk(notification
										.getString(Constants.JSONKEYS.NOTIFICATIONS.SENDER)));
						values.put(DbStructure.FileTable.COLUMN_URL, path);
						values.put(DbStructure.FileTable.COLUMN_STATE,
								Constants.FILE_STATE.RECEIVED);
						values.put(DbStructure.FileTable.COLUMN_SIZE,
								file.getLong(Constants.JSONKEYS.FILES.SIZE));
						int idx = path.replaceAll("\\\\", "/").lastIndexOf("/");
						String filename = idx >= 0 ? path.substring(idx + 1)
								: path;
						values.put(DbStructure.FileTable.COLUMN_NAME, filename);
						file_id = db.insert(DbStructure.FileTable.TABLE_NAME,
								null, values);
						values.clear();
						values.put(
								DbStructure.FileNotificationMapTable.COLUMN_NOTIFICATION_ID,
								noti_id);
						values.put(
								DbStructure.FileNotificationMapTable.COLUMN_FILE_ID,
								file_id);
						db.insert(
								DbStructure.FileNotificationMapTable.TABLE_NAME,
								null, values);
						values.clear();
						Utility.log(TAG, "hiiisfkhdkjs  " + j + filename);
					}
					values.clear();
				}
				sendBroadcast(Constants.LOCAL_INTENT_ACTION.RELOAD_NOTIFICATIONS);
			} catch (Exception e) {
				Utility.DEBUG(e);
			}
		}
		return ids;
	}

	/**
	 * update the state of messages to state ACK_SENT
	 * 
	 * should be called from a non UI thread
	 * 
	 * @param msg_ids
	 *            {@link JSONArray} of msg_ids (Primary key to message table)
	 * 
	 */
	public void updateMsgState(JSONArray msg_ids) {
		try {
			int len = msg_ids.length();
			StringBuilder strb = new StringBuilder(" _id IN (");
			String[] ids = new String[len];
			for (int i = 0; i < len; i++) {
				ids[i] = msg_ids.getString(i);
				strb.append("?,");
			}
			strb.deleteCharAt(strb.length() - 1);
			strb.append(") and state<> ");
			strb.append(String.valueOf(Constants.NOTI_STATE.READ));
			ContentValues values = new ContentValues();
			values.put(DbStructure.MessageTable.COLUMN_STATE,
					Constants.MSG_STATE.ACK_SEND);
			setDb();
			db.update(DbStructure.MessageTable.TABLE_NAME, values,
					strb.toString(), ids);

		} catch (Exception e) {
			Utility.DEBUG(e);
		}
	}

	/**
	 * update the state of notification to stateACK_SENT
	 * 
	 * should be called from a non UI thread
	 * 
	 * @param msg_ids
	 *            {@link JSONArray} of msg_ids (Primary key of Notification
	 *            table)
	 */
	public void updateNotiState(JSONArray noti_ids) {
		try {
			int len = noti_ids.length();
			StringBuilder strb = new StringBuilder(" _id IN (");
			String[] ids = new String[len];
			for (int i = 0; i < len; i++) {
				ids[i] = noti_ids.getString(i);
				strb.append("?,");
			}
			strb.deleteCharAt(strb.length() - 1);
			strb.append(") and state<> ");
			strb.append(String.valueOf(Constants.NOTI_STATE.READ));
			ContentValues values = new ContentValues();
			values.put(DbStructure.NotificationTable.COLUMN_STATE,
					Constants.NOTI_STATE.ACK_SEND);
			setDb();
			db.update(DbStructure.NotificationTable.TABLE_NAME, values,
					strb.toString(), ids);

		} catch (Exception e) {
			Utility.DEBUG(e);
		}
	}

	/**
	 * Update the state column of notification
	 * 
	 * @param not_id
	 *            ID(primary key) of notification who's state is to be updated
	 * @param state
	 *            to which state the value should be updated see
	 *            {@link Constants.STATES}
	 */
	public void updateNotificationState(long not_id, int state) {
		new UpdateNotificationState(not_id, state).execute();
	}

	/**
	 * {@link AsyncTask} class to update the notification state
	 * 
	 * @author Zeeshan Khan
	 * 
	 */
	private class UpdateNotificationState extends AsyncTask<Void, Void, Void> {
		long notification_id;
		int state;

		public UpdateNotificationState(long not_id, int state_) {
			notification_id = not_id;
			state = state_;
		}

		@Override
		protected Void doInBackground(Void... params) {
			setDb();
			ContentValues values = new ContentValues();
			values.put(DbStructure.NotificationTable.COLUMN_STATE, state);

			db.update(DbStructure.NotificationTable.TABLE_NAME, values,
					" _id=?", new String[] { String.valueOf(notification_id) });
			return null;
		}
	}

	/**
	 * insert a users with full details
	 * 
	 * @param response
	 *            {@link JSONObject} having 2 {@link JSONArray} one with Student
	 *            details and other with faculty details
	 */
	public void insertUsers(JSONObject response) {
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
				 * don't insert Student users for now give an option to user
				 */
				Utility.log(
						TAG,
						"ignoring "
								+ response.getJSONArray(
										Constants.JSONKEYS.STUDENT).length()
								+ " new student senders :P");
			}
		} catch (Exception e) {
			Utility.DEBUG(e);

		}
	}

	/**
	 * user is either FacultyFull or StudentFull
	 * 
	 * @param user
	 * @param is_faculty
	 */
	public void insertUser(User user, boolean is_faculty) {
		setDb();
		ContentValues values = new ContentValues();
		values.put(DbStructure.UserTable.COLUMN_FNAME, user.f_name);
		values.put(DbStructure.UserTable.COLUMN_LNAME, user.l_name);
		values.put(DbStructure.UserTable.COLUMN_PROFILE_PIC, user.picUrl);
		values.put(DbStructure.UserTable.COLUMN_LOGIN_ID, user.user_id);

		long user_pk = db.insertWithOnConflict(
				DbStructure.UserTable.TABLE_NAME, null, values,
				SQLiteDatabase.CONFLICT_REPLACE);

		if (user_pk != -1) {
			if (!is_faculty) {
				StudentFull s_user = (StudentFull) user;
				String[] args = { s_user.section, String.valueOf(s_user.year) };
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
				values.put(DbStructure.StudentContactsTable.COLUMN_USER_ID,
						user_pk);
				values.put(DbStructure.StudentContactsTable.COLUMN_SECTION_ID,
						section_id);
				values.put(DbStructure.StudentContactsTable.COLUMN_ROLL_NO,
						s_user.u_roll_no);
				db.insert(DbStructure.StudentContactsTable.TABLE_NAME, null,
						values);
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
				values.put(DbStructure.FcultyContactsTable.COLUMN_BRANCH_ID,
						branch_id);
				values.put(DbStructure.FcultyContactsTable.COLUMN_USER_ID,
						user_pk);

				long facut_id = db.insert(
						DbStructure.FcultyContactsTable.TABLE_NAME, null,
						values);
				// adding additional details
				if (facut_id != -1) {
					FacultyFull f_usr = (FacultyFull) user;
					values.clear();
					values.put(DbStructure.UserInfoTable.COLUMN_USER_ID,
							facut_id);

					values.put(DbStructure.UserInfoTable.COLUMN_STATE,
							f_usr.state);

					values.put(DbStructure.UserInfoTable.COLUMN_CITY,
							f_usr.city);

					values.put(DbStructure.UserInfoTable.COLUMN_STREET,
							f_usr.street);

					values.put(DbStructure.UserInfoTable.COLUMN_P_MOB,
							f_usr.p_mob);
					values.put(DbStructure.UserInfoTable.COLUMN_H_MOB,
							f_usr.h_mob);
					values.put(DbStructure.UserInfoTable.COLUMN_PIN, f_usr.pin);
					db.insert(DbStructure.UserInfoTable.TABLE_NAME, null,
							values);
				}
			}
			Utility.RaiseToast(context, user.f_name + " " + user.l_name
					+ " added Sucessfully", false);
		} else {
			Utility.log(TAG, "Error inserting user");
			Utility.RaiseToast(context, user.f_name + " " + user.l_name
					+ " cannot be inserted", false);
		}
	}

	/**
	 * get user info from server asynchronously add it to database show toast
	 * getting data from server should be moved to service
	 * 
	 * @param user
	 * @param is_student
	 */
	public void getAndInsertUser(final User user, final boolean is_faculty) {
		setDb();
		RequestParams params = new RequestParams();
		Utility.putCredentials(params, spf);
		params.put(Constants.QueryParameters.GET_DETAILS_OF_USER_ID,
				user.user_id);
		params.put(Constants.QueryParameters.USER_TYPE, is_faculty);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Utility.getBaseURL(context) + "query/get_user_info", params,
				new MyJsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						User n_user;
						try {
							if (!is_faculty) {
								StudentFull s_full = new StudentFull(
										(Student) user);
								s_full.u_roll_no = response
										.getString(Constants.JSONKEYS.ROLL_NO);
								n_user = s_full;

							} else {
								FacultyFull f_user = new FacultyFull(
										(Faculty) user);

								if (response.has(Constants.JSONKEYS.STATE))
									f_user.state = response
											.getString(Constants.JSONKEYS.STATE);
								if (response.has(Constants.JSONKEYS.CITY))
									f_user.city = response
											.getString(Constants.JSONKEYS.CITY);
								if (response.has(Constants.JSONKEYS.STREET))
									f_user.street = response
											.getString(Constants.JSONKEYS.STREET);
								if (response.has(Constants.JSONKEYS.P_MOB))
									f_user.p_mob = response
											.getString(Constants.JSONKEYS.P_MOB);
								if (response.has(Constants.JSONKEYS.H_MOB))
									f_user.h_mob = response
											.getString(Constants.JSONKEYS.H_MOB);
								if (response.has(Constants.JSONKEYS.PIN))
									f_user.pin = response
											.getString(Constants.JSONKEYS.PIN);
								n_user = f_user;
							}
							insertUser(n_user, is_faculty);
						} catch (Exception e) {
							Utility.DEBUG(e);
						}
					}
				});

	}

	/**
	 * Inserts courses, branches, year and section data passed, and fill
	 * user_mapper table accordingly
	 * 
	 * @author Zeeshan Khan
	 * 
	 */
	public void addInitialData(InitialData idata) {
		setDb();
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
	}

	private void setDb() {
		if (db == null || !db.isOpen())
			db = this.getWritableDatabase();
	}

	public SQLiteDatabase getDb() {
		setDb();
		return db;
	}

	public long insertNewMessage(Message msg) {
		ContentValues values = new ContentValues();
		values.put(DbStructure.MessageTable.COLUMN_TEXT, msg.text);
		values.put(DbStructure.MessageTable.COLUMN_TIME, msg.time);
		values.put(DbStructure.MessageTable.COLUMN_SENDER, msg.sender);
		values.put(DbStructure.MessageTable.COLUMN_RECEIVER, msg.receiver);
		values.put(DbStructure.MessageTable.COLUMN_STATE,
				Constants.MSG_STATE.PENDING);
		values.put(DbStructure.MessageTable.COLUMN_IS_GRP_MSG,
				Constants.IS_GROUP_MSG.NO);

		long msg_id = db.insert(DbStructure.MessageTable.TABLE_NAME, null,
				values);
		db.execSQL("update user set last_msg_on=(select time from messages where sender=user._id or receiver=user._id order by time desc limit 1)");
		Utility.startBackgroundService(context);
		return msg_id;
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
							Constants.MSG_STATE.ACK_RECEIVED);
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
					Intent intent = new Intent(
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
	 * Insert notification into database
	 * 
	 * @author Zeeshan Khan
	 * 
	 */
	private class InsertNotification extends
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

			len = n.files.size();
			Utility.log(TAG, "" + len);

			for (int i = 0; i < len; i++) {
				files = n.files.get(i);
				values.clear();
				values.put(DbStructure.FileTable.COLUMN_URL, files.url);
				values.put(DbStructure.FileTable.COLUMN_NAME, files.name);

				values.put(DbStructure.FileTable.COLUMN_SENDER, n.sid);
				values.put(DbStructure.FileTable.COLUMN_SIZE, files.size);
				values.put(DbStructure.FileTable.COLUMN_STATE,
						Constants.FILE_STATE.PENDING);
				Utility.log(TAG, files.url);
				Utility.log(TAG, files.name);
				Utility.log(TAG, "" + n.sid);
				Utility.log(TAG, "" + files.size);

				file_id = db.insert(DbStructure.FileTable.TABLE_NAME, null,
						values);

				values.clear();
				values_map.clear();
				values_map
						.put(DbStructure.FileNotificationMapTable.COLUMN_NOTIFICATION_ID,
								noti_id);
				values_map.put(
						DbStructure.FileNotificationMapTable.COLUMN_FILE_ID,
						file_id);
				db.insert(DbStructure.FileNotificationMapTable.TABLE_NAME,
						null, values_map);
				values_map.clear();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Utility.startBackgroundService(context);
		}
	}

	/**
	 * should be in service
	 * 
	 * @author Zeeshan Khan
	 * 
	 */
	private class FetchFilesOfNotification extends
			AsyncTask<Void, Void, ArrayList<Attachment>> {
		long noti_id;

		public FetchFilesOfNotification(long not_id_) {
			noti_id = not_id_;
		}

		@Override
		protected ArrayList<Attachment> doInBackground(Void... params) {

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
			Utility.log(TAG,query.toString());
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

	private void sendBroadcast(String action) {
		Intent intent = new Intent(action);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
}
