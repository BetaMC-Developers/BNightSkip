package org.betamc.bnightskip;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SleepListener implements Listener {

    private BNightSkip plugin;
    private final int sleepingPercentage;
    private final int sleepingTime;
    private boolean taskRunning = false;
    private HashMap<String, Set<Player>> sleepingPlayers = new HashMap<>();

    public SleepListener(BNightSkip plugin) {
        this.plugin = plugin;
        this.sleepingPercentage = plugin.getConfig().getInt("sleepingPercentage.value", 25);
        this.sleepingTime = plugin.getConfig().getInt("sleepingTime.value", 5) * 20;
    }

    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        Set<Player> sleepingPlayersInWorld = sleepingPlayers.getOrDefault(world.getName(), new HashSet<>());
        sleepingPlayersInWorld.add(player);
        sleepingPlayers.put(world.getName(), sleepingPlayersInWorld);

        Set<Player> playersInWorld = Arrays.stream(Bukkit.getOnlinePlayers()).filter(p -> p.getWorld().equals(world)).collect(Collectors.toSet());
        int sleeping = sleepingPlayers.get(world.getName()).size();
        int required = (int) Math.ceil(playersInWorld.size() * ((double) sleepingPercentage / 100));

        if (sleeping >= required) {
            playersInWorld.forEach(p -> p.sendMessage(String.format("§a%d/%d §7players sleeping", sleeping, required)));
            if (!taskRunning) {
                taskRunning = true;
                new SleepTimer(world);
            }
        } else {
            playersInWorld.forEach(p -> p.sendMessage(String.format("§c%d/%d §7players sleeping", sleeping, required)));
        }
    }

    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        Set<Player> sleepingPlayersInWorld = sleepingPlayers.get(world.getName());
        sleepingPlayersInWorld.remove(player);
        sleepingPlayers.put(world.getName(), sleepingPlayersInWorld);

        Set<Player> playersInWorld = Arrays.stream(Bukkit.getOnlinePlayers()).filter(p -> p.getWorld().equals(world)).collect(Collectors.toSet());
        int sleeping = sleepingPlayers.get(world.getName()).size();
        int required = (int) Math.ceil(playersInWorld.size() * ((double) sleepingPercentage / 100));

        if (world.getTime() > 10 && world.getTime() < 23450) {
            playersInWorld.forEach(p -> p.sendMessage(String.format(sleeping >= required ?
                    "§a%d/%d §7players sleeping" : "§c%d/%d §7players sleeping", sleeping, required))
            );
        }
    }

    class SleepTimer implements Runnable {

        private SleepCheck checker;
        private World world;
        private final int taskId;

        private SleepTimer(World world) {
            this.world = world;
            taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, sleepingTime);
            checker = new SleepCheck(this);
        }

        @Override
        public void run() {
            world.setTime(0);
            if (plugin.getConfig().getBoolean("clearRain.value", true) && world.hasStorm()) {
                world.setThundering(false);
                world.setWeatherDuration(5);
            }
            Bukkit.getScheduler().cancelTask(checker.taskId);
            taskRunning = false;
        }
    }

    class SleepCheck implements Runnable {

        private SleepTimer task;
        private final int taskId;

        private SleepCheck(SleepTimer timer) {
            task = timer;
            taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1);
        }

        @Override
        public void run() {
            int playersInWorld = (int) Arrays.stream(Bukkit.getOnlinePlayers()).filter(p -> p.getWorld().equals(task.world)).count();
            int sleeping = sleepingPlayers.get(task.world.getName()).size();
            int required = (int) Math.ceil(playersInWorld * ((double) sleepingPercentage / 100));
            if (sleeping < required) {
                Bukkit.getScheduler().cancelTask(task.taskId);
                Bukkit.getScheduler().cancelTask(taskId);
                taskRunning = false;
            }
        }
    }
}
