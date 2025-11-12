package com.studytracker.model;

/**
 * Represents a chapter or topic within a subject.
 * Status indicates the progress state.
 */
public class Chapter {
    private int id;
    private int subjectId;
    private String name;
    private String description;
    private ChapterStatus status;
    private int estimatedHours;

    public enum ChapterStatus {
        NOT_STARTED("Not Started", "#E74C3C"), // Red
        IN_PROGRESS("In Progress", "#F39C12"), // Orange
        COMPLETED("Completed", "#27AE60");     // Green

        private final String displayName;
        private final String color;

        ChapterStatus(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getColor() {
            return color;
        }
    }

    public Chapter() {
        this.status = ChapterStatus.NOT_STARTED;
    }

    public Chapter(int id, int subjectId, String name, String description, ChapterStatus status, int estimatedHours) {
        this.id = id;
        this.subjectId = subjectId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.estimatedHours = estimatedHours;
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

    public ChapterStatus getStatus() {
        return status;
    }

    public void setStatus(ChapterStatus status) {
        this.status = status;
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    @Override
    public String toString() {
        return name + " (" + status.getDisplayName() + ")";
    }
}
