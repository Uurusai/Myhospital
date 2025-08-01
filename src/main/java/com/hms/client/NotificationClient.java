package com.hms.client;

import com.hms.model.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

public class NotificationClient {
    private final int userId;
    private final Consumer<Message> messageHandler;

    public NotificationClient(int userId, Consumer<Message> messageHandler) {
        this.userId = userId;
        this.messageHandler = messageHandler;
        startListening();
    }

    private void startListening() {
        new Thread(() -> {
            try {
                Socket socket = new Socket("192.168.0.104", 12347);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeInt(userId);
                oos.flush();

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    Message message = (Message) ois.readObject();
                    messageHandler.accept(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}