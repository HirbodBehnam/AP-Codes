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
 */

public class Q4 {
    private static final Pattern MessagePattern = Pattern.compile("(?=(" + "Message\\{ messageId=%[0-9 ]+-[QWERYUIPLKJGFDZXCVBN][qweryuiplkjgfdzxcvbn][qweryuiplkjgfdzxcvbn][qweryuiplkjgfdzxcvbn][qweryuiplkjgfdzxcvbn]\\$([0-9][0-9]|[0-9][0-9][0-9][0-9])%, from=User\\{ firstName='[^']+', isBot=(true|false), lastName='[^']*', userName='[^']*' }, date=[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9], text='[^']*', location=[-.0-9]+ }" + "))");
    private static final Pattern MessageExtractor = Pattern.compile("firstName='([^']*)'.*isBot=([tf]).*lastName='([^']*)'.*userName='([^']*)'.*date=([0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]).*text='([^']*)'.*location=([-.0-9]+)");
    private static final SimpleDateFormat MessageDateFormatParse = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat MessageDateFormatResult = new SimpleDateFormat("HH:mm");
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
                System.out.println(ex.getMessage());
                return;
            }
        }
        // Get the location
        double location = scanner.nextDouble();
        // Find valid requests
        ArrayList<Message> messages = new ArrayList<>();
        Matcher messageMatcher = MessagePattern.matcher(initialText);
        while(messageMatcher.find()) {
            String message = messageMatcher.group(1);
            Matcher extractor = MessageExtractor.matcher(message);
            while(extractor.find()) {
                // Check the location
                double messageLocation;
                try {
                    messageLocation = Double.parseDouble(extractor.group(7));
                } catch (NumberFormatException ex) {
                    continue;
                }
                if (Math.abs(messageLocation - location) > 1)
                    continue;
                // Check date
                Date messageDate;
                try {
                    messageDate = MessageDateFormatParse.parse(extractor.group(5));
                    if (messageDate.before(startDate) || messageDate.after(endDate))
                        continue;
                } catch (ParseException ex) { // do we even reach here?
                    System.out.println(ex.getMessage());
                    return;
                }
                // Check ID
                if(!validUsername(extractor.group(4)))
                    continue;
                // Now create a message object
                messages.add(new Message(extractor.group(1) + " " + extractor.group(3), extractor.group(6), extractor.group(2).equals("t"), messageDate));
            }
        }
        // Print messages
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).isBot())
                i++; // skip next message
            else
                System.out.println(messages.get(i).toString());
        }
    }
    private static class Message {
        private final String name;
        private final String text;
        private final boolean isBot;
        private final Date messageDate;
        Message(String name, String text, boolean isBot, Date messageDate) {
            this.name = name;
            this.text = text;
            this.isBot = isBot;
            this.messageDate = messageDate;
        }

        public boolean isBot() {
            return this.isBot;
        }

        @Override
        public String toString() {
            return "--------------------\n" +
                    "*" + this.name + "*\n" +
                    this.text + "\n" +
                    "_" + MessageDateFormatResult.format(messageDate) + "_\n" +
                    "--------------------";
        }
    }
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
        if (!isAlphabet(username.charAt(0)) || username.charAt(username.length() - 1) == '_')
            return false;
        // Valid!
        return true;
    }
    private static boolean isAlphabet(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
}
