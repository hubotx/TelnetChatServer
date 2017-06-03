package pl.hubot.dev.telnet_chat;

import pl.hubot.dev.telnet_chat.net.ChatServer;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        ChatServer.run();
    }
}
