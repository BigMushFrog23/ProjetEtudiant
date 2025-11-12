package com.studytracker.dao;

import com.studytracker.database.DatabaseManager;
import com.studytracker.model.Chapter;
import com.studytracker.model.Chapter.ChapterStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Chapter entity.
 */
public class ChapterDAO {
    private final Connection connection;

    public ChapterDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Create a new chapter
     */
    public Chapter create(Chapter chapter) throws SQLException {
        String sql = "INSERT INTO chapters (subject_id, name, description, status, estimated_hours) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, chapter.getSubjectId());
            stmt.setString(2, chapter.getName());
            stmt.setString(3, chapter.getDescription());
            stmt.setString(4, chapter.getStatus().name());
            stmt.setInt(5, chapter.getEstimatedHours());

            stmt.executeUpdate();

            // Get the last inserted ID using SQLite's last_insert_rowid()
            try (Statement idStmt = connection.createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    chapter.setId(rs.getInt(1));
                    return chapter;
                }
            }
        }
        return null;
    }

    /**
     * Find chapter by ID
     */
    public Chapter findById(int id) throws SQLException {
        String sql = "SELECT * FROM chapters WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToChapter(rs);
            }
        }
        return null;
    }

    /**
     * Find all chapters for a subject
     */
    public List<Chapter> findBySubjectId(int subjectId) throws SQLException {
        List<Chapter> chapters = new ArrayList<>();
        String sql = "SELECT * FROM chapters WHERE subject_id = ? ORDER BY name";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                chapters.add(mapResultSetToChapter(rs));
            }
        }
        return chapters;
    }

    /**
     * Get all chapters
     */
    public List<Chapter> findAll() throws SQLException {
        List<Chapter> chapters = new ArrayList<>();
        String sql = "SELECT * FROM chapters ORDER BY name";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                chapters.add(mapResultSetToChapter(rs));
            }
        }
        return chapters;
    }

    /**
     * Update chapter
     */
    public void update(Chapter chapter) throws SQLException {
        String sql = "UPDATE chapters SET name = ?, description = ?, status = ?, estimated_hours = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, chapter.getName());
            stmt.setString(2, chapter.getDescription());
            stmt.setString(3, chapter.getStatus().name());
            stmt.setInt(4, chapter.getEstimatedHours());
            stmt.setInt(5, chapter.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Delete chapter
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM chapters WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Get completion statistics for a subject
     */
    public ChapterStats getStatsBySubjectId(int subjectId) throws SQLException {
        String sql = """
            SELECT
                COUNT(*) as total,
                SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed,
                SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress,
                SUM(CASE WHEN status = 'NOT_STARTED' THEN 1 ELSE 0 END) as not_started
            FROM chapters WHERE subject_id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new ChapterStats(
                    rs.getInt("total"),
                    rs.getInt("completed"),
                    rs.getInt("in_progress"),
                    rs.getInt("not_started")
                );
            }
        }
        return new ChapterStats(0, 0, 0, 0);
    }

    /**
     * Get total completed chapters for a user (across all subjects)
     */
    public int countCompletedByUserId(int userId) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM chapters c
            JOIN subjects s ON c.subject_id = s.id
            WHERE s.user_id = ? AND c.status = 'COMPLETED'
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Map ResultSet to Chapter object
     */
    private Chapter mapResultSetToChapter(ResultSet rs) throws SQLException {
        Chapter chapter = new Chapter();
        chapter.setId(rs.getInt("id"));
        chapter.setSubjectId(rs.getInt("subject_id"));
        chapter.setName(rs.getString("name"));
        chapter.setDescription(rs.getString("description"));
        chapter.setStatus(ChapterStatus.valueOf(rs.getString("status")));
        chapter.setEstimatedHours(rs.getInt("estimated_hours"));
        return chapter;
    }

    /**
     * Inner class for chapter statistics
     */
    public static class ChapterStats {
        private final int total;
        private final int completed;
        private final int inProgress;
        private final int notStarted;

        public ChapterStats(int total, int completed, int inProgress, int notStarted) {
            this.total = total;
            this.completed = completed;
            this.inProgress = inProgress;
            this.notStarted = notStarted;
        }

        public int getTotal() {
            return total;
        }

        public int getCompleted() {
            return completed;
        }

        public int getInProgress() {
            return inProgress;
        }

        public int getNotStarted() {
            return notStarted;
        }

        public double getCompletionPercentage() {
            return total > 0 ? (completed / (double) total) * 100 : 0;
        }
    }
}
