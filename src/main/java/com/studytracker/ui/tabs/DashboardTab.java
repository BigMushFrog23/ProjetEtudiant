package com.studytracker.ui.tabs;

import com.studytracker.dao.*;
import com.studytracker.model.*;
import com.studytracker.service.GamificationService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.List;

/**
 * Dashboard tab with charts, statistics, and gamification display.
 */
public class DashboardTab {
    private final User currentUser;
    private final ChapterDAO chapterDAO;
    private final SubjectDAO subjectDAO;
    private final ExamDAO examDAO;
    private final StudySessionDAO sessionDAO;
    private final GamificationService gamificationService;
    private final VBox content;

    public DashboardTab(User currentUser) {
        this.currentUser = currentUser;
        this.chapterDAO = new ChapterDAO();
        this.subjectDAO = new SubjectDAO();
        this.examDAO = new ExamDAO();
        this.sessionDAO = new StudySessionDAO();
        this.gamificationService = new GamificationService();
        this.content = new VBox(20);
        initializeContent();
    }

    private void initializeContent() {
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("📊 Dashboard");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox scrollContent = new VBox(20);
        scrollContent.setPadding(new Insets(10));

        content.getChildren().addAll(titleLabel, scrollPane);

        // Will be populated in refresh()
    }

    public VBox getContent() {
        return content;
    }

    public void refresh() {
        try {
            // Rebuild entire dashboard with fresh data
            content.getChildren().clear();

            Label titleLabel = new Label("📊 Dashboard");
            titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent;");

            VBox scrollContent = new VBox(20);
            scrollContent.setPadding(new Insets(10));

            // Row 1: Stats cards
            HBox statsRow = createStatsCards();

            // Row 2: Progress chart and XP/Level display
            HBox chartsRow = new HBox(20);
            VBox progressBox = createProgressChart();
            VBox gamificationBox = createGamificationDisplay();

            HBox.setHgrow(progressBox, Priority.ALWAYS);
            chartsRow.getChildren().addAll(progressBox, gamificationBox);

            // Row 3: Upcoming exams
            VBox examsBox = createUpcomingExams();

            // Row 4: Badges
            VBox badgesBox = createBadgesDisplay();

            scrollContent.getChildren().addAll(statsRow, chartsRow, examsBox, badgesBox);
            scrollPane.setContent(scrollContent);

            content.getChildren().addAll(titleLabel, scrollPane);
        } catch (SQLException ex) {
            content.getChildren().add(new Label("Error loading dashboard: " + ex.getMessage()));
        }
    }

    private HBox createStatsCards() throws SQLException {
        HBox statsRow = new HBox(15);
        statsRow.setAlignment(Pos.CENTER);

        // Total subjects
        int totalSubjects = subjectDAO.countByUserId(currentUser.getId());
        VBox subjectCard = createStatCard("📚", "Subjects", String.valueOf(totalSubjects), "#3498db");

        // Completed chapters
        int completedChapters = chapterDAO.countCompletedByUserId(currentUser.getId());
        VBox chaptersCard = createStatCard("✅", "Completed Chapters", String.valueOf(completedChapters), "#27ae60");

        // Total study hours
        double totalHours = sessionDAO.getTotalHoursByUserId(currentUser.getId());
        VBox hoursCard = createStatCard("⏱️", "Study Hours", String.format("%.1f", totalHours), "#e67e22");

        // Study streak
        VBox streakCard = createStatCard("🔥", "Day Streak", String.valueOf(currentUser.getStudyStreak()), "#e74c3c");

        statsRow.getChildren().addAll(subjectCard, chaptersCard, hoursCard, streakCard);

        return statsRow;
    }

    private VBox createStatCard(String icon, String label, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + color +
                "; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10;");
        card.setPrefWidth(200);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(36));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");

        Label textLabel = new Label(label);
        textLabel.setFont(Font.font("System", 14));
        textLabel.setStyle("-fx-text-fill: #7f8c8d;");

        card.getChildren().addAll(iconLabel, valueLabel, textLabel);

        return card;
    }

    private VBox createProgressChart() throws SQLException {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10;");

        Label titleLabel = new Label("📈 Chapter Progress by Subject");
        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));

        // Create pie chart
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Overall Progress");
        pieChart.setLegendVisible(true);

        // Get all subjects
        List<Subject> subjects = subjectDAO.findByUserId(currentUser.getId());

        if (subjects.isEmpty()) {
            Label emptyLabel = new Label("No data yet. Create subjects and chapters to see your progress!");
            emptyLabel.setStyle("-fx-text-fill: #7f8c8d;");
            box.getChildren().addAll(titleLabel, emptyLabel);
        } else {
            int totalCompleted = 0;
            int totalInProgress = 0;
            int totalNotStarted = 0;

            for (Subject subject : subjects) {
                ChapterDAO.ChapterStats stats = chapterDAO.getStatsBySubjectId(subject.getId());
                totalCompleted += stats.getCompleted();
                totalInProgress += stats.getInProgress();
                totalNotStarted += stats.getNotStarted();
            }

            if (totalCompleted + totalInProgress + totalNotStarted == 0) {
                Label emptyLabel = new Label("No chapters yet. Add chapters to track your progress!");
                emptyLabel.setStyle("-fx-text-fill: #7f8c8d;");
                box.getChildren().addAll(titleLabel, emptyLabel);
            } else {
                if (totalCompleted > 0) {
                    PieChart.Data completedData = new PieChart.Data("Completed (" + totalCompleted + ")", totalCompleted);
                    pieChart.getData().add(completedData);
                }
                if (totalInProgress > 0) {
                    PieChart.Data inProgressData = new PieChart.Data("In Progress (" + totalInProgress + ")", totalInProgress);
                    pieChart.getData().add(inProgressData);
                }
                if (totalNotStarted > 0) {
                    PieChart.Data notStartedData = new PieChart.Data("Not Started (" + totalNotStarted + ")", totalNotStarted);
                    pieChart.getData().add(notStartedData);
                }

                pieChart.setPrefHeight(300);
                box.getChildren().addAll(titleLabel, pieChart);
            }
        }

        return box;
    }

    private VBox createGamificationDisplay() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea, #764ba2); -fx-background-radius: 10;");
        box.setPrefWidth(350);
        box.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("🎮 Your Progress");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);

        // Level display
        Label levelLabel = new Label("Level " + currentUser.getLevel());
        levelLabel.setFont(Font.font("System", FontWeight.BOLD, 48));
        levelLabel.setTextFill(Color.WHITE);

        // XP display
        int currentLevelXp = (currentUser.getLevel() - 1) * 100;
        int xpInCurrentLevel = currentUser.getXp() - currentLevelXp;
        int xpNeeded = 100;

        Label xpLabel = new Label(xpInCurrentLevel + " / " + xpNeeded + " XP");
        xpLabel.setFont(Font.font("System", 16));
        xpLabel.setTextFill(Color.rgb(255, 255, 255, 0.9));

        // Progress bar
        ProgressBar xpBar = new ProgressBar(xpInCurrentLevel / 100.0);
        xpBar.setPrefWidth(280);
        xpBar.setPrefHeight(25);
        xpBar.setStyle("-fx-accent: #2ecc71;");

        Label nextLevelLabel = new Label((xpNeeded - xpInCurrentLevel) + " XP to Level " + (currentUser.getLevel() + 1));
        nextLevelLabel.setFont(Font.font("System", 12));
        nextLevelLabel.setTextFill(Color.rgb(255, 255, 255, 0.8));

        // Total XP
        Label totalXpLabel = new Label("Total XP: " + currentUser.getXp());
        totalXpLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        totalXpLabel.setTextFill(Color.WHITE);

        box.getChildren().addAll(titleLabel, levelLabel, xpLabel, xpBar, nextLevelLabel, totalXpLabel);

        return box;
    }

    private VBox createUpcomingExams() throws SQLException {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10;");

        Label titleLabel = new Label("⚠️ Upcoming Exams (Next 7 Days)");
        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));

        List<Exam> upcomingExams = examDAO.findUpcomingByUserId(currentUser.getId());

        if (upcomingExams.isEmpty()) {
            Label noExamsLabel = new Label("No upcoming exams! 🎉");
            noExamsLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px;");
            box.getChildren().addAll(titleLabel, noExamsLabel);
        } else {
            VBox examsList = new VBox(8);
            for (Exam exam : upcomingExams) {
                HBox examRow = new HBox(15);
                examRow.setPadding(new Insets(10));
                examRow.setAlignment(Pos.CENTER_LEFT);
                examRow.setStyle("-fx-background-color: #fff3cd; -fx-border-color: #f39c12; -fx-border-width: 1; -fx-background-radius: 5; -fx-border-radius: 5;");

                Label dateLabel = new Label("📅 " + exam.getExamDate().toString());
                dateLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
                dateLabel.setPrefWidth(150);

                Label nameLabel = new Label(exam.getName());
                nameLabel.setFont(Font.font("System", 14));

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                long daysUntil = exam.getDaysUntil();
                Label daysLabel = new Label(daysUntil + " day" + (daysUntil != 1 ? "s" : ""));
                daysLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
                daysLabel.setStyle("-fx-text-fill: #e67e22;");

                examRow.getChildren().addAll(dateLabel, nameLabel, spacer, daysLabel);
                examsList.getChildren().add(examRow);
            }

            box.getChildren().addAll(titleLabel, examsList);
        }

        return box;
    }

    private VBox createBadgesDisplay() throws SQLException {
        VBox box = new VBox(15);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10;");

        Label titleLabel = new Label("🏆 Achievements & Badges");
        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));

        List<GamificationService.BadgeInfo> badges = gamificationService.getAllBadgesWithStatus(currentUser.getId());

        FlowPane badgesFlow = new FlowPane(15, 15);
        badgesFlow.setPadding(new Insets(10));

        for (GamificationService.BadgeInfo badgeInfo : badges) {
            VBox badgeCard = new VBox(8);
            badgeCard.setAlignment(Pos.CENTER);
            badgeCard.setPadding(new Insets(15));
            badgeCard.setPrefWidth(140);
            badgeCard.setStyle("-fx-border-color: " + (badgeInfo.isUnlocked() ? "#2ecc71" : "#bdc3c7") +
                    "; -fx-border-width: 2; -fx-background-radius: 8; -fx-border-radius: 8;" +
                    (badgeInfo.isUnlocked() ? " -fx-background-color: #d5f4e6;" : " -fx-background-color: #ecf0f1;"));

            Label iconLabel = new Label(badgeInfo.getIcon());
            iconLabel.setFont(Font.font(48));

            Label nameLabel = new Label(badgeInfo.getName());
            nameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
            nameLabel.setWrapText(true);
            nameLabel.setAlignment(Pos.CENTER);

            Label descLabel = new Label(badgeInfo.getDescription());
            descLabel.setFont(Font.font("System", 10));
            descLabel.setStyle("-fx-text-fill: #7f8c8d;");
            descLabel.setWrapText(true);
            descLabel.setAlignment(Pos.CENTER);

            badgeCard.getChildren().addAll(iconLabel, nameLabel, descLabel);

            if (!badgeInfo.isUnlocked()) {
                badgeCard.setOpacity(0.5);
            }

            badgesFlow.getChildren().add(badgeCard);
        }

        box.getChildren().addAll(titleLabel, badgesFlow);

        return box;
    }
}
