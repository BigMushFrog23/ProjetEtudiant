package com.studytracker.service;

import com.studytracker.dao.ChapterDAO;
import com.studytracker.dao.UserDAO;
import com.studytracker.database.DatabaseManager;
import com.studytracker.model.Badge;
import com.studytracker.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling gamification features like XP, levels, and badges.
 */
public class GamificationService {
    private final Connection connection;
    private final UserDAO userDAO;
    private final ChapterDAO chapterDAO;

    public GamificationService() {
        this.connection = DatabaseManager.getInstance().getConnection();
        this.userDAO = new UserDAO();
        this.chapterDAO = new ChapterDAO();
    }

    /**
     * Award badge to user
     */
    public void awardBadge(int userId, Badge.BadgeType badgeType) throws SQLException {
        // Check if user already has this badge
        if (hasBadge(userId, badgeType)) {
            return;
        }

        String sql = "INSERT INTO user_badges (user_id, badge_type, earned_date) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, badgeType.name());
            stmt.setString(3, LocalDateTime.now().toString());
            stmt.executeUpdate();
        }
    }

    /**
     * Check if user has a specific badge
     */
    public boolean hasBadge(int userId, Badge.BadgeType badgeType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_badges WHERE user_id = ? AND badge_type = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, badgeType.name());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Get all badges earned by user
     */
    public List<Badge.BadgeType> getUserBadges(int userId) throws SQLException {
        List<Badge.BadgeType> badges = new ArrayList<>();
        String sql = "SELECT badge_type FROM user_badges WHERE user_id = ? ORDER BY earned_date";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                badges.add(Badge.BadgeType.valueOf(rs.getString("badge_type")));
            }
        }
        return badges;
    }

    /**
     * Check and award badges based on user progress
     */
    public void checkAndAwardBadges(int userId) throws SQLException {
        User user = userDAO.findById(userId);
        if (user == null) return;

        // First study badge
        if (user.getXp() >= 10 && !hasBadge(userId, Badge.BadgeType.FIRST_STUDY)) {
            awardBadge(userId, Badge.BadgeType.FIRST_STUDY);
        }

        // Streak badges
        if (user.getStudyStreak() >= 3 && !hasBadge(userId, Badge.BadgeType.STUDY_STREAK_3)) {
            awardBadge(userId, Badge.BadgeType.STUDY_STREAK_3);
        }

        if (user.getStudyStreak() >= 7 && !hasBadge(userId, Badge.BadgeType.STUDY_STREAK_7)) {
            awardBadge(userId, Badge.BadgeType.STUDY_STREAK_7);
        }

        // Chapter completion badges
        int completedChapters = chapterDAO.countCompletedByUserId(userId);

        if (completedChapters >= 5 && !hasBadge(userId, Badge.BadgeType.COMPLETED_5_CHAPTERS)) {
            awardBadge(userId, Badge.BadgeType.COMPLETED_5_CHAPTERS);
        }

        if (completedChapters >= 10 && !hasBadge(userId, Badge.BadgeType.COMPLETED_10_CHAPTERS)) {
            awardBadge(userId, Badge.BadgeType.COMPLETED_10_CHAPTERS);
        }

        // Level badges
        if (user.getLevel() >= 5 && !hasBadge(userId, Badge.BadgeType.LEVEL_5)) {
            awardBadge(userId, Badge.BadgeType.LEVEL_5);
        }

        if (user.getLevel() >= 10 && !hasBadge(userId, Badge.BadgeType.LEVEL_10)) {
            awardBadge(userId, Badge.BadgeType.LEVEL_10);
        }
    }

    /**
     * Get all available badges with unlock status
     */
    public List<BadgeInfo> getAllBadgesWithStatus(int userId) throws SQLException {
        List<BadgeInfo> badgeInfos = new ArrayList<>();

        for (Badge.BadgeType type : Badge.BadgeType.values()) {
            boolean unlocked = hasBadge(userId, type);
            badgeInfos.add(new BadgeInfo(type, unlocked));
        }

        return badgeInfos;
    }

    /**
     * Inner class to hold badge info with unlock status
     */
    public static class BadgeInfo {
        private final Badge.BadgeType type;
        private final boolean unlocked;

        public BadgeInfo(Badge.BadgeType type, boolean unlocked) {
            this.type = type;
            this.unlocked = unlocked;
        }

        public Badge.BadgeType getType() {
            return type;
        }

        public boolean isUnlocked() {
            return unlocked;
        }

        public String getIcon() {
            return unlocked ? type.getIcon() : "🔒";
        }

        public String getName() {
            return type.getName();
        }

        public String getDescription() {
            return type.getDescription();
        }
    }
}
