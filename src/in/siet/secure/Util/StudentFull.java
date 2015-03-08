package in.siet.secure.Util;

public class StudentFull extends Student {
	public String u_roll_no;

	public StudentFull(String f_name_, String l_name_, String user_id_,
			String picUrl_, int year_, String section_, String u_roll) {
		super(f_name_, l_name_, user_id_, picUrl_, year_, section_);
		u_roll_no = u_roll;
	}

	public StudentFull() {
		super();
	}

	@Override
	public String toString() {
		String COMMA = ",";
		return "[" + u_roll_no + COMMA + super.toString() + "]";
	}
}
