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
			if(!changed) changed |= pairsOfTwo(deducedMap);					// pairs of two
			if(!changed) changed |= XOcount(deducedMap);					// XO count (one missing)
			if(!changed) changed |= onlyValidCombination(deducedMap);		// only valid combination
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
					map[Util.coordsToField(col, y-1)] = new XO((type0==XO.Type.X ? XO.Type.O : XO.Type.X), false);
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
					map[Util.coordsToField(x-1, row)] = new XO((type0==XO.Type.X ? XO.Type.O : XO.Type.X), false);
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
						map[Util.coordsToField(col, y+1)] = new XO((type0==XO.Type.X ? XO.Type.O : XO.Type.X), false);
						changed = true;
					}
					if(y!=1 && map[Util.coordsToField(col, y-2)].type == XO.Type.BLANK) {
						map[Util.coordsToField(col, y-2)] = new XO((type0==XO.Type.X ? XO.Type.O : XO.Type.X), false);
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
						map[Util.coordsToField(x+1, row)] = new XO((type0==XO.Type.X ? XO.Type.O : XO.Type.X), false);
						changed = true;
					}
					if(x!=1 && map[Util.coordsToField(x-2, row)].type == XO.Type.BLANK) {
						map[Util.coordsToField(x-2, row)] = new XO((type0==XO.Type.X ? XO.Type.O : XO.Type.X), false);
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
					map[Util.coordsToField(x, y)] = new XO(fillType, false);
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
					map[Util.coordsToField(x, y)] = new XO(fillType, false);
				}
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean onlyValidCombination(XO[] map) {
		// if(true) return false; // TODO fix code and remove
		
		boolean changed = false;
		for(int x=0; x<Game.GRID_SIZE_X; x++) {
			ArrayList<Integer> blankInd = new ArrayList<Integer>();
			StringBuilder column = new StringBuilder(Game.GRID_SIZE_Y);
			
			for(int y=0; y<Game.GRID_SIZE_Y; y++) {
				String type;
				if( (type = map[Util.coordsToField(x, y)].type.toString() ) .equals("_")) {
					blankInd.add(y);
				}
				column.append(type);
			}
			
			if(blankInd.size()==0) { // no empty spots to fill, move on
				continue;
			}
			
			int combinations = (int) Math.pow(2, blankInd.size());
			
			StringBuilder[] possibleCombinations = new StringBuilder[combinations];
			for(int c=0; c<combinations; c++) { // pre-fill each combination
				possibleCombinations[c] = new StringBuilder(column);
			}
			
			for(int seed=0; seed<combinations; seed++) {	// set up all possible combinations; seed = what combination are we at TODO possibly very time consuming TODO also possibly wrong
				StringBuilder bin_s = new StringBuilder(Integer.toBinaryString(seed));
				while(column.length() > bin_s.length()) {
					bin_s.insert(0, "0");
				}
				bin_s.setLength(column.length());
				String bin_seed = bin_s.toString();
				
				for(int i=0; i<blankInd.size(); i++) { 		// fill each blank; i = what blank is being filled
					int place = blankInd.get(i);
					char type = (bin_seed.length()>i ? bin_seed.charAt(bin_seed.length()-(i+1)) : '0') == '1' ? 'X' : 'O'; // interpret bin_seed at i to 'X' or 'O'
					
					possibleCombinations[seed].setCharAt(place, type);
				}
			}
			
			ArrayList<Integer> validCombinationInd = new ArrayList<Integer>();
			XO[] wc_map = map.clone();
			
			for(int c=0; c<combinations; c++) {
				for(int i=0; i<Game.GRID_SIZE_Y; i++) { // place combination into map, TODO more efficient: only replace on blankInd
					wc_map[Util.coordsToField(x, i)] = XO.fromChar(possibleCombinations[c].charAt(i));
				}
				
				if(isGridValid(wc_map)) {
					validCombinationInd.add(c); // mark combination c as valid
				}
			}
			
			if(validCombinationInd.size()==0) {
				continue;
			}
			
			StringBuilder conclusion = new StringBuilder(possibleCombinations[ validCombinationInd.get(0) ].toString());
			for(int m=1; m<validCombinationInd.size(); m++) { // for each valid combination, update the conclusion
				String combination = possibleCombinations[ validCombinationInd.get(m) ].toString();
				
				for(int i=0; i<Game.GRID_SIZE_Y; i++) {
					if(conclusion.charAt(i)!=combination.charAt(i)) { // combination differs from conclusion
						conclusion.setCharAt(i, '?');
					}
				}
			}
			
			for(int i=0; i<conclusion.length(); i++) {
				if(conclusion.charAt(i)!='?' && map[Util.coordsToField(x, i)].type==XO.Type.BLANK) { // there is a conclusion on a blank field
					map[Util.coordsToField(x, i)] = XO.fromChar(conclusion.charAt(i)); // fill it in
					changed = true;
				}
			}
		}
		
		for(int y=0; y<Game.GRID_SIZE_Y; y++) {
			ArrayList<Integer> blankInd = new ArrayList<Integer>();
			StringBuilder column = new StringBuilder(Game.GRID_SIZE_X);
			
			for(int x=0; x<Game.GRID_SIZE_X; x++) {
				String type;
				if( (type = map[Util.coordsToField(x, y)].type.toString() ) .equals("_")) {
					blankInd.add(x);
				}
				column.append(type);
			}
			
			if(blankInd.size()==0) { // no empty spots to fill, move on
				continue;
			}
			
			int combinations = (int) Math.pow(2, blankInd.size());
			
			StringBuilder[] possibleCombinations = new StringBuilder[combinations];
			for(int c=0; c<combinations; c++) { // pre-fill each combination
				possibleCombinations[c] = new StringBuilder(column);
			}
			
			for(int seed=0; seed<combinations; seed++) {	// set up all possible combinations; seed = what combination are we at TODO possibly very time consuming TODO also possibly wrong
				StringBuilder bin_s = new StringBuilder(Integer.toBinaryString(seed));
				while(column.length() > bin_s.length()) {
					bin_s.insert(0, "0");
				}
				bin_s.setLength(column.length());
				String bin_seed = bin_s.toString();
				
				for(int i=0; i<blankInd.size(); i++) { 		// fill each blank; i = what blank is being filled
					int place = blankInd.get(i);
					char type = (bin_seed.length()>i ? bin_seed.charAt(bin_seed.length()-(i+1)) : '0') == '1' ? 'X' : 'O'; // interpret bin_seed at i to 'X' or 'O'
					
					possibleCombinations[seed].setCharAt(place, type);
				}
			}
			
			ArrayList<Integer> validCombinationInd = new ArrayList<Integer>();
			XO[] wc_map = map.clone();
			
			for(int c=0; c<combinations; c++) {
				for(int i=0; i<Game.GRID_SIZE_Y; i++) { // place combination into map, TODO more efficient: only replace on blankInd
					wc_map[Util.coordsToField(i, y)] = XO.fromChar(possibleCombinations[c].charAt(i));
				}
				
				if(isGridValid(wc_map)) {
					validCombinationInd.add(c); // mark combination c as valid
				}
			}
			
			if(validCombinationInd.size()==0) {
				continue;
			}
			
			StringBuilder conclusion = new StringBuilder(possibleCombinations[ validCombinationInd.get(0) ].toString());
			for(int m=1; m<validCombinationInd.size(); m++) { // for each valid combination, update the conclusion
				String combination = possibleCombinations[ validCombinationInd.get(m) ].toString();
				
				for(int i=0; i<Game.GRID_SIZE_Y; i++) {
					if(conclusion.charAt(i)!=combination.charAt(i)) { // combination differs from conclusion
						conclusion.setCharAt(i, '?');
					}
				}
			}
			
			for(int i=0; i<conclusion.length(); i++) {
				if(conclusion.charAt(i)!='?' && map[Util.coordsToField(i, y)].type==XO.Type.BLANK) { // there is a conclusion on a blank field
					map[Util.coordsToField(i, y)] = XO.fromChar(conclusion.charAt(i)); // fill it in
					changed = true;
				}
			}
		}
		
		return changed;
	}
}
