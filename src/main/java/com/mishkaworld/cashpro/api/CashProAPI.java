package com.mishkaworld.cashpro.api;

import com.mishkaworld.cashpro.CashProReloaded;
import java.util.UUID;

/**
 * API для интеграции с другими плагинами
 * 
 * @author Misha Ermakov
 */
public class CashProAPI {
    
    private static CashProReloaded plugin;
    
    /**
     * Инициализация API
     */
    public static void initialize(CashProReloaded pluginInstance) {
        plugin = pluginInstance;
    }
    
    /**
     * Получить баланс игрока
     * 
     * @param playerUuid UUID игрока
     * @param currency валюта
     * @return баланс игрока
     */
    public static long getBalance(UUID playerUuid, String currency) {
        if (plugin == null) {
            throw new IllegalStateException("CashPro API не инициализирован");
        }
        return plugin.getCurrencyManager().getBalance(playerUuid, currency);
    }
    
    /**
     * Получить баланс игрока по имени
     * 
     * @param playerName имя игрока
     * @param currency валюта
     * @return баланс игрока
     */
    public static long getBalance(String playerName, String currency) {
        if (plugin == null) {
            throw new IllegalStateException("CashPro API не инициализирован");
        }
        return plugin.getCurrencyManager().getBalance(playerName, currency);
    }
    
    /**
     * Добавить к балансу игрока
     * 
     * @param playerUuid UUID игрока
     * @param playerName имя игрока
     * @param currency валюта
     * @param amount сумма
     * @return true если операция успешна
     */
    public static boolean addBalance(UUID playerUuid, String playerName, String currency, long amount) {
        if (plugin == null) {
            throw new IllegalStateException("CashPro API не инициализирован");
        }
        return plugin.getCurrencyManager().addBalance(playerUuid, playerName, currency, amount);
    }
    
    /**
     * Вычесть из баланса игрока
     * 
     * @param playerUuid UUID игрока
     * @param playerName имя игрока
     * @param currency валюта
     * @param amount сумма
     * @return true если операция успешна
     */
    public static boolean subtractBalance(UUID playerUuid, String playerName, String currency, long amount) {
        if (plugin == null) {
            throw new IllegalStateException("CashPro API не инициализирован");
        }
        return plugin.getCurrencyManager().subtractBalance(playerUuid, playerName, currency, amount);
    }
    
    /**
     * Установить баланс игрока
     * 
     * @param playerUuid UUID игрока
     * @param playerName имя игрока
     * @param currency валюта
     * @param amount сумма
     * @return true если операция успешна
     */
    public static boolean setBalance(UUID playerUuid, String playerName, String currency, long amount) {
        if (plugin == null) {
            throw new IllegalStateException("CashPro API не инициализирован");
        }
        return plugin.getCurrencyManager().setBalance(playerUuid, playerName, currency, amount);
    }
    
    /**
     * Проверить, достаточно ли средств у игрока
     * 
     * @param playerUuid UUID игрока
     * @param currency валюта
     * @param amount сумма
     * @return true если у игрока достаточно средств
     */
    public static boolean hasEnoughFunds(UUID playerUuid, String currency, long amount) {
        if (plugin == null) {
            throw new IllegalStateException("CashPro API не инициализирован");
        }
        return plugin.getCurrencyManager().hasEnoughFunds(playerUuid, currency, amount);
    }
    
    /**
     * Проверить, существует ли валюта
     * 
     * @param currency валюта
     * @return true если валюта существует
     */
    public static boolean currencyExists(String currency) {
        if (plugin == null) {
            throw new IllegalStateException("CashPro API не инициализирован");
        }
        return plugin.getCurrencyManager().currencyExists(currency);
    }
    
    /**
     * Получить название валюты
     * 
     * @param currency валюта
     * @return название валюты
     */
    public static String getCurrencyName(String currency) {
        if (plugin == null) {
            throw new IllegalStateException("CashPro API не инициализирован");
        }
        return plugin.getCurrencyManager().getCurrencyName(currency);
    }
    
    /**
     * Получить символ валюты
     * 
     * @param currency валюта
     * @return символ валюты
     */
    public static String getCurrencySymbol(String currency) {
        if (plugin == null) {
            throw new IllegalStateException("CashPro API не инициализирован");
        }
        return plugin.getCurrencyManager().getCurrencySymbol(currency);
    }
    
    /**
     * Получить отформатированный баланс игрока
     * 
     * @param playerUuid UUID игрока
     * @param currency валюта
     * @return отформатированный баланс
     */
    public static String getFormattedBalance(UUID playerUuid, String currency) {
        if (plugin == null) {
            throw new IllegalStateException("CashPro API не инициализирован");
        }
        return plugin.getCurrencyFormatter().getFormattedBalance(playerUuid, currency);
    }
    
    /**
     * Получить отформатированный баланс игрока по имени
     * 
     * @param playerName имя игрока
     * @param currency валюта
     * @return отформатированный баланс
     */
    public static String getFormattedBalance(String playerName, String currency) {
        if (plugin == null) {
            throw new IllegalStateException("CashPro API не инициализирован");
        }
        return plugin.getCurrencyFormatter().getFormattedBalance(playerName, currency);
    }
} 