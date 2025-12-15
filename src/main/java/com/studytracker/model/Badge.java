package com.studytracker.model;

/**
 * Represents achievement badges that users can earn.
 */
public class Badge {
    private int id;
    private String name;
    private String description;
    private String icon; // Emoji or icon code
    private BadgeType type;

    public enum BadgeType {
        FIRST_STUDY("First Study", "Complete your first study session", "🎯"),
        STUDY_STREAK_3("3-Day Streak", "Study for 3 days in a row", "🔥"),
        STUDY_STREAK_7("7-Day Streak", "Study for 7 days in a row", "⚡"),
        COMPLETED_5_CHAPTERS("Chapter Master", "Complete 5 chapters", "📚"),
        COMPLETED_10_CHAPTERS("Knowledge Seeker", "Complete 10 chapters", "🏆"),
        LEVEL_5("Level 5", "Reach level 5", "⭐"),
        LEVEL_10("Level 10", "Reach level 10", "💎"),
        LEVEL_15("Level 15", "Reach level 15", "x"),
        EARLY_BIRD("Early Bird", "Complete an exam before the deadline", "🦅");

        private final String name;
        private final String description;
        private final String icon;

        BadgeType(String name, String description, String icon) {
            this.name = name;
            this.description = description;
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    public Badge() {
    }

    public Badge(int id, String name, String description, String icon, BadgeType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.type = type;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public BadgeType getType() {
        return type;
    }

    public void setType(BadgeType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return icon + " " + name;
    }
}
