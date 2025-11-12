package com.studytracker.ui;

import com.studytracker.model.User;
import com.studytracker.ui.tabs.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Main application window with tabbed interface.
 */
public class MainWindow {
    private final User currentUser;
    private DashboardTab dashboardTab;
    private SubjectsTab subjectsTab;
    private ChaptersTab chaptersTab;
    private ExamsTab examsTab;
    private StudySessionsTab studySessionsTab;
    private Label levelLabel; // Reference to update dynamically

    public MainWindow(User currentUser) {
        this.currentUser = currentUser;
    }

    public void show(Stage stage) {
        BorderPane root = new BorderPane();

        // Header
        HBox header = createHeader();
        root.setTop(header);

        // Tab pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Create tabs
        dashboardTab = new DashboardTab(currentUser);
        subjectsTab = new SubjectsTab(currentUser, this);
        chaptersTab = new ChaptersTab(currentUser, this);
        examsTab = new ExamsTab(currentUser, this);
        studySessionsTab = new StudySessionsTab(currentUser, this);

        Tab dashTab = new Tab("📊 Dashboard", dashboardTab.getContent());
        Tab subjTab = new Tab("📚 Subjects", subjectsTab.getContent());
        Tab chapTab = new Tab("📖 Chapters", chaptersTab.getContent());
        Tab examTab = new Tab("📝 Exams", examsTab.getContent());
        Tab sessTab = new Tab("⏱️ Study Sessions", studySessionsTab.getContent());

        tabPane.getTabs().addAll(dashTab, subjTab, chapTab, examTab, sessTab);

        // Refresh dashboard when it's selected
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == dashTab) {
                dashboardTab.refresh();
            }
        });

        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Study Tracker - " + currentUser.getUsername());
        stage.show();

        // Initial load
        dashboardTab.refresh();
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%);");
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label titleLabel = new Label("🎓 Study Tracker");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // User info
        VBox userInfo = new VBox(2);
        userInfo.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Label usernameLabel = new Label("👤 " + currentUser.getUsername());
        usernameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        usernameLabel.setTextFill(Color.WHITE);

        // Store reference to update later
        levelLabel = new Label("⭐ Level " + currentUser.getLevel() + " | XP: " + currentUser.getXp());
        levelLabel.setFont(Font.font("System", 12));
        levelLabel.setTextFill(Color.rgb(255, 255, 255, 0.9));

        userInfo.getChildren().addAll(usernameLabel, levelLabel);

        header.getChildren().addAll(titleLabel, spacer, userInfo);

        return header;
    }

    /**
     * Refresh all tabs (called when data changes)
     */
    public void refreshAll() {
        // Update header with latest XP/Level
        updateHeader();

        subjectsTab.refresh();
        chaptersTab.refresh();
        examsTab.refresh();
        studySessionsTab.refresh();
        dashboardTab.refresh();
    }

    /**
     * Update the header display with current user stats
     */
    private void updateHeader() {
        if (levelLabel != null) {
            levelLabel.setText("⭐ Level " + currentUser.getLevel() + " | XP: " + currentUser.getXp());
        }
    }

    /**
     * Get current user
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
