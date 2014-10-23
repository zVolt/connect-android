package in.siet.secure.Util;


public class Notification {
	public static String SUBJECT="Subject";
	public static String TEXT="Text";
	public static String TIME="Time";
	public static String SENDER="Sender";
	public static String ID="ID";
	public static String ATTACHMENT="Attachments";
	public String sender_id;
	public String text;
	public String subject;
	public int[] attachments_id;
	public String time; 
	public int id;
	public Notification(int pid,String uid,String title,String txt,String t,int[] files){
		id=pid;
		time=t;
		subject=title;
		sender_id=uid;
		text=txt;
		attachments_id=files;
	}
	
}
