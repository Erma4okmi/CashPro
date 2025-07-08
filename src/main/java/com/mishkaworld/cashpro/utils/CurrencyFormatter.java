package com.mishkaworld.cashpro.utils;

import com.mishkaworld.cashpro.CashProReloaded;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Класс для форматирования данных валют
 * 
 * @author Misha Ermakov
 */
public class CurrencyFormatter {
    
    private final CashProReloaded plugin;
    
    public CurrencyFormatter(CashProReloaded plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Получить отформатированный баланс игрока
     */
    public String getFormattedBalance(UUID playerUuid, String currency) {
        long balance = plugin.getCurrencyOperations().getBalance(playerUuid, currency);
        String symbol = getCurrencySymbol(currency);
        return MessageUtils.formatNumber(balance) + " " + symbol;
    }
    
    /**
     * Получить отформатированный баланс игрока по имени
     */
    public String getFormattedBalance(String playerName, String currency) {
        Player player = plugin.getServer().getPlayer(playerName);
        if (player != null) {
            return getFormattedBalance(player.getUniqueId(), currency);
        }
        return "0 " + getCurrencySymbol(currency);
    }
    
    /**
     * Получить символ валюты
     */
    public String getCurrencySymbol(String currency) {
        if (currency == null) {
            return "";
        }
        
        switch (currency.toLowerCase()) {
            case "rub":
            case "рубль":
            case "рубли":
                return "₽";
            case "mishka":
            case "мишка":
            case "мишки":
                return "🐻";
            default:
                return "";
        }
    }
    
    /**
     * Получить название валюты
     */
    public String getCurrencyName(String currency) {
        if (currency == null) {
            return "";
        }
        
        switch (currency.toLowerCase()) {
            case "rub":
            case "рубль":
            case "рубли":
                return "Рубль";
            case "mishka":
            case "мишка":
            case "мишки":
                return "Мишка";
            default:
                return currency;
        }
    }
    
    /**
     * Проверить, существует ли валюта
     */
    public boolean currencyExists(String currency) {
        return plugin.getConfigManager().getCurrencyConfig(currency) != null;
    }
    
    /**
     * Получить отформатированную сумму с символом валюты
     */
    public String formatAmount(long amount, String currency) {
        String symbol = getCurrencySymbol(currency);
        return MessageUtils.formatNumber(amount) + " " + symbol;
    }
    
    /**
     * Получить отформатированную сумму с названием валюты
     */
    public String formatAmountWithName(long amount, String currency) {
        String name = getCurrencyName(currency);
        return MessageUtils.formatNumber(amount) + " " + name;
    }
} 