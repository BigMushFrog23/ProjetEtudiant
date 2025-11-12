package com.studytracker.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents an exam or deadline for a subject.
 */
public class Exam {
    private int id;
    private int subjectId;
    private String name;
    private String description;
    private LocalDate examDate;
    private boolean isCompleted;

    public Exam() {
        this.isCompleted = false;
    }

    public Exam(int id, int subjectId, String name, String description, LocalDate examDate, boolean isCompleted) {
        this.id = id;
        this.subjectId = subjectId;
        this.name = name;
        this.description = description;
        this.examDate = examDate;
        this.isCompleted = isCompleted;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    /**
     * Get days until exam
     */
    public long getDaysUntil() {
        return ChronoUnit.DAYS.between(LocalDate.now(), examDate);
    }

    /**
     * Check if exam is upcoming (within 7 days)
     */
    public boolean isUpcoming() {
        long days = getDaysUntil();
        return days >= 0 && days <= 7;
    }

    /**
     * Check if exam is overdue
     */
    public boolean isOverdue() {
        return getDaysUntil() < 0 && !isCompleted;
    }

    @Override
    public String toString() {
        return name + " - " + examDate;
    }
}
