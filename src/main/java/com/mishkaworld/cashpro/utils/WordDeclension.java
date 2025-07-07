package com.mishkaworld.cashpro.utils;

/**
 * Утилитарный класс для работы со склонениями слов
 * в зависимости от количества
 * 
 * @author Misha Ermakov
 */
public class WordDeclension {
    
    /**
     * Получить правильное склонение слова "рубль" в зависимости от числа
     * 
     * @param amount количество
     * @return склонение слова
     */
    public static String getRubDeclension(long amount) {
        long lastDigit = Math.abs(amount) % 10;
        long lastTwoDigits = Math.abs(amount) % 100;
        
        if (lastTwoDigits >= 11 && lastTwoDigits <= 19) {
            return "рублей";
        }
        
        switch ((int) lastDigit) {
            case 1:
                return "рубль";
            case 2:
            case 3:
            case 4:
                return "рубля";
            default:
                return "рублей";
        }
    }
    
    /**
     * Получить правильное склонение слова "мишка" в зависимости от числа
     * 
     * @param amount количество
     * @return склонение слова
     */
    public static String getMishkaDeclension(long amount) {
        long lastDigit = Math.abs(amount) % 10;
        long lastTwoDigits = Math.abs(amount) % 100;
        
        if (lastTwoDigits >= 11 && lastTwoDigits <= 19) {
            return "мишек";
        }
        
        switch ((int) lastDigit) {
            case 1:
                return "мишка";
            case 2:
            case 3:
            case 4:
                return "мишки";
            default:
                return "мишек";
        }
    }
    
    /**
     * Получить склонение для любой валюты
     * 
     * @param currency код валюты
     * @param amount количество
     * @return склонение слова
     */
    public static String getDeclension(String currency, long amount) {
        switch (currency.toLowerCase()) {
            case "rub":
            case "рубль":
            case "рубли":
                return getRubDeclension(amount);
            case "mishka":
            case "мишка":
            case "мишки":
                return getMishkaDeclension(amount);
            default:
                return ""; // Для неизвестных валют возвращаем пустую строку
        }
    }
} 