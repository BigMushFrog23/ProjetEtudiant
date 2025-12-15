package com.studytracker.dao;

import com.studytracker.database.DatabaseManager;
import com.studytracker.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entity.
 * Handles all database operations related to users.
 */
public class UserDAO {
    private final Connection connection;

    public UserDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Create a new user with hashed password
     */
    public User create(String username, String password) throws SQLException {
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO users (username, password_hash, created_at) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            stmt.setString(3, LocalDateTime.now().toString());

            stmt.executeUpdate();

            // Get the last inserted ID using SQLite's last_insert_rowid()
            try (Statement idStmt = connection.createStatement();
                ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    return findById(rs.getInt(1));
                }
            }
        }
        return null;
    }

    /**
     * Authenticate user with username and password
     */
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (BCrypt.checkpw(password, storedHash)) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Find user by ID
     */
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    /**
     * Find user by username
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    /**
     * Get all users
     */
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    /**
     * Update user information
     */
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET xp = ?, level = ?, study_streak = ?, last_study_date = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.getXp());
            stmt.setInt(2, user.getLevel());
            stmt.setInt(3, user.getStudyStreak());
            stmt.setString(4, user.getLastStudyDate() != null ? user.getLastStudyDate().toString() : null);
            stmt.setInt(5, user.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Delete user
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Add XP to user and update level
     */
    public void addXp(int userId, int xp) throws SQLException {
        User user = findById(userId);
        if (user != null) {
            user.setXp(user.getXp() + xp);
            update(user);
        }
    }

    /**
     * Update study streak
     */
    public void updateStreak(int userId) throws SQLException {
        User user = findById(userId);
        if (user != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastStudy = user.getLastStudyDate();

            if (lastStudy == null) {
                // First study session
                user.setStudyStreak(1);
            } else {
                long daysBetween = java.time.Duration.between(lastStudy, now).toDays();
                if (daysBetween == 1) {
                    // Consecutive day
                    user.setStudyStreak(user.getStudyStreak() + 1);
                } else if (daysBetween > 1) {
                    // Streak broken
                    user.setStudyStreak(1);
                }
                // If same day, don't change streak
            }

            user.setLastStudyDate(now);
            update(user);
        }
    }

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setXp(rs.getInt("xp"));
        user.setLevel(rs.getInt("level"));
        user.setStudyStreak(rs.getInt("study_streak"));

        String lastStudyDateStr = rs.getString("last_study_date");
        if (lastStudyDateStr != null) {
            user.setLastStudyDate(LocalDateTime.parse(lastStudyDateStr));
        }

        String createdAtStr = rs.getString("created_at");
        if (createdAtStr != null) {
            user.setCreatedAt(LocalDateTime.parse(createdAtStr));
        }

        return user;
    }
}
