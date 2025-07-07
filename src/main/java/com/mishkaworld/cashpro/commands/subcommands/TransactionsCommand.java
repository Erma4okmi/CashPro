package com.mishkaworld.cashpro.commands.subcommands;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.commands.SubCommand;
import com.mishkaworld.cashpro.economy.Transaction;
import com.mishkaworld.cashpro.utils.MessageUtils;
import com.mishkaworld.cashpro.utils.ValidationUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Подкоманда для просмотра транзакций
 * 
 * @author Misha Ermakov
 */
public class TransactionsCommand implements SubCommand {
    
    private final CashProReloaded plugin;
    private final String currency;
    
    public TransactionsCommand(CashProReloaded plugin, String currency) {
        this.plugin = plugin;
        this.currency = currency;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        String targetPlayer = null;
        int page = 1;
        
        // Определение целевого игрока и страницы
        if (args.length > 1) {
            // Если первый аргумент - это число (страница), то смотрим свои транзакции
            if (ValidationUtils.isValidPage(args[1])) {
                if (sender instanceof Player) {
                    targetPlayer = sender.getName();
                    page = ValidationUtils.parsePage(args[1]);
                } else {
                    sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("errors.player_only_command")));
                    return;
                }
            } else {
                // Если первый аргумент - это имя игрока
                targetPlayer = args[1];
                
                // Проверка прав администратора
                if (!sender.hasPermission("cashpro." + currency + ".admin")) {
                    sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("no_permission")));
                    return;
                }
                
                // Проверка существования игрока
                if (!ValidationUtils.playerExists(targetPlayer)) {
                    sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("player_not_found", "player", targetPlayer)));
                    return;
                }
                
                // Определение страницы (если указана)
                if (args.length > 2) {
                    if (!ValidationUtils.isValidPage(args[2])) {
                        sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("transactions.invalid_page")));
                        return;
                    }
                    page = ValidationUtils.parsePage(args[2]);
                }
            }
        } else {
            // Если аргументов нет, смотрим свои транзакции
            if (sender instanceof Player) {
                targetPlayer = sender.getName();
            } else {
                sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("errors.player_only_command")));
                return;
            }
        }
        
        // Получение общего количества транзакций и вычисление количества страниц
        int totalTransactions = plugin.getCurrencyManager().getPlayerTransactionsCount(targetPlayer, currency);
        int pageSize = 10; // Размер страницы
        int totalPages = (int) Math.ceil((double) totalTransactions / pageSize);
        
        // Получение транзакций
        List<Transaction> transactions = plugin.getCurrencyManager().getPlayerTransactions(targetPlayer, currency, page);
        
        if (transactions.isEmpty()) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("transactions.no_transactions")));
            return;
        }
        
        // Отображение заголовка
        sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("transactions.title",
            "page", String.valueOf(page),
            "total_pages", String.valueOf(totalPages)
        )));
        
        // Отображение транзакций
        for (Transaction transaction : transactions) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("transactions.transaction_line",
                "transaction", transaction.getFormattedString()
            )));
        }
        
        // Отображение подвала
        sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("transactions.footer")));
    }
    
    @Override
    public String getDescription() {
        return "Показать транзакции игрока";
    }
    
    @Override
    public String getUsage() {
        return "/" + currency + " trans [игрок] [страница]";
    }
} 