import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import javax.swing.*;

public class GameBoard{
	public static enum HeadDir{
		UP, DOWN, LEFT, RIGHT, NONE
	}
	HeadDir currDir;
	GridCell[][] grid;
	Queue<GridCell> body;
	GridCell head;
	int score;
	private static int GRID_LENGTH = 30;
	private static int GRID_HEIGHT = 30;

	public int getLen() {
		return GRID_LENGTH;
	}
	public int getHeight() {
		return GRID_HEIGHT;
	}
	public GameBoard() {
		score = 0;
	}
	
	public int init(int headx, int heady, int applex, int appley) {
		
		File f = new File("settings.txt");
		try {
			Scanner scnr = new Scanner(f);
			String line = scnr.nextLine();	
			line = scnr.nextLine();
			GRID_HEIGHT = Integer.parseInt(line);
			line = scnr.nextLine();
			
			grid = new GridCell[GRID_LENGTH][GRID_HEIGHT];
			body = new LinkedList<GridCell>();
			for(int i = 0; i < GRID_LENGTH; i++)
				for(int j = 0; j < GRID_HEIGHT; j++) {
					grid[i][j] = new GridCell(i*20, j*20);
				}
			
			for(int i = 0; i < GRID_LENGTH; i++) {
				for(int j = 0; j < GRID_HEIGHT; j++) {
					if((i == headx) && (j == heady)) {
						grid[i][j].setHead();
						body.add(grid[i][j]);
					}
					if((i == applex) && (j == appley)) {
						grid[i][j].setApple();
					}
				}
			}
			currDir = HeadDir.NONE;
			scnr.close();
			return Integer.parseInt(line);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		return 10;
	}
	
	public boolean update() {
		boolean validMove = true;
		if(currDir == HeadDir.NONE) {
			return validMove;
		}
		if(currDir == HeadDir.DOWN) {
			validMove = moveDown();
		}
		if(currDir == HeadDir.UP) {
			validMove = moveUp();
		}
		if(currDir == HeadDir.LEFT) {
			validMove = moveLeft();
		}
		if(currDir == HeadDir.RIGHT) {
			validMove = moveRight();
		}
		
		GridCell toRemove = body.remove();
		for(int i = 0; i < GRID_LENGTH; i++) {
			for(int j = 0; j < GRID_HEIGHT; j++) {
				if(grid[i][j] == toRemove) grid[i][j].removeBody();
			}
		}
		
		return validMove;
	}
	
	public void draw(Graphics g) {
		
		for(int i = 0; i < GRID_LENGTH; i++) {
			for(int j = 0; j < GRID_HEIGHT; j++) {
				grid[i][j].draw(g);
			}
		}
	}
	
	public void up() {
		if(currDir == HeadDir.DOWN) return;
		currDir = HeadDir.UP;
	}
	public void down() {
		if(currDir == HeadDir.UP) return;
		currDir = HeadDir.DOWN;
	}
	public void left() {
		if(currDir == HeadDir.RIGHT) return;
		currDir = HeadDir.LEFT;
	}
	public void right() {
		if(currDir == HeadDir.LEFT) return;
		currDir = HeadDir.RIGHT;
	}
	
	public void grow(int c, int r) {
		grid[c][r].setBody();
		body.add(grid[c][r]);
		score++;
		newApple();
	}
	public void newApple() {
		int maxL = GRID_LENGTH - 3;
		int minL = 3;
		int c = (int)(Math.random()*(maxL-minL))+minL;
		int maxH = GRID_HEIGHT - 3;
		int minH = 3;
		int r = (int)(Math.random()*(maxH-minH))+minH;
		while(isBody(c, r)) {
			c = (int)(Math.random()*(maxL-minL))+minL;
			r = (int)(Math.random()*(maxH-minH))+minH;
		}
		grid[c][r].setApple();
	}
	public boolean isBody(int c, int r) {
		if(grid[c][r].isSnake()) return true;
		return false;
	}
	public boolean moveDown() {
		int r = 0;
		int c = 0;
		for(int i = 0; i < GRID_LENGTH; i++) {
			boolean foundHead = false;
			for(int j = 0; j < GRID_HEIGHT; j++) {
				if(grid[i][j].isHead()) {
					r = j;
					c = i;
					//System.out.println("Head: " + c + ", " + r);
					foundHead = true;
				}
			}
			if(foundHead) {
				break;
			}
		}
		if(r + 1 < GRID_HEIGHT) {
			if(grid[c][r+1].isApple()) {
				grow(c, r - 1);
			}
			if(grid[c][r+1].isBody()) {
				return false;
			}
			grid[c][r+1].setHead();
			body.add(grid[c][r+1]);
			grid[c][r].removeHead();
			//System.out.println("Move down. New head: " + c + ", " + (r+1));
			return true;
		}
		else {
			return false;
		}
	}
	public boolean moveUp() {
		int r = 0;
		int c = 0;
		for(int i = 0; i < GRID_LENGTH; i++) {
			boolean foundHead = false;
			for(int j = 0; j < GRID_HEIGHT; j++) {
				if(grid[i][j].isHead()) {
					r = j;
					c = i;
					foundHead = true;
				}
			}
			if(foundHead) {
				break;
			}
		}
		if(r - 1 >= 0) {
			if(grid[c][r-1].isApple()) {
				grow(c, r + 1);
			}
			if(grid[c][r-1].isBody()) {
				return false;
			}
			grid[c][r-1].setHead();
			body.add(grid[c][r-1]);
			grid[c][r].removeHead();
			return true;
		}
		else {
			return false;
		}
	}
	public boolean moveRight() {
		int r = 0;
		int c = 0;
		for(int i = 0; i < GRID_LENGTH; i++) {
			boolean foundHead = false;
			for(int j = 0; j < GRID_HEIGHT; j++) {
				if(grid[i][j].isHead()) {
					r = j;
					c = i;
					foundHead = true;
				}
			}
			if(foundHead) {
				break;
			}
		}
		if(c + 1 < GRID_LENGTH) {
			if(grid[c+1][r].isApple()) {
				grow(c-1, r);
			}
			if(grid[c+1][r].isBody()) {
				return false;
			}
			grid[c+1][r].setHead();
			body.add(grid[c+1][r]);
			grid[c][r].removeHead();
			return true;
		}
		else {
			return false;
		}
	}
	public boolean moveLeft() {
		int r = 0;
		int c = 0;
		for(int i = 0; i < GRID_LENGTH; i++) {
			boolean foundHead = false;
			for(int j = 0; j < GRID_HEIGHT; j++) {
				if(grid[i][j].isHead()) {
					r = j;
					c = i;
					foundHead = true;
				}
			}
			if(foundHead) {
				break;
			}
		}
		if(c - 1 >= 0) {
			if(grid[c-1][r].isApple()) {
				grow(c+1, r);
			}
			if(grid[c-1][r].isBody()) {
				return false;
			}
			grid[c-1][r].setHead();
			body.add(grid[c-1][r]);
			grid[c][r].removeHead();
			return true;
		}
		else {
			return false;
		}
	}

	public int getScore() {
		return score;
	}
	
	
}
