package com.mishkaworld.cashpro.config;

import com.mishkaworld.cashpro.CashProReloaded;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Менеджер конфигурации плагина
 * 
 * @author Misha Ermakov
 */
public class ConfigManager {
    
    private final CashProReloaded plugin;
    private FileConfiguration config;
    private FileConfiguration currenciesConfig;
    private FileConfiguration messagesConfig;
    
    private final Map<String, CurrencyConfig> currencies = new HashMap<>();
    
    public ConfigManager(CashProReloaded plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Загрузить все конфигурации
     */
    public void loadConfigs() {
        loadMainConfig();
        loadCurrenciesConfig();
        loadMessagesConfig();
    }
    
    /**
     * Загрузить основной конфиг
     */
    private void loadMainConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    /**
     * Загрузить конфиг валют
     */
    private void loadCurrenciesConfig() {
        File currenciesFile = new File(plugin.getDataFolder(), "currencies.yml");
        if (!currenciesFile.exists()) {
            plugin.saveResource("currencies.yml", false);
        }
        
        currenciesConfig = YamlConfiguration.loadConfiguration(currenciesFile);
        loadCurrencies();
    }
    
    /**
     * Загрузить конфиг сообщений
     */
    private void loadMessagesConfig() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    /**
     * Загрузить валюты из конфига
     */
    private void loadCurrencies() {
        currencies.clear();
        
        ConfigurationSection currenciesSection = currenciesConfig.getConfigurationSection("currencies");
        if (currenciesSection == null) {
            plugin.getLogger().warning("Секция currencies не найдена в currencies.yml");
            return;
        }
        
        for (String currencyKey : currenciesSection.getKeys(false)) {
            ConfigurationSection currencySection = currenciesSection.getConfigurationSection(currencyKey);
            if (currencySection != null) {
                CurrencyConfig currencyConfig = new CurrencyConfig(
                    currencyKey,
                    currencySection.getString("name", currencyKey),
                    currencySection.getString("symbol", ""),
                    currencySection.getString("command", currencyKey),
                    currencySection.getLong("start_value", 1000)
                );
                currencies.put(currencyKey, currencyConfig);
            }
        }
        
        plugin.getLogger().info("Загружено валют: " + currencies.size());
    }
    
    /**
     * Получить список валют
     */
    public Set<String> getCurrencies() {
        return currencies.keySet();
    }
    
    /**
     * Получить конфиг валюты
     */
    public CurrencyConfig getCurrencyConfig(String currency) {
        return currencies.get(currency);
    }
    
    /**
     * Получить основную валюту
     */
    public String getMainCurrency() {
        return config.getString("main_coins", "rub");
    }
    
    /**
     * Проверить, включено ли логирование транзакций
     */
    public boolean isTransactionLoggingEnabled() {
        return config.getBoolean("transactions-log", true);
    }
    
    /**
     * Получить тип базы данных
     */
    public String getDatabaseType() {
        return config.getString("database.type", "sqlite");
    }
    
    /**
     * Получить файл базы данных
     */
    public String getDatabaseFile() {
        return config.getString("database.file", "cashpro.db");
    }
    
    /**
     * Получить сообщение из конфига
     */
    public String getMessage(String path) {
        return messagesConfig.getString("messages." + path, "&cСообщение не найдено: " + path);
    }
    
    /**
     * Получить сообщение с заменой плейсхолдеров
     */
    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);
        if (replacements != null && replacements.length >= 2) {
            for (int i = 0; i < replacements.length; i += 2) {
                if (i + 1 < replacements.length) {
                    message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
                }
            }
        }
        return message;
    }
    
    /**
     * Перезагрузить конфигурации
     */
    public void reloadConfigs() {
        loadConfigs();
    }
    
    /**
     * Сохранить конфигурацию
     */
    public void saveConfig() {
        try {
            plugin.saveConfig();
        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка при сохранении конфигурации: " + e.getMessage());
        }
    }
    
    /**
     * Класс для хранения конфигурации валюты
     */
    public static class CurrencyConfig {
        private final String key;
        private final String name;
        private final String symbol;
        private final String command;
        private final long startValue;
        
        public CurrencyConfig(String key, String name, String symbol, String command, long startValue) {
            this.key = key;
            this.name = name;
            this.symbol = symbol;
            this.command = command;
            this.startValue = startValue;
        }
        
        public String getKey() {
            return key;
        }
        
        public String getName() {
            return name;
        }
        
        public String getSymbol() {
            return symbol;
        }
        
        public String getCommand() {
            return command;
        }
        
        public long getStartValue() {
            return startValue;
        }
    }
} 