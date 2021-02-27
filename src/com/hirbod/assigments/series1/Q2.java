package com.hirbod.assigments.series1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

/**
 * I knew what hashset was.
 * Sites used:
 * https://stackoverflow.com/a/16252290/4213397
 */

public class Q2 {

    // LOC of main without comments: 25
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int days = scanner.nextInt();
        scanner.nextLine(); // go to next line
        // Create two hashsets. First one represents the line before and the next one represents the shared names between the before and this
        HashSet<String> lineBefore = new HashSet<>();
        // At very very first read the first line and save everything in set
        for (String word : scanner.nextLine().split(" "))
            lineBefore.add(word.toLowerCase());
        // Reach each line
        for (int i = 1; i < days; i++) {
            HashSet<String> currentLine = new HashSet<>();
            String[] words = scanner.nextLine().split(" ");
            for (String word : words)
                if (lineBefore.contains(word.toLowerCase())) // only add stuff we have seen before
                    currentLine.add(word.toLowerCase());
            // Replace the current line with line before
            lineBefore = currentLine;
        }
        // Now remove other words
        for (int i = 0; i < days; i++)
            for (String word : scanner.nextLine().split(" "))
                lineBefore.remove(word.toLowerCase());
        // Print them
        if (lineBefore.size() == 0) {
            System.out.println("Nothing in common");
        } else {
            // At first convert the hashmap to array
            ArrayList<String> words = new ArrayList<>(lineBefore);
            Collections.sort(words);
            for (int i = words.size() - 1; i >= 0; i--)
                System.out.printf("%s ", words.get(i));
        }
    }
}
