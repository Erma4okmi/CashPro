package com.mishkaworld.cashpro.commands;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 * Основная команда для работы с валютами
 * 
 * @author Misha Ermakov
 */
public class CurrencyCommand implements CommandExecutor, TabCompleter {
    
    private final CashProReloaded plugin;
    private final String currency;
    private final SubCommandRegistry subCommandRegistry;
    private final CommandTabCompleter tabCompleter;
    
    public CurrencyCommand(CashProReloaded plugin, String currency) {
        this.plugin = plugin;
        this.currency = currency;
        this.subCommandRegistry = new SubCommandRegistry(plugin, currency);
        this.tabCompleter = new CommandTabCompleter(plugin, currency);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Проверка прав
        if (!PermissionManager.hasCurrencyPermission(sender, currency)) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("no_permission")));
            return true;
        }
        
        // Если нет аргументов, показываем баланс
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("errors.player_only_command")));
                return true;
            }
            
            Player player = (Player) sender;
            // Создаем новый массив с командой balance
            String[] balanceArgs = {"balance"};
            subCommandRegistry.getSubCommand("balance").execute(player, balanceArgs);
            return true;
        }
        
        // Получаем подкоманду
        String subCommand = args[0].toLowerCase();
        SubCommand cmd = subCommandRegistry.getSubCommand(subCommand);
        
        if (cmd == null) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("errors.command_not_found")));
            return true;
        }
        
        // Проверяем права для подкоманды
        if (!PermissionManager.hasSubCommandPermission(sender, currency, subCommand)) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("no_permission")));
            return true;
        }
        
        // Выполняем подкоманду
        try {
            cmd.execute(sender, args);
        } catch (Exception e) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("errors.command_execution_error", "error", e.getMessage())));
            plugin.getLogger().severe("Ошибка при выполнении команды " + subCommand + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return true;
    }
    
    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return tabCompleter.getCompletions(sender, args);
    }
    

} 