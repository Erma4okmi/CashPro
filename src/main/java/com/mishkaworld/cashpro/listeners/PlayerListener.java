package com.mishkaworld.cashpro.listeners;

import com.mishkaworld.cashpro.CashProReloaded;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Слушатель событий игроков
 * 
 * @author Misha Ermakov
 */
public class PlayerListener implements Listener {
    
    private final CashProReloaded plugin;
    
    public PlayerListener(CashProReloaded plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Обработка события входа игрока на сервер
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Создание начального баланса для нового игрока
        plugin.getCurrencyManager().createInitialBalance(
            event.getPlayer().getUniqueId(),
            event.getPlayer().getName()
        );
    }
} 