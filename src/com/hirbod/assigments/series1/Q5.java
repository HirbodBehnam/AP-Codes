package com.hirbod.assigments.series1;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sites used:
 * https://stackoverflow.com/questions/988655/can-i-replace-groups-in-java-regex
 * https://stackoverflow.com/questions/3395286/remove-last-character-of-a-stringbuilder/3395329
 * https://stackoverflow.com/a/1066603/4213397
 * https://stackoverflow.com/a/767833/4213397
 * https://stackoverflow.com/a/5374359/4213397
 * https://stackoverflow.com/a/157950/4213397
 * https://stackoverflow.com/a/3395329/4213397
 * Special thanks to Arad Maleki and Ali Salesi
 */

public class Q5 {
    static HashMap<String, StringBuilder> docs = new HashMap<>(); // key=name, value=content
    final static String InvalidCommand = "invalid command!";
    final static String InvalidFile = "invalid file name!";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String commandRaw = scanner.nextLine().trim();
            String[] command = commandRaw.split(" ");
            try {
                switch (command[0]) {
                    case "END": { // end the program
                        if (command.length != 1) {
                            System.out.println(InvalidCommand);
                            break;
                        }
                        return;
                    }
                    case "ADD": // add doc
                        switch (command[1]) {
                            case "DOC": { // create a new doc
                                if (command.length != 3) {
                                    System.out.println(InvalidCommand);
                                    break;
                                }
                                String name = command[2];
                                String content = scanner.nextLine();
                                // Content and write
                                docs.put(name, fixContent(content)); // this command overwrites as well
                            }
                            break;
                            case "WORD": {
                                if (command.length != 4) {
                                    System.out.println(InvalidCommand);
                                    break;
                                }
                                String name = command[2];
                                String word = command[3];
                                if (name.equals("-ALL")) {
                                    for (StringBuilder doc : docs.values()) {
                                        doc.append(word);
                                    }
                                } else if (docs.containsKey(name)) {
                                    docs.get(name).append(word);
                                } else {
                                    System.out.println(InvalidFile);
                                }
                            }
                            break;
                            default:
                                System.out.println(InvalidCommand);
                                break;
                        }
                        break;
                    case "RMV":
                        switch (command[1]) {
                            case "DOC": { // remove the doc
                                if (command.length != 3) {
                                    System.out.println(InvalidCommand);
                                    break;
                                }
                                String name = command[2];
                                if (docs.containsKey(name))
                                    docs.remove(name);
                                else
                                    System.out.println(InvalidFile);
                            }
                            break;
                            case "WORD": {
                                if (command.length != 4) {
                                    System.out.println(InvalidCommand);
                                    break;
                                }
                                String name = command[2];
                                String wordToRemove = command[3];
                                if (name.equals("-ALL")) {
                                    for (Map.Entry<String, StringBuilder> entry : docs.entrySet()) {
                                        name = entry.getKey();
                                        StringBuilder doc = entry.getValue();
                                        docs.replace(name, remove(doc, wordToRemove));
                                    }
                                } else if (docs.containsKey(name)) {
                                    docs.replace(name, remove(docs.get(name), wordToRemove));
                                } else {
                                    System.out.println(InvalidFile);
                                }
                            }
                            break;
                            default:
                                System.out.println(InvalidCommand);
                                break;
                        }
                        break;
                    case "RPLC": { // Replace words
                        if (command.length != 4) {
                            System.out.println(InvalidCommand);
                            break;
                        }
                        String name = command[1];
                        String[] wordsToReplace = command[2].split(",");
                        String toReplaceWord = fixContent(command[3]).toString();
                        if (name.equals("-ALL")) {
                            for (Map.Entry<String, StringBuilder> entry : docs.entrySet()) {
                                name = entry.getKey();
                                StringBuilder doc = entry.getValue();
                                docs.replace(name, replace(doc, wordsToReplace, toReplaceWord));
                            }
                        } else if (docs.containsKey(name)) {
                            docs.replace(name, replace(docs.get(name), wordsToReplace, toReplaceWord));
                        } else {
                            System.out.println(InvalidFile);
                        }
                    }
                    break;
                    case "FIND": {
                        switch (command[1]) {
                            case "REP": {
                                String name = command[2];
                                String string = commandRaw.substring("FIND REP ".length() + name.length() + 1);
                                if (docs.containsKey(name)) {
                                    int lastIndex = 0;
                                    int count = 0;
                                    while (lastIndex != -1) {
                                        lastIndex = docs.get(name).indexOf(string, lastIndex);
                                        if (lastIndex != -1) {
                                            count++;
                                            lastIndex++;
                                        }
                                    }
                                    System.out.printf("%s is repeated %d times in %s\n", string, count, name);
                                } else {
                                    System.out.println(InvalidFile);
                                }
                            }
                            break;
                            case "MIRROR": {
                                if (command.length != 4) {
                                    System.out.println(InvalidCommand);
                                    break;
                                }
                                String name = command[2];
                                String c = command[3];
                                if (c.length() != 1) {
                                    System.out.println(InvalidCommand);
                                    break;
                                }
                                if (docs.containsKey(name)) {
                                    long count = countMirrors(docs.get(name), c);
                                    System.out.printf("%d mirror words!\n", count);
                                } else {
                                    System.out.println(InvalidFile);
                                }
                            }
                            break;
                            case "ALPHABET": {
                                if (command.length != 4) {
                                    System.out.println(InvalidCommand);
                                    break;
                                }
                                if (command[2].equals("WORDS")) {
                                    String name = command[3];
                                    if (docs.containsKey(name)) {
                                        long count = countAlphabetWords(docs.get(name));
                                        System.out.printf("%d alphabetical words!\n", count);
                                    } else {
                                        System.out.println(InvalidFile);
                                    }
                                } else {
                                    System.out.println(InvalidCommand);
                                }
                            }
                            break;
                            default:
                                System.out.println(InvalidCommand);
                                break;
                        }
                    }
                    break;
                    case "GCD": {
                        if (command.length != 2) {
                            System.out.println(InvalidCommand);
                            break;
                        }
                        String name = command[1];
                        if (docs.containsKey(name)) {
                            long gcd = getGCD(docs.get(name));
                            if (gcd != -1)
                                docs.get(name).append(gcd);
                        } else
                            System.out.println(InvalidFile);
                    }
                    break;
                    case "PRINT": {
                        if (command.length != 2) {
                            System.out.println(InvalidCommand);
                            break;
                        }
                        String name = command[1];
                        if (docs.containsKey(name))
                            System.out.println(docs.get(name));
                        else
                            System.out.println(InvalidFile);
                    }
                    break;
                    default:
                        System.out.println(InvalidCommand);
                        break;
                }
            } catch (IndexOutOfBoundsException ex) {
                System.out.println(InvalidCommand);
            }
        }
    }

    /**
     * Fix the markdown and remove unneeded stuff stuff
     * @param content The text to fix
     * @return The fixed text
     */
    private static StringBuilder fixContent(String content) {
        // At first fix the pictures and links
        Pattern pattern = Pattern.compile("!?\\[([^\\[\\] ]*)]\\([^()]+\\)");
        Matcher matcher;
        while (true) { // use while to fix the nested links
            matcher = pattern.matcher(content);
            if (matcher.find())
                content = matcher.replaceAll("$1");
            else
                break;
        }
        // Fix bolds
        pattern = Pattern.compile("(?<= |^|\\*)\\*\\*([^*]+)\\*\\*(?= |$|\\*)");
        while (true) { // use while to fix the nested bolds
            matcher = pattern.matcher(content);
            if (matcher.find())
                content = matcher.replaceAll("$1");
            else
                break;
        }
        // Remove all noisy words
        String[] words = getWords(content);
        StringBuilder contentBuilder = new StringBuilder(content.length());
        for (String word : words) {
            if (word.matches("[a-zA-Z0-9]*"))
                contentBuilder.append(word);
            contentBuilder.append(' ');
        }
        // Remove the last space
        if (contentBuilder.length() > 0)
            contentBuilder.setLength(contentBuilder.length() - 1);
        return contentBuilder;
    }

    /**
     * Replaces the last occurrence of a word in a doc
     * @param source The doc to read from it
     * @param wordsToReplace The words to replace them with replacedWord
     * @param replacedWord The word to be replaced with wordsToReplace
     * @return A new string builder which have it words replaced
     */
    private static StringBuilder replace(StringBuilder source, String[] wordsToReplace, String replacedWord) {
        String[] words = getWords(source.toString());
        StringBuilder newBuilder = new StringBuilder(source.length());
        for (String toReplaceWord : wordsToReplace) { // for each word search from last and match
            for (int i = words.length - 1; i >= 0; i--) {
                if (words[i].equals(toReplaceWord)) {
                    words[i] = replacedWord;
                    break;
                }
            }
        }
        for (String word : words) {
            newBuilder.append(word);
            newBuilder.append(' ');
        }
        // Remove the last space
        if (newBuilder.length() > 0)
            newBuilder.setLength(newBuilder.length() - 1);
        return newBuilder;
    }

    /**
     * Removes all occurrences of a word from doc
     * @param source The source to remove the words in
     * @param wordToRemove The word to remove from doc
     * @return A "NEW" StringBuilder which have the text without the word
     */
    private static StringBuilder remove(StringBuilder source, String wordToRemove) {
        String[] words = getWords(source.toString());
        StringBuilder newBuilder = new StringBuilder(source.length());
        for (int i = 0; i < words.length; i++)
            if (words[i].equals(wordToRemove))
                words[i] = ""; // remove it

        for (String word : words) {
            newBuilder.append(word);
            newBuilder.append(' ');
        }
        // Remove the last space
        if (newBuilder.length() > 0)
            newBuilder.setLength(newBuilder.length() - 1);
        return newBuilder;
    }

    /**
     * Find the GCD of numbers in a doc
     * @param source The doc to check in
     * @return The GCD of numbers. If nothing exists, return -1
     */
    private static long getGCD(StringBuilder source) {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(source);
        ArrayList<Long> numbers = new ArrayList<>();
        while (matcher.find())
            numbers.add(Long.parseLong(matcher.group(1)));
        if (numbers.size() == 0) // nothing exits
            return -1;
        // From here, calculate the GCD of all numbers in numbers
        if (numbers.size() == 1)
            return numbers.get(0);
        long gcd = GCD(numbers.get(0), numbers.get(1));
        for (int i = 2; i < numbers.size(); i++)
            gcd = GCD(gcd, numbers.get(i));
        return gcd;
    }

    /**
     * Returns GCD of two numbers
     * If any of them is zero, returns the other
     * If both are zero returns 0
     * @param number1 First number
     * @param number2 Second number
     * @return The GCD of them
     */
    private static long GCD(long number1, long number2) {
        if (number1 == 0)
            return number2;
        if (number2 == 0)
            return number1;
        return number1 % number2 == 0 ? number2 : GCD(number2, number1 % number2);
    }

    /**
     * Count the mirror words in a string
     * @param source The string to count in
     * @param mirroredCharacter The world which must be mirrored
     * @return The number of words
     */
    private static long countMirrors(StringBuilder source, String mirroredCharacter) {
        long counter = 0;
        String[] words = getWords(source.toString());
        Pattern pattern = Pattern.compile("^(\\d+)" + mirroredCharacter + "(\\d+)$"); // first of string + number + mirroredCharacter + number + last of string
        for (String word : words) { // check each word
            Matcher matcher = pattern.matcher(word);
            while (matcher.find())
                if (matcher.group(1).equals(matcher.group(2)))
                    counter++;
        }
        return counter;
    }

    /**
     * Counts all alphabetic words in a string builder
     * @param source The string builder to check
     * @return The number of alphabetic words
     */
    private static long countAlphabetWords(StringBuilder source) {
        long counter = 0;
        String[] words = getWords(source.toString());
        for (String word : words)
            if (word.matches("[a-zA-Z]+"))
                counter++;

        return counter;
    }

    /**
     * Split string by space. But why not use String.split?
     * Apparently, String.split ingress the last whitespaces in a string
     * For example "hello " with split becomes {"hello"} not {"hello", ""}
     * This function correctly, splits the string
     * @param sentence The string to split it
     * @return The worlds
     */
    private static String[] getWords(String sentence) {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(sentence.split(" ")));
        for (int i = sentence.length() - 1; i >= 0; i--) {
            if (sentence.charAt(i) == ' ')
                words.add("");
            else
                break;
        }
        String[] wordsArray = new String[words.size()];
        return words.toArray(wordsArray);
    }
}
