package com.mishkaworld.cashpro.commands;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.commands.subcommands.*;
import com.mishkaworld.cashpro.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Основная команда для работы с валютами
 * 
 * @author Misha Ermakov
 */
public class CurrencyCommand implements CommandExecutor, TabCompleter {
    
    private final CashProReloaded plugin;
    private final String currency;
    private final Map<String, SubCommand> subCommands;
    
    public CurrencyCommand(CashProReloaded plugin, String currency) {
        this.plugin = plugin;
        this.currency = currency;
        this.subCommands = new HashMap<>();
        
        // Регистрация подкоманд
        registerSubCommands();
    }
    
    /**
     * Регистрация подкоманд
     */
    private void registerSubCommands() {
        subCommands.put("balance", new BalanceCommand(plugin, currency));
        subCommands.put("pay", new PayCommand(plugin, currency));
        subCommands.put("top", new TopCommand(plugin, currency));
        subCommands.put("trans", new TransactionsCommand(plugin, currency));
        subCommands.put("set", new SetCommand(plugin, currency));
        subCommands.put("give", new GiveCommand(plugin, currency));
        subCommands.put("take", new TakeCommand(plugin, currency));
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Проверка прав
        if (!sender.hasPermission("cashpro." + currency)) {
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
            subCommands.get("balance").execute(player, balanceArgs);
            return true;
        }
        
        // Получаем подкоманду
        String subCommand = args[0].toLowerCase();
        SubCommand cmd = subCommands.get(subCommand);
        
        if (cmd == null) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("errors.command_not_found")));
            return true;
        }
        
        // Проверяем права для подкоманды
        if (!hasPermission(sender, subCommand)) {
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
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        // Проверка прав
        if (!sender.hasPermission("cashpro." + currency)) {
            return completions;
        }
        
        if (args.length == 1) {
            // Первый аргумент - подкоманда
            String partial = args[0].toLowerCase();
            
            // Добавляем доступные подкоманды
            if (sender.hasPermission("cashpro." + currency)) {
                if ("balance".startsWith(partial)) completions.add("balance");
                if ("pay".startsWith(partial)) completions.add("pay");
                if ("top".startsWith(partial)) completions.add("top");
                if ("trans".startsWith(partial)) completions.add("trans");
            }
            
            // Административные команды
            if (sender.hasPermission("cashpro." + currency + ".admin") || sender.hasPermission("cashpro.admin")) {
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
                if (subCommand.equals("trans") && !sender.hasPermission("cashpro." + currency + ".admin")) {
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
    
    /**
     * Проверить права для подкоманды
     */
    private boolean hasPermission(CommandSender sender, String subCommand) {
        switch (subCommand) {
            case "balance":
            case "pay":
            case "top":
            case "trans":
                return sender.hasPermission("cashpro." + currency);
            case "set":
            case "give":
            case "take":
                return sender.hasPermission("cashpro." + currency + ".admin") || sender.hasPermission("cashpro.admin");
            default:
                return false;
        }
    }
    

} 