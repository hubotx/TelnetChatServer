package pl.hubot.dev.telnet_chat.plugin;

import pl.hubot.dev.telnet_chat.net.MessageHandler;
import pl.hubot.dev.telnet_chat.user.User;
import pl.hubot.dev.telnet_chat.util.Plugin;

public class PluginQuery implements Plugin {

    @Override
    public void execute(String... args) {
        for (MessageHandler handler : MessageHandler.getHandlers()) {
            User desiredUser = MessageHandler.getUsers().get(handler.getUser().getNickname());
            if (desiredUser.getNickname().equals(args[0])) {
                handler.getOut().println("[privmsg] " + args[0] + ": " + args[1]);
            }
        }
    }

    @Override
    public String displayHelp() {
        return "Usage: :PRIVMSG user message";
    }

    @Override
    public int getArgsCount() {
        return 2;
    }
}
