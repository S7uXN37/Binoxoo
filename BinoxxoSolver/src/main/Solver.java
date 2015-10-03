package main;

import java.util.ArrayList;

public class Solver {
	public static boolean isGridValid(XO[] map) {
		int maxXOinX = Game.GRID_SIZE_X/2;
		int maxXOinY = Game.GRID_SIZE_Y/2;
		
		for(int x=0; x<Game.GRID_SIZE_X ;x++) {
			boolean lastCheckMatched = false;
			int xs = 0;
			int os = 0;
			
			for(int y=0; y<Game.GRID_SIZE_Y ;y++) {
				// check for three in a row
				if(y!=0) {
					if(map[Util.coordsToField(x, y)].type == map[Util.coordsToField(x, y-1)].type && map[Util.coordsToField(x, y)].type != XO.BLANK) {
						if(lastCheckMatched) {
							return false;
						} else {
							lastCheckMatched = true;
						}
					} else {
						lastCheckMatched = false;
					}
				}
				
				// check for too many X's or O's
				if(map[Util.coordsToField(x, y)]!=null) {
					switch(map[Util.coordsToField(x, y)].type) {
						case XO.X:
							xs++;
							break;
						case XO.O:
							os++;
							break;
					}
					if(xs>maxXOinY || os>maxXOinY) {
						return false;
					}
				}
			}
		}
		
		for(int y=0 ; y<Game.GRID_SIZE_Y ;y++) {
			boolean lastCheckMatched = false;
			int xs = 0;
			int os = 0;
			
			for(int x=0; x<Game.GRID_SIZE_Y ;x++) {
				// check for three in a row
				if(x!=0) {
					if(map[Util.coordsToField(x, y)].type == map[Util.coordsToField(x-1, y)].type && map[Util.coordsToField(x, y)].type != XO.BLANK) {
						if(lastCheckMatched) {
							return false;
						} else {
							lastCheckMatched = true;
						}
					} else {
						lastCheckMatched = false;
					}
				}
				
				// check for too many X's or O's
				switch(map[Util.coordsToField(x, y)].type) {
					case XO.X:
						xs++;
						break;
					case XO.O:
						os++;
						break;
				}
				if(xs>maxXOinX || os>maxXOinX) {
					return false;
				}
			}
		}
		
		// check for same columns
		ArrayList<String> columns = new ArrayList<String>();
		for(int x=0; x<Game.GRID_SIZE_X ;x++) {
			String column = "";
			for(int y=0; y<Game.GRID_SIZE_Y ;y++) {
				switch(map[Util.coordsToField(x, y)].type) {
					case XO.X:
						column += "X";
						break;
					case XO.O:
						column += "O";
						break;
				}
			}
			if(column.length()==Game.GRID_SIZE_Y) {
				columns.add(column);
			}
		}
		for(int i=0; i<columns.size()-1 ;i++) {
			String col1 = columns.get(i);
			for(int m=i+1; m<columns.size() ;m++) {
				String col2 = columns.get(m);
				if(col1.equals(col2)) {
					return false;
				}
			}
		}
		
		// check for same rows
		ArrayList<String> rows = new ArrayList<String>();
		for(int y=0; y<Game.GRID_SIZE_Y ;y++) {
			String row = "";
			for(int x=0; x<Game.GRID_SIZE_X ;x++) {
				switch(map[Util.coordsToField(x, y)].type) {
					case XO.X:
						row += "X";
						break;
					case XO.O:
						row += "O";
						break;
				}
			}
			if(row.length()==Game.GRID_SIZE_X) {
				rows.add(row);
			}
		}
		for(int i=0; i<rows.size()-1 ;i++) {
			String row1 = rows.get(i);
			for(int m=i+1; m<rows.size() ;m++) {
				String row2 = rows.get(m);
				if(row1.equals(row2)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static void update(Game game, XO[] map) {
		
	}
}
