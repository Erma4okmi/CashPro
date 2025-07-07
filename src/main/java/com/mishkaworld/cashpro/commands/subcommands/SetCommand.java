package com.mishkaworld.cashpro.commands.subcommands;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.commands.SubCommand;
import com.mishkaworld.cashpro.utils.MessageUtils;
import com.mishkaworld.cashpro.utils.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Административная подкоманда для установки баланса
 * 
 * @author Misha Ermakov
 */
public class SetCommand implements SubCommand {
    
    private final CashProReloaded plugin;
    private final String currency;
    
    public SetCommand(CashProReloaded plugin, String currency) {
        this.plugin = plugin;
        this.currency = currency;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Проверка количества аргументов
        if (args.length < 3) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("usage.currency_set", "currency", currency)));
            return;
        }
        
        String targetPlayerName = args[1];
        String amountStr = args[2];
        
        // Проверка существования целевого игрока
        if (!ValidationUtils.playerExists(targetPlayerName)) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("player_not_found", "player", targetPlayerName)));
            return;
        }
        
        // Валидация суммы
        Long amount = ValidationUtils.parseAmount(amountStr);
        if (amount == null) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("invalid_amount")));
            return;
        }
        
        // Получение целевого игрока
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("errors.player_not_online", "player", targetPlayerName)));
            return;
        }
        
        // Установка баланса
        boolean success = plugin.getCurrencyManager().setBalance(
            targetPlayer.getUniqueId(), targetPlayer.getName(),
            currency, amount
        );
        
        if (success) {
            String symbol = plugin.getCurrencyManager().getCurrencySymbol(currency);
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("admin.set_success",
                "player", targetPlayer.getName(),
                "amount", MessageUtils.formatNumber(amount),
                "symbol", symbol
            )));
        } else {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("admin.operation_failed")));
        }
    }
    
    @Override
    public String getDescription() {
        return "Установить баланс игрока";
    }
    
    @Override
    public String getUsage() {
        return "/" + currency + " set <игрок> <сумма>";
    }
} 