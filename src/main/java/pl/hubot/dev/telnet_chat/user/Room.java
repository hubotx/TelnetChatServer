package pl.hubot.dev.telnet_chat.user;

import java.util.ArrayList;
import java.util.List;

public class Room {
    public Room() {
        this.name = "";
        this.topic = "";
    }

    public Room(String name) {
        this.name = name;
    }

    public Room(String name, String topic) {
        this.name = name;
        this.topic = topic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<User> getConnectedUsers() {
        return connectedUsers;
    }

    public void setConnectedUsers(List<User> connectedUsers) {
        this.connectedUsers = connectedUsers;
    }

    private String name;
    private String topic;
    private List<User> connectedUsers = new ArrayList<User>();
}
