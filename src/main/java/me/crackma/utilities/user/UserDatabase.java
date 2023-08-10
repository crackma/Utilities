package me.crackma.utilities.user;

import me.crackma.utilities.UtilitiesPlugin;
import me.crackma.utilities.rank.Rank;
import me.crackma.utilities.rank.RankManager;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserDatabase {
    private RankManager rankManager;
    private Connection connection;
    public UserDatabase(UtilitiesPlugin plugin) {
        rankManager = plugin.getRankManager();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "users.db"));
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS users(" +
                            "uuid varchar(36) NOT NULL," +
                            "rank varchar(36) NOT NULL," +
                            "punishments String NOT NULL);"
            )) {
                preparedStatement.execute();
            }
        } catch (SQLException | ClassNotFoundException exception) {
            Bukkit.getLogger().severe(exception.toString());
        }
    }
    public CompletableFuture<Void> insert(UUID uuid) {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE uuid = ?;")) {
                preparedStatement.setString(1, uuid.toString());
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.getString(1) != null) return null;
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (uuid,rank,punishments) VALUES (?,?,?);")) {
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, rankManager.getPrimaryRank().getName());
                preparedStatement.setString(3, "");
                preparedStatement.execute();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return null;
        });
        return future;
    }
    public CompletableFuture<User> get(UUID uuid) {
        CompletableFuture<User> future = CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT rank, punishments FROM users WHERE uuid = ?")) {
                preparedStatement.setString(1, uuid.toString());
                ResultSet rs = preparedStatement.executeQuery();
                User user = new User(uuid, rankManager.get(rs.getString(1)), rs.getString(2));
                return user;
            } catch (SQLException exception) {
                exception.printStackTrace();
                return null;
            }
        });
        future.exceptionally(exception -> {
            exception.printStackTrace();
            return null;
        });
        return future;
    }
    public CompletableFuture<Void> updateOne(User user) {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET rank = ?, punishments = ? WHERE uuid = ?;")) {
                if (user.getRank() == null) {
                    user.setRank(rankManager.getPrimaryRank());
                }
                preparedStatement.setString(1, user.getRank().getName());
                preparedStatement.setString(2, user.punishmentsToString());
                preparedStatement.setString(3, user.getUniqueId().toString());
                preparedStatement.execute();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return null;
        });
        future.exceptionally(exception -> {
            exception.printStackTrace();
            return null;
        });
        return future;
    }
    public CompletableFuture<Void> updateMany(Rank rank) {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET rank = ? WHERE rank = ?;")) {
                preparedStatement.setString(1, rankManager.getPrimaryRank().toString());
                preparedStatement.setString(2, rank.toString());
                preparedStatement.execute();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return null;
        });
        future.exceptionally(exception -> {
            exception.printStackTrace();
            return null;
        });
        return future;
    }
}
