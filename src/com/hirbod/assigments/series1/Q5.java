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
 */

public class Q5 {
    static HashMap<String, StringBuilder> docs = new HashMap<>(); // key=name, value=content
    final static String InvalidCommand = "invalid command!";
    final static String InvalidFile = "invalid file name!";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.next();
            switch (command) {
                case "END": // end the program
                    return;
                case "ADD": // add doc
                    switch (scanner.next()) {
                        case "DOC": { // create a new doc
                            String name = scanner.next();
                            scanner.nextLine(); // skip command line
                            String content = scanner.nextLine();
                            // Content and write
                            docs.put(name, fixContent(content)); // this command overwrites as well
                        }
                        break;
                        case "WORD": {
                            String name = scanner.next();
                            String word = scanner.next();
                            if (name.equals("-ALL")) {
                                for (StringBuilder doc : docs.values()) {
                                    doc.append(word);
                                }
                            } else if (docs.containsKey(name)) {
                                docs.get(name).append(word);
                            } else {
                                System.out.println(InvalidFile);
                            }
                            scanner.nextLine(); // skip to newline
                        }
                        break;
                        default:
                            System.out.println(InvalidCommand);
                            scanner.nextLine();
                            break;
                    }
                    break;
                case "RMV":
                    switch (scanner.next()) {
                        case "DOC": { // remove the doc
                            String name = scanner.next();
                            if (docs.containsKey(name))
                                docs.remove(name);
                            else
                                System.out.println(InvalidFile);
                            scanner.nextLine(); // skip to newline
                        }
                        break;
                        case "WORD": {
                            String name = scanner.next();
                            String wordToRemove = scanner.next();
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
                            scanner.nextLine(); // skip to newline
                        }
                        break;
                        default:
                            System.out.println(InvalidCommand);
                            scanner.nextLine();
                            break;
                    }
                    break;
                case "RPLC": { // Replace words
                    String name = scanner.next();
                    String[] wordsToReplace = scanner.next().split(",");
                    String toReplaceWord = scanner.next();
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
                    scanner.nextLine(); // skip to newline
                }
                break;
                case "FIND": {
                    switch (scanner.next()) {
                        case "REP": {
                            String name = scanner.next();
                            String string = scanner.next();
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
                            scanner.nextLine(); // skip to newline
                        }
                        break;
                        case "MIRROR": {
                            String name = scanner.next();
                            String c = scanner.next();
                            if (docs.containsKey(name)) {
                                long count = countMirrors(docs.get(name), c);
                                System.out.printf("%d mirror words!\n", count);
                            } else {
                                System.out.println(InvalidFile);
                            }
                            scanner.nextLine(); // skip to newline
                        }
                        break;
                        case "ALPHABET": {
                            if (scanner.next().equals("WORDS")) {
                                String name = scanner.next();
                                if (docs.containsKey(name)) {
                                    long count = countAlphabetWords(docs.get(name));
                                    System.out.printf("%d alphabetical words!\n", count);
                                } else {
                                    System.out.println(InvalidFile);
                                }
                            } else {
                                System.out.println(InvalidCommand);
                            }
                            scanner.nextLine();
                        }
                        break;
                        default:
                            System.out.println(InvalidCommand);
                            scanner.nextLine();
                            break;
                    }
                }
                break;
                case "GCD": {
                    String name = scanner.next();
                    if (docs.containsKey(name)) {
                        long gcd = getGCD(docs.get(name));
                        docs.get(name).append(gcd);
                    } else
                        System.out.println(InvalidFile);
                    scanner.nextLine(); // skip to newline
                }
                break;
                case "PRINT": {
                    String name = scanner.next();
                    if (docs.containsKey(name))
                        System.out.println(docs.get(name));
                    else
                        System.out.println(InvalidFile);
                    scanner.nextLine(); // skip to newline
                }
                break;
                default:
                    System.out.println(InvalidCommand);
                    scanner.nextLine();
                    break;
            }
        }
    }

    private static StringBuilder fixContent(String content) {
        // At first fix the pictures
        Pattern p = Pattern.compile("!\\[([^\\[\\]]+)\\]\\([^\\(\\)]+\\)");
        Matcher m = p.matcher(content);
        if (m.find())
            content = m.replaceAll("$1");
        // Fix the links
        p = Pattern.compile("\\[([^\\[\\]]+)\\]\\([^\\(\\)]+\\)");
        m = p.matcher(content);
        if (m.find())
            content = m.replaceAll("$1");
        // Fix bolds
        p = Pattern.compile("\\*\\*([^\\*]*)\\*\\*");
        m = p.matcher(content);
        if (m.find())
            content = m.replaceAll("$1");
        // Remove all noisy words
        String[] words = content.split(" ");
        StringBuilder stringBuilder = new StringBuilder(content.length());
        for (String word : words) {
            if (word.matches("[a-zA-Z0-9]*"))
                stringBuilder.append(word);
            stringBuilder.append(' ');
        }
        // Remove the last space
        if (stringBuilder.length() > 0)
            stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder;
    }

    private static StringBuilder replace(StringBuilder source, String[] toReplaceWords, String replaceWord) {
        String[] words = source.toString().split(" ");
        StringBuilder newBuilder = new StringBuilder(source.length());
        for (String toReplaceWord : toReplaceWords) { // for each word search from last and match
            for (int i = words.length - 1; i >= 0; i--) {
                if (words[i].equals(toReplaceWord)) {
                    words[i] = replaceWord;
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

    private static StringBuilder remove(StringBuilder source, String wordToRemove) {
        String[] words = source.toString().split(" ");
        StringBuilder newBuilder = new StringBuilder(source.length());
        for (int i = 0; i < words.length; i++)
            if (words[i].equals(wordToRemove))
                words[i] = "";

        for (String word : words) {
            newBuilder.append(word);
            newBuilder.append(' ');
        }
        // Remove the last space
        if (newBuilder.length() > 0)
            newBuilder.setLength(newBuilder.length() - 1);
        return newBuilder;
    }

    private static long getGCD(StringBuilder source) {
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(source);
        ArrayList<Long> numbers = new ArrayList<>();
        while (m.find())
            numbers.add(Long.parseLong(m.group(1)));
        if (numbers.size() == 0)
            return 0; // TODO: check
        if (numbers.size() == 1)
            return numbers.get(0);
        long gcd = GCD(numbers.get(0), numbers.get(1));
        for (int i = 2; i < numbers.size(); i++)
            gcd = GCD(gcd, numbers.get(i));
        return gcd;
    }

    private static long GCD(long a, long b) {
        return a % b == 0 ? b : GCD(b, a % b);
    }

    private static long countMirrors(StringBuilder source, String c) {
        long counter = 0;
        Pattern p = Pattern.compile("(^|\\s)(\\d)+" + c + "(\\d)+(\\s|$)");
        Matcher m = p.matcher(source);
        while (m.find())
            if (m.group(2).equals(m.group(3)))
                counter++;
        return counter;
    }

    private static long countAlphabetWords(StringBuilder source) {
        long counter = 0;
        String[] words = source.toString().split(" ");
        for (String word : words)
            if (word.matches("[a-zA-Z]+"))
                counter++;

        return counter;
    }

    /**
     * escape()
     * <p>
     * Escape a give String to make it safe to be printed or stored.
     *
     * @param s The input String.
     * @return The output String.
     **/
    public static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                .replace("\'", "\\'")
                .replace("\"", "\\\"");
    }
}
