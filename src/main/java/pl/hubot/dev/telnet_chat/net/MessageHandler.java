package pl.hubot.dev.telnet_chat.net;

import pl.hubot.dev.telnet_chat.user.User;
import pl.hubot.dev.telnet_chat.util.PluginExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.*;

public class MessageHandler extends Thread {
    // Available connections
    private static final Set<MessageHandler> handlers = Collections.synchronizedSet(new HashSet<MessageHandler>());
    // Connected users
    private static final Map<String, User> users = Collections.synchronizedMap(new HashMap<String, User>());
    // Reserved nicknames
    private static final Map<String, UUID> reservedNicks = Collections.synchronizedMap(new HashMap<String, UUID>());

    // Connection socket
    private Socket incoming;
    // Stream output
    private PrintWriter out;
    // The connected user
    private User user;

    MessageHandler(Socket incoming) {
        this.incoming = incoming;
        this.user = new User();
    }

    public void run() {
        try {
            handlers.add(this);

            InputStream inputStream = incoming.getInputStream();
            OutputStream outputStream = incoming.getOutputStream();

            Scanner in = new Scanner(inputStream);
            out = new PrintWriter(outputStream, true /* autoFlush */);

            out.println("dev::hubot.pl - Telnet Chat Demo");

            out.println("Choose your nick:");
            String choosedNick = in.nextLine();
            while (reservedNicks.containsKey(choosedNick)) {
                out.println("Sorry, but this nickname is reserved. Please try again.");
                choosedNick = in.nextLine();
            }
            reservedNicks.put(choosedNick, user.getUuid());
            user.setNickname(choosedNick);
            users.put(choosedNick, user);
            reservedNicks.put(user.getNickname(), user.getUuid());
            user.setAuthenticated(true);

            out.println("Hello! Enter BYE to exit.");

            boolean done = false;
            while (!done && in.hasNextLine()) {
                String line = in.nextLine();
                if (line.trim().equals("BYE"))
                    done = true;
                if (line.startsWith(":")) {
                    PluginExecutor pluginExecutor = new PluginExecutor(user);
                    try {
                        pluginExecutor.execute(line);
                    } catch (ClassNotFoundException ex) {
                        out.println("Error: Plugin not found!!!");
                    } catch (IllegalAccessException
                            | InstantiationException
                            | InvocationTargetException
                            | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                } else {
                    sendAll(user.getNickname() + ": " + line);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (user != null) {
                users.remove(user.getNickname());
                reservedNicks.remove(user.getNickname());
                user = null;
                handlers.remove(this);
            }
            try {
                incoming.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Set<MessageHandler> getHandlers() {
        return handlers;
    }

    public static Map<String, User> getUsers() {
        return users;
    }

    public User getUser() {
        return user;
    }

    public PrintWriter getOut() {
        return out;
    }

    private void sendAll(String message) {
        synchronized (handlers) {
            for (MessageHandler handler : handlers) {
                handler.respond(message);
            }
        }
    }

    private void respond(String text) {
        this.out.println(text);
        this.out.flush();
    }
}
