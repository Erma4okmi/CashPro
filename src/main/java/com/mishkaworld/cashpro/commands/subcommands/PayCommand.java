package com.mishkaworld.cashpro.commands.subcommands;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.commands.SubCommand;
import com.mishkaworld.cashpro.utils.MessageUtils;
import com.mishkaworld.cashpro.utils.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Подкоманда для переводов между игроками
 * 
 * @author Misha Ermakov
 */
public class PayCommand implements SubCommand {
    
    private final CashProReloaded plugin;
    private final String currency;
    
    public PayCommand(CashProReloaded plugin, String currency) {
        this.plugin = plugin;
        this.currency = currency;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Проверка, что команда выполняется игроком
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("errors.player_only_command")));
            return;
        }
        
        Player player = (Player) sender;
        
        // Проверка количества аргументов
        if (args.length < 3) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("usage.currency_pay", "currency", currency)));
            return;
        }
        
        String targetPlayerName = args[1];
        String amountStr = args[2];
        
        // Проверка существования целевого игрока
        if (!ValidationUtils.playerExists(targetPlayerName)) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("player_not_found", "player", targetPlayerName)));
            return;
        }
        
        // Проверка, что игрок не пытается перевести деньги самому себе
        if (player.getName().equalsIgnoreCase(targetPlayerName)) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("cannot_pay_yourself")));
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
        
        // Проверка баланса
        long currentBalance = plugin.getCurrencyManager().getBalance(player.getUniqueId(), currency);
        if (currentBalance < amount) {
            String symbol = plugin.getCurrencyManager().getCurrencySymbol(currency);
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("insufficient_funds", 
                "balance", MessageUtils.formatNumber(currentBalance),
                "symbol", symbol
            )));
            return;
        }
        
        // Выполнение перевода
        boolean success = plugin.getCurrencyManager().transferMoney(
            player.getUniqueId(), player.getName(),
            targetPlayer.getUniqueId(), targetPlayer.getName(),
            currency, amount
        );
        
        if (success) {
            String symbol = plugin.getCurrencyManager().getCurrencySymbol(currency);
            
            // Сообщение отправителю
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("pay.success_sender",
                "amount", MessageUtils.formatNumber(amount),
                "symbol", symbol,
                "player", targetPlayer.getName()
            )));
            
            // Сообщение получателю
            targetPlayer.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("pay.success_receiver",
                "amount", MessageUtils.formatNumber(amount),
                "symbol", symbol,
                "sender", player.getName()
            )));
        } else {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("pay.failed")));
        }
    }
    
    @Override
    public String getDescription() {
        return "Перевести деньги другому игроку";
    }
    
    @Override
    public String getUsage() {
        return "/" + currency + " pay <игрок> <сумма>";
    }
} 