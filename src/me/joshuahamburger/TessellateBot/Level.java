package me.joshuahamburger.TessellateBot;

import java.util.Arrays;

public class Level {
	int playerX;
	int playerY;
	int size;
	String wallId = ":orange_square:";
	String emptyId = ":black_medium_square:";
	String playerId = ":sauropod:";
	String trailId = ":yellow_square:";
	String goalId = ":green_square:";
	String[][] grid;
	String returnString;
	int[][] positionHistory;
	int currentMove;
	Boolean hasWon = false;
	
	public Level(int size, int startX, int startY) {
        this.grid = new String[size][size];
        for (int i = 0; i < grid.length; i++) {
            Arrays.fill(grid[i], emptyId);
		}
        
        this.size = size;
        grid[startY][startX] = playerId;
        
        playerX = startX;
        playerY = startY;
        positionHistory = new int[(int) Math.pow(size, 2) + 3][2];
        positionHistory[0][0] = startX;
        positionHistory[0][1] = startY;
        currentMove = 0;
	}
	
	private Boolean checkWin() {
		for (int i = 0; i<grid.length; i++) {
			for(int j = 0; j<grid[i].length; j++) {
				if (grid[i][j] == emptyId) {
					return false;
				}
			}
		} 
		return true;
	}
	
	public Boolean hasWon() {
		return hasWon;
	}
	
	public void addWalls(int[][] walls) {
		for (int i = 0; i<walls.length; i++) {
			grid[walls[i][1]][walls[i][0]] = wallId;
		}
		
	}
    
	public void move(int difX, int difY) {
		String nextSpace = grid[(playerY + difY + size) % size][(playerX + difX + size) % size];
		if (nextSpace == emptyId || nextSpace == goalId) {
			//Add trail
			if (playerX == positionHistory[0][0] && playerY == positionHistory[0][1]) {
				grid[playerY][playerX] = goalId;
			} else {
				grid[playerY][playerX] = trailId;
			}
			
			//move
			playerX = (playerX + difX + size) % size;
			playerY = (playerY + difY + size) % size;
			
			currentMove++;

			if (nextSpace == goalId) {
				hasWon = checkWin();
			}
			//record position history
			positionHistory[currentMove][0] = playerX;
			positionHistory[currentMove][1] = playerY;
			grid[playerY][playerX] = playerId;
		}
	}
	
	public void undo() {
		if (currentMove>0) {
			//reset current tile
			if (positionHistory[currentMove][0] == positionHistory[0][0] &&
				positionHistory[currentMove][1] == positionHistory[0][1]) {
				grid[positionHistory[currentMove][1]][positionHistory[currentMove][0]] = goalId;
			} else {
				grid[positionHistory[currentMove][1]][positionHistory[currentMove][0]] = emptyId;
			}
			
			//go back 1
			currentMove--;
			//move player to position
			playerX = positionHistory[currentMove][0];
			playerY = positionHistory[currentMove][1];
			grid[positionHistory[currentMove][1]][positionHistory[currentMove][0]] = playerId;
		}
	}
	
	public void reset() {
		currentMove = 0;
		playerX = positionHistory[0][0];
		playerY = positionHistory[0][1];
		for (int i = 0; i<grid.length; i++) {
			for(int j = 0; j<grid[i].length; j++) {
				if (grid[i][j] != wallId) {
					grid[i][j] = emptyId;
				}
			}
		}
		grid[playerY][playerX] = playerId;
	}
	
	public String getString() {
		returnString = "";
		for (int i = 0; i<grid.length; i++) {
			for(int j = 0; j<grid[i].length; j++) {
				returnString += grid[i][j];
			}
			returnString += "\n";
		}
		return returnString;
	}
}
