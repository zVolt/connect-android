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
	public String sid; // sender ki id like EMP-100

	/**
	 * Notification fetched from server should be instantiated using this
	 * constructor
	 * 
	 * @param pid
	 * @param uimg
	 * @param title
	 * @param txt
	 * @param t
	 */
	public Notification(int pid, String uimg, String title, String txt, String t) {
		sender_id = pid;
		time = t;
		subject = title;
		image = uimg;
		text = txt;
	}

	/**
	 * New notification created should be instantiated using this constructor
	 * 
	 * @param subject_
	 *            String Subject of the notification
	 * @param txt
	 *            String Content of notification
	 * @param time_
	 *            Long Time in milisecounds
	 * @param pid
	 *            String Sender id like EMP-100
	 * @param course_
	 *            String Course name
	 * @param branch_
	 *            String branch name
	 * @param section_
	 *            String section name
	 * @param year_
	 *            String year
	 * 
	 */
	public Notification(String subject_, String txt, long time_, String pid,
			String course_, String branch_, String section_, int year_) {
		dtime = time_;
		subject = subject_;
		text = txt;
		sid = pid;

		// target audience
		course = course_;
		branch = branch_;
		year = year_;
		section = section_;
	}
}
