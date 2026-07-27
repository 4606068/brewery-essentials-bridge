package org.example;

import org.bukkit.plugin.java.JavaPlugin;

public class BreweryEssentialsBridge extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ShopSignListener(), this);
        getLogger().info("BreweryEssentialsBridge enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BreweryEssentialsBridge disabled!");
    }
}