package main;

import java.awt.Font;
import java.util.ArrayList;
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
	public static final Color GRID_COLOR = new Color(1F, 1F, 1F, 0.5F);
	public static final Color BCKG_COLOR = Color.black;
	public static final Color XO_USER_COLOR = Color.white;
	public static final Color XO_GEN_COLOR = new Color(1F, 1F, 1F, 0.5F);
	
	public GameContainer gameContainer;
	
	private ArrayList<XO> xos = new ArrayList<XO>();
	private boolean closeRequested = false;
	private boolean usingAI = false;
	private org.newdawn.slick.Font font;
	protected static int WIDTH;
	protected static int HEIGHT;
	
	public Game(String gamename)
	{
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		gameContainer = gc;
		gameContainer.setAlwaysRender(true);
		font = new TrueTypeFont(new Font("Arial", Font.BOLD, PX_PER_GRID), true);
		reset();
	}
	
	public void reset() {
		xos.clear();
		
		closeRequested = false;
		
		gameContainer.getInput().removeAllKeyListeners();
		gameContainer.getInput().addKeyListener(new InputInterface(this));
		gameContainer.getInput().removeAllMouseListeners();
		gameContainer.getInput().addMouseListener(new InputInterface(this));
	}
	
	boolean needsUpdate = false;
	boolean gridValid = true;
	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		gridValid = Solver.isGridValid(Util.xosToFieldMap(xos));
		
		if(gridValid && usingAI) {
			Solver.update(this, Util.xosToFieldMap(xos));
		}
		
		if(closeRequested) {
			Log.info("Close has been requested, exiting...");
			gc.exit();
		}
	}

	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		// draw borders
		g.setColor(gridValid ? (usingAI ? AI_COLOR : NO_AI_COLOR) : ERR_COLOR);
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
		for(XO xo : xos) {
			if(xo.userSet) {
				g.setColor(XO_USER_COLOR);
			} else {
				g.setColor(XO_GEN_COLOR);
			}
			
			g.setFont(font);
			int[] absCoords = Util.fieldToAbsCoords(xo.field);
			int x = (int) (absCoords[0] + ((float) PX_PER_GRID) * 0.25F);
			int y = (int) (absCoords[2] - ((float) PX_PER_GRID) * 0.15F);
			
			if(xo.type == XO.X) {
				g.drawString("x", x, y);
			} else {
				g.drawString("o", x, y);
			}
		}
	}
	
	public void setXO(int type, int field, boolean userSet) {
		XO existingXO = null;
		for(XO xo : xos) {
			if(xo.field == field) {
				existingXO = xo;
				break;
			}
		}
		if(existingXO != null) {
			xos.remove(existingXO);
		}
		
		if(type!=XO.REMOVE) {
			xos.add(new XO(field, type, userSet));
		}
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
}