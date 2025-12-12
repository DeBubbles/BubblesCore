package me.bubbles.text;

public final class MessageFormatter {
    private final String prefix;

    public MessageFormatter(String prefix) {
        this.prefix = prefix == null ? "" : prefix;
    }

    public String msg(String message) {
        return ColorUtil.color(prefix + message);
    }

    public String color(String text) {
        return ColorUtil.color(text);
    }
}
