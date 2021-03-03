package com.hirbod.assigments.series1;

import java.util.*;

/**
 * After plowing github to find a good implementation, I said fuck it and used wikipedia english text
 * to write the code.
 * My only source: https://en.wikipedia.org/wiki/Maze_generation_algorithm#Recursive_implementation
 */

public class Q3 {
    /**
     * Create only and only one random number generator.
     * Because random seed is based on the system clock and if we define random in loop,
     * we will get same numbers because the system time doesn't change.
     * Apparently, java uses System.nanoTime(), but I think it's not the worth of risk to create a random number generator everytime
     */
    final static Random random = new Random();
    final static char visitedTile = '*';
    final static char notVisitedTile = '-';
    final static char wall = '1';
    final static char pathway = '0';
    static int dimensionX, dimensionY;
    static char[][] maze;

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        dimensionX = scanner.nextInt() * 2 + 1;
        dimensionY = scanner.nextInt() * 2 + 1;
        int count = scanner.nextInt();
        for (; count > 0; count--) {
            maze = new char[dimensionX][dimensionY];
            // Fill the array with walls
            for (char[] chars : maze)
                Arrays.fill(chars, wall);
            // Mark the points as not visited (they are going to be * later)
            for (int i = 1; i < maze.length; i += 2)
                for (int j = 1; j < maze[i].length; j += 2)
                    maze[i][j] = notVisitedTile;
            // Start the backtrack
            dfsMaze(1, 1);
            // Mark the exit points
            maze[0][1] = 'e';
            maze[maze.length - 1][maze[0].length - 2] = 'e';
            // Print maze
            for (char[] row : maze)
                System.out.println(new String(row));
            System.out.println();
        }
    }

    /**
     * Backtrack the path in the maze
     * @param currentX Current x position
     * @param currentY Current y position
     */
    public static void dfsMaze(final int currentX, final int currentY) {
        final ArrayList<int[]> unvisited = new ArrayList<>();
        maze[currentX][currentY] = visitedTile;
        while (true) {
            // Check upper row
            if (isValidTile(currentX - 2, currentY) && isNotVisited(currentX - 2, currentY))
                unvisited.add(new int[]{currentX - 2, currentY});
            // Check down row
            if (isValidTile(currentX + 2, currentY) && isNotVisited(currentX + 2, currentY))
                unvisited.add(new int[]{currentX + 2, currentY});
            // Check left
            if (isValidTile(currentX, currentY - 2) && isNotVisited(currentX, currentY - 2))
                unvisited.add(new int[]{currentX, currentY - 2});
            // Check right
            if (isValidTile(currentX, currentY + 2) && isNotVisited(currentX, currentY + 2))
                unvisited.add(new int[]{currentX, currentY + 2});
            // Check unvisited
            if (unvisited.size() == 0)
                return;
            // Pick a random one
            final int[] next = unvisited.get(random.nextInt(unvisited.size()));
            // Remove wall
            maze[(currentX + next[0]) / 2][(currentY + next[1]) / 2] = pathway;
            // Backtrack!
            dfsMaze(next[0], next[1]);
            unvisited.clear(); // reset the array
        }
    }

    /**
     * Checks if a dimension is valid or not
     * @param x X
     * @param y Y
     * @return True if valid otherwise false
     */
    public static boolean isValidTile(final int x, final int y) {
        return (x >= 0 && x < dimensionX) && (y >= 0 && y < dimensionY);
    }

    /**
     * Checks if we have not visited a tile in the maze
     * @param x x
     * @param y y
     * @return True if we have not visited this tile. Otherwise false
     */
    public static boolean isNotVisited(final int x, final int y) {
        return maze[x][y] == notVisitedTile;
    }

}
