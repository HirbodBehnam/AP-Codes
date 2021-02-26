package com.hirbod.assigments.series1;

import java.util.Scanner;

public class Q1 {
    final static int MapLength = 100;
    /**
     * This map contains which tiles are visited
     */
    static boolean[][] visited;

    public static void main(String[] args) {
        // At first create the visited map and assign the middle index to visited
        // Please note that we create the map bigger in order to make our lives easier by accessing directly to that index
        visited = new boolean[MapLength + 1][MapLength + 1];
        visited[50][50] = true;
        // Our initial state
        int currentX = 50, currentY = 50;
        // Read input
        Scanner scanner = new Scanner(System.in);
        int movesCount = scanner.nextInt();
        for (int i = 0; i < movesCount; i++) { // read each move
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            if (validMove(currentX, currentY, x, y)) {
                currentX += x;
                currentY += y;
            }
        }
        // Print current position
        System.out.printf("%d %d", currentX, currentY);
    }

    /**
     * Check if a move is valid or not
     * At first checks out of bounds and then checks each tile to see if it's visited or not
     * @param currentX The current X position
     * @param currentY The current Y position
     * @param deltaX   How much we should go left or right
     * @param deltaY   How much we should go up or down
     * @return True if there is no problem with move. Otherwise false
     */
    public static boolean validMove(final int currentX, final int currentY, final int deltaX, final int deltaY) {
        if (!validPosition(currentX + deltaX, currentY + deltaY))
            return false;
        // At first move in x position
        final int startX = Math.min(currentX, currentX + deltaX);
        final int toX = Math.max(currentX, currentX + deltaX);
        for (int x = startX; x <= toX; x++) // move along x axis
            if (visited[x][currentY] && x != currentX)
                return false;

        // If we have reached here it means that we can move alongside X axis
        // At first move in x position
        final int startY = Math.min(currentY, currentY + deltaY);
        final int toY = Math.max(currentY, currentY + deltaY);
        for (int y = startY; y <= toY; y++) // move along y axis
            if (visited[currentX + deltaX][y] && y != currentY)
                return false;

        // Mark tiles as visited
        for (int x = startX; x <= toX; x++) // move along x axis
            visited[x][currentY] = true;
        for (int y = startY; y <= toY; y++) // move along y axis
            visited[currentX + deltaX][y] = true;
         return true;
    }

    /**
     * Checks if a position is valid or not
     * @param x x
     * @param y y
     * @return True if valid
     */
    static boolean validPosition(int x, int y) {
        return x >= 1 && x <= MapLength && y >= 1 && y <= MapLength;
    }
}
