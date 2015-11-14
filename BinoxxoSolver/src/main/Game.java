package main;

import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.Log;

public class Game extends BasicGame
{
	public static int GRID_SIZE_X = 10;
	public static int GRID_SIZE_Y = 10;
	public static int PX_PER_GRID = 30;
	public static int BORDER_SIZE = 10;
	public static final Color NO_AI_COLOR = Color.white;
	public static final Color AI_COLOR = Color.yellow;
	public static final Color ERR_COLOR = Color.red;
	public static final Color SLN_COLOR = Color.green;
	public static final Color GRID_COLOR = new Color(1F, 1F, 1F, 0.5F);
	public static final Color BCKG_COLOR = Color.black;
	public static final Color XO_USER_COLOR = Color.white;
	public static final Color XO_GEN_COLOR = new Color(1F, 1F, 1F, 0.5F);
	
	public GameContainer gameContainer;
	
	private XO[] map;
	private boolean solved = false;
	private boolean closeRequested = false;
	private boolean usingAI = false;
	private boolean useAIOnce = false;
	protected org.newdawn.slick.Font xoFont;
	protected org.newdawn.slick.Font smallFont;
	protected static int WIDTH;
	protected static int HEIGHT;
	
	public Game(String gamename)
	{
		super(gamename);
		map = new XO[GRID_SIZE_X*GRID_SIZE_Y];
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		gameContainer = gc;
		gameContainer.setAlwaysRender(true);
		xoFont = new TrueTypeFont(new Font("Arial", Font.BOLD, PX_PER_GRID), true);
		smallFont = new TrueTypeFont(new Font("Arial", Font.PLAIN, 10), true);
		reset();
	}
	
	public void reset() {
		for(int i=0; i<GRID_SIZE_X*GRID_SIZE_Y ;i++) {
			map[i] = new XO(XO.Type.BLANK, false);
		}
		
		closeRequested = false;
		
		
		gameContainer.getInput().removeAllKeyListeners();
		gameContainer.getInput().addKeyListener(new InputInterface(this));
		gameContainer.getInput().removeAllMouseListeners();
		gameContainer.getInput().addMouseListener(new InputInterface(this));
	}
	
	boolean needsUpdate = true;
	boolean gridValid = true;
	int updatesPerSecond = 0;
	
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		updatesPerSecond = (updatesPerSecond + 1000/delta)/2;
		if(!needsUpdate && !useAIOnce) return;
		boolean hasBlank = false;
		for(XO xo : map) {
			if(xo.type==XO.Type.BLANK) {
				hasBlank = true;
				break;
			}
		}

		solved = !hasBlank && gridValid;
		gridValid = Solver.isGridValid(map);
		
		XO[] givenXOs = new XO[map.length];
		for(int m=0; m<map.length ;m++) {
			if(map[m].userSet) {
				givenXOs[m] = map[m];
			} else {
				givenXOs[m] = new XO(XO.Type.BLANK, false);
			}
		}
		
		if(usingAI || useAIOnce) {
			Solver.update(this, givenXOs);
			useAIOnce = false;
		}
		
		if(closeRequested) {
			Log.info("Close has been requested, exiting...");
			container.exit();
		}
	}

	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		// draw borders
		g.setColor(gridValid ? (solved ? SLN_COLOR : (usingAI ? AI_COLOR : NO_AI_COLOR) ) : ERR_COLOR);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(BCKG_COLOR);
		g.fillRect(BORDER_SIZE,
			BORDER_SIZE,
			WIDTH-2*BORDER_SIZE,
			HEIGHT-2*BORDER_SIZE
		);
		
		// draw grid
		g.setColor(GRID_COLOR);
		for(int i=1 ; i<GRID_SIZE_X ; i++) { // vertical lines
			g.drawLine(i*PX_PER_GRID + BORDER_SIZE,
				BORDER_SIZE,
				i*PX_PER_GRID + BORDER_SIZE,
				HEIGHT - BORDER_SIZE
			);
		}
		for(int i=1 ; i<GRID_SIZE_Y ; i++) { // horizontal lines
			g.drawLine(BORDER_SIZE,
				i*PX_PER_GRID + BORDER_SIZE,
				WIDTH - BORDER_SIZE,
				i*PX_PER_GRID + BORDER_SIZE
			);
		}
		
		// draw icons
		for(int i=0; i<map.length; i++) {
			XO xo = map[i];
			if(xo.userSet) {
				g.setColor(XO_USER_COLOR);
			} else {
				g.setColor(XO_GEN_COLOR);
			}
			
			g.setFont(xoFont);
			int[] absCoords = Util.fieldToAbsCoords(i);
			int x = (int) (absCoords[0] + ((float) PX_PER_GRID) * 0.25F);
			int y = (int) (absCoords[2] - ((float) PX_PER_GRID) * 0.15F);
			
			if(xo.type == XO.Type.X) {
				g.drawString("x", x, y);
			} else if(xo.type == XO.Type.O) {
				g.drawString("o", x, y);
			}
		}
		
		// draw update counter
		g.setColor(Color.black);
		g.setFont(smallFont);
		g.drawString(""+updatesPerSecond, 2, -2);;
	}
	
	public void setXO(XO.Type type, int field, boolean userSet) {
		map[field] = new XO(type, userSet);
		needsUpdate = true;
	}
	
	public void toggleUseAI() {
		usingAI = !usingAI;
	}
	
	public static void main(String[] args) {
		if(GRID_SIZE_X%2!=0 || GRID_SIZE_Y%2!=0) {
			throw new IllegalStateException("Grid sizes are invalid!");
		}
		
		try
		{
			WIDTH = PX_PER_GRID * GRID_SIZE_X + BORDER_SIZE*2;
			HEIGHT = PX_PER_GRID * GRID_SIZE_Y + BORDER_SIZE*2;
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Game("Binoxxo Solver"));
			appgc.setDisplayMode(WIDTH, HEIGHT, false);
			appgc.setShowFPS(false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void close() {
		closeRequested = true;
	}

	public void updateMap(XO[] newMap) {
		map = newMap.clone();
	}

	public void useAIOnce() {
		useAIOnce = true;
	}
}