package pl.hubot.dev.telnet_chat.util;

public interface Plugin {
    void execute(String... args);
    String displayHelp();
    int getArgsCount();
}
