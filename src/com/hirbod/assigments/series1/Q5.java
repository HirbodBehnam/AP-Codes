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
    final static HashMap<String, StringBuilder> docs = new HashMap<>(); // key=name, value=content
    final static String InvalidCommand = "invalid command!";
    final static String InvalidFile = "invalid file name!";
    final static Scanner InputScanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            String commandRaw = InputScanner.nextLine().trim();
            String[] arguments = commandRaw.split(" ");
            if (handleCommand(commandRaw, arguments))
                break;
        }
    }

    /**
     * Handles all commands
     * I really cannot make this method smaller :|
     *
     * @param commandRaw The raw arguments which has been passed to program
     * @param arguments  The commands split by a whitespace
     * @return True if we must terminate the program, otherwise false
     */
    private static boolean handleCommand(final String commandRaw, final String[] arguments) {
        switch (arguments[0]) {
            case "END": { // end the program
                if (arguments.length != 1) {
                    System.out.println(InvalidCommand);
                    break;
                }
                return true;
            }
            case "ADD": // add doc
                handleAddCommand(arguments);
                break;
            case "RMV":
                handleRemoveCommand(arguments);
                break;
            case "RPLC": // Replace words
                handleReplaceCommand(arguments);
                break;
            case "FIND":
                handleFindCommand(commandRaw, arguments);
                break;
            case "GCD":
                handleGCDCommand(arguments);
                break;
            case "PRINT":
                handlePrintCommand(arguments);
                break;
            default:
                System.out.println(InvalidCommand);
        }
        return false;
    }

    /**
     * Handles the PRINT command
     *
     * @param arguments The arguments sent to this command
     */
    private static void handlePrintCommand(String[] arguments) {
        if (arguments.length != 2) {
            System.out.println(InvalidCommand);
            return;
        }
        String docName = arguments[1];
        if (docs.containsKey(docName))
            System.out.println(docs.get(docName));
        else
            System.out.println(InvalidFile);
    }

    /**
     * Handles calculating GCD in a doc
     *
     * @param arguments The arguments sent to this command
     */
    private static void handleGCDCommand(String[] arguments) {
        if (arguments.length != 2) {
            System.out.println(InvalidCommand);
            return;
        }
        String docName = arguments[1];
        if (docs.containsKey(docName)) {
            long gcd = getGCDOfDocument(docs.get(docName));
            if (gcd != -1) // if gcd is -1, it means that there was no number in doc
                docs.get(docName).append(gcd);
        } else
            System.out.println(InvalidFile);
    }

    /**
     * Handles the commands which start with "FIND"
     *
     * @param commandRaw The raw arguments user has entered
     * @param arguments  The commands split by whitespace
     */
    private static void handleFindCommand(String commandRaw, String[] arguments) {
        if (arguments.length < 4) {
            System.out.println(InvalidCommand);
            return;
        }
        switch (arguments[1]) {
            case "REP": {
                String docName = arguments[2];
                String stringToCheck = commandRaw.substring("FIND REP ".length() + docName.length() + 1);
                handleFindRepeatCommand(docName, stringToCheck);
            }
            break;
            case "MIRROR": {
                handleFindMirrorCommand(arguments);
            }
            break;
            case "ALPHABET": {
                HandleFindAlphabetCommand(arguments);
            }
            break;
            default:
                System.out.println(InvalidCommand);
                break;
        }
    }

    /**
     * Handles the FIND ALPHABET commands
     *
     * @param arguments The arguments passed
     */
    private static void HandleFindAlphabetCommand(String[] arguments) {
        if (arguments.length != 4) {
            System.out.println(InvalidCommand);
            return;
        }
        if (arguments[2].equals("WORDS")) {
            String docName = arguments[3];
            if (docs.containsKey(docName)) {
                long numberOfAlphabeticWords = countAlphabetWordsInDocument(docs.get(docName));
                System.out.printf("%d alphabetical words!\n", numberOfAlphabeticWords);
            } else {
                System.out.println(InvalidFile);
            }
        } else {
            System.out.println(InvalidCommand);
        }
    }

    /**
     * Handles the FIND MIRROR arguments
     *
     * @param arguments The args passed
     */
    private static void handleFindMirrorCommand(String[] arguments) {
        if (arguments.length != 4) {
            System.out.println(InvalidCommand);
            return;
        }
        String docName = arguments[2];
        String characterInMiddle = arguments[3];
        if (characterInMiddle.length() != 1) {
            System.out.println(InvalidCommand);
            return;
        }
        if (docs.containsKey(docName)) {
            long count = countMirrorsInDocument(docs.get(docName), characterInMiddle);
            System.out.printf("%d mirror words!\n", count);
        } else {
            System.out.println(InvalidFile);
        }
    }

    /**
     * Handles the FIND REP command
     *
     * @param name          The document name
     * @param stringToCheck The string to check if number of repeated times
     */
    private static void handleFindRepeatCommand(String name, String stringToCheck) {
        if (docs.containsKey(name)) {
            int lastIndex = 0;
            int repeatedCounts = 0;
            while (lastIndex != -1) {
                lastIndex = docs.get(name).indexOf(stringToCheck, lastIndex);
                if (lastIndex != -1) {
                    repeatedCounts++;
                    lastIndex++;
                }
            }
            System.out.printf("%s is repeated %d times in %s\n", stringToCheck, repeatedCounts, name);
        } else {
            System.out.println(InvalidFile);
        }
    }

    /**
     * Handles the replace command to replace last word of a doc
     *
     * @param arguments The args sent to this command
     */
    private static void handleReplaceCommand(String[] arguments) {
        if (arguments.length != 4) {
            System.out.println(InvalidCommand);
            return;
        }
        String docName = arguments[1];
        String[] wordsToReplace = arguments[2].split(",");
        String toReplaceWord = arguments[3];
        if (docName.equals("-ALL")) {
            for (Map.Entry<String, StringBuilder> entry : docs.entrySet()) {
                docName = entry.getKey();
                StringBuilder doc = entry.getValue();
                docs.replace(docName, replaceWordInDocument(doc, wordsToReplace, toReplaceWord));
            }
        } else if (docs.containsKey(docName)) {
            docs.replace(docName, replaceWordInDocument(docs.get(docName), wordsToReplace, toReplaceWord));
        } else {
            System.out.println(InvalidFile);
        }
    }

    /**
     * Handles all commands which start with remove
     *
     * @param arguments The args sent to this command
     */
    private static void handleRemoveCommand(String[] arguments) {
        if (arguments.length < 2) {
            System.out.println(InvalidCommand);
            return;
        }
        switch (arguments[1]) {
            case "DOC": { // remove the doc
                if (arguments.length != 3) {
                    System.out.println(InvalidCommand);
                    break;
                }
                String docName = arguments[2];
                if (docs.containsKey(docName))
                    docs.remove(docName);
                else
                    System.out.println(InvalidFile);
            }
            break;
            case "WORD": {
                handleRemoveWordCommand(arguments);
            }
            break;
            default:
                System.out.println(InvalidCommand);
                break;
        }
    }

    /**
     * Handles the REMOVE WORD command
     *
     * @param arguments The args sent to this command
     */
    private static void handleRemoveWordCommand(String[] arguments) {
        if (arguments.length != 4) {
            System.out.println(InvalidCommand);
            return;
        }
        String name = arguments[2];
        String wordToRemove = arguments[3];
        if (name.equals("-ALL")) {
            for (Map.Entry<String, StringBuilder> entry : docs.entrySet()) {
                name = entry.getKey();
                StringBuilder doc = entry.getValue();
                docs.replace(name, removeWordInDocument(doc, wordToRemove));
            }
        } else if (docs.containsKey(name)) {
            docs.replace(name, removeWordInDocument(docs.get(name), wordToRemove));
        } else {
            System.out.println(InvalidFile);
        }
    }

    /**
     * Handles commands which start with ADD
     *
     * @param arguments The args passed
     */
    private static void handleAddCommand(String[] arguments) {
        if (arguments.length < 2) {
            System.out.println(InvalidCommand);
            return;
        }
        switch (arguments[1]) {
            case "DOC": { // create a new doc
                if (arguments.length != 3) {
                    System.out.println(InvalidCommand);
                    break;
                }
                String docName = arguments[2];
                String docContent = InputScanner.nextLine();
                // Content and write
                docs.put(docName, fixDocumentContent(docContent)); // this method overwrites as well
            }
            break;
            case "WORD": {
                handleAddWordCommand(arguments);
            }
            break;
            default:
                System.out.println(InvalidCommand);
                break;
        }
    }

    /**
     * Handle the ADD WORD arguments
     *
     * @param arguments The args passed
     */
    private static void handleAddWordCommand(String[] arguments) {
        if (arguments.length != 4) {
            System.out.println(InvalidCommand);
            return;
        }
        String docName = arguments[2];
        String word = arguments[3];
        if (docName.equals("-ALL")) {
            for (StringBuilder doc : docs.values()) {
                doc.append(word);
            }
        } else if (docs.containsKey(docName)) {
            docs.get(docName).append(word);
        } else {
            System.out.println(InvalidFile);
        }
    }

    /**
     * Fix the markdown and remove unneeded stuff stuff
     * This method is exactly 30 lines without commands
     *
     * @param documentContent The text to fix
     * @return The fixed text
     */
    private static StringBuilder fixDocumentContent(String documentContent) {
        // At first fix the pictures and links
        Pattern pattern = Pattern.compile("!?\\[([^\\[\\] ]*)]\\([^()]+\\)");
        Matcher matcher;
        while (true) { // use while to fix the nested links/pics
            matcher = pattern.matcher(documentContent);
            if (matcher.find())
                documentContent = matcher.replaceAll("$1");
            else
                break;
        }
        // Fix bolds
        pattern = Pattern.compile("(?<= |^|\\*)\\*\\*([^*]+)\\*\\*(?= |$|\\*)");
        while (true) { // use while to fix the nested bolds
            matcher = pattern.matcher(documentContent);
            if (matcher.find())
                documentContent = matcher.replaceAll("$1");
            else
                break;
        }
        // Remove all noisy words
        String[] words = getWords(documentContent);
        StringBuilder contentBuilder = new StringBuilder(documentContent.length());
        for (String word : words) {
            if (word.matches("[a-zA-Z0-9]*")) // only append words that are not noisy
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
     *
     * @param source         The doc to read from it
     * @param wordsToReplace The words to replace them with replacedWord
     * @param replacedWord   The word to be replaced with wordsToReplace
     * @return A new string builder which have it words replaced
     */
    private static StringBuilder replaceWordInDocument(StringBuilder source, String[] wordsToReplace, String replacedWord) {
        String[] words = getWords(source.toString());
        StringBuilder newDocument = new StringBuilder(source.length());
        for (String toReplaceWord : wordsToReplace) { // for each word search from last and match
            for (int i = words.length - 1; i >= 0; i--) {
                if (words[i].equals(toReplaceWord)) {
                    words[i] = replacedWord;
                    break;
                }
            }
        }
        for (String word : words) {
            newDocument.append(word);
            newDocument.append(' ');
        }
        // Remove the last space
        if (newDocument.length() > 0)
            newDocument.setLength(newDocument.length() - 1);
        return newDocument;
    }

    /**
     * Removes all occurrences of a word from doc
     *
     * @param source       The source to remove the words in
     * @param wordToRemove The word to remove from doc
     * @return A "NEW" StringBuilder which have the text without the word
     */
    private static StringBuilder removeWordInDocument(StringBuilder source, String wordToRemove) {
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
     *
     * @param source The doc to check in
     * @return The GCD of numbers. If nothing exists, return -1
     */
    private static long getGCDOfDocument(StringBuilder source) {
        Pattern numberPattern = Pattern.compile("(\\d+)");
        Matcher numberMatcher = numberPattern.matcher(source);
        ArrayList<Long> numbers = new ArrayList<>(); // list of numbers in document
        while (numberMatcher.find())
            numbers.add(Long.parseLong(numberMatcher.group(1)));
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
     *
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
     *
     * @param source            The string to count in
     * @param mirroredCharacter The world which must be mirrored
     * @return The number of words
     */
    private static long countMirrorsInDocument(StringBuilder source, String mirroredCharacter) {
        long mirroredCounter = 0;
        String[] words = getWords(source.toString());
        Pattern pattern = Pattern.compile("^(\\d+)" + mirroredCharacter + "(\\d+)$"); // first of string + number + mirroredCharacter + number + last of string
        for (String word : words) { // check each word
            Matcher matcher = pattern.matcher(word);
            while (matcher.find())
                if (matcher.group(1).equals(matcher.group(2)))
                    mirroredCounter++;
        }
        return mirroredCounter;
    }

    /**
     * Counts all alphabetic words in a string builder
     *
     * @param source The string builder to check
     * @return The number of alphabetic words
     */
    private static long countAlphabetWordsInDocument(StringBuilder source) {
        long alphabeticWordCounter = 0;
        String[] words = getWords(source.toString());
        for (String word : words)
            if (word.matches("[a-zA-Z]+"))
                alphabeticWordCounter++;

        return alphabeticWordCounter;
    }

    /**
     * Split string by space. But why not use String.split?
     * Apparently, String.split ingress the last whitespaces in a string
     * For example "hello " with split becomes {"hello"} not {"hello", ""}
     * This function correctly, splits the string
     *
     * @param sentence The string to split it
     * @return The worlds
     */
    private static String[] getWords(String sentence) {
        ArrayList<String> wordsList = new ArrayList<>(Arrays.asList(sentence.split(" ")));
        for (int i = sentence.length() - 1; i >= 0; i--) {
            if (sentence.charAt(i) == ' ')
                wordsList.add("");
            else
                break;
        }
        String[] wordsArray = new String[wordsList.size()];
        return wordsList.toArray(wordsArray);
    }
}
