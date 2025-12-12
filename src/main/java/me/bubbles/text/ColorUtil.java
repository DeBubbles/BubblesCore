package me.bubbles.text;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("<#[a-fA-F0-9]{6}>");
    private static final boolean HEX_SUPPORTED = isHexSupported();
    private static volatile String prefix = "";

    private ColorUtil() {}

    public static void setPrefix(String prefix) {
        ColorUtil.prefix = prefix == null ? "" : prefix;
    }

    public static String color(String message) {
        if (message == null) return null;
        return translateHexAndAmpersand(message);
    }

    public static String msg(String message) {
        if (message == null) return null;
        String p = prefix; // local copy
        return translateHexAndAmpersand(p.isEmpty() ? message : p + message);
    }

    public static List<String> color(List<String> lines) {
        if (lines == null) return null;
        return lines.stream().map(ColorUtil::color).toList();
    }

    public static List<String> msg(List<String> lines) {
        if (lines == null) return null;
        return lines.stream().map(ColorUtil::msg).toList();
    }

    private static String translateHexAndAmpersand(String input) {
        String colored = input;

        if (HEX_SUPPORTED) {
            Matcher matcher = HEX_PATTERN.matcher(colored);
            while (matcher.find()) {
                String hexCode = matcher.group();      // <#RRGGBB>
                String hex = hexCode.substring(2, 8);  // RRGGBB

                String replaceSharp = "x" + hex;
                StringBuilder builder = new StringBuilder();
                for (char c : replaceSharp.toCharArray()) {
                    builder.append("&").append(c);
                }

                colored = colored.replace(hexCode, builder.toString());
                matcher = HEX_PATTERN.matcher(colored);
            }
        }

        return ChatColor.translateAlternateColorCodes('&', colored);
    }

    private static boolean isHexSupported() {
        String version = Bukkit.getBukkitVersion();
        return version.startsWith("1.16") ||
                version.startsWith("1.17") ||
                version.startsWith("1.18") ||
                version.startsWith("1.19") ||
                version.startsWith("1.20") ||
                version.startsWith("1.21");
    }
}