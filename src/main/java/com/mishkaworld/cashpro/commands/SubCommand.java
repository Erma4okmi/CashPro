package com.mishkaworld.cashpro.commands;

import org.bukkit.command.CommandSender;

/**
 * Интерфейс для подкоманд
 * 
 * @author Misha Ermakov
 */
public interface SubCommand {
    
    /**
     * Выполнить подкоманду
     * 
     * @param sender отправитель команды
     * @param args аргументы команды
     */
    void execute(CommandSender sender, String[] args);
    
    /**
     * Получить описание подкоманды
     * 
     * @return описание
     */
    String getDescription();
    
    /**
     * Получить использование подкоманды
     * 
     * @return использование
     */
    String getUsage();
} 