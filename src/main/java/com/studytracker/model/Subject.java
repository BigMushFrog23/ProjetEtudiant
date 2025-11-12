package com.studytracker.model;

/**
 * Represents a subject/course that the user is studying.
 */
public class Subject {
    private int id;
    private int userId;
    private String name;
    private String description;
    private String color; // For UI color coding

    public Subject() {
    }

    public Subject(int id, int userId, String name, String description, String color) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.color = color;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return name;
    }
}
