package me.bubbles.commands;

import me.bubbles.text.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final String commandName;
    private final List<SubCommand> subCommands = new ArrayList<>();

    private String noPermissionMessage = "&cJe hebt hier geen permissie voor.";
    private String playersOnlyMessage = "&cDit commando is alleen voor spelers.";
    private String unknownCommandMessage = "&cOnbekend subcommand.";
    private String invalidArgsMessage = "&cVerkeerde argumenten, gebruik: &e{usage}";

    public CommandHandler(String commandName) {
        this.commandName = commandName;
    }

    public CommandHandler register(SubCommand subCommand) {
        this.subCommands.add(subCommand);
        return this;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase(commandName)) return true;

        if (args.length == 0) {
            sender.sendMessage(ColorUtil.color(unknownCommandMessage));
            return true;
        }

        String subName = args[0].toLowerCase();
        SubCommand match = null;

        outer:
        for (SubCommand sub : subCommands) {
            for (String cmdName : sub.getCommands()) {
                if (cmdName.equalsIgnoreCase(subName)) {
                    match = sub;
                    break outer;
                }
            }
        }

        if (match == null) {
            sender.sendMessage(ColorUtil.color(unknownCommandMessage));
            return true;
        }

        if (match.isPlayerOnly() && !(sender instanceof org.bukkit.entity.Player)) {
            sender.sendMessage(ColorUtil.color(playersOnlyMessage));
            return true;
        }

        if (match.getPermission() != null && !match.getPermission().isEmpty()) {
            if (!sender.hasPermission(match.getPermission())) {
                sender.sendMessage(ColorUtil.color(noPermissionMessage));
                return true;
            }
        }

        ArrayList<String> subArgs = new ArrayList<>(Arrays.asList(args).subList(1, args.length));

        if (match.getExpectedArgs() != -1 && subArgs.size() != match.getExpectedArgs()) {
            String usage = "/" + label + " " + match.getBaseName() + " " + match.getArgumentsHint();
            String msg = invalidArgsMessage.replace("{usage}", usage);
            sender.sendMessage(ColorUtil.color(msg));
            return true;
        }

        try {
            match.onCommand(sender, subArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase(commandName)) return Collections.emptyList();

        // Eerste argument → subcommand namen
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();

            return subCommands.stream()
                    .filter(sub -> {
                        // perm check
                        String perm = sub.getPermission();
                        if (perm != null && !perm.isEmpty() && !sender.hasPermission(perm)) {
                            return false;
                        }
                        // playerOnly check
                        if (sub.isPlayerOnly() && !(sender instanceof org.bukkit.entity.Player)) {
                            return false;
                        }
                        return true;
                    })
                    .flatMap(sub -> sub.getCommands().stream())
                    .filter(name -> name.toLowerCase().startsWith(prefix))
                    .distinct()
                    .collect(Collectors.toList());
        }

        // Meer args → delegate naar subcommand zelf
        SubCommand match = findSubCommand(args[0]);
        if (match == null) return Collections.emptyList();

        // Perm / playerOnly respecteren
        String perm = match.getPermission();
        if (perm != null && !perm.isEmpty() && !sender.hasPermission(perm)) {
            return Collections.emptyList();
        }
        if (match.isPlayerOnly() && !(sender instanceof org.bukkit.entity.Player)) {
            return Collections.emptyList();
        }

        List<String> subArgs = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
        return match.onTabComplete(sender, subArgs);
    }

    private SubCommand findSubCommand(String name) {
        for (SubCommand sub : subCommands) {
            for (String cmdName : sub.getCommands()) {
                if (cmdName.equalsIgnoreCase(name)) {
                    return sub;
                }
            }
        }
        return null;
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    public CommandHandler noPermissionMessage(String message) {
        this.noPermissionMessage = message;
        return this;
    }
    public CommandHandler playersOnlyMessage(String message) {
        this.playersOnlyMessage = message;
        return this;
    }
    public CommandHandler unknownCommandMessage(String message) {
        this.unknownCommandMessage = message;
        return this;
    }
    public CommandHandler invalidArgsMessage(String message) {
        this.invalidArgsMessage = message;
        return this;
    }
}
