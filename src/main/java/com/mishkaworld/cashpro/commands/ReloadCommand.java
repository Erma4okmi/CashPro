package com.mishkaworld.cashpro.commands;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Команда для перезагрузки плагина
 * 
 * @author Misha Ermakov
 */
public class ReloadCommand implements CommandExecutor {
    
    private final CashProReloaded plugin;
    
    public ReloadCommand(CashProReloaded plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Проверка, что команда выполняется из консоли
        if (sender instanceof org.bukkit.entity.Player) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("errors.console_only_command")));
            return true;
        }
        
        // Проверка аргументов
        if (args.length < 1 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("usage.cashpro_reload")));
            return true;
        }
        
        try {
            // Перезагрузка конфигураций
            plugin.getConfigManager().reloadConfigs();
            
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("reload.success")));
            plugin.getLogger().info("Плагин перезагружен администратором " + sender.getName());
            
        } catch (Exception e) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("reload.failed")));
            plugin.getLogger().severe("Ошибка при перезагрузке плагина: " + e.getMessage());
            e.printStackTrace();
        }
        
        return true;
    }
} 