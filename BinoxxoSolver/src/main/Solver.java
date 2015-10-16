package main;

import java.util.ArrayList;

import org.newdawn.slick.util.Log;

public class Solver {
	final static int maxXOinX = Game.GRID_SIZE_X/2;
	final static int maxXOinY = Game.GRID_SIZE_Y/2;
	
	public static boolean isGridValid(XO[] map) {
		for(int x=0; x<Game.GRID_SIZE_X; x++) {
			boolean lastCheckMatched = false;
			int xs = 0;
			int os = 0;
			
			for(int y=0; y<Game.GRID_SIZE_Y; y++) {
				// check for three in a row
				if(y!=0) {
					if(map[Util.coordsToField(x, y)].type == map[Util.coordsToField(x, y-1)].type && map[Util.coordsToField(x, y)].type != XO.Type.BLANK) {
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
						case X:
							xs++;
							break;
						case O:
							os++;
							break;
						default:
							break;
					}
					if(xs>maxXOinY || os>maxXOinY) {
						return false;
					}
				}
			}
		}
		
		for(int y=0; y<Game.GRID_SIZE_Y; y++) {
			boolean lastCheckMatched = false;
			int xs = 0;
			int os = 0;
			
			for(int x=0; x<Game.GRID_SIZE_Y; x++) {
				// check for three in a row
				if(x!=0) {
					if(map[Util.coordsToField(x, y)].type == map[Util.coordsToField(x-1, y)].type && map[Util.coordsToField(x, y)].type != XO.Type.BLANK) {
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
					case X:
						xs++;
						break;
					case O:
						os++;
						break;
					default:
						break;
				}
				if(xs>maxXOinX || os>maxXOinX) {
					return false;
				}
			}
		}
		
		// check for same columns
		ArrayList<String> columns = new ArrayList<String>();
		for(int x=0; x<Game.GRID_SIZE_X; x++) {
			String column = "";
			for(int y=0; y<Game.GRID_SIZE_Y; y++) {
				switch(map[Util.coordsToField(x, y)].type) {
					case X:
						column += "X";
						break;
					case O:
						column += "O";
						break;
					default:
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
		for(int y=0; y<Game.GRID_SIZE_Y; y++) {
			String row = "";
			for(int x=0; x<Game.GRID_SIZE_X; x++) {
				switch(map[Util.coordsToField(x, y)].type) {
					case X:
						row += "X";
						break;
					case O:
						row += "O";
						break;
					default:
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
			if(!changed) changed |= gaps(deducedMap);						// gaps
			if(!changed) changed |= pairsOfTwo(deducedMap);				// pairs of two
			if(!changed) changed |= XOcount(deducedMap);					// XO count (one missing)
			if(!changed) changed |= onlyValidCombination(deducedMap);	// only valid combination
		} while(changed);
		
		game.updateMap(deducedMap);
	}
	
	private static boolean gaps(XO[] map) {
		for(int col=0; col<Game.GRID_SIZE_X; col++) {
			XO.Type type0 = XO.Type.BLANK; // will be cycled
			XO.Type type1 = map[Util.coordsToField(col, 0)].type;
			XO.Type type2 = map[Util.coordsToField(col, 1)].type;
			for(int y=2; y<Game.GRID_SIZE_Y; y++) {
				type0 = type1; // cycle variables
				type1 = type2;
				type2 = map[Util.coordsToField(col, y)].type;
				
				if(type0==type2 && type0!=XO.Type.BLANK && type1==XO.Type.BLANK) { // gap found
					map[Util.coordsToField(col, y-1)] = new XO(Util.coordsToField(col, y-1), (type0==XO.Type.X ? XO.Type.O : XO.Type.X), false);
					return true;
				}
			}
		}
		
		for(int row=0; row<Game.GRID_SIZE_Y; row++) {
			XO.Type type0 = XO.Type.BLANK; // will be cycled
			XO.Type type1 = map[Util.coordsToField(0, row)].type;
			XO.Type type2 = map[Util.coordsToField(1, row)].type;
			
			for(int x=2; x<Game.GRID_SIZE_X; x++) {
				type0 = type1; // cycle variables
				type1 = type2;
				type2 = map[Util.coordsToField(x, row)].type;
				
				if(type0==type2 && type0!=XO.Type.BLANK && type1==XO.Type.BLANK) { // gap found
					map[Util.coordsToField(x-1, row)] = new XO(Util.coordsToField(x-1, row), (type0==XO.Type.X ? XO.Type.O : XO.Type.X), false);
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static boolean pairsOfTwo(XO[] map) {
		for(int col=0; col<Game.GRID_SIZE_X; col++) {
			XO.Type type0 = XO.Type.BLANK; // will be cycled
			XO.Type type1 = map[Util.coordsToField(col, 0)].type;
			
			for(int y=1; y<Game.GRID_SIZE_Y; y++) {
				type0 = type1; // cycle variables
				type1 = map[Util.coordsToField(col, y)].type;
				
				if(type0==type1 && type0!=XO.Type.BLANK) { // pair found
					boolean changed = false;
					if(y!=Game.GRID_SIZE_Y-1 && map[Util.coordsToField(col, y+1)].type == XO.Type.BLANK) {
						map[Util.coordsToField(col, y+1)] = new XO(Util.coordsToField(col, y+1), (type0==XO.Type.X ? XO.Type.O : XO.Type.X), false);
						changed = true;
					}
					if(y!=1 && map[Util.coordsToField(col, y-2)].type == XO.Type.BLANK) {
						map[Util.coordsToField(col, y-2)] = new XO(Util.coordsToField(col, y-2), (type0==XO.Type.X ? XO.Type.O : XO.Type.X), false);
						changed = true;
					}
					if(changed) {
						return true;
					}
				}
			}
		}
		
		for(int row=0; row<Game.GRID_SIZE_Y; row++) {
			XO.Type type0 = XO.Type.BLANK; // will be cycled
			XO.Type type1 = map[Util.coordsToField(0, row)].type;
			
			for(int x=1; x<Game.GRID_SIZE_X; x++) {
				type0 = type1; // cycle variables
				type1 = map[Util.coordsToField(x, row)].type;
				
				if(type0==type1 && type0!=XO.Type.BLANK) { // pair found
					boolean changed = false;
					if(x!=Game.GRID_SIZE_X-1 && map[Util.coordsToField(x+1, row)].type == XO.Type.BLANK) {
						map[Util.coordsToField(x+1, row)] = new XO(Util.coordsToField(x+1, row), (type0==XO.Type.X ? XO.Type.O : XO.Type.X), false);
						changed = true;
					}
					if(x!=1 && map[Util.coordsToField(x-2, row)].type == XO.Type.BLANK) {
						map[Util.coordsToField(x-2, row)] = new XO(Util.coordsToField(x-2, row), (type0==XO.Type.X ? XO.Type.O : XO.Type.X), false);
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
		for(int x=0; x<Game.GRID_SIZE_X; x++) {
			int xs = 0;
			int os = 0;
			ArrayList<Integer> blankYs = new ArrayList<Integer>();
			
			for(int y=0; y<Game.GRID_SIZE_Y; y++)  {
				switch(map[Util.coordsToField(x, y)].type) {
					case X:
						xs++;
						break;
					case O:
						os++;
						break;
					case BLANK:
						blankYs.add(y);
						break;
				}
			}
			
			XO.Type fillType = XO.Type.BLANK;
			if(xs==maxXOinY) {			// only O's missing
				fillType = XO.Type.O;
			} else if(os==maxXOinY) {	// only X's missing
				fillType = XO.Type.X;
			}
			if(fillType!=XO.Type.BLANK && blankYs.size()>0) {
				for(int y : blankYs) {
					map[Util.coordsToField(x, y)] = new XO(Util.coordsToField(x, y), fillType, false);
				}
				return true;
			}
		}
		
		for(int y=0; y<Game.GRID_SIZE_Y; y++) {
			int xs = 0;
			int os = 0;
			ArrayList<Integer> blankXs = new ArrayList<Integer>();
			
			for(int x=0; x<Game.GRID_SIZE_X; x++)  {
				switch(map[Util.coordsToField(x, y)].type) {
					case X:
						xs++;
						break;
					case O:
						os++;
						break;
					case BLANK:
						blankXs.add(x);
						break;
				}
			}
			
			XO.Type fillType = XO.Type.BLANK;
			if(xs==maxXOinX) {			// only O's missing
				fillType = XO.Type.O;
			} else if(os==maxXOinX) {	// only X's missing
				fillType = XO.Type.X;
			}
			if(fillType!=XO.Type.BLANK && blankXs.size()>0) {
				for(int x : blankXs) {
					map[Util.coordsToField(x, y)] = new XO(Util.coordsToField(x, y), fillType, false);
				}
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean onlyValidCombination(XO[] map) {
		if(true) return false; // TODO fix code and remove
		
		boolean changed = false;
		for(int atCol=0; atCol<Game.GRID_SIZE_X; atCol++) {
			ArrayList<Integer> blankInd = new ArrayList<Integer>();
			XO[] column = new XO[Game.GRID_SIZE_Y];
			
			for(int y=0; y<Game.GRID_SIZE_Y; y++) {
				if(map[Util.coordsToField(atCol, y)].type==XO.Type.BLANK) {
					blankInd.add(y);
				}
				column[y] = map[Util.coordsToField(atCol, y)];
			}
			
			if(blankInd.size()==0) {
				continue;
			}
			
			int combinations = (int) Math.pow(2, blankInd.size());
			
			XO[][] possibleCombinations = new XO[Game.GRID_SIZE_Y][combinations];
			for(int c=0; c<combinations; c++) { // for each combination
				for(int i=0; i<Game.GRID_SIZE_Y; i++) { // fill the combination-data with what exists
					possibleCombinations[i][c] = column[i];
				}
			}
			
			for(int seed=0; seed<combinations; seed++) {	// seed = what combination I'm at
				String bin_seed = Integer.toBinaryString(seed);
				for(int i=0; i<blankInd.size(); i++) { 		// fill each blank; i = what blank is being filled
					int y = blankInd.get(i);				// y = y-coordinate of the blank that's being filled
					char charAtY = bin_seed.length()>i ? bin_seed.charAt(i) : '0';
					switch(charAtY) {
						case '1':
							possibleCombinations[y][seed] = new XO(Util.coordsToField(atCol, y), XO.Type.X, false);
							break;
						case '0':
							possibleCombinations[y][seed] = new XO(Util.coordsToField(atCol, y), XO.Type.O, false);
							break;
					}
				}
			}
			
			ArrayList<Integer> validCombinations = new ArrayList<Integer>();
			for(int c=0; c<combinations; c++) {
				XO[] combination = new XO[Game.GRID_SIZE_Y];
				for(int i=0; i<combination.length; i++) {
					combination[i] = possibleCombinations[i][c];
				}
				
				XO[] wc_map = map.clone();
				for(int y=0; y<Game.GRID_SIZE_Y; y++) {
					wc_map[Util.coordsToField(atCol, y)] = combination[y];
				}
				
				if(isGridValid(wc_map)) {
					validCombinations.add(c);
					if(atCol==2) { // DEBUGGING TODO why does it output the same combination multiple times? why are all of those considered valid?
						String v = "";
						for(XO xo : combination) {
							v += xo.type==XO.Type.BLANK ? "?" : (xo.type==XO.Type.X ? " X " : " O ");
						}
						Log.info("Valid: " + v);
					}
				}
			}
			
			if(validCombinations.size()==0) {
				continue;
			}
			
			XO[] conclusion = null;
			for(int c : validCombinations) { // for each valid combination update the conclusion
				if(conclusion==null) { // if it's the first time, fill in the first valid combination as conclusion
					conclusion = new XO[Game.GRID_SIZE_Y];
					for(int i=0; i<Game.GRID_SIZE_Y; i++) {
						conclusion[i] = possibleCombinations[i][c];
					}
				} else { // if it's not, update the conclusion
					for(int i=0; i<Game.GRID_SIZE_Y; i++) {
						if(conclusion[i]!=null) { // conclusion exists
							if(conclusion[i].type!=possibleCombinations[i][c].type) { // conclusion incorrect
								conclusion[i] = null;
							}
						}
					}
				}
			}
			
			for(int y=0; y<conclusion.length; y++) {
				if(conclusion[y]!=null && map[Util.coordsToField(atCol, y)].type==XO.Type.BLANK) { // there is a conclusion on a blank field
					map[Util.coordsToField(atCol, y)] = conclusion[y]; // fill it in
					changed = true;
				}
			}
		}
		
		for(int atRow=0; atRow<Game.GRID_SIZE_Y; atRow++) {
			ArrayList<Integer> blankInd = new ArrayList<Integer>();
			XO[] row = new XO[Game.GRID_SIZE_X];
			
			for(int x=0; x<Game.GRID_SIZE_X; x++) {
				if(map[Util.coordsToField(x, atRow)].type==XO.Type.BLANK) {
					blankInd.add(x);
				}
				row[x] = map[Util.coordsToField(x, atRow)];
			}
			
			if(blankInd.size()==0) {
				continue;
			}
			
			int combinations = (int) Math.pow(2, blankInd.size());
			if(((double)combinations)!=Math.pow(2, blankInd.size())) {
				Log.info("Calculation of all posibilities failed for row " + atRow + ", err code 1");
				continue;
			}
			
			XO[][] possibleCombinations = new XO[Game.GRID_SIZE_X][combinations];
			for(int c=0; c<combinations; c++) { // for each combination
				for(int i=0; i<Game.GRID_SIZE_X; i++) { // fill the combination-data with what exists
					possibleCombinations[i][c] = row[i];
				}
			}
			
			for(int seed=0; seed<combinations; seed++) {	// seed = what combination I'm at
				String bin_seed = Integer.toBinaryString(seed);
				for(int i=0; i<blankInd.size(); i++) { 		// fill each blank; i = what blank is being filled
					int x = blankInd.get(i);				// y = y-coordinate of the blank that's being filled
					char charAtY = bin_seed.length()>i ? bin_seed.charAt(i) : '0';
					switch(charAtY) {
						case '1':
							possibleCombinations[x][seed] = new XO(Util.coordsToField(x, atRow), XO.Type.X, false);
							break;
						case '0':
							possibleCombinations[x][seed] = new XO(Util.coordsToField(x, atRow), XO.Type.O, false);
							break;
					}
				}
			}
			
			ArrayList<Integer> validCombinations = new ArrayList<Integer>();
			for(int c=0; c<combinations; c++) {
				XO[] combination = new XO[Game.GRID_SIZE_X];
				for(int i=0; i<combination.length; i++) {
					combination[i] = possibleCombinations[i][c];
				}
				
				XO[] wc_map = map.clone();
				for(int x=0; x<Game.GRID_SIZE_X; x++) {
					wc_map[Util.coordsToField(x, atRow)] = combination[x];
				}
				
				if(isGridValid(wc_map)) {
					validCombinations.add(c);
				}
			}
			
			if(validCombinations.size()==0) {
				continue;
			}
			
			XO[] conclusion = null;
			for(int c : validCombinations) { // for each valid combination update the conclusion
				if(conclusion==null) { // if it's the first time, fill in the first valid combination as conclusion
					conclusion = new XO[Game.GRID_SIZE_X];
					for(int i=0; i<Game.GRID_SIZE_X; i++) {
						conclusion[i] = possibleCombinations[i][c];
					}
				} else { // if it's not, update the conclusion
					for(int i=0; i<Game.GRID_SIZE_X; i++) {
						if(conclusion[i]!=null) { // conclusion exists
							if(conclusion[i].type!=possibleCombinations[i][c].type) { // conclusion incorrect
								conclusion[i] = null;
							}
						}
					}
				}
			}
			
			for(int x=0; x<conclusion.length; x++) {
				if(conclusion[x]!=null && map[Util.coordsToField(x, atRow)].type==XO.Type.BLANK) { // there is a conclusion on a blank field
					map[Util.coordsToField(x, atRow)] = conclusion[x]; // fill it in
					changed = true;
				}
			}
		}
		
		return changed;
	}
}
