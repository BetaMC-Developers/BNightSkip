package org.betamc.bnightskip;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BNightSkip extends JavaPlugin {

    private Config config;

    @Override
    public void onEnable() {
        config = new Config(this);
        Bukkit.getPluginManager().registerEvents(new SleepListener(this), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player player: Bukkit.getOnlinePlayers()) {
                if (player.isSleeping()) {
                    ((CraftPlayer) player).getHandle().sleepTicks = 0;
                }
            }
        }, 0, 10);
        Bukkit.getLogger().info("[" + getDescription().getName() + "] Has loaded, Version: " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("[" + getDescription().getName() + "] Stopping plugin");
    }

    public Config getConfig() {
        return config;
    }

}
