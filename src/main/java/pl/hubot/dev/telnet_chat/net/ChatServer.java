package pl.hubot.dev.telnet_chat.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private static final int PORT = 8189;

    public static void run() {
        try {
            int i = 0;
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (true) {
                Socket incoming = serverSocket.accept();
                System.out.println("Spawning " + i);
                MessageHandler handler = new MessageHandler(incoming);
                handler.start();
                i++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
