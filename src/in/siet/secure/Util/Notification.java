package in.siet.secure.Util;

public class Notification {
	// for Bundle arguments
	public static String SUBJECT = "Subject";
	public static String TEXT = "Text";
	public static String TIME = "Time";
	public static String SENDER_IMAGE = "Image";
	public static String ID = "ID";
	public static String ATTACHMENT = "Attachments";

	public static interface STATE {
		public static int FETCHED = 0;
		public static int CREATED = 1;
		public static int SENT = 2;
	}

	public String image, text, subject, time, branch, section, course;
	public long dtime;
	public int sender_id, state, year;
	public int sid; // sender ki primary key according to local table

	/**
	 * Notification fetched from server should be instantiated using this
	 * constructor
	 * 
	 * @param pk_user_id
	 * @param image_
	 * @param subject_
	 * @param text_
	 * @param time_
	 */
	public Notification(int pk_user_id, String image_, String subject_,
			String text_, String time_) {
		sender_id = pk_user_id;
		time = time_;
		subject = subject_;
		image = image_;
		text = text_;
	}

	/**
	 * New notification created should be instantiated using this constructor
	 * The parameters course_ branch_ section_ year_ is used to identify target
	 * 
	 * @param subject_
	 *            String Subject of the notification
	 * @param text_
	 *            String Content of notification
	 * @param time_
	 *            Long Time in milliseconds
	 * @param pk_user_id
	 *            integer Sender's primary key
	 * @param course_
	 *            String Course name
	 * @param branch_
	 *            String branch name
	 * @param section_
	 *            String section name
	 * @param year_
	 *            String year
	 * 
	 * 
	 */
	public Notification(String subject_, String text_, long time_,
			int pk_user_id, String course_, String branch_, String section_,
			int year_) {
		dtime = time_;
		subject = subject_;
		text = text_;
		sid = pk_user_id;

		// target audience
		course = course_;
		branch = branch_;
		year = year_;
		section = section_;
	}
}
