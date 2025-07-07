package com.mishkaworld.cashpro.commands.subcommands;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.commands.SubCommand;
import com.mishkaworld.cashpro.utils.MessageUtils;
import com.mishkaworld.cashpro.utils.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Подкоманда для просмотра баланса
 * 
 * @author Misha Ermakov
 */
public class BalanceCommand implements SubCommand {
    
    private final CashProReloaded plugin;
    private final String currency;
    
    public BalanceCommand(CashProReloaded plugin, String currency) {
        this.plugin = plugin;
        this.currency = currency;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        String targetPlayer = null;
        
        // Если указан игрок и у отправителя есть права администратора
        if (args.length > 1 && sender.hasPermission("cashpro." + currency + ".admin")) {
            targetPlayer = args[1];
        } else if (sender instanceof Player) {
            targetPlayer = sender.getName();
        } else {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("errors.specify_player")));
            return;
        }
        
        // Проверка существования игрока
        if (!ValidationUtils.playerExists(targetPlayer)) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("player_not_found", "player", targetPlayer)));
            return;
        }
        
        // Получение баланса
        long balance = plugin.getCurrencyManager().getBalance(targetPlayer, currency);
        String symbol = plugin.getCurrencyManager().getCurrencySymbol(currency);
        String currencyName = plugin.getCurrencyManager().getCurrencyName(currency);
        
        // Отображение баланса
        sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("balance.balance_line", 
            "currency", currencyName,
            "amount", MessageUtils.formatNumber(balance),
            "symbol", symbol
        )));
    }
    
    @Override
    public String getDescription() {
        return "Показать баланс игрока";
    }
    
    @Override
    public String getUsage() {
        return "/" + currency + " balance [игрок]";
    }
} 