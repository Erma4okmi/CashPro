package com.mishkaworld.cashpro.placeholders;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.database.DatabaseManager;
import com.mishkaworld.cashpro.utils.MessageUtils;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Интеграция с PlaceholderAPI
 * 
 * @author Misha Ermakov
 */
public class CashProPlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {
    
    private final CashProReloaded plugin;
    
    public CashProPlaceholderExpansion(CashProReloaded plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getIdentifier() {
        return "cashpro";
    }
    
    @Override
    public String getAuthor() {
        return "Misha Ermakov";
    }
    
    @Override
    public String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        
        // Обработка плейсхолдеров
        if (identifier.toLowerCase().startsWith("balance_")) {
            String currency = identifier.substring(8); // Убираем "balance_"
            return getFormattedBalance(player, currency);
        } else if (identifier.toLowerCase().startsWith("top_")) {
            // Обработка топ игроков: top_currency_position
            String[] parts = identifier.toLowerCase().split("_");
            if (parts.length >= 3) {
                String currency = parts[1];
                try {
                    int position = Integer.parseInt(parts[2]);
                    if (position >= 1 && position <= 10) {
                        return getTopPlayer(currency, position);
                    }
                } catch (NumberFormatException e) {
                    // Игнорируем некорректные номера позиций
                }
            }
        } else if (identifier.toLowerCase().startsWith("toplist_")) {
            // Обработка списка топ игроков: toplist_currency
            String currency = identifier.substring(9); // Убираем "toplist_"
            return getTopList(currency);
        }
        
        return null;
    }
    
    /**
     * Получить отформатированный баланс игрока
     */
    private String getFormattedBalance(Player player, String currency) {
        long balance = plugin.getCurrencyManager().getBalance(player.getUniqueId(), currency);
        String symbol = plugin.getCurrencyManager().getCurrencySymbol(currency);
        return MessageUtils.formatNumber(balance) + " " + symbol;
    }
    
    /**
     * Получить игрока из топ по позиции
     */
    private String getTopPlayer(String currency, int position) {
        List<DatabaseManager.PlayerBalance> topPlayers = plugin.getCurrencyManager().getTopPlayers(currency, position);
        
        if (topPlayers.size() >= position) {
            DatabaseManager.PlayerBalance playerBalance = topPlayers.get(position - 1);
            String symbol = plugin.getCurrencyManager().getCurrencySymbol(currency);
            return playerBalance.getPlayerName() + ": " + MessageUtils.formatNumber(playerBalance.getBalance()) + " " + symbol;
        }
        
        return "Нет данных";
    }
    
    /**
     * Получить список топ-10 игроков
     */
    private String getTopList(String currency) {
        List<DatabaseManager.PlayerBalance> topPlayers = plugin.getCurrencyManager().getTopPlayers(currency, 10);
        String symbol = plugin.getCurrencyManager().getCurrencySymbol(currency);
        
        if (topPlayers.isEmpty()) {
            return "Нет данных";
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < topPlayers.size(); i++) {
            DatabaseManager.PlayerBalance playerBalance = topPlayers.get(i);
            result.append(playerBalance.getPlayerName())
                  .append(" - ")
                  .append(MessageUtils.formatNumber(playerBalance.getBalance()))
                  .append(" ")
                  .append(symbol);
            
            // Добавляем перенос строки, кроме последней записи
            if (i < topPlayers.size() - 1) {
                result.append("\n");
            }
        }
        
        return result.toString();
    }
} 