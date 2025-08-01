package com.hms.test;

import com.hms.client.HMSClient;
import com.hms.client.NotificationClient;
import com.hms.model.Message;
import com.hms.notification.NotificationServer;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class notifTester2 {
    public static void main(String[] args) {
        // Simulate a user with ID 1
        int testUserId = 4;

        // 1. Connect to main HMS server
        HMSClient hmsClient = new HMSClient("192.168.0.104", 12345);

        // 2. Create and connect notification client
        System.out.println("Starting notification client...");
        NotificationClient notificationClient = new NotificationClient(testUserId, message -> {
            System.out.println("\n=== RECEIVED NOTIFICATION ===");
            System.out.println("From: " + message.getSenderName());
            System.out.println("Content: " + message.getContent());
            System.out.println("Timestamp: " + message.getTimestamp());
            System.out.println("============================\n");
        });

        // 3. Send a test message after 3 seconds (to ensure connection is established)
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Sending test notification...");
                try {
                    // This would normally be done server-side, but for testing:
                    Message testMessage = new Message();
                    testMessage.setSenderName("Test Server");
                    testMessage.setContent("This is a test notification.");
                    testMessage.setTimestamp(LocalDateTime.now());

                    // Normally you'd call this from server code, but for testing:
                    NotificationServer.sendNotification(testUserId, testMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3000);
    }
}
