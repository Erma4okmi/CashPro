package com.mishkaworld.cashpro.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Утилитарный класс для валидации данных
 * 
 * @author Misha Ermakov
 */
public class ValidationUtils {
    
    // Константы для ограничений
    public static final long MIN_AMOUNT = 1L;
    public static final long MAX_AMOUNT = 10_000_000L;
    
    /**
     * Проверить, является ли строка валидным числом
     * 
     * @param input строка для проверки
     * @return true если строка является валидным числом
     */
    public static boolean isValidNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = input.trim();
        
        // Проверка на отрицательные числа и числа с ведущими нулями
        if (trimmed.startsWith("-") || trimmed.startsWith("-0") || 
            (trimmed.startsWith("0") && trimmed.length() > 1)) {
            return false;
        }
        
        try {
            long amount = Long.parseLong(trimmed);
            return amount >= MIN_AMOUNT && amount <= MAX_AMOUNT;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Получить число из строки с валидацией
     * 
     * @param input строка для парсинга
     * @return число или null если невалидно
     */
    public static Long parseAmount(String input) {
        if (!isValidNumber(input)) {
            return null;
        }
        
        try {
            return Long.parseLong(input.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Проверить, существует ли игрок
     * 
     * @param playerName имя игрока
     * @return true если игрок существует
     */
    public static boolean playerExists(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return false;
        }
        
        return Bukkit.getPlayer(playerName) != null || 
               Bukkit.getOfflinePlayer(playerName).hasPlayedBefore();
    }
    
    /**
     * Получить игрока по имени
     * 
     * @param playerName имя игрока
     * @return Player объект или null если игрок не найден
     */
    public static Player getPlayer(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return null;
        }
        
        return Bukkit.getPlayer(playerName);
    }
    
    /**
     * Проверить, является ли игрок онлайн
     * 
     * @param playerName имя игрока
     * @return true если игрок онлайн
     */
    public static boolean isPlayerOnline(String playerName) {
        Player player = getPlayer(playerName);
        return player != null && player.isOnline();
    }
    
    /**
     * Проверить, является ли строка валидным именем игрока
     * 
     * @param playerName имя игрока
     * @return true если имя валидно
     */
    public static boolean isValidPlayerName(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = playerName.trim();
        
        // Проверка длины имени (Minecraft ограничения)
        if (trimmed.length() < 3 || trimmed.length() > 16) {
            return false;
        }
        
        // Проверка на допустимые символы
        return trimmed.matches("^[a-zA-Z0-9_]+$");
    }
    
    /**
     * Проверить, является ли страница валидной
     * 
     * @param page строка с номером страницы
     * @return true если страница валидна
     */
    public static boolean isValidPage(String page) {
        if (page == null || page.trim().isEmpty()) {
            return false;
        }
        
        try {
            int pageNum = Integer.parseInt(page.trim());
            return pageNum > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Получить номер страницы из строки
     * 
     * @param page строка с номером страницы
     * @return номер страницы или 1 по умолчанию
     */
    public static int parsePage(String page) {
        if (!isValidPage(page)) {
            return 1;
        }
        
        try {
            return Integer.parseInt(page.trim());
        } catch (NumberFormatException e) {
            return 1;
        }
    }
} 