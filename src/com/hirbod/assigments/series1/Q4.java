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
 * Big help in regex from Arad Maleki
 */

public class Q4 {
    private static final Pattern MessagePattern = Pattern.compile("Message\\{ messageId=%[0-9 ]+-[QWERYUIPLKJGFDZXCVBN][qweryuiplkjgfdzxcvbn]{4}\\$([0-9][0-9]|[0-9][0-9][0-9][0-9])%, from=User\\{ firstName='([^']+)', isBot=(true|false), lastName='([^']*)', userName='([^']*)' }, date=([0-9]{14}), text='([^']*)', location=([+-]?([0-9]+|[0-9]+\\.[0-9]+)) }");
    private static final SimpleDateFormat MessageDateFormatParse = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat MessageDateHourFormatParse = new SimpleDateFormat("yyyyMMddHHmmss");
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
                throw new RuntimeException();
            }
        }
        // Get the location
        double location = scanner.nextDouble();
        // Find valid requests
        int o = 1;
        ArrayList<Message> messages = new ArrayList<>();
        Matcher messageMatcher = MessagePattern.matcher(initialText);
        while (messageMatcher.find()) {
            // Check the location
            double messageLocation = Double.parseDouble(messageMatcher.group(8));
            if (Math.abs(messageLocation - location) > 1)
                continue;
            // Check date
            Date messageDate;
            try {
                messageDate = MessageDateFormatParse.parse(messageMatcher.group(6).substring(0, 8));
                if (messageDate.before(startDate) || messageDate.after(endDate))
                    continue;
                messageDate = MessageDateHourFormatParse.parse(messageMatcher.group(6));
            } catch (ParseException ex) { // do we even reach here?
                throw new RuntimeException();
            }
            // Validate the username
            if(!validUsername(messageMatcher.group(5)))
                continue;
            // Now create a message object
            messages.add(new Message(messageMatcher.group(2) + " " + messageMatcher.group(4), messageMatcher.group(7), messageMatcher.group(3).equals("true"), messageDate));
        }
        // Print messages
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).isBot())
                i++; // skip next message
            else
                System.out.println(messages.get(i).toString());
        }
    }

    /**
     * Represents a message
     */
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

        /**
         * Is this message been sent from a bot?
         *
         * @return True if bot
         */
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
