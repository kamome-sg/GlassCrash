package com.github.kamomesg.glasscrash;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.*;

public class GlassCrashCommand implements CommandExecutor, TabCompleter {
    private final GlassCrashPlugin plugin;

    public GlassCrashCommand(GlassCrashPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("config")) {
            // /config reset
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reset")) {
                    plugin.config.setDamage(3);
                    plugin.config.setDamageRadius(1.0);
                    sender.sendMessage(ChatColor.GREEN + "すべての設定項目をリセットしました");
                    return true;
                }
            }

            // /config get <項目名>
            // /config reset <項目名>
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("get")) {
                    if (args[1].equalsIgnoreCase("damage")) {
                        sender.sendMessage(ChatColor.GREEN + "damage は " + plugin.config.getDamage() + " に設定されています");
                        return true;
                    }
                    else if (args[1].equalsIgnoreCase("damage-radius")) {
                        sender.sendMessage(ChatColor.GREEN + "damage-radius は " + plugin.config.getDamageRadius() + " に設定されています");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("reset")) {
                    if (args[1].equalsIgnoreCase("damage")) {
                        plugin.config.setDamage(3);
                        sender.sendMessage(ChatColor.GREEN + "damage をリセットしました");
                        return true;
                    }
                    else if (args[1].equalsIgnoreCase("damage-radius")) {
                        plugin.config.setDamageRadius(1.0);
                        sender.sendMessage(ChatColor.GREEN + "damage-radius をリセットしました");
                        return true;
                    }
                }
            }

            // /config set <項目名> <値>
            else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("set")) {
                    if (args[1].equalsIgnoreCase("damage")) {
                        try {
                            int damage = Integer.parseInt(args[2]);
                            if (damage < 0) {
                                throw new NumberFormatException();
                            }
                            plugin.config.setDamage(damage);
                            sender.sendMessage(ChatColor.GREEN + "damage が " + damage + " に設定されました");
                            return true;
                        }
                        catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "damage は 0 以上のint値で指定する必要があります");
                            return true;
                        }
                    }
                    else if (args[1].equalsIgnoreCase("damage-radius")) {
                        try {
                            double damageRadius = Double.parseDouble(args[2]);
                            if (damageRadius < 0) {
                                throw new NumberFormatException();
                            }
                            plugin.config.setDamageRadius(damageRadius);
                            sender.sendMessage(ChatColor.GREEN + "damage-radius が " + damageRadius + " に設定されました");
                            return true;
                        }
                        catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "damage-radius は 0 以上のdouble値で指定する必要があります");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        Set<String> commands = new HashSet<>();

        if (command.getName().equalsIgnoreCase("config")) {
            if (args.length == 1) {
                Collections.addAll(commands, "set", "get", "reset");
                StringUtil.copyPartialMatches(args[0], commands, completions);
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set") ||
                        args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("reset")) {
                    Collections.addAll(commands, "damage", "damage-radius");
                }
                StringUtil.copyPartialMatches(args[1], commands, completions);
            }
        }
        return completions;
    }
}
