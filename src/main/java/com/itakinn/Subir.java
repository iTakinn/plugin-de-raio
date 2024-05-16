package com.itakinn;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Subir implements Listener, CommandExecutor {
    private final JavaPlugin plugin;

    public Subir(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("TKNRaios.subir") || sender.isOp()) {
            if (args.length < 1) { // se argumento for menor de um (sem argumento) usa quem mandou o comando
                subirPlayer(Bukkit.getPlayer(sender.getName()));
                return true;
            } else if (args.length == 1) { // se tiver argumento (player) chama ele
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(args[0] + " não está online.");
                    return true;
                }
                subirPlayer(target);
                return true;
            } else {
                sender.sendMessage("Uso correto: /subir <player>");
                return true;
            }
        } else {
            sender.sendMessage("Você não tem permissão para usar este comando.");
            return true;
        }
    }

    private void subirPlayer(Player player) {
        World max;
        max = player.getWorld();
        Location loc = player.getLocation();
        loc.setY(max.getHighestBlockYAt(loc) + 1);
        player.teleport(loc);

    }
}
