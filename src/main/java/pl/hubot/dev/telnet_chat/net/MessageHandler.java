package pl.hubot.dev.telnet_chat.net;

import pl.hubot.dev.telnet_chat.user.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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

                if (line.trim().startsWith(":PRIVMSG")) {
                    String[] args = line.trim().split(" ");
                    if (args.length > 2) {
                        priv(args[1], line.trim().substring(args[0].length() + args[1].length() + 2, line.trim().length()));
                    } else {
                        out.println("Usage: :PRIVMSG [user] message");
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

    private PrintWriter getOut() {
        return out;
    }

    private void respond(String text) {
        this.out.println(text);
        this.out.flush();
    }

    private void sendAll(String message) {
        synchronized (handlers) {
            for (MessageHandler handler : handlers) {
                handler.respond(message);
            }
        }
    }

    private void priv(String nickname, String message) {
        for (MessageHandler handler : handlers) {
            User desiredUser = users.get(user.getNickname());
            if (desiredUser.getNickname().equals(nickname)) {
                handler.getOut().println("[privmsg] " + nickname + ": " + message);
            }
        }
    }
}
