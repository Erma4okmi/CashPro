package com.mishkaworld.cashpro.utils;

import org.bukkit.ChatColor;

/**
 * Утилитарный класс для форматирования сообщений
 * 
 * @author Misha Ermakov
 */
public class MessageUtils {
    
    /**
     * Форматировать сообщение с цветовыми кодами
     * 
     * @param message исходное сообщение
     * @return отформатированное сообщение
     */
    public static String format(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * Форматировать сообщение с заменой плейсхолдеров
     * 
     * @param message исходное сообщение
     * @param replacements массив замен в формате [ключ, значение, ключ, значение...]
     * @return отформатированное сообщение
     */
    public static String format(String message, String... replacements) {
        if (message == null) {
            return "";
        }
        
        String formatted = format(message);
        
        if (replacements != null && replacements.length >= 2) {
            for (int i = 0; i < replacements.length; i += 2) {
                if (i + 1 < replacements.length) {
                    String key = "{" + replacements[i] + "}";
                    String value = replacements[i + 1];
                    formatted = formatted.replace(key, value);
                }
            }
        }
        
        return formatted;
    }
    
    /**
     * Форматировать сообщение с заменой плейсхолдеров для валюты
     * 
     * @param message исходное сообщение
     * @param amount сумма
     * @param currency валюта
     * @param playerName имя игрока
     * @return отформатированное сообщение
     */
    public static String formatCurrency(String message, long amount, String currency, String playerName) {
        String declension = WordDeclension.getDeclension(currency, amount);
        
        return format(message,
            "amount", String.valueOf(amount),
            "currency", currency,
            "declension", declension,
            "player", playerName != null ? playerName : "",
            "symbol", getCurrencySymbol(currency)
        );
    }
    
    /**
     * Получить символ валюты
     * 
     * @param currency код валюты
     * @return символ валюты
     */
    public static String getCurrencySymbol(String currency) {
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
     * 
     * @param currency код валюты
     * @return название валюты
     */
    public static String getCurrencyName(String currency) {
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
     * Форматировать число с разделителями
     * 
     * @param number число для форматирования
     * @return отформатированное число
     */
    public static String formatNumber(long number) {
        return String.format("%,d", number);
    }
    
    /**
     * Создать разделительную линию
     * 
     * @param length длина линии
     * @param symbol символ для линии
     * @return разделительная линия
     */
    public static String createLine(int length, String symbol) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < length; i++) {
            line.append(symbol);
        }
        return line.toString();
    }
    
    /**
     * Создать стандартную разделительную линию
     * 
     * @return разделительная линия
     */
    public static String createLine() {
        return createLine(50, "=");
    }
    
    /**
     * Проверить, является ли строка пустой или null
     * 
     * @param str строка для проверки
     * @return true если строка пустая или null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Получить безопасную строку (заменяет null на пустую строку)
     * 
     * @param str исходная строка
     * @return безопасная строка
     */
    public static String safeString(String str) {
        return str != null ? str : "";
    }
} 