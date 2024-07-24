package com.passwordManager;

import java.util.Stack;

public class Log {
    private static Stack<String> activityStack = new Stack<>();

    public static void logActivity(String activity) {
        if (activityStack.size() >= 100) { // Keeping the last 100 activities
            activityStack.remove(0);
        }
        activityStack.push(activity);
    }

    public static void printLog() {
        System.out.println("Recent Activities:");
        for (String activity : activityStack) {
            System.out.println(activity);
        }
    }
}
