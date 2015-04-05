package in.siet.secure.Util;

public class Message {
	public long id, sender, receiver;
	public int state;
	public String text;
	public long time;
	public boolean is_group_msg;

	public Message(long sender_, long receiver_, String text_, long time_,
			boolean is_group_msg_) {
		sender = sender_;
		receiver = receiver_;
		text = text_;
		time = time_;
		is_group_msg = is_group_msg_;
	}

	public Message(long sender_, long receiver_, String text_, long time_) {
		this(sender_, receiver_, text_, time_, false);
	}

}
