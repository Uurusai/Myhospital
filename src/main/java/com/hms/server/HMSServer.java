package com.hms.server;

import com.hms.dao.DAOCommand;
import com.hms.dao.DAOProvider;
import com.hms.dao.DAOProviderImpl;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HMSServer {
    private static final int PORT = 12345; //should i use 5432 ??
    private final DAOProvider daoProvider = new DAOProviderImpl();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("HMS Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {

            while (true) {
                DAOCommand command = (DAOCommand) ois.readObject();
                Object result = command.execute(daoProvider);
                oos.writeObject(result);
                oos.flush();
            }
        } catch (EOFException e) {
            System.out.println("Client disconnected");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}