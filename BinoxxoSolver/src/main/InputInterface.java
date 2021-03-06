package main;

import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.MouseListener;

public class InputInterface implements KeyListener, MouseListener {
	private Game game;
	
	public InputInterface(Game g) {
		game = g;
	}
	
	@Override
	public void setInput(Input input) {}
	
	@Override
	public boolean isAcceptingInput() {
		return true;
	}
	
	@Override
	public void inputEnded() {}
	
	@Override
	public void inputStarted() {}
	
	@Override
	public void keyPressed(int key, char c) {
		switch(key) {
			case Input.KEY_ESCAPE:
				game.close();
				break;
			case Input.KEY_R:
				game.reset();
				break;
			case Input.KEY_SPACE:
				game.toggleUseAI();
				break;
			case Input.KEY_F:
				game.useAIOnce();
				break;
		}
	}
	
	@Override
	public void keyReleased(int key, char c) {}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {}

	@Override
	public void mousePressed(int button, int x, int y) {
		XO.Type type;
		switch(button) {
			case Input.MOUSE_LEFT_BUTTON:
				type = XO.Type.X;
				break;
			case Input.MOUSE_RIGHT_BUTTON:
				type = XO.Type.O;
				break;
			case Input.MOUSE_MIDDLE_BUTTON:
				type = XO.Type.BLANK;
				break;
			default:
				return;
		}
		if(Util.absCoordsToField(x, y)<Game.GRID_SIZE_X*Game.GRID_SIZE_Y && Util.absCoordsToField(x, y)>=0) {
			game.setXO(type, Util.absCoordsToField(x, y), true);
		}
	}

	@Override
	public void mouseReleased(int button, int x, int y) {}

	@Override
	public void mouseWheelMoved(int change) {}
}
