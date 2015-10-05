package main;

import java.util.ArrayList;

public class Solver {
	final static int maxXOinX = Game.GRID_SIZE_X/2;
	final static int maxXOinY = Game.GRID_SIZE_Y/2;
	
	public static boolean isGridValid(XO[] map) {
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
		if(Util.hasSameElements(columns)) {
			return false;
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
		if(Util.hasSameElements(rows)) {
			return false;
		}
		
		// no pairs of three, not too many XO's, no rows or columns match => valid
		return true;
	}
	
	public static void update(Game game, XO[] map) {
		XO[] deducedMap = map.clone();
		boolean changed;
		do {
			changed = false;
			changed |= gaps(deducedMap);						// gaps
			changed |= pairsOfTwo(deducedMap);				// pairs of two
			changed |= XOcount(deducedMap);					// XO count (one missing)
			changed |= onlyValidCombination(deducedMap);	// only valid combination
		} while(changed);
		
		game.updateMap(deducedMap);
	}
	
	private static boolean gaps(XO[] map) {
		for(int col=0; col<Game.GRID_SIZE_X ;col++) {
			int type0 = -1; // will be cycled
			int type1 = map[Util.coordsToField(col, 0)].type;
			int type2 = map[Util.coordsToField(col, 1)].type;
			for(int y=2; y<Game.GRID_SIZE_Y ;y++) {
				type0 = type1; // cycle variables
				type1 = type2;
				type2 = map[Util.coordsToField(col, y)].type;
				
				if(type0==type2 && type0!=XO.BLANK && type1==XO.BLANK) { // gap found
					map[Util.coordsToField(col, y-1)] = new XO(Util.coordsToField(col, y-1), (type0==XO.X ? XO.O : XO.X), false);
					return true;
				}
			}
		}
		
		for(int row=0; row<Game.GRID_SIZE_Y ;row++) {
			int type0 = -1; // will be cycled
			int type1 = map[Util.coordsToField(0, row)].type;
			int type2 = map[Util.coordsToField(1, row)].type;
			
			for(int x=2; x<Game.GRID_SIZE_X ;x++) {
				type0 = type1; // cycle variables
				type1 = type2;
				type2 = map[Util.coordsToField(x, row)].type;
				
				if(type0==type2 && type0!=XO.BLANK && type1==XO.BLANK) { // gap found
					map[Util.coordsToField(x-1, row)] = new XO(Util.coordsToField(x-1, row), (type0==XO.X ? XO.O : XO.X), false);
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static boolean pairsOfTwo(XO[] map) {
		for(int col=0; col<Game.GRID_SIZE_X ;col++) {
			int type0 = -1; // will be cycled
			int type1 = map[Util.coordsToField(col, 0)].type;
			
			for(int y=1; y<Game.GRID_SIZE_Y ;y++) {
				type0 = type1; // cycle variables
				type1 = map[Util.coordsToField(col, y)].type;
				
				if(type0==type1 && type0!=XO.BLANK) { // pair found
					boolean changed = false;
					if(y!=Game.GRID_SIZE_Y-1 && map[Util.coordsToField(col, y+1)].type == XO.BLANK) {
						map[Util.coordsToField(col, y+1)] = new XO(Util.coordsToField(col, y+1), (type0==XO.X ? XO.O : XO.X), false);
						changed = true;
					}
					if(y!=1 && map[Util.coordsToField(col, y-2)].type == XO.BLANK) {
						map[Util.coordsToField(col, y-2)] = new XO(Util.coordsToField(col, y-2), (type0==XO.X ? XO.O : XO.X), false);
						changed = true;
					}
					if(changed) {
						return true;
					}
				}
			}
		}
		
		for(int row=0; row<Game.GRID_SIZE_Y ;row++) {
			int type0 = -1; // will be cycled
			int type1 = map[Util.coordsToField(0, row)].type;
			
			for(int x=1; x<Game.GRID_SIZE_X ;x++) {
				type0 = type1; // cycle variables
				type1 = map[Util.coordsToField(x, row)].type;
				
				if(type0==type1 && type0!=XO.BLANK) { // pair found
					boolean changed = false;
					if(x!=Game.GRID_SIZE_X-1 && map[Util.coordsToField(x+1, row)].type == XO.BLANK) {
						map[Util.coordsToField(x+1, row)] = new XO(Util.coordsToField(x+1, row), (type0==XO.X ? XO.O : XO.X), false);
						changed = true;
					}
					if(x!=1 && map[Util.coordsToField(x-2, row)].type == XO.BLANK) {
						map[Util.coordsToField(x-2, row)] = new XO(Util.coordsToField(x-2, row), (type0==XO.X ? XO.O : XO.X), false);
						changed = true;
					}
					if(changed) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private static boolean XOcount(XO[] map) {
		for(int x=0; x<Game.GRID_SIZE_X ;x++) {
			int xs = 0;
			int os = 0;
			ArrayList<Integer> blankYs = new ArrayList<Integer>();
			
			for(int y=0; y<Game.GRID_SIZE_Y ;y++)  {
				switch(map[Util.coordsToField(x, y)].type) {
					case XO.X:
						xs++;
						break;
					case XO.O:
						os++;
						break;
					case XO.BLANK:
						blankYs.add(y);
						break;
				}
			}
			
			int fillType = XO.BLANK;
			if(xs==maxXOinY) {			// only O's missing
				fillType = XO.O;
			} else if(os==maxXOinY) {	// only X's missing
				fillType = XO.X;
			}
			if(fillType!=XO.BLANK && blankYs.size()>0) {
				for(int y : blankYs) {
					map[Util.coordsToField(x, y)] = new XO(Util.coordsToField(x, y), fillType, false);
				}
				return true;
			}
		}
		
		for(int y=0; y<Game.GRID_SIZE_Y ;y++) {
			int xs = 0;
			int os = 0;
			ArrayList<Integer> blankXs = new ArrayList<Integer>();
			
			for(int x=0; x<Game.GRID_SIZE_X ;x++)  {
				switch(map[Util.coordsToField(x, y)].type) {
					case XO.X:
						xs++;
						break;
					case XO.O:
						os++;
						break;
					case XO.BLANK:
						blankXs.add(x);
						break;
				}
			}
			
			int fillType = XO.BLANK;
			if(xs==maxXOinX) {			// only O's missing
				fillType = XO.O;
			} else if(os==maxXOinX) {	// only X's missing
				fillType = XO.X;
			}
			if(fillType!=XO.BLANK && blankXs.size()>0) {
				for(int x : blankXs) {
					map[Util.coordsToField(x, y)] = new XO(Util.coordsToField(x, y), fillType, false);
				}
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean onlyValidCombination(XO[] map) {
		// TODO Auto-generated method stub
		return false;
	}
}
