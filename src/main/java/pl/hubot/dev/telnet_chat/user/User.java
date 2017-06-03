package pl.hubot.dev.telnet_chat.user;

import java.util.UUID;

public class User {
    private String nickname;
    private UUID uuid;
    private boolean authenticated;

    public User() {
        this.uuid = UUID.randomUUID();
        this.authenticated = false;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
