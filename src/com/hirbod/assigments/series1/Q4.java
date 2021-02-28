package com.hirbod.assigments.series1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sites used:
 * https://www.javatpoint.com/java-string-to-date
 * https://regexr.com/
 * https://stackoverflow.com/a/43570891/4213397
 * https://www3.ntu.edu.sg/home/ehchua/programming/howto/Regexe.html
 * https://stackoverflow.com/a/6863551/4213397
 * https://stackoverflow.com/questions/5705111/how-to-get-all-substring-for-a-given-regex
 * https://stackoverflow.com/questions/237061/using-regular-expressions-to-extract-a-value-in-java
 * https://stackoverflow.com/questions/4662215/how-to-extract-a-substring-using-regex
 * https://stackoverflow.com/questions/2811031/decimal-or-numeric-values-in-regular-expression-validation
 * https://stackoverflow.com/questions/4047808/what-is-the-best-way-to-tell-if-a-character-is-a-letter-or-number-in-java-withou/4047836
 * Big help in regex and dates from Arad Maleki
 * Also another big help from Ali Salesi where he said to validate the date and location when printing
 */

public class Q4 {
    private static final Pattern MessagePattern = Pattern.compile("Message\\{ messageId=%[0-9 ]+-[BCDEFGIJKLNPQRUVWXYZ][bcdefgijklnpqruvwxyz]{4}\\$([0-9][0-9]|[0-9][0-9][0-9][0-9])%, from=User\\{ firstName='([^']+)', isBot=(true|false), lastName='([^']*)', userName='([^']*)' }, date=([0-9]{14}), text='([^']*)', location=(-?[0-9]\\d*(\\.\\d+)?) }");
    private static final SimpleDateFormat MessageDateFormatParse = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat MessageDateHourFormatParse = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat MessageDateFormatResult = new SimpleDateFormat("HH:mm");
    /**
     * Because we can't classes, we use a simple String array to store data. To make thing easy for useless,
     * here is the complete index table of the array.
     */
    static final int IndexName = 0, IndexIsBot = 1, IndexMessage = 2, IndexDate = 3, IndexLocation = 4;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String initialText = scanner.nextLine();
        // Parse start and end date
        Date startDate, endDate;
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String startDateString = scanner.nextLine();
            String endDateString = scanner.nextLine();
            try {
                startDate = formatter.parse(startDateString);
                endDate = formatter.parse(endDateString);
            } catch (ParseException ex) { // do we even reach here?
                throw new RuntimeException();
            }
        }
        // Get the location
        double ourLocation = scanner.nextDouble();
        // Find valid requests
        ArrayList<String[]> messages = getValidMessages(initialText);
        // Print messages
        validateAndPrint(messages, startDate, endDate, ourLocation);
    }

    /**
     * Gets a list of valid messages from a master string
     *
     * @param initialText Master string to extract messages from it
     * @return Array of messages. Messages are basically just a String array
     */
    private static ArrayList<String[]> getValidMessages(String initialText) {
        ArrayList<String[]> messages = new ArrayList<>();
        Matcher messageMatcher = MessagePattern.matcher(initialText);
        while (messageMatcher.find()) {
            // Validate the username
            if (!validUsername(messageMatcher.group(5)))
                continue;
            // Create the message
            String[] message = new String[5];
            message[IndexMessage] = messageMatcher.group(7);
            message[IndexName] = messageMatcher.group(2) + " " + messageMatcher.group(4);
            message[IndexIsBot] = messageMatcher.group(3);
            message[IndexLocation] = messageMatcher.group(8);
            message[IndexDate] = messageMatcher.group(6);
            messages.add(message);
        }
        return messages;
    }

    /**
     * Iterates over all messages and prints the valid ones
     *
     * @param messages  The messages to iterate
     * @param startDate The allowed min date
     * @param endDate   The allowed max date
     * @param location  Our current location
     */
    private static void validateAndPrint(ArrayList<String[]> messages, Date startDate, Date endDate, double location) {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i)[IndexIsBot].equals("true"))
                i++; // skip next message
            else if (validateDateLocation(messages.get(i), startDate, endDate, location))
                printMessage(messages.get(i));
        }
    }

    /**
     * Validates the date and location of a message
     *
     * @param message   The message to validate
     * @param startDate The allowed min date
     * @param endDate   The allowed max date
     * @param location  Our current location
     * @return True if valid, otherwise false
     */
    private static boolean validateDateLocation(String[] message, Date startDate, Date endDate, double location) {
        // Check the date
        double messageLocation = Double.parseDouble(message[IndexLocation]);
        if (Math.abs(messageLocation - location) > 1)
            return false;
        // Check the date
        Date messageDate;
        try {
            messageDate = MessageDateFormatParse.parse(message[IndexDate].substring(0, 8)); // 0 to 8 is the yyyyMMdd
            if (messageDate.before(startDate) || messageDate.after(endDate))
                return false;
        } catch (ParseException ex) { // we will never reach here
            throw new RuntimeException();
        }
        return true;
    }

    /**
     * Prints one message into stdout
     *
     * @param message The message to write it
     */
    private static void printMessage(String[] message) {
        try {
            Date messageDate = MessageDateHourFormatParse.parse(message[IndexDate]);
            System.out.println("--------------------\n" +
                    "*" + message[IndexName] + "*\n" +
                    message[IndexMessage] + "\n" +
                    "_" + MessageDateFormatResult.format(messageDate) + "_\n" +
                    "--------------------");
        } catch (ParseException ex) { // we will never reach here
            throw new RuntimeException();
        }
    }

    /**
     * Checks if a username is valid or not
     *
     * @param username The username to check
     * @return True if valid; Otherwise false
     */
    private static boolean validUsername(String username) {
        // Empty usernames are valid
        if (username.equals(""))
            return true;
        // Check the length
        if (username.length() < 5 || username.length() > 32)
            return false;
        // Check the chars
        if (!username.matches("[a-zA-Z0-9_]+"))
            return false;
        // Check last and first char
        if (!Character.isLetter(username.charAt(0)) || username.charAt(username.length() - 1) == '_')
            return false;
        // Valid!
        return true;
    }
}
