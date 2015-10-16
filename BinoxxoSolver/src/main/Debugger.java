package main;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class Debugger {
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 800;
	public static final int BORDER_SIZE = 50;
	public static final int LOGGER_W = 320;
	public static final int LOGGER_H = 400;
	
	private static final Color BCKG_COLOR = Color.lightGray;
	
	private boolean enabled = false;
	private Game gameInst;
	
	public LoggingModule logger;
	public VisualizerModule visualizer;
	public ButeForceModule graphics;
	public ControlPanelModule controls;

	public Debugger(boolean show, Game instance) {
		gameInst = instance;
		enabled = show;
		logger = new LoggingModule(Game.WIDTH + BORDER_SIZE, 0, LOGGER_W, LOGGER_H);
		visualizer = new VisualizerModule(Game.WIDTH + BORDER_SIZE*2 + LOGGER_W, 0, gameInst);
		graphics = new ButeForceModule(Game.WIDTH + BORDER_SIZE*2 + LOGGER_W, Game.HEIGHT + BORDER_SIZE, gameInst);
		controls = new ControlPanelModule(Game.BORDER_SIZE, Game.HEIGHT + BORDER_SIZE, Game.WIDTH-2 * Game.BORDER_SIZE);
	}
	
	public void render(GameContainer gc, Graphics g) {
		if(!enabled) {
			return;
		} else {
			g.setColor(BCKG_COLOR);
			g.fillRect(0, 0, WIDTH, HEIGHT);
			
			logger.render(gc, g);
			visualizer.render(gc, g);
			graphics.render(gc, g);
			controls.render(gc, g);
		}
	}
}

class LoggingModule {
	private static final Color BCKG_COLOR = Color.white;
	
	private int x,y,w,h;
	
	public LoggingModule(int x, int y, int w, int h) {
		this.w = w;
		this.h = h;
		this.x = x;
		this.y = y;
	}
	
	public void render(GameContainer gc, Graphics g) {
		// background
		g.setColor(BCKG_COLOR);
		g.fillRect(x, y, w, h);
	}
}

class VisualizerModule {
	private int x,y;
	private Game game;
	public XO[] map = new XO[Game.GRID_SIZE_X * Game.GRID_SIZE_Y];
	
	public VisualizerModule(int x, int y, Game instance) {
		game = instance;
		this.x = x;
		this.y = y;
		for(int i=0; i<map.length; i++) {
			map[i] = new XO(i, XO.Type.BLANK, false);
		}
	}
	
	public void render(GameContainer gc, Graphics g) {
		// draw borders
		g.setColor(Game.NO_AI_COLOR);
		g.fillRect(x, y, Game.WIDTH, Game.HEIGHT);
		
		// draw background
		g.setColor(Game.BCKG_COLOR);
		g.fillRect(Game.BORDER_SIZE + x,
			Game.BORDER_SIZE + y,
			Game.WIDTH-2*Game.BORDER_SIZE,
			Game.HEIGHT-2*Game.BORDER_SIZE
		);
		
		// draw grid
		g.setColor(Game.GRID_COLOR);
		for(int i=1 ; i<Game.GRID_SIZE_X ; i++) { // vertical lines
			g.drawLine(i*Game.PX_PER_GRID + Game.BORDER_SIZE + x,
					Game.BORDER_SIZE + y,
				i*Game.PX_PER_GRID + Game.BORDER_SIZE + x,
				Game.HEIGHT - Game.BORDER_SIZE + y
			);
		}
		for(int i=1 ; i<Game.GRID_SIZE_Y ; i++) { // horizontal lines
			g.drawLine(Game.BORDER_SIZE + x,
				i*Game.PX_PER_GRID + Game.BORDER_SIZE + y,
				Game.WIDTH - Game.BORDER_SIZE + x,
				i*Game.PX_PER_GRID + Game.BORDER_SIZE + y
			);
		}
		
		// draw icons
		for(XO xo : map) {
			if(xo.userSet) {
				g.setColor(Game.XO_USER_COLOR);
			} else {
				g.setColor(Game.XO_GEN_COLOR);
			}
			
			g.setFont(game.font);
			int[] absCoords = Util.fieldToAbsCoords(xo.field);
			int x = (int) (absCoords[0] + ((float) Game.PX_PER_GRID) * 0.25F);
			int y = (int) (absCoords[2] - ((float) Game.PX_PER_GRID) * 0.15F);
			
			if(xo.type == XO.Type.X) {
				g.drawString("x", x + this.x, y + this.y);
			} else if(xo.type == XO.Type.O) {
				g.drawString("o", x + this.x, y + this.y);
			}
		}
	}
}

class ButeForceModule {
	private int x,y;
	private Game game;
	public XO[] map = new XO[Game.GRID_SIZE_X * Game.GRID_SIZE_Y];
	
	public ButeForceModule(int x, int y, Game instance) {
		game = instance;
		this.x = x;
		this.y = y;
		for(int i=0; i<map.length; i++) {
			map[i] = new XO(i, XO.Type.BLANK, false);
		}
	}
	
	public void render(GameContainer gc, Graphics g) {
		// draw borders
		g.setColor(Game.NO_AI_COLOR);
		g.fillRect(x, y, Game.WIDTH, Game.HEIGHT);
		
		// draw background
		g.setColor(Game.BCKG_COLOR);
		g.fillRect(Game.BORDER_SIZE + x,
			Game.BORDER_SIZE + y,
			Game.WIDTH-2*Game.BORDER_SIZE,
			Game.HEIGHT-2*Game.BORDER_SIZE
		);
		
		// draw grid
		g.setColor(Game.GRID_COLOR);
		for(int i=1 ; i<Game.GRID_SIZE_X ; i++) { // vertical lines
			g.drawLine(i*Game.PX_PER_GRID + Game.BORDER_SIZE + x,
					Game.BORDER_SIZE + y,
				i*Game.PX_PER_GRID + Game.BORDER_SIZE + x,
				Game.HEIGHT - Game.BORDER_SIZE + y
			);
		}
		for(int i=1 ; i<Game.GRID_SIZE_Y ; i++) { // horizontal lines
			if(i+1==Game.GRID_SIZE_Y) {
				for(int a=0; a<3; a++) {
					g.drawLine(Game.BORDER_SIZE + x,
						i*Game.PX_PER_GRID + Game.BORDER_SIZE + y + a,
						Game.WIDTH - Game.BORDER_SIZE + x,
						i*Game.PX_PER_GRID + Game.BORDER_SIZE + y + a
					);
				}
			} else {
				g.drawLine(Game.BORDER_SIZE + x,
					i*Game.PX_PER_GRID + Game.BORDER_SIZE + y,
					Game.WIDTH - Game.BORDER_SIZE + x,
					i*Game.PX_PER_GRID + Game.BORDER_SIZE + y
				);
			}
		}
		
		// draw icons
		for(XO xo : map) {
			if(xo.userSet) {
				g.setColor(Game.XO_USER_COLOR);
			} else {
				g.setColor(Game.XO_GEN_COLOR);
			}
			
			g.setFont(game.font);
			int[] absCoords = Util.fieldToAbsCoords(xo.field);
			int x = (int) (absCoords[0] + ((float) Game.PX_PER_GRID) * 0.25F);
			int y = (int) (absCoords[2] - ((float) Game.PX_PER_GRID) * 0.15F);
			
			if(xo.type == XO.Type.X) {
				g.drawString("x", x + this.x, y + this.y);
			} else if(xo.type == XO.Type.O) {
				g.drawString("o", x + this.x, y + this.y);
			}
		}
	}
	
	public void setLine(int line, XO[] newLine) {
		if(line>=Game.GRID_SIZE_Y || line<0) {
			throw new IllegalArgumentException("line must be inside bounds of min=0, max=" + (Game.GRID_SIZE_Y-1));
		} else {
			for(int i=0; i<Game.GRID_SIZE_X; i++) {
				if(i<=newLine.length) {
					map[Util.coordsToField(i, line)] = newLine[i];
				} else {
					map[Util.coordsToField(i, line)] = new XO(Util.coordsToField(i, line), XO.Type.BLANK, false);
				}
			}
		}
	}
}

class ControlPanelModule {
	public static final int HEIGHT = 100;
	public static final Color BCKG_COLOR = Color.white;
	
	private int x,y, WIDTH;
	
	public ControlPanelModule(int x, int y, int w) {
		this.x = x;
		this.y = y;
		WIDTH = w;
	}
	
	public void render(GameContainer gc, Graphics g) {
		// background
		g.setColor(BCKG_COLOR);
		g.fillRect(x, y, WIDTH, HEIGHT);
	}
}