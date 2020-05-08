import java.util.ArrayList;
import java.util.Collections;

//Key:
//	empty - land
//	W - water
//	N - outside the board
//	S - spy
//	2-10 - soldiers
//	B - bomb
//	F - flag
//	Add R/B to each of these for the team.
//	Change to r/b if moved this turn to prevent multiple moving

public class Board {
	static int size = 10;
	String[][] values;
	int pieces = 40;
	String[] redPieces = {"SR", "2R", "2R", "2R", "2R", "2R", "2R", "2R", "2R", "3R", "3R", "3R", "3R", "3R", "4R", "4R", "4R", "4R", "5R", "5R", "5R", "5R", "6R", "6R", "6R", "6R", "7R", "7R", "7R", "8R", "8R", "9R", "10R", "BR", "BR", "BR", "BR", "BR", "BR", "FR"};
	String[] bluePieces = {"SB", "2B", "2B", "2B", "2B", "2B", "2B", "2B", "2B", "3B", "3B", "3B", "3B", "3B", "4B", "4B", "4B", "4B", "5B", "5B", "5B", "5B", "6B", "6B", "6B", "6B", "7B", "7B", "7B", "8B", "8B", "9B", "10B", "BB", "BB", "BB", "BB", "BB", "BB", "FB"};
	
	ArrayList<String> redLayout;
	ArrayList<String> blueLayout;
	
	ArrayList<String> redDeadPieces;
	ArrayList<String> blueDeadPieces;
	
	public Board() {
		init();
	}
	
	public void init() {
		redLayout = new ArrayList<String>();
		blueLayout = new ArrayList<String>();
		for (int i = 0; i < pieces; i++) {
			redLayout.add(redPieces[i]);
			blueLayout.add(bluePieces[i]);
		}
		redDeadPieces = new ArrayList<String>();
		blueDeadPieces = new ArrayList<String>();
		values = new String[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				values[i][j] = "";
			}
		}
		values[2][4] = "W";
		values[2][5] = "W";
		values[3][4] = "W";
		values[3][5] = "W";
		values[6][4] = "W";
		values[6][5] = "W";
		values[7][4] = "W";
		values[7][5] = "W";
		
		populate();
	}
	
	public void populate() {
		Collections.shuffle(blueLayout);
		//Collections.shuffle(redLayout);
		for (int i = 0; i < pieces; i++) {
			values[i%10][i/10] = blueLayout.get(i);
			values[i%10][6+i/10] = redLayout.get(i);
		}
	}
	
	public void swap(int x1, int y1, int x2, int y2) {
		String v1 = get(x1, y1);
		String v2 = get(x2, y2);
		values[x1][y1] = v2;
		values[x2][y2] = v1;
	}
	
	public String get(int x, int y) {
		if (x < 0 || x >= size || y < 0 || y >= size) {
			return "N";
		}
		return values[x][y];
	}
	
	public void immobilize(int x, int y) {
		String val = get(x, y);
		if (val.endsWith(Stratego.playerTeam) || val.endsWith(Stratego.opponentTeam)) {
			values[x][y] = val.substring(0, val.length()-1) + val.substring(val.length()-1).toLowerCase();
		}
	}
	
	public void refreshMoves() {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				String v = get(x, y);
				values[x][y] = v.toUpperCase(); //Easy
			}
		}
	}
	
	public void lockGame() {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				immobilize(x, y);
			}
		}
	}
	
	public String moveType(boolean turn, int x1, int y1, int x2, int y2) {
		String val1 = get(x1, y1);
		String val2 = get(x2, y2);
		//Can't move the void, land, water, flags, or bombs
		if (val1.equals("N") || val1.isEmpty() || val1.equals("W") || val1.startsWith("F") || val1.startsWith("B")) {
			return "INVALID";
		}
		//Can't move your opponent's pieces or your already moved pieces on your turn
		if (turn && !val1.endsWith(Stratego.playerTeam)) {
			return "INVALID";
		}
		//Your opponent can't move your pieces or their already moved pieces on their turn
		if (!turn && !val1.endsWith(Stratego.opponentTeam)) {
			return "INVALID";
		}
		//You can't move into the void, into water, or into another piece from your team
		if (!val2.isEmpty() && (val2.equals("N") || val2.equals("W") || val1.toLowerCase().substring(val1.length()-1).equals(val2.toLowerCase().substring(val2.length()-1)))) {
			return "INVALID";
		}
		//Can't move more distance than to an adjacent tile, use taxicab metric to tell
		int dx = Math.abs(x2-x1);
		int dy = Math.abs(y2-y1);
		if (dx + dy > 1) {
			return "INVALID";
		}
		//Case where moving onto empty land: OK
		if (val2.isEmpty()) {
			return "MOVE";
		}
		//Only remaining cases move onto an enemy player, so they are the complex combat checks...
		return "COMBAT";
	}
	
	public String combatResult(boolean turn, int x1, int y1, int x2, int y2) {
		//Assumes it's a valid move
		String val1 = get(x1, y1);
		String val2 = get(x2, y2);
		val1 = val1.substring(0, val1.length()-1);
		val2 = val2.substring(0, val2.length()-1);
		//System.out.println(val1 + " " + val2);
		if (val2.equals("F") && turn) {
			return "VICTORY";
		}
		if (val2.equals("F") && !turn) {
			return "DEFEAT";
		}
		if (val2.equals("B") && !val1.equals("3")) {
			return "DRAW"; //As in the bomb blows up and both are destroyed
		}
		if (val2.equals("B") && val1.equals("3")) {
			return "WIN";
		}
		if (val2.equals("10") && val1.equals("S")) {
			return "WIN";
		}
		if (val1.equals("S") && val2.equals("S")) {
			return "WIN";
		}
		if (!val2.equals("10") && val1.equals("S")) {
			return "LOSE";
		}
		if (val2.equals("S")) {
			return "WIN";
		}
		//Rest are all numbers
		try {
			int nv1 = Integer.parseInt(val1);
			int nv2 = Integer.parseInt(val2);
			if (nv1 >= nv2) {
				return "WIN";
			}
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		return "LOSE";
	}
	
	public void move(int x1, int y1, int x2, int y2) {
		//Assume this is a valid move into an empty space
		//String val1 = get(x1, y1);
		String val2 = get(x2, y2);
		//Redundant check?
		if (val2.isEmpty()) {
			swap(x1, y1, x2, y2);
			//Make this piece not able to move again
			immobilize(x2, y2);
		}
	}
	
	public void combat(boolean turn, int x1, int y1, int x2, int y2) {
		String combatResult = combatResult(turn, x1, y1, x2, y2);
		String v1 = get(x1, y1);
		String v2 = get(x2, y2);
		String v1v = v1.substring(0, v1.length()-1);
		String v1t = v1.substring(v1.length()-1).toLowerCase();
		String v2v = v2.substring(0, v2.length()-1);
		String v2t = v2.substring(v2.length()-1).toLowerCase();
		if (combatResult.equals("VICTORY")) {
			//Do nothing?
		}
		else if (combatResult.equals("DEFEAT")) {
			//Do nothing?
		}
		else if (combatResult.equals("DRAW")) {
			if (v1t.equals(Stratego.playerTeam.toLowerCase())) {
				redDeadPieces.add(v1v);
			}
			else if (v1t.equals(Stratego.opponentTeam.toLowerCase())) {
				blueDeadPieces.add(v1v);
			}
			if (v2t.equals(Stratego.playerTeam.toLowerCase())) {
				redDeadPieces.add(v2v);
			}
			else if (v2t.equals(Stratego.opponentTeam.toLowerCase())) {
				blueDeadPieces.add(v2v);
			}
			values[x1][y1] = "";
			values[x2][y2] = "";
		}
		else if (combatResult.equals("WIN")) {
			if (v2t.equals(Stratego.playerTeam.toLowerCase())) {
				redDeadPieces.add(v2v);
			}
			else if (v2t.equals(Stratego.opponentTeam.toLowerCase())) {
				blueDeadPieces.add(v2v);
			}
			//Advance
			values[x2][y2] = values[x1][y1];
			values[x1][y1] = "";
			immobilize(x2, y2);
		}
		else if (combatResult.equals("LOSE")) {
			if (v1t.equals(Stratego.playerTeam.toLowerCase())) {
				redDeadPieces.add(v1v);
			}
			else if (v1t.equals(Stratego.opponentTeam.toLowerCase())) {
				blueDeadPieces.add(v1v);
			}
			//Do not advance
			values[x1][y1] = "";
		}
	}
}
