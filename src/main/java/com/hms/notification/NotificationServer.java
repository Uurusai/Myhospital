package com.hms.notification;

import com.hms.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class NotificationServer {
    private static final int NOTIFICATION_PORT = 12346;
    private static final Map<Integer, List<Socket>> clientSockets = new ConcurrentHashMap<>();

    public static void start() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(NOTIFICATION_PORT)) {
                System.out.println("Notification server started on port " + NOTIFICATION_PORT);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                    int userId = ois.readInt(); // Client identifies itself

                    synchronized (clientSockets) {
                        clientSockets.computeIfAbsent(userId, k -> new ArrayList<>()).add(clientSocket);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void sendNotification(int userId, Message message) {
        synchronized (clientSockets) {
            List<Socket> sockets = clientSockets.get(userId);
            if (sockets != null) {
                Iterator<Socket> iterator = sockets.iterator();
                while (iterator.hasNext()) {
                    Socket socket = iterator.next();
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(message);
                        oos.flush();
                    } catch (IOException e) {
                        iterator.remove();
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}