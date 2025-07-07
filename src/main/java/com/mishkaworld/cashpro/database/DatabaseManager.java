package com.mishkaworld.cashpro.database;

import com.mishkaworld.cashpro.CashProReloaded;
import com.mishkaworld.cashpro.economy.Transaction;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Менеджер базы данных
 * 
 * @author Misha Ermakov
 */
public class DatabaseManager {
    
    private final CashProReloaded plugin;
    private Connection connection;
    private final String databaseFile;
    
    public DatabaseManager(CashProReloaded plugin) {
        this.plugin = plugin;
        this.databaseFile = plugin.getConfigManager().getDatabaseFile();
    }
    
    /**
     * Инициализировать базу данных
     */
    public void initialize() {
        try {
            createConnection();
            createTables();
            plugin.getLogger().info("База данных успешно инициализирована");
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при инициализации базы данных: " + e.getMessage());
            throw new RuntimeException("Не удалось инициализировать базу данных", e);
        }
    }
    
    /**
     * Создать соединение с базой данных
     */
    private void createConnection() throws SQLException {
        File dbFile = new File(plugin.getDataFolder(), databaseFile);
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        
        connection = DriverManager.getConnection(url);
        connection.setAutoCommit(true);
    }
    
    /**
     * Создать таблицы в базе данных
     */
    private void createTables() throws SQLException {
        // Таблица балансов
        String createBalancesTable = """
            CREATE TABLE IF NOT EXISTS balances (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_uuid TEXT NOT NULL,
                player_name TEXT NOT NULL,
                currency TEXT NOT NULL,
                balance BIGINT NOT NULL DEFAULT 0,
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(player_uuid, currency)
            )
            """;
        
        // Таблица транзакций
        String createTransactionsTable = """
            CREATE TABLE IF NOT EXISTS transactions (
                id TEXT PRIMARY KEY,
                from_player TEXT,
                to_player TEXT NOT NULL,
                currency TEXT NOT NULL,
                amount BIGINT NOT NULL,
                transaction_type TEXT NOT NULL,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createBalancesTable);
            stmt.execute(createTransactionsTable);
        }
    }
    
    /**
     * Получить баланс игрока
     */
    public long getBalance(UUID playerUuid, String currency) {
        String sql = "SELECT balance FROM balances WHERE player_uuid = ? AND currency = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, currency);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("balance");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при получении баланса: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Установить баланс игрока
     */
    public boolean setBalance(UUID playerUuid, String playerName, String currency, long amount) {
        String sql = """
            INSERT OR REPLACE INTO balances (player_uuid, player_name, currency, balance, last_updated)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, playerName);
            stmt.setString(3, currency);
            stmt.setLong(4, amount);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при установке баланса: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Добавить к балансу игрока
     */
    public boolean addBalance(UUID playerUuid, String playerName, String currency, long amount) {
        long currentBalance = getBalance(playerUuid, currency);
        return setBalance(playerUuid, playerName, currency, currentBalance + amount);
    }
    
    /**
     * Вычесть из баланса игрока
     */
    public boolean subtractBalance(UUID playerUuid, String playerName, String currency, long amount) {
        long currentBalance = getBalance(playerUuid, currency);
        if (currentBalance < amount) {
            return false;
        }
        return setBalance(playerUuid, playerName, currency, currentBalance - amount);
    }
    
    /**
     * Сохранить транзакцию
     */
    public boolean saveTransaction(Transaction transaction) {
        String sql = """
            INSERT INTO transactions (id, from_player, to_player, currency, amount, transaction_type, timestamp)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, transaction.getId());
            stmt.setString(2, transaction.getFromPlayer());
            stmt.setString(3, transaction.getToPlayer());
            stmt.setString(4, transaction.getCurrency());
            stmt.setLong(5, transaction.getAmount());
            stmt.setString(6, transaction.getType().name());
            stmt.setTimestamp(7, Timestamp.valueOf(transaction.getTimestamp()));
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при сохранении транзакции: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Получить общее количество транзакций игрока
     */
    public int getPlayerTransactionsCount(String playerName, String currency) {
        String sql = """
            SELECT COUNT(*) FROM transactions 
            WHERE (from_player = ? OR to_player = ?) AND currency = ?
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.setString(2, playerName);
            stmt.setString(3, currency);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при подсчете транзакций: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Получить транзакции игрока
     */
    public List<Transaction> getPlayerTransactions(String playerName, String currency, int page, int pageSize) {
        List<Transaction> transactions = new ArrayList<>();
        
        String sql = """
            SELECT * FROM transactions 
            WHERE (from_player = ? OR to_player = ?) AND currency = ?
            ORDER BY timestamp DESC
            LIMIT ? OFFSET ?
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.setString(2, playerName);
            stmt.setString(3, currency);
            stmt.setInt(4, pageSize);
            stmt.setInt(5, (page - 1) * pageSize);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction(
                        rs.getString("id"),
                        rs.getString("from_player"),
                        rs.getString("to_player"),
                        rs.getString("currency"),
                        rs.getLong("amount"),
                        Transaction.TransactionType.valueOf(rs.getString("transaction_type")),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                    );
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при получении транзакций: " + e.getMessage());
        }
        
        return transactions;
    }
    
    /**
     * Получить топ игроков по валюте
     */
    public List<PlayerBalance> getTopPlayers(String currency, int limit) {
        List<PlayerBalance> topPlayers = new ArrayList<>();
        
        String sql = """
            SELECT player_name, balance 
            FROM balances 
            WHERE currency = ? 
            ORDER BY balance DESC 
            LIMIT ?
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, currency);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PlayerBalance balance = new PlayerBalance(
                        rs.getString("player_name"),
                        rs.getLong("balance")
                    );
                    topPlayers.add(balance);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при получении топ игроков: " + e.getMessage());
        }
        
        return topPlayers;
    }
    
    /**
     * Проверить, существует ли игрок в базе
     */
    public boolean playerExists(UUID playerUuid, String currency) {
        String sql = "SELECT 1 FROM balances WHERE player_uuid = ? AND currency = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, currency);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при проверке существования игрока: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Закрыть соединение с базой данных
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().severe("Ошибка при закрытии соединения с БД: " + e.getMessage());
            }
        }
    }
    
    /**
     * Класс для представления баланса игрока
     */
    public static class PlayerBalance {
        private final String playerName;
        private final long balance;
        
        public PlayerBalance(String playerName, long balance) {
            this.playerName = playerName;
            this.balance = balance;
        }
        
        public String getPlayerName() {
            return playerName;
        }
        
        public long getBalance() {
            return balance;
        }
    }
} 