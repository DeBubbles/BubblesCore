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
    private static String prefix = "";

    private ColorUtil() {}

    private static boolean isHexSupported() {
        String version = Bukkit.getBukkitVersion();
        return version.startsWith("1.16") ||
                version.startsWith("1.17") ||
                version.startsWith("1.18") ||
                version.startsWith("1.19") ||
                version.startsWith("1.20") ||
                version.startsWith("1.21");
    }

    public static String color(String message) {
        if (message == null) return null;

        String colored = message;

        if (HEX_SUPPORTED) {
            Matcher matcher = HEX_PATTERN.matcher(colored);
            while (matcher.find()) {
                String hexCode = matcher.group();      // <#RRGGBB>
                String hex = hexCode.substring(2, 8);  // RRGGBB

                String replaceSharp = "x" + hex;
                char[] ch = replaceSharp.toCharArray();
                StringBuilder builder = new StringBuilder();
                for (char c : ch) {
                    builder.append("&").append(c);
                }
                colored = colored.replace(hexCode, builder.toString());
                matcher = HEX_PATTERN.matcher(colored);
            }
        }

        return ChatColor.translateAlternateColorCodes('&', colored);
    }

    public static String color(String message, Boolean prefix) {
        if (prefix && !ColorUtil.prefix.isEmpty()) {
            message = ColorUtil.prefix + message;
        }
        return color(message);
    }

    public static void setPrefix(String prefix){
        ColorUtil.prefix = prefix;
    }

    public static List<String> color(List<String> lines) {
        if (lines == null) return null;
        return lines.stream()
                .map(ColorUtil::color)
                .collect(Collectors.toList());
    }
}