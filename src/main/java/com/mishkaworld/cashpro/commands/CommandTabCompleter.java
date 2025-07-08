package com.mishkaworld.cashpro.commands;

import com.mishkaworld.cashpro.CashProReloaded;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для автодополнения команд
 * 
 * @author Misha Ermakov
 */
public class CommandTabCompleter {
    
    private final CashProReloaded plugin;
    private final String currency;
    
    public CommandTabCompleter(CashProReloaded plugin, String currency) {
        this.plugin = plugin;
        this.currency = currency;
    }
    
    /**
     * Получить автодополнение для команды
     */
    public List<String> getCompletions(org.bukkit.command.CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        
        // Проверка прав
        if (!PermissionManager.hasCurrencyPermission(sender, currency)) {
            return completions;
        }
        
        if (args.length == 1) {
            // Первый аргумент - подкоманда
            String partial = args[0].toLowerCase();
            
            // Добавляем доступные подкоманды
            if (PermissionManager.hasCurrencyPermission(sender, currency)) {
                if ("balance".startsWith(partial)) completions.add("balance");
                if ("pay".startsWith(partial)) completions.add("pay");
                if ("top".startsWith(partial)) completions.add("top");
                if ("trans".startsWith(partial)) completions.add("trans");
            }
            
            // Административные команды
            if (PermissionManager.hasAdminPermission(sender, currency)) {
                if ("set".startsWith(partial)) completions.add("set");
                if ("give".startsWith(partial)) completions.add("give");
                if ("take".startsWith(partial)) completions.add("take");
            }
        } else if (args.length == 2) {
            // Второй аргумент - имя игрока для некоторых команд
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("pay") || subCommand.equals("set") || 
                subCommand.equals("give") || subCommand.equals("take") || subCommand.equals("trans")) {
                
                String partial = args[1].toLowerCase();
                
                // Для транзакций проверяем права администратора
                if (subCommand.equals("trans") && !PermissionManager.hasAdminPermission(sender, currency)) {
                    // Если нет прав администратора, предлагаем только номера страниц
                    completions.add("1");
                    completions.add("2");
                    completions.add("3");
                    return completions;
                }
                
                // Добавляем онлайн игроков
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(partial)) {
                        completions.add(player.getName());
                    }
                }
            }
        } else if (args.length == 3) {
            // Третий аргумент - сумма для команд с деньгами или страница для транзакций
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("pay") || subCommand.equals("set") || 
                subCommand.equals("give") || subCommand.equals("take")) {
                
                String partial = args[2].toLowerCase();
                
                // Предлагаем примеры сумм
                if (partial.isEmpty()) {
                    completions.add("100");
                    completions.add("1000");
                    completions.add("10000");
                } else if (partial.matches("\\d*")) {
                    completions.add(partial + "0");
                    completions.add(partial + "00");
                }
            } else if (subCommand.equals("trans")) {
                // Для транзакций третий аргумент - это страница
                String partial = args[2].toLowerCase();
                
                if (partial.isEmpty()) {
                    completions.add("1");
                    completions.add("2");
                    completions.add("3");
                } else if (partial.matches("\\d*")) {
                    completions.add(partial + "0");
                }
            }
        }
        
        return completions;
    }
} 