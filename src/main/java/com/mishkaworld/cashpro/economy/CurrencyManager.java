package com.mishkaworld.cashpro.economy;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.config.ConfigManager;
import com.mishkaworld.cashpro.database.DatabaseManager;
import com.mishkaworld.cashpro.utils.MessageUtils;
import com.mishkaworld.cashpro.utils.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Менеджер валют и экономических операций
 * 
 * @author Misha Ermakov
 */
public class CurrencyManager {
    
    private final CashProReloaded plugin;
    private final DatabaseManager databaseManager;
    
    public CurrencyManager(CashProReloaded plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
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
        return databaseManager.getBalance(playerUuid, currency);
    }
    
    /**
     * Получить баланс игрока по имени
     */
    public long getBalance(String playerName, String currency) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            return getBalance(player.getUniqueId(), currency);
        }
        return 0;
    }
    
    /**
     * Установить баланс игрока
     */
    public boolean setBalance(UUID playerUuid, String playerName, String currency, long amount) {
        if (!ValidationUtils.isValidNumber(String.valueOf(amount))) {
            return false;
        }
        
        boolean success = databaseManager.setBalance(playerUuid, playerName, currency, amount);
        
        if (success && plugin.getConfigManager().isTransactionLoggingEnabled()) {
            Transaction transaction = new Transaction(
                "ADMIN", playerName, currency, amount, Transaction.TransactionType.SET
            );
            databaseManager.saveTransaction(transaction);
        }
        
        return success;
    }
    
    /**
     * Добавить к балансу игрока
     */
    public boolean addBalance(UUID playerUuid, String playerName, String currency, long amount) {
        if (!ValidationUtils.isValidNumber(String.valueOf(amount))) {
            return false;
        }
        
        boolean success = databaseManager.addBalance(playerUuid, playerName, currency, amount);
        
        if (success && plugin.getConfigManager().isTransactionLoggingEnabled()) {
            Transaction transaction = new Transaction(
                "ADMIN", playerName, currency, amount, Transaction.TransactionType.GIVE
            );
            databaseManager.saveTransaction(transaction);
        }
        
        return success;
    }
    
    /**
     * Вычесть из баланса игрока
     */
    public boolean subtractBalance(UUID playerUuid, String playerName, String currency, long amount) {
        if (!ValidationUtils.isValidNumber(String.valueOf(amount))) {
            return false;
        }
        
        boolean success = databaseManager.subtractBalance(playerUuid, playerName, currency, amount);
        
        if (success && plugin.getConfigManager().isTransactionLoggingEnabled()) {
            Transaction transaction = new Transaction(
                "ADMIN", playerName, currency, amount, Transaction.TransactionType.TAKE
            );
            databaseManager.saveTransaction(transaction);
        }
        
        return success;
    }
    
    /**
     * Перевести деньги между игроками
     */
    public boolean transferMoney(UUID fromUuid, String fromName, UUID toUuid, String toName, String currency, long amount) {
        if (!ValidationUtils.isValidNumber(String.valueOf(amount))) {
            return false;
        }
        
        // Проверка баланса
        long fromBalance = getBalance(fromUuid, currency);
        if (fromBalance < amount) {
            return false;
        }
        
        // Проверка, что игроки не одинаковые
        if (fromUuid.equals(toUuid)) {
            return false;
        }
        
        // Выполнение перевода
        boolean success = databaseManager.subtractBalance(fromUuid, fromName, currency, amount) &&
                         databaseManager.addBalance(toUuid, toName, currency, amount);
        
        if (success && plugin.getConfigManager().isTransactionLoggingEnabled()) {
            Transaction transaction = new Transaction(fromName, toName, currency, amount, Transaction.TransactionType.PAY);
            databaseManager.saveTransaction(transaction);
        }
        
        return success;
    }
    
    /**
     * Получить топ игроков по валюте
     */
    public List<DatabaseManager.PlayerBalance> getTopPlayers(String currency, int limit) {
        return databaseManager.getTopPlayers(currency, limit);
    }
    
    /**
     * Получить транзакции игрока
     */
    public List<Transaction> getPlayerTransactions(String playerName, String currency, int page) {
        return databaseManager.getPlayerTransactions(playerName, currency, page, 10);
    }
    
    /**
     * Получить общее количество транзакций игрока
     */
    public int getPlayerTransactionsCount(String playerName, String currency) {
        return databaseManager.getPlayerTransactionsCount(playerName, currency);
    }
    
    /**
     * Проверить, достаточно ли средств у игрока
     */
    public boolean hasEnoughFunds(UUID playerUuid, String currency, long amount) {
        return getBalance(playerUuid, currency) >= amount;
    }
    
    /**
     * Создать начальный баланс для нового игрока
     */
    public void createInitialBalance(UUID playerUuid, String playerName) {
        for (String currency : plugin.getConfigManager().getCurrencies()) {
            if (!databaseManager.playerExists(playerUuid, currency)) {
                long startValue = plugin.getConfigManager().getCurrencyConfig(currency).getStartValue();
                databaseManager.setBalance(playerUuid, playerName, currency, startValue);
            }
        }
    }
    
    /**
     * Получить отформатированный баланс игрока
     */
    public String getFormattedBalance(UUID playerUuid, String currency) {
        long balance = getBalance(playerUuid, currency);
        String symbol = MessageUtils.getCurrencySymbol(currency);
        return MessageUtils.formatNumber(balance) + " " + symbol;
    }
    
    /**
     * Получить отформатированный баланс игрока по имени
     */
    public String getFormattedBalance(String playerName, String currency) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            return getFormattedBalance(player.getUniqueId(), currency);
        }
        return "0";
    }
    
    /**
     * Проверить, существует ли валюта
     */
    public boolean currencyExists(String currency) {
        return plugin.getConfigManager().getCurrencies().contains(currency);
    }
    
    /**
     * Получить название валюты
     */
    public String getCurrencyName(String currency) {
        com.mishkaworld.cashpro.config.ConfigManager.CurrencyConfig config = plugin.getConfigManager().getCurrencyConfig(currency);
        return config != null ? config.getName() : currency;
    }
    
    /**
     * Получить символ валюты
     */
    public String getCurrencySymbol(String currency) {
        com.mishkaworld.cashpro.config.ConfigManager.CurrencyConfig config = plugin.getConfigManager().getCurrencyConfig(currency);
        return config != null ? config.getSymbol() : "";
    }
} 