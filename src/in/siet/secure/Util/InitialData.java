package in.siet.secure.Util;

import java.util.ArrayList;

public class InitialData {
	
	public ArrayList<Courses> courses=new ArrayList<Courses>();
	public ArrayList<Branches> branches=new ArrayList<Branches>();
	public ArrayList<Sections> sections=new ArrayList<Sections>();
	public ArrayList<Year> years=new ArrayList<Year>();
	
	public static class Courses{
		public int id;
		public String name;
		public int duration;
	}
	public static class Branches{
		public int id;
		public String name;
		public int course_id;
	}
	public static class Sections{
		public int id;
		public String name;
		public int year_id;
	}
	public static class Year{
		public int id;
		public int branch_id;
		public int year;
	}
	
}
