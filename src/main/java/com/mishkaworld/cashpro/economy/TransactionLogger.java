package com.mishkaworld.cashpro.economy;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.database.DatabaseManager;

/**
 * Класс для логирования транзакций
 * 
 * @author Misha Ermakov
 */
public class TransactionLogger {
    
    private final CashProReloaded plugin;
    private final DatabaseManager databaseManager;
    
    public TransactionLogger(CashProReloaded plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }
    
    /**
     * Логировать операцию установки баланса
     */
    public void logSetBalance(String playerName, String currency, long amount) {
        if (plugin.getConfigManager().isTransactionLoggingEnabled()) {
            Transaction transaction = new Transaction(
                "ADMIN", playerName, currency, amount, Transaction.TransactionType.SET
            );
            databaseManager.saveTransaction(transaction);
        }
    }
    
    /**
     * Логировать операцию добавления к балансу
     */
    public void logAddBalance(String playerName, String currency, long amount) {
        if (plugin.getConfigManager().isTransactionLoggingEnabled()) {
            Transaction transaction = new Transaction(
                "ADMIN", playerName, currency, amount, Transaction.TransactionType.GIVE
            );
            databaseManager.saveTransaction(transaction);
        }
    }
    
    /**
     * Логировать операцию вычитания из баланса
     */
    public void logSubtractBalance(String playerName, String currency, long amount) {
        if (plugin.getConfigManager().isTransactionLoggingEnabled()) {
            Transaction transaction = new Transaction(
                "ADMIN", playerName, currency, amount, Transaction.TransactionType.TAKE
            );
            databaseManager.saveTransaction(transaction);
        }
    }
    
    /**
     * Логировать перевод между игроками
     */
    public void logTransfer(String fromPlayer, String toPlayer, String currency, long amount) {
        if (plugin.getConfigManager().isTransactionLoggingEnabled()) {
            Transaction transaction = new Transaction(fromPlayer, toPlayer, currency, amount, Transaction.TransactionType.PAY);
            databaseManager.saveTransaction(transaction);
        }
    }
    
    /**
     * Проверить, включено ли логирование транзакций
     */
    public boolean isLoggingEnabled() {
        return plugin.getConfigManager().isTransactionLoggingEnabled();
    }
} 