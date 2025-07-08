package com.mishkaworld.cashpro.economy;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.database.DatabaseManager;
import java.util.List;
import java.util.UUID;

/**
 * Координатор экономических операций
 * Делегирует выполнение специализированным компонентам
 * 
 * @author Misha Ermakov
 */
public class CurrencyManager {
    
    private final CashProReloaded plugin;
    
    public CurrencyManager(CashProReloaded plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Инициализировать менеджер валют
     */
    public void initialize() {
        plugin.getLogger().info("Менеджер валют инициализирован");
    }
    
    /**
     * Получить баланс игрока
     */
    public long getBalance(UUID playerUuid, String currency) {
        return plugin.getCurrencyOperations().getBalance(playerUuid, currency);
    }
    
    /**
     * Получить баланс игрока по имени
     */
    public long getBalance(String playerName, String currency) {
        return plugin.getCurrencyOperations().getBalance(playerName, currency);
    }
    
    /**
     * Установить баланс игрока
     */
    public boolean setBalance(UUID playerUuid, String playerName, String currency, long amount) {
        boolean success = plugin.getCurrencyOperations().setBalance(playerUuid, playerName, currency, amount);
        if (success) {
            plugin.getTransactionLogger().logSetBalance(playerName, currency, amount);
        }
        return success;
    }
    
    /**
     * Добавить к балансу игрока
     */
    public boolean addBalance(UUID playerUuid, String playerName, String currency, long amount) {
        boolean success = plugin.getCurrencyOperations().addBalance(playerUuid, playerName, currency, amount);
        if (success) {
            plugin.getTransactionLogger().logAddBalance(playerName, currency, amount);
        }
        return success;
    }
    
    /**
     * Вычесть из баланса игрока
     */
    public boolean subtractBalance(UUID playerUuid, String playerName, String currency, long amount) {
        boolean success = plugin.getCurrencyOperations().subtractBalance(playerUuid, playerName, currency, amount);
        if (success) {
            plugin.getTransactionLogger().logSubtractBalance(playerName, currency, amount);
        }
        return success;
    }
    
    /**
     * Перевести деньги между игроками
     */
    public boolean transferMoney(UUID fromUuid, String fromName, UUID toUuid, String toName, String currency, long amount) {
        boolean success = plugin.getCurrencyOperations().transferMoney(fromUuid, fromName, toUuid, toName, currency, amount);
        if (success) {
            plugin.getTransactionLogger().logTransfer(fromName, toName, currency, amount);
        }
        return success;
    }
    
    /**
     * Получить топ игроков по валюте
     */
    public List<DatabaseManager.PlayerBalance> getTopPlayers(String currency, int limit) {
        return plugin.getCurrencyOperations().getTopPlayers(currency, limit);
    }
    
    /**
     * Получить транзакции игрока
     */
    public List<Transaction> getPlayerTransactions(String playerName, String currency, int page) {
        return plugin.getCurrencyOperations().getPlayerTransactions(playerName, currency, page);
    }
    
    /**
     * Получить общее количество транзакций игрока
     */
    public int getPlayerTransactionsCount(String playerName, String currency) {
        return plugin.getCurrencyOperations().getPlayerTransactionsCount(playerName, currency);
    }
    
    /**
     * Проверить, достаточно ли средств у игрока
     */
    public boolean hasEnoughFunds(UUID playerUuid, String currency, long amount) {
        return plugin.getCurrencyOperations().hasEnoughFunds(playerUuid, currency, amount);
    }
    
    /**
     * Создать начальный баланс для нового игрока
     */
    public void createInitialBalance(UUID playerUuid, String playerName) {
        plugin.getCurrencyOperations().createInitialBalance(playerUuid, playerName);
    }
    
    /**
     * Получить отформатированный баланс игрока
     */
    public String getFormattedBalance(UUID playerUuid, String currency) {
        return plugin.getCurrencyFormatter().getFormattedBalance(playerUuid, currency);
    }
    
    /**
     * Получить отформатированный баланс игрока по имени
     */
    public String getFormattedBalance(String playerName, String currency) {
        return plugin.getCurrencyFormatter().getFormattedBalance(playerName, currency);
    }
    
    /**
     * Проверить, существует ли валюта
     */
    public boolean currencyExists(String currency) {
        return plugin.getCurrencyFormatter().currencyExists(currency);
    }
    
    /**
     * Получить название валюты
     */
    public String getCurrencyName(String currency) {
        return plugin.getCurrencyFormatter().getCurrencyName(currency);
    }
    
    /**
     * Получить символ валюты
     */
    public String getCurrencySymbol(String currency) {
        return plugin.getCurrencyFormatter().getCurrencySymbol(currency);
    }
} 