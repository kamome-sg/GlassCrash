package com.github.kamomesg.glasscrash;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class GlassCrashConfig {
    private final Plugin plugin;
    private FileConfiguration config = null;

    private int damage;
    private double damageRadius;

    private final String DAMAGE_KEY = "damage";
    private final String DAMAGE_RADIUS_KEY = "damage-radius";

    public GlassCrashConfig(Plugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.saveDefaultConfig();
        if (config != null) {
            plugin.reloadConfig();
        }
        config = plugin.getConfig();

        int damage = config.getInt(DAMAGE_KEY, -1);
        if (damage < 0) {
            plugin.getLogger().info(ChatColor.RED + "damage は 0 以上のint値で指定する必要があります");
            setDamage(3); //デフォルト値3をconfig.ymlに直接保存
        }
        else {
            this.damage = damage;
        }

        double damageRadius = config.getDouble(DAMAGE_RADIUS_KEY, -1);
        if (damageRadius < 0) {
            plugin.getLogger().info(ChatColor.RED + "damage-radius は 0 以上のdouble値で指定する必要があります");
            setDamageRadius(1.0);
        }
        else {
            this.damageRadius = damageRadius;
        }
    }

    public void setDamage(int damage) {
        this.damage = damage;
        plugin.getConfig().set(DAMAGE_KEY, damage);
        plugin.saveConfig();
    }

    public void setDamageRadius(double damageRadius) {
        this.damageRadius = damageRadius;
        plugin.getConfig().set(DAMAGE_RADIUS_KEY, damageRadius);
        plugin.saveConfig();
    }

    public int getDamage() {
        return damage;
    }

    public double getDamageRadius() {
        return damageRadius;
    }
}
