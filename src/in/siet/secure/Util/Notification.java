package in.siet.secure.Util;


public class Notification {
	public String sender_id;
	public String text;
	public String subject;
	public int[] attachments_id;
	public String time; 
	public Notification(String id,String title,String txt,String t,int[] files){
		time=t;
		subject=title;
		sender_id=id;
		text=txt;
		attachments_id=files;
	}
}
