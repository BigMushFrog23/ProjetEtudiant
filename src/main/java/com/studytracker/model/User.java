package com.studytracker.model;

import java.time.LocalDateTime;

/**
 * Represents a user in the study tracker system.
 * Each user has their own study progress, gamification stats, and data.
 */
public class User {
    private int id;
    private String username;
    private String passwordHash;
    private int xp;
    private int level;
    private int studyStreak;
    private LocalDateTime lastStudyDate;
    private LocalDateTime createdAt;

    public User() {
        this.xp = 0;
        this.level = 1;
        this.studyStreak = 0;
        this.createdAt = LocalDateTime.now();
    }

    public User(int id, String username, String passwordHash) {
        this();
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
        // Auto-level up based on XP (100 XP per level)
        this.level = (xp / 100) + 1;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStudyStreak() {
        return studyStreak;
    }

    public void setStudyStreak(int studyStreak) {
        this.studyStreak = studyStreak;
    }

    public LocalDateTime getLastStudyDate() {
        return lastStudyDate;
    }

    public void setLastStudyDate(LocalDateTime lastStudyDate) {
        this.lastStudyDate = lastStudyDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Calculate XP needed for next level
     */
    public int getXpForNextLevel() {
        return level * 100;
    }

    /**
     * Get current progress towards next level (0-100%)
     */
    public double getLevelProgress() {
        int currentLevelXp = (level - 1) * 100;
        int xpInCurrentLevel = xp - currentLevelXp;
        return (xpInCurrentLevel / 100.0) * 100;
    }
}
