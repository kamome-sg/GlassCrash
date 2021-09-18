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
            // /config reload
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reset")) {
                    plugin.config.setDamage(3);
                    plugin.config.setDamageRadius(1.0);
                    sender.sendMessage(ChatColor.GREEN + "すべての設定項目をリセットしました");
                    return true;
                }
                else if (args[0].equalsIgnoreCase("reload")) {
                    plugin.config.load();
                    sender.sendMessage(ChatColor.GREEN + "設定の再読み込みが完了しました");
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
                        ParsedResult<Integer> damage = ParsedResult.tryParseUnsignedInt(args[2]);
                        if (damage.isSuccess()) {
                            plugin.config.setDamage(damage.getParsed());
                            sender.sendMessage(ChatColor.GREEN + "damage が " + damage.getParsed() + " に設定されました");
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "damage は 0 以上のint値で指定する必要があります");
                        }
                        return true;
                    }
                    else if (args[1].equalsIgnoreCase("damage-radius")) {
                        ParsedResult<Double> damageRadius = ParsedResult.tryParseUnsignedDouble(args[2]);
                        if (damageRadius.isSuccess()) {
                            plugin.config.setDamageRadius(damageRadius.getParsed());
                            sender.sendMessage(ChatColor.GREEN + "damage-radius が " + damageRadius.getParsed() + " に設定されました");
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "damage-radius は 0 以上のdouble値で指定する必要があります");
                        }
                        return true;
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
                Collections.addAll(commands, "set", "get", "reset", "reload");
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

class ParsedResult<T extends Number> {
    private final T parsed;
    private final boolean success;

    public ParsedResult(T parsed, boolean success) {
        this.parsed = parsed;
        this.success = success;
    }

    public T getParsed() {
        return parsed;
    }

    public boolean isSuccess() {
        return success;
    }

    static ParsedResult<Integer> tryParseUnsignedInt(String s) {
        try {
            int parsed = Integer.parseInt(s);
            if (parsed < 0) {
                return new ParsedResult<>(parsed, false);
            }
            return new ParsedResult<>(parsed, true);
        }
        catch (NumberFormatException e) {
            return new ParsedResult<>(null, false);
        }
    }

    static ParsedResult<Double> tryParseUnsignedDouble(String s) {
        try {
            double parsed = Double.parseDouble(s);
            if (parsed < 0) {
                return new ParsedResult<>(parsed, false);
            }
            return new ParsedResult<>(parsed, true);
        }
        catch (NumberFormatException e) {
            return new ParsedResult<>(null, false);
        }
    }
}
