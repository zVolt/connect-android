package in.siet.secure.Util;

import java.util.Date;


public class Notification {
	//for Bundle arguments
	public static String SUBJECT="Subject";
	public static String TEXT="Text";
	public static String TIME="Time";
	public static String SENDER_IMAGE="Image";
	public static String ID="ID";
	public static String ATTACHMENT="Attachments";
	public static interface STATE{
		public static int FETCHED=0;
		public static int CREATED=1;
		public static int SENT=2;
	}
	public String image;
	public String text;
	public String subject;
	public String time;
	public Date dtime;
	public int id;
	public String sid;
	public int state;
	//fetched
	public Notification(int pid,String uimg,String title,String txt,String t){
		id=pid;
		time=t;
		subject=title;
		image=uimg;
		text=txt;
	}
	//new
	public Notification(String title,String txt,Date t,String pid){
		dtime=t;
		subject=title;
		sid=pid;
		text=txt;
	}
}
