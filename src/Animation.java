import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Animation {
	String type;
	int x1;
	int y1;
	int x2;
	int y2;
	String v1;
	String v2;
	int length;
	int frame;
	boolean isDone;
	
	public Animation(String type, int x1, int y1, int x2, int y2, String v1, String v2) {
		this.type = type;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.v1 = v1;
		this.v2 = v2;
		if (type.equals("SWAP")) {
			length = 20;
		}
		else if (type.equals("BOOM")) {
			length = 60;
		}
		else if (type.equals("LOSE")) {
			length = 30;
		}
		else if (type.equals("WIN")) {
			length = 40;
		}
		else if (type.equals("MOVE")) {
			length = 10;  
		}
		else if (type.equals("VICTORY") || type.equals("DEFEAT")) {
			length = 120;
		}
		frame = 0;
		isDone = false;
	}
	
	public void advance() {
		frame++;
		if (frame == length) {
			isDone = true;
		}
	}
	
	public void render(Graphics gr) {
		//Assume both spots are land. Fill them over to not show the pieces which are actually there...
		gr.setColor(Stratego.landColor);
		gr.fillRect(x1*Stratego.scale, y1*Stratego.scale, Stratego.scale, Stratego.scale);
		gr.fillRect(x2*Stratego.scale, y2*Stratego.scale, Stratego.scale, Stratego.scale);
		//Including the border
		gr.setColor(Color.white);
		gr.drawRect(x1*Stratego.scale, y1*Stratego.scale, Stratego.scale, Stratego.scale);
		gr.drawRect(x2*Stratego.scale, y2*Stratego.scale, Stratego.scale, Stratego.scale);
		if (type.equals("SWAP")) {
			//Math to find circle centered at the midpoint of the two spots, going through them
			double r = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2))/2.0;
			double mx = (x1+x2)/2.0;
			double my = (y1+y2)/2.0;
			double theta0 = Math.atan2(y1-my, x1-mx);
			//First piece
			double theta1 = theta0 + Math.PI*frame/(double)length;
			double tx1 = mx + r*Math.cos(theta1);
			double ty1 = my + r*Math.sin(theta1);
			Rectangle r1 = new Rectangle((int)(tx1*Stratego.scale+Stratego.scale/8), (int)(ty1*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
			if (v1.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
				gr.setColor(Stratego.playerColor);
				gr.fillRect(r1.x, r1.y, r1.width, r1.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r1, Stratego.font);
			}
			else {
				gr.setColor(Stratego.opponentColor);
				gr.fillRect(r1.x, r1.y, r1.width, r1.height);
				if (Stratego.debug) {
					gr.setColor(Color.white);
					Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r1, Stratego.font);
				}
			}
			//Second piece
			double theta2 = theta1 + Math.PI;
			double tx2 = mx + r*Math.cos(theta2);
			double ty2 = my + r*Math.sin(theta2);
			Rectangle r2 = new Rectangle((int)(tx2*Stratego.scale+Stratego.scale/8), (int)(ty2*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
			if (v2.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
				gr.setColor(Stratego.playerColor);
				gr.fillRect(r2.x, r2.y, r2.width, r2.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v2.substring(0, v2.length()-1), r2, Stratego.font);
			}
			else {
				gr.setColor(Stratego.opponentColor);
				gr.fillRect(r2.x, r2.y, r2.width, r2.height);
				if (Stratego.debug) {
					gr.setColor(Color.white);
					Stratego.drawCenteredString(gr, v2.substring(0, v2.length()-1), r2, Stratego.font);
				}
			}
		}
		else if (type.equals("MOVE")) {
			double tx = x1 + (x2-x1)*frame/(double)length;
			double ty = y1 + (y2-y1)*frame/(double)length;
			Rectangle r = new Rectangle((int)(tx*Stratego.scale+Stratego.scale/8), (int)(ty*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
			if (v1.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
				gr.setColor(Stratego.playerColor);
				gr.fillRect(r.x, r.y, r.width, r.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r, Stratego.font);
			}
			else {
				gr.setColor(Stratego.opponentColor);
				gr.fillRect(r.x, r.y, r.width, r.height);
				if (Stratego.debug) {
					gr.setColor(Color.white);
					Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r, Stratego.font);
				}
			}
		}
		else if (type.equals("BOOM")) {
			//Reveal
			Rectangle r1 = new Rectangle((int)(x1*Stratego.scale+Stratego.scale/8), (int)(y1*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
			if (v1.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
				gr.setColor(Stratego.playerColor);
				gr.fillRect(r1.x, r1.y, r1.width, r1.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r1, Stratego.font);
			}
			else {
				gr.setColor(Stratego.opponentColor);
				gr.fillRect(r1.x, r1.y, r1.width, r1.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r1, Stratego.font);
			}
			Rectangle r2 = new Rectangle((int)(x2*Stratego.scale+Stratego.scale/8), (int)(y2*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
			if (v2.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
				gr.setColor(Stratego.playerColor);
				gr.fillRect(r2.x, r2.y, r2.width, r2.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v2.substring(0, v2.length()-1), r2, Stratego.font);
			}
			else {
				gr.setColor(Stratego.opponentColor);
				gr.fillRect(r2.x, r2.y, r2.width, r2.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v2.substring(0, v2.length()-1), r2, Stratego.font);
			}
			//Big Explosion
			for (int i = 0; i < 100; i++) {
				double crv = Math.random();
				if (crv < 0.25) {
					gr.setColor(Color.white);
				}
				else if (crv < 0.5) {
					gr.setColor(Color.red);
				}
				else if (crv < 0.75) {
					gr.setColor(Color.orange);
				}
				else {
					gr.setColor(Color.yellow);
				}
				double ra1 = (0.25+Math.random())*Stratego.scale;
				double ra2 = (0.25+2*Math.random())*Stratego.scale;
				double th = Math.random()*2*Math.PI;
				gr.drawLine((int)((x2+0.5)*Stratego.scale + ra1*Math.cos(th)), (int)((y2+0.5)*Stratego.scale + ra1*Math.sin(th)), (int)((x2+0.5)*Stratego.scale + ra2*Math.cos(th)), (int)((y2+0.5)*Stratego.scale + ra2*Math.sin(th)));
			}
		}
		else if (type.equals("WIN")) {
			if (frame < 30) {
				//Reveal
				Rectangle r1 = new Rectangle((int)(x1*Stratego.scale+Stratego.scale/8), (int)(y1*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
				if (v1.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
					gr.setColor(Stratego.playerColor);
					gr.fillRect(r1.x, r1.y, r1.width, r1.height);
					gr.setColor(Color.white);
					Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r1, Stratego.font);
				}
				else {
					gr.setColor(Stratego.opponentColor);
					gr.fillRect(r1.x, r1.y, r1.width, r1.height);
					gr.setColor(Color.white);
					Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r1, Stratego.font);
				}
				Rectangle r2 = new Rectangle((int)(x2*Stratego.scale+Stratego.scale/8), (int)(y2*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
				if (v2.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
					gr.setColor(Stratego.playerColor);
					gr.fillRect(r2.x, r2.y, r2.width, r2.height);
					gr.setColor(Color.white);
					Stratego.drawCenteredString(gr, v2.substring(0, v2.length()-1), r2, Stratego.font);
				}
				else {
					gr.setColor(Stratego.opponentColor);
					gr.fillRect(r2.x, r2.y, r2.width, r2.height);
					gr.setColor(Color.white);
					Stratego.drawCenteredString(gr, v2.substring(0, v2.length()-1), r2, Stratego.font);
				}
				//Small Explosion
				gr.setColor(Color.white);
				for (int i = 0; i < 30; i++) {
					double ra1 = (0.25+0.5*Math.random())*Stratego.scale;
					double ra2 = (0.25+Math.random())*Stratego.scale;
					double th = Math.random()*2*Math.PI;
					gr.drawLine((int)((x2+0.5)*Stratego.scale + ra1*Math.cos(th)), (int)((y2+0.5)*Stratego.scale + ra1*Math.sin(th)), (int)((x2+0.5)*Stratego.scale + ra2*Math.cos(th)), (int)((y2+0.5)*Stratego.scale + ra2*Math.sin(th)));
				}
			}
			else {
				//Move forward
				double tx = x1 + (x2-x1)*(frame-30)/(double)(length-30);
				double ty = y1 + (y2-y1)*(frame-30)/(double)(length-30);
				Rectangle r = new Rectangle((int)(tx*Stratego.scale+Stratego.scale/8), (int)(ty*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
				if (v1.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
					gr.setColor(Stratego.playerColor);
					gr.fillRect(r.x, r.y, r.width, r.height);
					gr.setColor(Color.white);
					Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r, Stratego.font);
				}
				else {
					gr.setColor(Stratego.opponentColor);
					gr.fillRect(r.x, r.y, r.width, r.height);
					gr.setColor(Color.white);
					Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r, Stratego.font);
				}
			}
		}
		else if (type.equals("LOSE")) {
			//Reveal
			Rectangle r1 = new Rectangle((int)(x1*Stratego.scale+Stratego.scale/8), (int)(y1*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
			if (v1.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
				gr.setColor(Stratego.playerColor);
				gr.fillRect(r1.x, r1.y, r1.width, r1.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r1, Stratego.font);
			}
			else {
				gr.setColor(Stratego.opponentColor);
				gr.fillRect(r1.x, r1.y, r1.width, r1.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r1, Stratego.font);
			}
			Rectangle r2 = new Rectangle((int)(x2*Stratego.scale+Stratego.scale/8), (int)(y2*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
			if (v2.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
				gr.setColor(Stratego.playerColor);
				gr.fillRect(r2.x, r2.y, r2.width, r2.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v2.substring(0, v2.length()-1), r2, Stratego.font);
			}
			else {
				gr.setColor(Stratego.opponentColor);
				gr.fillRect(r2.x, r2.y, r2.width, r2.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v2.substring(0, v2.length()-1), r2, Stratego.font);
			}
			//Small Explosion
			gr.setColor(Color.white);
			for (int i = 0; i < 30; i++) {
				double ra1 = (0.25+0.5*Math.random())*Stratego.scale;
				double ra2 = (0.25+Math.random())*Stratego.scale;
				double th = Math.random()*2*Math.PI;
				gr.drawLine((int)((x1+0.5)*Stratego.scale + ra1*Math.cos(th)), (int)((y1+0.5)*Stratego.scale + ra1*Math.sin(th)), (int)((x1+0.5)*Stratego.scale + ra2*Math.cos(th)), (int)((y1+0.5)*Stratego.scale + ra2*Math.sin(th)));
			}
		}
		else if (type.equals("VICTORY")) {
			//Reveal
			Rectangle r1 = new Rectangle((int)(x1*Stratego.scale+Stratego.scale/8), (int)(y1*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
			if (v1.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
				gr.setColor(Stratego.playerColor);
				gr.fillRect(r1.x, r1.y, r1.width, r1.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r1, Stratego.font);
			}
			else {
				gr.setColor(Stratego.opponentColor);
				gr.fillRect(r1.x, r1.y, r1.width, r1.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r1, Stratego.font);
			}
			Rectangle r2 = new Rectangle((int)(x2*Stratego.scale+Stratego.scale/8), (int)(y2*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
			if (v2.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
				gr.setColor(Stratego.playerColor);
				gr.fillRect(r2.x, r2.y, r2.width, r2.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v2.substring(0, v2.length()-1), r2, Stratego.font);
			}
			else {
				gr.setColor(Stratego.opponentColor);
				gr.fillRect(r2.x, r2.y, r2.width, r2.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v2.substring(0, v2.length()-1), r2, Stratego.font);
			}
			//TODO: Victory animation
			//Many small explosions
			gr.setColor(Color.white);
			for (int k = 0; k < 20; k++) {
				double tx = Math.random()*Board.size;
				double ty = Math.random()*Board.size;
				for (int i = 0; i < 30; i++) {
					double ra1 = (0.25+0.5*Math.random())*Stratego.scale;
					double ra2 = (0.25+Math.random())*Stratego.scale;
					double th = Math.random()*2*Math.PI;
					gr.drawLine((int)(tx*Stratego.scale + ra1*Math.cos(th)), (int)(ty*Stratego.scale + ra1*Math.sin(th)), (int)(tx*Stratego.scale + ra2*Math.cos(th)), (int)(ty*Stratego.scale + ra2*Math.sin(th)));
				}
			}
		}
		else if (type.equals("DEFEAT")) {
			//Reveal
			Rectangle r1 = new Rectangle((int)(x1*Stratego.scale+Stratego.scale/8), (int)(y1*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
			if (v1.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
				gr.setColor(Stratego.playerColor);
				gr.fillRect(r1.x, r1.y, r1.width, r1.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r1, Stratego.font);
			}
			else {
				gr.setColor(Stratego.opponentColor);
				gr.fillRect(r1.x, r1.y, r1.width, r1.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v1.substring(0, v1.length()-1), r1, Stratego.font);
			}
			Rectangle r2 = new Rectangle((int)(x2*Stratego.scale+Stratego.scale/8), (int)(y2*Stratego.scale+Stratego.scale/8), Stratego.scale*3/4, Stratego.scale*3/4);
			if (v2.toLowerCase().endsWith(Stratego.playerTeam.toLowerCase())) {
				gr.setColor(Stratego.playerColor);
				gr.fillRect(r2.x, r2.y, r2.width, r2.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v2.substring(0, v2.length()-1), r2, Stratego.font);
			}
			else {
				gr.setColor(Stratego.opponentColor);
				gr.fillRect(r2.x, r2.y, r2.width, r2.height);
				gr.setColor(Color.white);
				Stratego.drawCenteredString(gr, v2.substring(0, v2.length()-1), r2, Stratego.font);
			}
			//TODO: Defeat animation
			//Many big Explosions
			for (int k = 0; k < 5; k++) {
				double tx = Math.random()*Board.size;
				double ty = Math.random()*Board.size;
				for (int i = 0; i < 100; i++) {
					double crv = Math.random();
					if (crv < 0.25) {
						gr.setColor(Color.white);
					}
					else if (crv < 0.5) {
						gr.setColor(Color.red);
					}
					else if (crv < 0.75) {
						gr.setColor(Color.orange);
					}
					else {
						gr.setColor(Color.yellow);
					}
					double ra1 = (0.25+Math.random())*Stratego.scale;
					double ra2 = (0.25+2*Math.random())*Stratego.scale;
					double th = Math.random()*2*Math.PI;
					gr.drawLine((int)(tx*Stratego.scale + ra1*Math.cos(th)), (int)(ty*Stratego.scale + ra1*Math.sin(th)), (int)(tx*Stratego.scale + ra2*Math.cos(th)), (int)(ty*Stratego.scale + ra2*Math.sin(th)));
				}
			}
		}
	}
}
