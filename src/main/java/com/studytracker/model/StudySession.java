package com.studytracker.model;

import java.time.LocalDateTime;

/**
 * Represents a study session for a specific chapter.
 * Used to track time spent studying and award XP.
 */
public class StudySession {
    private int id;
    private int chapterId;
    private int userId;
    private double hoursStudied;
    private LocalDateTime sessionDate;
    private String notes;
    private int xpEarned;

    public StudySession() {
        this.sessionDate = LocalDateTime.now();
    }

    public StudySession(int id, int chapterId, int userId, double hoursStudied, LocalDateTime sessionDate, String notes, int xpEarned) {
        this.id = id;
        this.chapterId = chapterId;
        this.userId = userId;
        this.hoursStudied = hoursStudied;
        this.sessionDate = sessionDate;
        this.notes = notes;
        this.xpEarned = xpEarned;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getHoursStudied() {
        return hoursStudied;
    }

    public void setHoursStudied(double hoursStudied) {
        this.hoursStudied = hoursStudied;
    }

    public LocalDateTime getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(int xpEarned) {
        this.xpEarned = xpEarned;
    }

    /**
     * Calculate XP based on hours studied (10 XP per hour)
     */
    public void calculateXp() {
        this.xpEarned = (int) (hoursStudied * 10);
    }
}
