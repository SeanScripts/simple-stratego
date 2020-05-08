import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Stratego extends Canvas implements MouseListener {
	final static int WIDTH = 1260;
	final static int HEIGHT = 1080;
	static int debounce = 0;
	static int frameCountdown = 0;
	static Color landColor = new Color(0x337711);
	static Color waterColor = new Color(0x005599);
	static String playerTeam = "R";
	static String opponentTeam = "B";
	static Color playerColor = new Color(0xdd2222);
	static Color opponentColor = new Color(0x2222dd);
	static Font font;
	static Font font2;
	static int scale = 100;
	static boolean debug = false;
	ArrayList<Animation> animationQueue;
	Image mem;
	Graphics gr;
	int turncount = 0;
	boolean turn = true;
	Board board;
	int clicktx = -1;
	int clickty = -1;
	String phase;
	Rectangle mainButton = new Rectangle(1010, 100, 220, 100);
	double aggression = 1.0; //Max "difficulty"?
	
	public static void main(String[] args) {
		JFrame f = new JFrame("Stratego");
		f.setDefaultCloseOperation(3);
		f.setSize(WIDTH, HEIGHT);
		Stratego game = new Stratego();
		f.add(game);
		f.setVisible(true);
		
		boolean running = true;
		while (running) {
			game.step();
			game.repaint();
			debounce--;
			frameCountdown--;
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Draw a String centered in the middle of a Rectangle.
	 *
	 * @param g The Graphics instance.
	 * @param text The String to draw.
	 * @param rect The Rectangle to center the text in.
	 */
	public static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
	    // Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    // Set the font
	    g.setFont(font);
	    // Draw the String
	    g.drawString(text, x, y);
	}
	
	public Stratego() {
		//Initialize
		addMouseListener(this);
		font = new Font("Consolas", 0, 48);
		font2 = new Font("Consolas", 0, 24);
		board = new Board();
		phase = "SETUP"; //Lets you swap your pieces before the game starts
		animationQueue = new ArrayList<Animation>();
		//aggression = Math.random();
	}
	
	public void turnStep() {
		board.refreshMoves();
		turncount++;
		turn = !turn;
		if (!turn) {
			//Opponent turn... AI???
			opponentMove(aggression);
			waitForAnimations();
			//waitFrames(60);
		}
		
	}
	
	public void step() {
		//Advances animations in queue
		for (Animation anim : animationQueue) {
			anim.advance();
		}
		//Delete finished animations
		for (int i = animationQueue.size()-1; i >= 0; i--) {
			if (animationQueue.get(i).isDone) {
				animationQueue.remove(i);
			}
		}
		//Is the AI turn over?
		if (!turn && frameCountdown == 0) {
			turnStep();
		}
	}
	
	public void opponentMove(double aggression) {
		//Do something.
		for (int x = 0; x < Board.size; x++) {
			for (int y = 0; y < Board.size; y++) {
				String v = board.get(x, y);
				if (v.endsWith(opponentTeam)) {
					//Attempt to move with probability aggression
					if (Math.random() < aggression) {
						//Attempt to move this piece. First advance if possible
						String moveType = board.moveType(turn, x, y, x, y+1);
						executeMovement(moveType, x, y, x, y+1);
						if (moveType.equals("INVALID")) {
							//Okay, fine. Try moving another direction? Sideways, equal chance of each
							int xoff = 1;
							if (Math.random() < 0.5) {
								xoff = -1;
							}
							moveType = board.moveType(turn, x, y, x+1*xoff, y);
							executeMovement(moveType, x, y, x+1*xoff, y);
							if (moveType.equals("INVALID")) {
								moveType = board.moveType(turn, x, y, x-1*xoff, y);
								executeMovement(moveType, x, y, x-1*xoff, y);
								if (moveType.equals("INVALID")) {
									//Retreat, maybe...
									if (Math.random() < 1-aggression) {
										moveType = board.moveType(turn, x, y, x, y-1);
										executeMovement(moveType, x, y, x, y-1);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void paint(Graphics g) {
		mem = createImage(WIDTH, HEIGHT);
		gr = mem.getGraphics();
		
		gr.setColor(Color.black);
		gr.fillRect(0, 0, WIDTH, HEIGHT);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				//Tile backgrounds
				String val = board.get(i, j);
				if (val.equals("W")) {
					gr.setColor(waterColor);
					gr.fillRect(i*scale, j*scale, scale, scale);
				}
				else {
					gr.setColor(landColor);
					gr.fillRect(i*scale, j*scale, scale, scale);
				}
				//Tile foregrounds
				if (!val.isEmpty() && !val.equals("W")) {
					//This is a soldier of some sort
					//To lower case is important: Lower case is a hidden variable telling whether the piece has been moved this turn.
					if (val.toLowerCase().endsWith(playerTeam.toLowerCase())) {
						//Visible
						gr.setColor(playerColor);
						gr.fillRect(i*scale+scale/8, j*scale+scale/8, scale*3/4, scale*3/4);
						gr.setColor(Color.white);
						drawCenteredString(gr, val.substring(0, val.length()-1), new Rectangle(i*scale+scale/8, j*scale+scale/8, scale*3/4, scale*3/4), font);
					}
					else {
						gr.setColor(opponentColor);
						gr.fillRect(i*scale+scale/8, j*scale+scale/8, scale*3/4, scale*3/4);
						if (debug) {
							gr.setColor(Color.white);
							drawCenteredString(gr, val.substring(0, val.length()-1), new Rectangle(i*scale+scale/8, j*scale+scale/8, scale*3/4, scale*3/4), font);
						}
					}
				}
				//Tile outlines
				gr.setColor(Color.white);
				gr.drawRect(i*scale, j*scale, scale, scale);
			}
		}
		
		//Animations
		for (Animation anim : animationQueue) {
			anim.render(gr);
		}
			
		gr.setColor(Color.white);
		if (phase.equals("SETUP")) {
			drawCenteredString(gr, "Setup", new Rectangle(1000, 0, 260, 100), font2);
		}
		else if (phase.equals("PLAYING")) {
			if (turn) {
				drawCenteredString(gr, "Your turn", new Rectangle(1000, 0, 260, 100), font2);
			}
			else {
				drawCenteredString(gr, "Enemy turn", new Rectangle(1000, 0, 260, 100), font2);
			}
		}
		else if (phase.equals("VICTORY")) {
			drawCenteredString(gr, "Victory!", new Rectangle(1000, 0, 260, 100), font2);
		}
		else if (phase.equals("DEFEAT")) {
			drawCenteredString(gr, "Defeat!", new Rectangle(1000, 0, 260, 100), font2);
		}
		
		gr.setColor(Color.white);
		gr.fillRect(mainButton.x, mainButton.y, mainButton.width, mainButton.height);
		gr.setColor(Color.black);
		if (phase.equals("SETUP")) {
			drawCenteredString(gr, "Start", mainButton, font);
		}
		else if (phase.equals("PLAYING")) {
			if (turn) {
				drawCenteredString(gr, "End turn", mainButton, font);
			}
			else {
				drawCenteredString(gr, "Wait", mainButton, font);
			}
		}
		else if (phase.equals("VICTORY")) {
			drawCenteredString(gr, "Again", mainButton, font);
		}
		else if (phase.equals("DEFEAT")) {
			drawCenteredString(gr, "Again", mainButton, font);
		}
		
		g.drawImage(mem, 0, 0, null);
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	
	public void reset() {
		board.init();
		turncount = 0;
		turn = true;
		phase = "SETUP";
		animationQueue = new ArrayList<Animation>();
		aggression = Math.random();
	}
	
	public void waitFrames(int num) {
		frameCountdown = num;
	}
	
	public void waitForAnimations() {
		if (animationQueue.size() > 0) {
			frameCountdown = animationQueue.get(animationQueue.size()-1).length - animationQueue.get(animationQueue.size()-1).frame;
		}
		else {
			frameCountdown = 10;
		}
	}
	
	public void executeMovement(String moveType, int x, int y, int nx, int ny) {
		if (moveType.equals("MOVE")) {
			animationQueue.add(new Animation("MOVE", x, y, nx, ny, board.get(x, y), board.get(nx, ny)));
			board.move(x, y, nx, ny);
		}
		else if (moveType.equals("COMBAT")) {
			String combatResult = board.combatResult(turn, x, y, nx, ny);
			//System.out.println(combatResult);
			if (combatResult.equals("VICTORY")) {
				animationQueue.add(new Animation("VICTORY", x, y, nx, ny, board.get(x, y), board.get(nx, ny)));
				//TODO: You win!
				phase = "VICTORY";
				board.lockGame();
			}
			else if (combatResult.equals("DEFEAT")) {
				animationQueue.add(new Animation("DEFEAT", x, y, nx, ny, board.get(x, y), board.get(nx, ny)));
				//TODO: You lose!
				phase = "DEFEAT";
				board.lockGame();
			}
			else {
				if (combatResult.equals("DRAW")) {
					//boom
					animationQueue.add(new Animation("BOOM", x, y, nx, ny, board.get(x, y), board.get(nx, ny)));
				}
				else if (combatResult.equals("WIN")) {
					animationQueue.add(new Animation("WIN", x, y, nx, ny, board.get(x, y), board.get(nx, ny)));
				}
				else if (combatResult.equals("LOSE")) {
					animationQueue.add(new Animation("LOSE", x, y, nx, ny, board.get(x, y), board.get(nx, ny)));
				}
				board.combat(turn, x, y, nx, ny);
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		//System.out.println(debounce);
		if (debounce < 0 && mainButton.contains(x, y)) {
			//Clicked on button
			if (phase.equals("SETUP")) {
				phase = "PLAYING";
				debounce = 10;
			}
			else if (phase.equals("PLAYING")) {
				turnStep();
				debounce = 10;
			}
			else {
				reset();
				debounce = 10;
			}
		}
	}
	
	public void mouseEntered(MouseEvent e) {}
	
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int tx = x/scale;
		int ty = y/scale;
		if (tx >= 0 && tx < 10 && ty >= 0 && ty < 10) {
			//valid
			clicktx = tx;
			clickty = ty;
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int tx = x/scale;
		int ty = y/scale;
		if (tx >= 0 && tx < 10 && ty >= 0 && ty < 10) {
			//valid
			if (tx == clicktx && ty == clickty) {
				clicktx = -1;
				clickty = -1;
			}
			else {
				//Action
				if (phase.equals("SETUP")) {
					if (board.get(clicktx, clickty).endsWith(playerTeam) && board.get(tx, ty).endsWith(playerTeam)) {
						animationQueue.add(new Animation("SWAP", clicktx, clickty, tx, ty, board.get(clicktx, clickty), board.get(tx, ty)));
						board.swap(clicktx, clickty, tx, ty);
					}
				}
				else if (phase.equals("PLAYING") && turn) {
					String moveType = board.moveType(turn, clicktx, clickty, tx, ty);
					executeMovement(moveType, clicktx, clickty, tx, ty);
				}
			}
		}
		else {
			clicktx = -1;
			clickty = -1;
		}
	}
}
