package com.fomdev.sasm.command;

import com.fomdev.sasm.api.PluginClassUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandSasm {
    public static class SasmCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            switch (args.length) {
                case 0 -> {
                    return false;
                }

                case 1 -> {
                    switch (args[0]) {
                        case "list" -> {
                            for (String p : PluginClassUtil.getPluginEntries()) {
                                for (Class<?> k : PluginClassUtil.getASMData(p)) {
                                    sender.sendMessage
                                            (
                                                    ChatColor.AQUA + "[SASM] @@0: ".replace("@0", p) + ChatColor.GOLD + "@1".replace("@1", k.getName())
                                            );
                                }
                            }
                            return true;
                        }

                        case "rescan" -> {
                            PluginClassUtil.rescanCache();
                            sender.sendMessage("Successfully scheduled recache and rescan");
                            return true;
                        }

                        default -> {
                            return false;
                        }
                    }
                }

                case 2 -> {
                    if (args[0].equals("locate")) {
                         Class<?> klazz = PluginClassUtil.findClass(args[1]);

                         if (klazz == null) {
                             sender.sendMessage(ChatColor.RED + "INVALID CLASS ID [Either not existing or not registered]");
                             return true;
                         }

                         sender.sendMessage(ChatColor.AQUA + "[SASM] " + PluginClassUtil.findPlugin(klazz) + ": " + ChatColor.GOLD + klazz.getName());
                    }

                    return true;
                }

                case 3 -> {
                    if (!args[0].equals("list")) {
                        return false;
                    }

                    switch (args[1]) {
                        case "annotation" -> {
                            try {
                                for  (Class<?> k : PluginClassUtil.getAllMatch((Class<? extends Annotation>) Class.forName(args[2]))) {
                                    sender.sendMessage(ChatColor.AQUA + "[SASM] " + PluginClassUtil.findPlugin(k) + ": " + ChatColor.GOLD + k.getName());
                                }
                            } catch (ClassCastException | ClassNotFoundException ignored) {

                            }

                            return true;
                        }

                        case "plugin" -> {
                            for (Class<?> k : PluginClassUtil.getASMData(args[2])) {
                                sender.sendMessage(ChatColor.AQUA + "[SASM] @" + PluginClassUtil.findPlugin(k) + net.md_5.bungee.api.ChatColor.GOLD + ": " + k.getName());
                            }

                            return true;
                        }

                        default -> {
                            return false;
                        }
                    }
                }
            }
            return false;
        }
    }

    public static class SasmCompletion implements TabCompleter {
        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            switch (args.length) {
                case 1 -> {
                    return Arrays.asList
                            (
                                    "list",
                                    "locate",
                                    "rescan"
                            );
                }

                case 2 -> {
                    if (args[0].equals("list")) {
                        return Arrays.asList("annotation", "plugin");
                    }
                    return Collections.emptyList();
                }

                case 3 -> {
                    if (args[0].equals("list") && args[1].equals("plugin")) {
                        return new ArrayList<>(PluginClassUtil.getPluginEntries());
                    }
                    return Collections.emptyList();
                }
            }
            return Collections.emptyList();
        }
    }
}