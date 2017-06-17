package pl.hubot.dev.telnet_chat.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NickServ {
    public NickServ() {
    }

    public void addConnectedNick(String nick, User user) {
        connectedNicks.put(nick, user);
    }

    public void removeConnectedNick(String nick) {
        connectedNicks.remove(nick);
    }

    public Set<Map.Entry<String, User>> getConnectedNicks() {
        return connectedNicks.entrySet();
    }

    private Map<String, User> connectedNicks = new HashMap<String, User>();
}
