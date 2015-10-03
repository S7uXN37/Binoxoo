package main;

import java.util.ArrayList;

public class Util {
	public static int randomIntInRange(int lowerBound, int upperBound) {
		if(upperBound < lowerBound) throw new IllegalArgumentException("lowerBound must be smaller than upperBound");
		if(upperBound == lowerBound) return upperBound;
		double rand = Math.random();
		rand = rand*(upperBound-lowerBound);
		rand += lowerBound;
		float r = (float)rand;
		return Math.round(r);
	}
	
	public static int coordsToField(int x, int y) {
		return y*Game.GRID_SIZE_X + x;
	}
	
	public static int[] fieldToCoords(int field) {
		int x = field%Game.GRID_SIZE_X;
		int y = field/Game.GRID_SIZE_X;
		return new int[]{x,y};
	}
	
	/**
	 * @param field
	 * @return {LeftX, RightX, UpperY, LowerY, MidX, MidY}
	 */
	public static int[] fieldToAbsCoords(int field) {
		int GridX = field%Game.GRID_SIZE_X;
		int GridY = field/Game.GRID_SIZE_X;
		
		int Lx = Game.PX_PER_GRID * GridX + Game.BORDER_SIZE;
		int Rx = Game.PX_PER_GRID * (GridX+1) + Game.BORDER_SIZE;
		int Uy = Game.PX_PER_GRID * GridY + Game.BORDER_SIZE;
		int Ly = Game.PX_PER_GRID * (GridY+1) + Game.BORDER_SIZE;
		int MidX = (int) (Game.PX_PER_GRID * (GridX+0.5) + Game.BORDER_SIZE);
		int MidY = (int) (Game.PX_PER_GRID * (GridY+0.5) + Game.BORDER_SIZE);
		
		return new int[]{Lx, Rx, Uy, Ly, MidX, MidY};
	}
	
	public static int absCoordsToField(int x, int y) {
		int GridX = (x-Game.BORDER_SIZE)/Game.PX_PER_GRID;
		int GridY = (y-Game.BORDER_SIZE)/Game.PX_PER_GRID;
		return coordsToField(GridX, GridY);
	}
	
	public static XO[] xosToFieldMap(ArrayList<XO> xos) {
		XO[] map = new XO[Game.GRID_SIZE_X*Game.GRID_SIZE_Y];
		for(int i=0; i<map.length ;i++) {
			map[i] = new XO(i, XO.BLANK, false);
		}
		
		for(XO xo : xos) {
			map[xo.field] = xo;
		}
		
		return map;
	}
}
