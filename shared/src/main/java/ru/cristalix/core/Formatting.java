package ru.cristalix.core;

public final class Formatting {

    private Formatting() { }

    public static String ofTotalPercent(int count, int total) {
        if (total == 0) {
            total = count;
        }

        int percent = (int) ((float) (count * 100) / (float) total);
        String color = percent > 95 ? "§c" : (percent > 90 ? "§6" : (percent > 50 ? "§e" : "§a"));
        return "§7(" + color + count + "§7 /§a " + total + "§7, " + color + percent + " %§7)";
    }
}
