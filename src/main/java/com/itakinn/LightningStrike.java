package com.itakinn;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LightningStrike extends JavaPlugin implements Listener {
    private final Map<UUID, BukkitRunnable> tasks = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("Plugin LightningStrike habilitado!");
        getCommand("raios").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin LightningStrike desabilitado!");
        for (BukkitRunnable task : tasks.values()) {
            task.cancel();
        }
        tasks.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Uso correto: /raios <player>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Jogador não encontrado.");
            return false;
        }

        toggleLightning(target);
        return true;
    }

    private void toggleLightning(Player player) {
        UUID playerId = player.getUniqueId();
        if (tasks.containsKey(playerId)) {
            // Se o jogador já está sendo atingido por raios, cancela a tarefa
            tasks.get(playerId).cancel();
            tasks.remove(playerId);
            player.sendMessage("Os raios foram desativados.");
        } else {
            // Inicia uma tarefa que atinge o jogador com raios constantemente enquanto ele se move
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || player.isDead()) {
                        // Se o jogador desconectar ou morrer, cancela a tarefa
                        cancel();
                        tasks.remove(playerId);
                        return;
                    }
                    player.getWorld().strikeLightning(player.getLocation());
                }
            };
            task.runTaskTimer(this, 0, 1); // A cada segundo (20 ticks)
            tasks.put(playerId, task);
            player.sendMessage("Os raios foram ativados. Mantenha-se em movimento!");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if (!player.isDead() && tasks.containsKey(playerId)) {
            // Se o jogador está se movendo e sendo atingido por raios, não faz nada
            return;
        }
    }
}
