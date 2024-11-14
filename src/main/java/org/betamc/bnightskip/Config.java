package org.betamc.bnightskip;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import java.io.File;

public class Config extends Configuration {

    public Config(Plugin plugin) {
        super(new File(plugin.getDataFolder(), "config.yml"));
        reload();
    }

    private void reload() {
        load();
        write();
        save();
    }

    private void write() {
        generateOption("sleepingPercentage.info", "The percentage of players that need to sleep for the time to change to day.");
        generateOption("sleepingPercentage.value", 25);
        generateOption("sleepingTime.info", "How long (in seconds) the required percentage of players have to sleep before the time changes to day.");
        generateOption("sleepingTime.value", 5);
    }

    private void generateOption(String key, Object defaultValue) {
        if (this.getProperty(key) == null) {
            this.setProperty(key, defaultValue);
        }
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

}
