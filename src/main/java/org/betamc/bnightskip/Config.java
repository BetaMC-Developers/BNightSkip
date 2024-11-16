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
        generateOption("clearRain.info", "Should the rain be cleared when time is changed to day?");
        generateOption("clearRain.value", true);
    }

    private void generateOption(String key, Object defaultValue) {
        if (this.getProperty(key) == null) {
            this.setProperty(key, defaultValue);
        }
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    public int getUInt(String path, int def) {
        Integer o = castInt(this.getProperty(path));
        if (o == null || o < 0) {
            this.setProperty(path, def);
            return def;
        } else {
            return o;
        }
    }

    /*
    why does this method NEED to be private bukkit?
    this code looks like shit but i'm using it anyway
    since i'm too lazy to code a proper solution
     */
    private static Integer castInt(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof Byte) {
            return (int) (Byte) o;
        } else if (o instanceof Integer) {
            return (Integer) o;
        } else if (o instanceof Double) {
            return (int) (double) (Double) o;
        } else if (o instanceof Float) {
            return (int) (float) (Float) o;
        } else if (o instanceof Long) {
            return (int) (long) (Long) o;
        } else {
            return null;
        }
    }

}
