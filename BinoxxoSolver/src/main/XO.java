package main;

public class XO {
	public static enum Type { X, O, BLANK };
	
	public Type type;
	public boolean userSet;
	
	public XO(int field, Type type, boolean userSet) {
		this.type = type;
		this.userSet = userSet;
	}
}
