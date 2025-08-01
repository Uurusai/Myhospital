package com.hms.test;

import com.hms.notification.NotificationServer;
import com.hms.server.HMSServer;

public class notifTester1 {
    public static void main(String[] args) {
        // Start the main HMS server in one thread
        new Thread(() -> new HMSServer().start()).start();

        // Start the notification server in another thread
        new Thread(() -> NotificationServer.start()).start();

        System.out.println("Both servers started on localhost");
    }
}
