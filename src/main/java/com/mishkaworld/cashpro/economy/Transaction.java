package com.mishkaworld.cashpro.economy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс для представления транзакции в системе
 * 
 * @author Misha Ermakov
 */
public class Transaction {
    
    private final String id;
    private final String fromPlayer;
    private final String toPlayer;
    private final String currency;
    private final long amount;
    private final TransactionType type;
    private final LocalDateTime timestamp;
    
    public Transaction(String fromPlayer, String toPlayer, String currency, long amount, TransactionType type) {
        this.id = generateId();
        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
        this.currency = currency;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
    
    public Transaction(String id, String fromPlayer, String toPlayer, String currency, long amount, TransactionType type, LocalDateTime timestamp) {
        this.id = id;
        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
        this.currency = currency;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
    }
    
    /**
     * Генерировать уникальный ID транзакции
     */
    private String generateId() {
        return System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    /**
     * Получить отформатированную строку транзакции
     */
    public String getFormattedString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        String time = timestamp.format(formatter);
        
        switch (type) {
            case PAY:
                return String.format("[%s] %s -> %s PAY %d", time, fromPlayer, toPlayer, amount);
            case SET:
                return String.format("[%s] ADMIN SET %s %d", time, toPlayer, amount);
            case GIVE:
                return String.format("[%s] ADMIN GIVE %s %d", time, toPlayer, amount);
            case TAKE:
                return String.format("[%s] ADMIN TAKE %s %d", time, toPlayer, amount);
            default:
                return String.format("[%s] %s %s %s %d", time, fromPlayer, toPlayer, type.name(), amount);
        }
    }
    
    // Геттеры
    public String getId() {
        return id;
    }
    
    public String getFromPlayer() {
        return fromPlayer;
    }
    
    public String getToPlayer() {
        return toPlayer;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public long getAmount() {
        return amount;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Перечисление типов транзакций
     */
    public enum TransactionType {
        PAY,    // Перевод между игроками
        SET,    // Установка баланса администратором
        GIVE,   // Выдача валюты администратором
        TAKE    // Списание валюты администратором
    }
} 