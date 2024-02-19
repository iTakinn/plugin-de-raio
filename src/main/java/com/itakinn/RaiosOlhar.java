package com.itakinn;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RaiosOlhar implements Listener, CommandExecutor {
    private final Map<UUID, BukkitRunnable> tasksOlhar = new HashMap<>();
    private final JavaPlugin plugin;

    public RaiosOlhar(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("lightningstrike.raiosolhar") || sender.isOp()) {

            if (args.length < 1) {
                toggleLightningOlhar(Bukkit.getPlayer(sender.getName()));
                return true;
            } else if (args.length == 1) { // se tiver argumento (player) chama ele
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(args[0] + " não está online.");
                    return true;
                }
                toggleLightningOlhar(target);
                return true;
            } else { // se tiver mais de um argumento
                sender.sendMessage("Uso correto: /raiosolhar <player>");
                return true;
            }
        } else {
            sender.sendMessage("Você não tem permissão para usar este comando.");
            return true;
        }
    }

    private void toggleLightningOlhar(Player target) {
        UUID uuid = target.getUniqueId();
        if (tasksOlhar.containsKey(uuid)) {
            // Se o jogador já está sendo atingido por raios, cancela a tarefa
            tasksOlhar.get(uuid).cancel();
            tasksOlhar.remove(uuid);
            target.sendMessage("Os raios foram desativados.");
        } else {
            BukkitRunnable tasks = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!target.isOnline() || target.isDead()) {
                        return;
                    }
                    Block bloco = target.getTargetBlock(null, 100);
                    if (!bloco.isEmpty())
                        target.getWorld().strikeLightningEffect(bloco.getLocation());
                    else {
                        return;
                    }
                }
            };
            tasks.runTaskTimer(plugin, 0, 1);
            tasksOlhar.put(uuid, tasks);
            target.sendMessage("Os raios foram ativados para o olhar.");
        }
    }
}
