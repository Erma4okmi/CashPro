package com.mishkaworld.cashpro.commands;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.commands.subcommands.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Реестр подкоманд для валют
 * 
 * @author Misha Ermakov
 */
public class SubCommandRegistry {
    
    private final Map<String, SubCommand> subCommands;
    private final CashProReloaded plugin;
    private final String currency;
    
    public SubCommandRegistry(CashProReloaded plugin, String currency) {
        this.plugin = plugin;
        this.currency = currency;
        this.subCommands = new HashMap<>();
        
        registerSubCommands();
    }
    
    /**
     * Регистрация всех подкоманд
     */
    private void registerSubCommands() {
        subCommands.put("balance", new BalanceCommand(plugin, currency));
        subCommands.put("pay", new PayCommand(plugin, currency));
        subCommands.put("top", new TopCommand(plugin, currency));
        subCommands.put("trans", new TransactionsCommand(plugin, currency));
        subCommands.put("set", new SetCommand(plugin, currency));
        subCommands.put("give", new GiveCommand(plugin, currency));
        subCommands.put("take", new TakeCommand(plugin, currency));
    }
    
    /**
     * Получить подкоманду по имени
     */
    public SubCommand getSubCommand(String name) {
        return subCommands.get(name.toLowerCase());
    }
    
    /**
     * Проверить, существует ли подкоманда
     */
    public boolean hasSubCommand(String name) {
        return subCommands.containsKey(name.toLowerCase());
    }
    
    /**
     * Получить все доступные подкоманды
     */
    public Map<String, SubCommand> getAllSubCommands() {
        return new HashMap<>(subCommands);
    }
    
    /**
     * Получить список имен подкоманд
     */
    public java.util.Set<String> getSubCommandNames() {
        return subCommands.keySet();
    }
} 