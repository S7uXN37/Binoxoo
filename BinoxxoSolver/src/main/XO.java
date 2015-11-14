package main;

public class XO {
	public static enum Type {
		X, O, BLANK;
		
		@Override
		public String toString() {
			switch(this) {
				case BLANK:
					return "_";
				case O:
					return "O";				
				case X:
					return "X";				
				default:
					return "?";
			}
		}
	};
	
	public Type type;
	public boolean userSet;
	
	public XO(Type type, boolean userSet) {
		this.type = type;
		this.userSet = userSet;
	}
	
	public static XO fromChar(char c) {
		Type type = c=='_' ? Type.BLANK : (c=='X' ? Type.X : Type.O);
		return new XO(type, false);
	}
}
