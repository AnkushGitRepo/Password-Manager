package com.passwordManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static StackDSA<String> activityStack = new StackDSA<>();

    public static void printLog(StackDSA<String> userActionStack) {
        System.out.println("Recent Activities:\n");
        System.out.println("  Date And Time              Log Details     ");
        StackDSA<String> tempStack = new StackDSA<>();
        while (!activityStack.isEmpty()) {
            String activity = activityStack.pop();
            userActionStack.push(activity);
            tempStack.push(activity);
        }
        while (!tempStack.isEmpty()) {
            activityStack.push(tempStack.pop());
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        while (!userActionStack.isEmpty()) {
            String logEntry = userActionStack.pop();
            String[] parts = logEntry.split(": ", 2);
            if (parts.length == 2) {
                String timestampStr = parts[0];
                String activity = parts[1];
                try {
                    Date timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(timestampStr);
                    System.out.println(dateFormat.format(timestamp) + " - " + activity);
                } catch (Exception e) {
                    System.out.println(logEntry);
                }
            } else {
                System.out.println(logEntry);
            }
        }
    }
}
