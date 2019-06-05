package duplicateTwo;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.*;

enum Direction {
	LEFT, UP, RIGHT, DOWN;
	
	private static final Map<Integer, Direction> dirByCode = new HashMap<>();
	static {
		dirByCode.put(KeyEvent.VK_LEFT, LEFT);
		dirByCode.put(KeyEvent.VK_UP, UP);
		dirByCode.put(KeyEvent.VK_RIGHT, RIGHT);
		dirByCode.put(KeyEvent.VK_DOWN, DOWN);
	}
	
	public static Direction getDirectionByCode(int code) {
		return dirByCode.get(code);
	}
}

public class NumberField {
	
	public static int ROW = 4;
	public static int COL = 4;
	
	public static int EMPTY = 0;
	
	private int[][] mPane;
	private NumberField previous;
	
	public NumberField() {
		mPane = new int[ROW][COL];
		for(int i=0; i<mPane.length; i++)
			for(int j=0; j<mPane[i].length; j++)
				mPane[i][j] = EMPTY;
		
		previous = null;
		randomGenerate();
	}
	
	public NumberField(NumberField numField) {
		mPane = new int[ROW][COL];
		for(int i=0; i<mPane.length; i++)
			System.arraycopy(numField.mPane[i], 0, mPane[i], 0, mPane[i].length);
		previous = numField.previous;
	}
	
	public int getNum(int i, int j) {
		if(!isValid(i, j))
			throw new IllegalArgumentException();
		return mPane[i][j];
	}
	
	public NumberField getPrevious() {
		return previous;
	}

	public void setPrevious(NumberField previous) {
		this.previous = previous;
	}

	public void randomGenerate() {
		List<Point> pts = new ArrayList<>();
		for(int i=0; i<mPane.length; i++)
			for(int j=0; j<mPane[i].length; j++)
				if(mPane[i][j] == EMPTY)
					pts.add(new Point(i, j));
		
		Point ran = pts.get((int)(Math.random()*pts.size()));
		if((int)(Math.random()*4) < 3) {
			mPane[ran.x][ran.y] = 2;
		} else {
			mPane[ran.x][ran.y] = 4;
		}
	}
	
	public void duplicateTwo(Direction dir) {
		if(dir == null)
			return;
		switch(dir) {
		case LEFT : duplicateLeft(); break;
		case RIGHT : duplicateRight(); break;
		case UP : duplicateUp(); break;
		case DOWN : duplicateDown(); break;
		}
	}
	
	private void duplicateLeft() {
		rotateLeft();
		gravitate();
		duplicateTwo();
		rotateRight();
	}
	
	private void duplicateRight() {
		rotateRight();
		gravitate();
		duplicateTwo();
		rotateLeft();
	}
	
	private void duplicateUp() {
		rotateOpposite();
		gravitate();
		duplicateTwo();
		rotateOpposite();
	}
	
	private void duplicateDown() {
		gravitate();
		duplicateTwo();
	}
	
	private void duplicateTwo() {
		for(int j=0; j<mPane[0].length; j++) {
			for(int i=mPane.length-1; i>0; i--) {
				if(mPane[i][j] != EMPTY) {
					if(mPane[i][j] == mPane[i-1][j]) {
						mPane[i][j]*=2;
						for(int k=i-1; k>0; k--)
							mPane[k][j] = mPane[k-1][j];
						mPane[0][j] = EMPTY;
					}
				}
			}
		}
	}
	
	private void gravitate() {
		for(int j=0; j<mPane[0].length; j++) {
			int k = mPane.length-1;
			for(int i=mPane.length-1; i>=0; i--) {
				if(mPane[i][j] != EMPTY)
					mPane[k--][j] = mPane[i][j];
			}
			while(k>=0)
				mPane[k--][j] = EMPTY;
		}
	}
	
	private void rotateRight() {
		int[][] tmpPane = new int[mPane[0].length][mPane.length];
		for(int i=0; i<tmpPane.length; i++)
			for(int j=0; j<tmpPane[i].length; j++)
				tmpPane[i][j] = mPane[mPane.length-1-j][i];
		
		mPane = tmpPane;
	}
	
	private void rotateLeft() {
		int[][] tmpPane = new int[mPane[0].length][mPane.length];
		for(int i=0; i<tmpPane.length; i++)
			for(int j=0; j<tmpPane[i].length; j++)
				tmpPane[i][j] = mPane[j][mPane[0].length-1-i];
		
		mPane = tmpPane;
	}
	
	private void rotateOpposite() {
		int[][] tmpPane = new int[mPane.length][mPane[0].length];
		for(int i=0; i<tmpPane.length; i++)
			for(int j=0; j<tmpPane[i].length; j++)
				tmpPane[i][j] = mPane[mPane.length-1-i][mPane[0].length-1-j];
		
		mPane = tmpPane;
	}
	
	public boolean isMoveable() {
		if(!isFull())
			return true;
		
		for(int i=0; i<mPane.length; i++)
			for(int j=0; j<mPane[i].length; j++)
				if(hasAdjacentSame(i, j))
					return true;
		return false;
	}
	
	public boolean hasChange(NumberField numField) {
		for(int i=0; i<mPane.length; i++)
			for(int j=0; j<mPane[i].length; j++)
				if(mPane[i][j] != numField.mPane[i][j])
						return true;
		return false;
	}
	
	public boolean has2048() {
		for(int i=0; i<mPane.length; i++)
			for(int j=0; j<mPane[i].length; j++)
				if(mPane[i][j]==2048)
					return true;
		return false;
	}
	
	public boolean isFull() {
		for(int i=0; i<mPane.length; i++)
			for(int j=0; j<mPane[i].length; j++)
				if(mPane[i][j] == EMPTY)
					return false;
		return true;
	}
	
	private boolean hasAdjacentSame(int i, int j) {
		return (isValid(i+1, j) && mPane[i][j]==mPane[i+1][j]) ||
				(isValid(i, j+1) && mPane[i][j]==mPane[i][j+1]);
	}
	
	private boolean isValid(int i, int j) {
		return !(i<0 || i>=ROW || j<0 || j>=COL);
	}
}
