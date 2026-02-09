package com.fomdev.sasm.init;

import com.fomdev.sasm.api.PluginClassUtil;
import com.fomdev.sasm.command.CommandSasm;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotASM extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        PluginClassUtil.applyClasses();
        PluginClassUtil.buildAnnotations();

        assert getCommand("sasm") != null;
        getCommand("sasm").setExecutor(new CommandSasm.SasmCommand());
        getCommand("sasm").setTabCompleter(new CommandSasm.SasmCompletion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        PluginClassUtil.rescanCache();
    }
}