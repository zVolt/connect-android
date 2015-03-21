package in.siet.secure.Util;

public class Faculty extends User {
	public String branch;

	public Faculty(String f_name_, String l_name_, String picUrl_,
			String branch_, String user_id_) {
		super(f_name_, l_name_, picUrl_, user_id_);
		branch = branch_;
	}

	public Faculty() {
		super();
	}

	@Override
	public String toString() {
		String COMMA = ",";
		return "[" + branch + COMMA + super.toString() + "]";
	}
}