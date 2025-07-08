package com.mishkaworld.cashpro.commands;

import org.bukkit.command.CommandSender;

/**
 * Менеджер прав доступа для команд
 * 
 * @author Misha Ermakov
 */
public class PermissionManager {
    
    /**
     * Проверить права для основной команды валюты
     */
    public static boolean hasCurrencyPermission(CommandSender sender, String currency) {
        return sender.hasPermission("cashpro." + currency);
    }
    
    /**
     * Проверить права для подкоманды
     */
    public static boolean hasSubCommandPermission(CommandSender sender, String currency, String subCommand) {
        switch (subCommand) {
            case "balance":
            case "pay":
            case "top":
            case "trans":
                return sender.hasPermission("cashpro." + currency);
            case "set":
            case "give":
            case "take":
                return sender.hasPermission("cashpro." + currency + ".admin") || 
                       sender.hasPermission("cashpro.admin");
            default:
                return false;
        }
    }
    
    /**
     * Проверить права администратора
     */
    public static boolean hasAdminPermission(CommandSender sender, String currency) {
        return sender.hasPermission("cashpro." + currency + ".admin") || 
               sender.hasPermission("cashpro.admin");
    }
    
    /**
     * Проверить права для просмотра транзакций других игроков
     */
    public static boolean canViewOtherTransactions(CommandSender sender, String currency) {
        return hasAdminPermission(sender, currency);
    }
    
    /**
     * Проверить права для административных операций
     */
    public static boolean canPerformAdminOperations(CommandSender sender, String currency) {
        return hasAdminPermission(sender, currency);
    }
} 