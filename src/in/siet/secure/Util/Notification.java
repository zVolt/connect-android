package in.siet.secure.Util;


public class Notification {
	public static String SUBJECT="Subject";
	public static String TEXT="Text";
	public static String TIME="Time";
	public static String SENDER_IMAGE="Image";
	public static String ID="ID";
	public static String ATTACHMENT="Attachments";
	
	public String image;
	public String text;
	public String subject;
	public int[] attachments_id;
	public String time; 
	public int id;
	public Notification(int pid,String uimg,String title,String txt,String t){
		id=pid;
		time=t;
		subject=title;
		image=uimg;
		text=txt;
	}
	
}
