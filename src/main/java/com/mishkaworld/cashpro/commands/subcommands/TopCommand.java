package com.mishkaworld.cashpro.commands.subcommands;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.commands.SubCommand;
import com.mishkaworld.cashpro.database.DatabaseManager;
import com.mishkaworld.cashpro.utils.MessageUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Подкоманда для отображения топ игроков
 * 
 * @author Misha Ermakov
 */
public class TopCommand implements SubCommand {
    
    private final CashProReloaded plugin;
    private final String currency;
    
    public TopCommand(CashProReloaded plugin, String currency) {
        this.plugin = plugin;
        this.currency = currency;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Получение топ игроков
        List<DatabaseManager.PlayerBalance> topPlayers = plugin.getCurrencyManager().getTopPlayers(currency, 10);
        
        if (topPlayers.isEmpty()) {
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("top.no_data")));
            return;
        }
        
        String currencyName = plugin.getCurrencyManager().getCurrencyName(currency);
        String symbol = plugin.getCurrencyManager().getCurrencySymbol(currency);
        
        // Отображение заголовка
        sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("top.title",
            "currency", currencyName
        )));
        
        // Отображение игроков
        for (int i = 0; i < topPlayers.size(); i++) {
            DatabaseManager.PlayerBalance playerBalance = topPlayers.get(i);
            int position = i + 1;
            
            sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("top.player_line",
                "position", String.valueOf(position),
                "player", playerBalance.getPlayerName(),
                "amount", MessageUtils.formatNumber(playerBalance.getBalance()),
                "symbol", symbol
            )));
        }
        
        // Отображение подвала
        sender.sendMessage(MessageUtils.format(plugin.getConfigManager().getMessage("top.footer")));
    }
    
    @Override
    public String getDescription() {
        return "Показать топ игроков по валюте";
    }
    
    @Override
    public String getUsage() {
        return "/" + currency + " top";
    }
} 