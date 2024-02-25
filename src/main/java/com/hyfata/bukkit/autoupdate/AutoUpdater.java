package com.hyfata.bukkit.autoupdate;

public class AutoUpdater {
    // parameter ex: paper 1.20.2
    public static void main(String[] args) {
        System.out.println("업데이트 확인 중...");
        if (args[0].equals("purpur")) {
            new BukkitUpdater(args[1], BukkitUpdater.PURPUR);
        } else if (args[0].equals("paper")) {
            new BukkitUpdater(args[1], BukkitUpdater.PAPER);
        }
    }
}