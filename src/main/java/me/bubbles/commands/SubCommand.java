package me.bubbles.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SubCommand {

    private final List<String> commands = new ArrayList<>();
    private String permission = null;
    private boolean playerOnly = false;
    private int expectedArgs = -1; // -1 = niet checken
    private String argsHint = "";
    private String description = "";

    public SubCommand(String name, boolean playerOnly) {
        this.commands.add(name);
        this.playerOnly = playerOnly;
    }
    public SubCommand(String name, String perm, boolean playerOnly) {
        this(name, playerOnly);
        this.permission = perm;
    }
    public SubCommand(String name, int expectedArgs, boolean playerOnly) {
        this(name, playerOnly);
        this.expectedArgs = expectedArgs;
    }
    public SubCommand(String name, String perm, int expectedArgs, boolean playerOnly) {
        this(name, perm, playerOnly);
        this.expectedArgs = expectedArgs;
    }

    public abstract void onCommand(CommandSender sender, ArrayList<String> args);

    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        return Collections.emptyList();
    }

    public List<String> getCommands() {
        return commands;
    }

    public void addAlias(String alias) {
        this.commands.add(alias);
    }

    public String getBaseName() {
        return commands.get(0);
    }

    public String getPermission() {
        return permission;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public int getExpectedArgs() {
        return expectedArgs;
    }

    public String getArgumentsHint() {
        return argsHint;
    }

    protected void setArgumentsHint(String argumentsHint) {
        this.argsHint = argumentsHint;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }
}
