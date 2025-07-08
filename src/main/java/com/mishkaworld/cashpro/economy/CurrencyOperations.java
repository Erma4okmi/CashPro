package com.mishkaworld.cashpro.economy;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.database.DatabaseManager;
import com.mishkaworld.cashpro.utils.ValidationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Класс для выполнения экономических операций с балансом
 * 
 * @author Misha Ermakov
 */
public class CurrencyOperations {
    
    private final CashProReloaded plugin;
    private final DatabaseManager databaseManager;
    
    public CurrencyOperations(CashProReloaded plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
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
        
        return databaseManager.setBalance(playerUuid, playerName, currency, amount);
    }
    
    /**
     * Добавить к балансу игрока
     */
    public boolean addBalance(UUID playerUuid, String playerName, String currency, long amount) {
        if (!ValidationUtils.isValidNumber(String.valueOf(amount))) {
            return false;
        }
        
        return databaseManager.addBalance(playerUuid, playerName, currency, amount);
    }
    
    /**
     * Вычесть из баланса игрока
     */
    public boolean subtractBalance(UUID playerUuid, String playerName, String currency, long amount) {
        if (!ValidationUtils.isValidNumber(String.valueOf(amount))) {
            return false;
        }
        
        return databaseManager.subtractBalance(playerUuid, playerName, currency, amount);
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
        return databaseManager.subtractBalance(fromUuid, fromName, currency, amount) &&
               databaseManager.addBalance(toUuid, toName, currency, amount);
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
} 