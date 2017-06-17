package pl.hubot.dev.telnet_chat.util;

import pl.hubot.dev.telnet_chat.net.MessageHandler;
import pl.hubot.dev.telnet_chat.user.User;
import sun.security.krb5.internal.PAData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PluginExecutor {
    public PluginExecutor(User user) {
        this.user = user;
    }

    public void execute(String input) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Command parsedInput = parseCommand(input);
        if (parsedInput.getName() != null) {
            String pluginName = "pl.hubot.dev.telnet_chat.plugin.Plugin"
                    + parsedInput.getName().substring(0, 1).toUpperCase()
                    + parsedInput.getName().substring(1).toLowerCase();
            Class<?> cls = Class.forName(pluginName);
            Object obj = cls.newInstance();
            Method method1 = cls.getDeclaredMethod("getArgsCount");
            Object value1 = method1.invoke(obj);
            int argsCount = (int) value1;
            if (parsedInput.getParams() != null
                    && !parsedInput.getParams().isEmpty()
                    && !parsedInput.getParams().equals("")
                    && parsedInput.getParams().split(" ").length == argsCount) {
                Method method = cls.getDeclaredMethod("execute", String[].class);
                method.invoke(obj, new Object[] { parsedInput.getParams().split(" ") });
            } else {
                Method method = cls.getDeclaredMethod("displayHelp");
                Object value = method.invoke(obj);
                String helpMsg = (String) value;
                for (MessageHandler handler : MessageHandler.getHandlers()) {
                    User desiredUser = MessageHandler.getUsers().get(handler.getUser().getNickname());
                    if (desiredUser != null && desiredUser.getNickname().equals(user.getNickname())) {
                        handler.getOut().println(helpMsg);
                    }
                }
            }
        }
    }

    private Command parseCommand(String input) {
        String[] args = input.split(" ");

        if (args[0].startsWith(":")) {
            String cmdName = !args[0].equals(":")
                    ? args[0].substring(1, args[0].length())
                    : null;
            StringBuilder cmdParamBuilder = new StringBuilder();
            String cmdParams = null;

            if (args.length > 0) {
                for (int i = 1; i < args.length; i++) {
                    cmdParamBuilder.append(args[i]);
                    if (i != args.length - 1) cmdParamBuilder.append(" ");
                }
                cmdParams = cmdParamBuilder.toString();
            }

            return new Command(cmdName, cmdParams);
        }

        return new Command(null, null);
    }

    private class Command {
        Command(String name, String params) {
            this.name = name;
            this.params = params;
        }

        public String getName() {
            return name;
        }

        public String getParams() {
            return params;
        }

        private String name;
        private String params;
    }

    private User user;
}
