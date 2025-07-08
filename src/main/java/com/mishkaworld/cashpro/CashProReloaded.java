package com.mishkaworld.cashpro;

import com.mishkaworld.cashpro.api.CashProAPI;
import com.mishkaworld.cashpro.commands.CurrencyCommand;
import com.mishkaworld.cashpro.commands.ReloadCommand;
import com.mishkaworld.cashpro.config.ConfigManager;
import com.mishkaworld.cashpro.database.DatabaseManager;
import com.mishkaworld.cashpro.economy.CurrencyManager;
import com.mishkaworld.cashpro.economy.CurrencyOperations;
import com.mishkaworld.cashpro.economy.TransactionLogger;
import com.mishkaworld.cashpro.listeners.PlayerListener;
import com.mishkaworld.cashpro.placeholders.CashProPlaceholderExpansion;
import com.mishkaworld.cashpro.utils.CurrencyFormatter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Основной класс плагина CashPro Reloaded
 * Валютная экономика для сервера Minecraft
 * 
 * @author Misha Ermakov
 * @version 1.13
 */
public class CashProReloaded extends JavaPlugin {
    
    private static CashProReloaded instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private CurrencyManager currencyManager;
    private CurrencyOperations currencyOperations;
    private TransactionLogger transactionLogger;
    private CurrencyFormatter currencyFormatter;
    private Logger logger;
    
    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        
        logger.info("=== CashPro Reloaded v1.13 ===");
        logger.info("Автор: Misha Ermakov");
        logger.info("Компания: MishkaWorld");
        logger.info("Загрузка плагина...");
        
        try {
            // Инициализация конфигурации
            configManager = new ConfigManager(this);
            configManager.loadConfigs();
            
            // Инициализация базы данных
            databaseManager = new DatabaseManager(this);
            databaseManager.initialize();
            
            // Инициализация менеджера валют
            currencyManager = new CurrencyManager(this);
            currencyManager.initialize();
            
            // Инициализация компонентов экономики
            currencyOperations = new CurrencyOperations(this);
            transactionLogger = new TransactionLogger(this);
            currencyFormatter = new CurrencyFormatter(this);
            
            // Регистрация команд
            registerCommands();
            
            // Регистрация слушателей
            registerListeners();
            
            // Регистрация PlaceholderAPI
            registerPlaceholders();
            
            // Инициализация API
            CashProAPI.initialize(this);
            
            logger.info("Плагин успешно загружен!");
            
        } catch (Exception e) {
            logger.severe("Ошибка при загрузке плагина: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        logger.info("Выгрузка плагина CashPro Reloaded...");
        
        try {
            if (databaseManager != null) {
                databaseManager.close();
            }
            
            logger.info("Плагин успешно выгружен!");
            
        } catch (Exception e) {
            logger.severe("Ошибка при выгрузке плагина: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Регистрация команд плагина
     */
    private void registerCommands() {
        // Команды для валют
        for (String currency : configManager.getCurrencies()) {
            CurrencyCommand currencyCommand = new CurrencyCommand(this, currency);
            getCommand(currency).setExecutor(currencyCommand);
            getCommand(currency).setTabCompleter(currencyCommand);
        }
        
        // Команда перезагрузки
        getCommand("cashpro").setExecutor(new ReloadCommand(this));
    }
    
    /**
     * Регистрация слушателей событий
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }
    
    /**
     * Регистрация PlaceholderAPI
     */
    private void registerPlaceholders() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CashProPlaceholderExpansion(this).register();
            logger.info("PlaceholderAPI интеграция активирована");
        } else {
            logger.warning("PlaceholderAPI не найден, интеграция отключена");
        }
    }
    
    /**
     * Получить экземпляр плагина
     */
    public static CashProReloaded getInstance() {
        return instance;
    }
    
    /**
     * Получить менеджер конфигурации
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * Получить менеджер базы данных
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    /**
     * Получить менеджер валют
     */
    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }
    
    /**
     * Получить операции с валютой
     */
    public CurrencyOperations getCurrencyOperations() {
        return currencyOperations;
    }
    
    /**
     * Получить логгер транзакций
     */
    public TransactionLogger getTransactionLogger() {
        return transactionLogger;
    }
    
    /**
     * Получить форматтер валют
     */
    public CurrencyFormatter getCurrencyFormatter() {
        return currencyFormatter;
    }
} 