package main;

public class XO {
	public static final int X = 0;
	public static final int O = 1;
	public static final int REMOVE = -1;
	
	public int field;
	public int type;
	public boolean userSet;
	
	public XO(int field, int type, boolean userSet) {
		this.field = field;
		this.type = type;
		this.userSet = userSet;
	}
}
