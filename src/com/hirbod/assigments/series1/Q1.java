package com.hirbod.assigments.series1;

import java.util.Scanner;

public class Q1 {
    final static int MapLength = 100;
    /**
     * This map contains which tiles are visited
     * Please note that we create the map bigger in order to make our lives easier by accessing directly to that index
     */
    static boolean[][] visited = new boolean[MapLength + 1][MapLength + 1];

    public static void main(String[] args) {
        visited[50][50] = true; // we are here and we have visited our initial state
        readAndMove();
    }

    /**
     * Reads the stdin and moves according to it
     */
    public static void readAndMove() {
        // Our initial position
        int currentX = 50, currentY = 50;
        Scanner scanner = new Scanner(System.in);
        int movesCount = scanner.nextInt();
        for (int i = 0; i < movesCount; i++) { // read each move
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            if (validMove(currentX, currentY, x, y)) { // ignore invalid moves
                currentX += x;
                currentY += y;
            }
        }
        // Print final position
        System.out.printf("%d %d", currentX, currentY);
    }

    /**
     * Check if a move is valid or not
     * At first checks out of bounds and then checks each tile to see if it's visited or not
     * LOC without comments: 17
     *
     * @param currentX The current X position
     * @param currentY The current Y position
     * @param deltaX   How much we should go left or right
     * @param deltaY   How much we should go up or down
     * @return True if there is no problem with move. Otherwise false
     */
    public static boolean validMove(final int currentX, final int currentY, final int deltaX, final int deltaY) {
        if (!validPosition(currentX + deltaX, currentY + deltaY))
            return false;
        // At first move in x axis. (move from smaller to bigger value)
        final int startX = Math.min(currentX, currentX + deltaX);
        final int toX = Math.max(currentX, currentX + deltaX);
        for (int x = startX; x <= toX; x++)
            if (visited[x][currentY] && x != currentX)
                return false;

        // If we have reached here it means that we can move alongside X axis
        // Move in y axis (move from smaller to bigger value)
        final int startY = Math.min(currentY, currentY + deltaY);
        final int toY = Math.max(currentY, currentY + deltaY);
        for (int y = startY; y <= toY; y++)
            if (visited[currentX + deltaX][y] && y != currentY)
                return false;

        // If we have reached here, mark tiles as visited
        for (int x = startX; x <= toX; x++)
            visited[x][currentY] = true;
        for (int y = startY; y <= toY; y++)
            visited[currentX + deltaX][y] = true;
        return true;
    }

    /**
     * Checks if a position is valid or not
     *
     * @param x x
     * @param y y
     * @return True if valid
     */
    static boolean validPosition(int x, int y) {
        return x >= 1 && x <= MapLength && y >= 1 && y <= MapLength;
    }
}
