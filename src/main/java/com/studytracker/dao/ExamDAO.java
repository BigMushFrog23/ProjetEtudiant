package com.studytracker.dao;

import com.studytracker.database.DatabaseManager;
import com.studytracker.model.Exam;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Exam entity.
 */
public class ExamDAO {
    private final Connection connection;

    public ExamDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Create a new exam
     */
    public Exam create(Exam exam) throws SQLException {
        String sql = "INSERT INTO exams (subject_id, name, description, exam_date, is_completed) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exam.getSubjectId());
            stmt.setString(2, exam.getName());
            stmt.setString(3, exam.getDescription());
            stmt.setString(4, exam.getExamDate().toString());
            stmt.setInt(5, exam.isCompleted() ? 1 : 0);

            stmt.executeUpdate();

            // Get the last inserted ID using SQLite's last_insert_rowid()
            try (Statement idStmt = connection.createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    exam.setId(rs.getInt(1));
                    return exam;
                }
            }
        }
        return null;
    }

    /**
     * Find exam by ID
     */
    public Exam findById(int id) throws SQLException {
        String sql = "SELECT * FROM exams WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToExam(rs);
            }
        }
        return null;
    }

    /**
     * Find all exams for a subject
     */
    public List<Exam> findBySubjectId(int subjectId) throws SQLException {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT * FROM exams WHERE subject_id = ? ORDER BY exam_date";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                exams.add(mapResultSetToExam(rs));
            }
        }
        return exams;
    }

    /**
     * Find all exams for a user
     */
    public List<Exam> findByUserId(int userId) throws SQLException {
        List<Exam> exams = new ArrayList<>();
        String sql = """
            SELECT e.* FROM exams e
            JOIN subjects s ON e.subject_id = s.id
            WHERE s.user_id = ?
            ORDER BY e.exam_date
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                exams.add(mapResultSetToExam(rs));
            }
        }
        return exams;
    }

    /**
     * Find upcoming exams (within next 7 days)
     */
    public List<Exam> findUpcomingByUserId(int userId) throws SQLException {
        List<Exam> exams = new ArrayList<>();
        String sql = """
            SELECT e.* FROM exams e
            JOIN subjects s ON e.subject_id = s.id
            WHERE s.user_id = ? AND e.is_completed = 0
            AND e.exam_date BETWEEN date('now') AND date('now', '+7 days')
            ORDER BY e.exam_date
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                exams.add(mapResultSetToExam(rs));
            }
        }
        return exams;
    }

    /**
     * Get all exams
     */
    public List<Exam> findAll() throws SQLException {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT * FROM exams ORDER BY exam_date";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                exams.add(mapResultSetToExam(rs));
            }
        }
        return exams;
    }

    /**
     * Update exam
     */
    public void update(Exam exam) throws SQLException {
        String sql = "UPDATE exams SET name = ?, description = ?, exam_date = ?, is_completed = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, exam.getName());
            stmt.setString(2, exam.getDescription());
            stmt.setString(3, exam.getExamDate().toString());
            stmt.setInt(4, exam.isCompleted() ? 1 : 0);
            stmt.setInt(5, exam.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Delete exam
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM exams WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Map ResultSet to Exam object
     */
    private Exam mapResultSetToExam(ResultSet rs) throws SQLException {
        Exam exam = new Exam();
        exam.setId(rs.getInt("id"));
        exam.setSubjectId(rs.getInt("subject_id"));
        exam.setName(rs.getString("name"));
        exam.setDescription(rs.getString("description"));
        exam.setExamDate(LocalDate.parse(rs.getString("exam_date")));
        exam.setCompleted(rs.getInt("is_completed") == 1);
        return exam;
    }
}
