package com.studytracker.ui.tabs;

import com.studytracker.dao.*;
import com.studytracker.model.*;
import com.studytracker.service.GamificationService;
import com.studytracker.ui.MainWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Tab for tracking study sessions and awarding XP.
 */
public class StudySessionsTab {
    private final User currentUser;
    private final MainWindow mainWindow;
    private final StudySessionDAO sessionDAO;
    private final ChapterDAO chapterDAO;
    private final SubjectDAO subjectDAO;
    private final UserDAO userDAO;
    private final GamificationService gamificationService;
    private final TableView<StudySession> table;
    private final ObservableList<StudySession> sessions;

    public StudySessionsTab(User currentUser, MainWindow mainWindow) {
        this.currentUser = currentUser;
        this.mainWindow = mainWindow;
        this.sessionDAO = new StudySessionDAO();
        this.chapterDAO = new ChapterDAO();
        this.subjectDAO = new SubjectDAO();
        this.userDAO = new UserDAO();
        this.gamificationService = new GamificationService();
        this.sessions = FXCollections.observableArrayList();
        this.table = createTable();
    }

    public VBox getContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("⏱️ Study Sessions");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label infoLabel = new Label("💡 Log your study sessions to earn XP! (10 XP per hour studied)");
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        // Buttons
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("➕ Log Study Session");
        Button deleteButton = new Button("🗑️ Delete");

        addButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        buttonBox.getChildren().addAll(addButton, deleteButton);

        addButton.setOnAction(e -> showAddDialog());
        deleteButton.setOnAction(e -> deleteSelected());

        content.getChildren().addAll(titleLabel, infoLabel, buttonBox, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        return content;
    }

    private TableView<StudySession> createTable() {
        TableView<StudySession> table = new TableView<>();
        table.setItems(sessions);

        TableColumn<StudySession, String> chapterCol = new TableColumn<>("Chapter");
        chapterCol.setCellValueFactory(cellData -> {
            try {
                Chapter chapter = chapterDAO.findById(cellData.getValue().getChapterId());
                return new javafx.beans.property.SimpleStringProperty(chapter != null ? chapter.getName() : "Unknown");
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });
        chapterCol.setPrefWidth(200);

        TableColumn<StudySession, Double> hoursCol = new TableColumn<>("Hours");
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("hoursStudied"));
        hoursCol.setPrefWidth(80);

        TableColumn<StudySession, LocalDateTime> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        dateCol.setPrefWidth(150);
        dateCol.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : formatter.format(date));
            }
        });

        TableColumn<StudySession, Integer> xpCol = new TableColumn<>("XP Earned");
        xpCol.setCellValueFactory(new PropertyValueFactory<>("xpEarned"));
        xpCol.setPrefWidth(100);
        xpCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer xp, boolean empty) {
                super.updateItem(xp, empty);
                if (empty || xp == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("+" + xp + " XP");
                    setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<StudySession, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));
        notesCol.setPrefWidth(300);

        table.getColumns().addAll(chapterCol, hoursCol, dateCol, xpCol, notesCol);

        return table;
    }

    private void showAddDialog() {
        try {
            // Get all chapters for user's subjects
            List<Subject> userSubjects = subjectDAO.findByUserId(currentUser.getId());
            List<Chapter> allChapters = new java.util.ArrayList<>();
            for (Subject subject : userSubjects) {
                allChapters.addAll(chapterDAO.findBySubjectId(subject.getId()));
            }

            if (allChapters.isEmpty()) {
                showAlert("Please create a chapter first!", Alert.AlertType.WARNING);
                return;
            }

            Dialog<StudySession> dialog = new Dialog<>();
            dialog.setTitle("Log Study Session");
            dialog.setHeaderText("Record your study progress");

            ButtonType saveButtonType = new ButtonType("Save & Earn XP", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            ComboBox<Chapter> chapterCombo = new ComboBox<>(FXCollections.observableArrayList(allChapters));
            chapterCombo.setPromptText("Select chapter");

            Spinner<Double> hoursSpinner = new Spinner<>(0.5, 24.0, 1.0, 0.5);
            hoursSpinner.setEditable(true);

            TextArea notesField = new TextArea();
            notesField.setPromptText("Notes (optional)");
            notesField.setPrefRowCount(4);

            Label xpPreviewLabel = new Label();
            xpPreviewLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

            // Update XP preview when hours change
            hoursSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                int xp = (int) (newVal * 10);
                xpPreviewLabel.setText("You will earn: +" + xp + " XP");
            });
            xpPreviewLabel.setText("You will earn: +10 XP");

            grid.add(new Label("Chapter:"), 0, 0);
            grid.add(chapterCombo, 1, 0);
            grid.add(new Label("Hours Studied:"), 0, 1);
            grid.add(hoursSpinner, 1, 1);
            grid.add(xpPreviewLabel, 1, 2);
            grid.add(new Label("Notes:"), 0, 3);
            grid.add(notesField, 1, 3);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    if (chapterCombo.getValue() == null) return null;
                    StudySession session = new StudySession();
                    session.setChapterId(chapterCombo.getValue().getId());
                    session.setUserId(currentUser.getId());
                    session.setHoursStudied(hoursSpinner.getValue());
                    session.setNotes(notesField.getText());
                    session.calculateXp();
                    return session;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(session -> {
                try {
                    // Save session
                    sessionDAO.create(session);

                    // Auto-update chapter status to "In Progress" if it's "Not Started"
                    Chapter chapter = chapterDAO.findById(session.getChapterId());
                    if (chapter != null && chapter.getStatus() == Chapter.ChapterStatus.NOT_STARTED) {
                        chapter.setStatus(Chapter.ChapterStatus.IN_PROGRESS);
                        chapterDAO.update(chapter);
                    }

                    // Award XP to user
                    userDAO.addXp(currentUser.getId(), session.getXpEarned());

                    // Update streak
                    userDAO.updateStreak(currentUser.getId());

                    // Check for new badges
                    gamificationService.checkAndAwardBadges(currentUser.getId());

                    // Update current user object
                    User updatedUser = userDAO.findById(currentUser.getId());
                    currentUser.setXp(updatedUser.getXp());
                    currentUser.setLevel(updatedUser.getLevel());
                    currentUser.setStudyStreak(updatedUser.getStudyStreak());

                    refresh();
                    mainWindow.refreshAll();

                    showAlert("Study session logged! You earned +" + session.getXpEarned() + " XP!\nLevel: " +
                            currentUser.getLevel() + " | Total XP: " + currentUser.getXp(), Alert.AlertType.INFORMATION);
                } catch (SQLException ex) {
                    showAlert("Error logging session: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        } catch (SQLException ex) {
            showAlert("Error loading data: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void deleteSelected() {
        StudySession selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a session to delete", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete study session");
        confirm.setContentText("Note: This will NOT remove the XP already earned. Are you sure?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    sessionDAO.delete(selected.getId());
                    refresh();
                    mainWindow.refreshAll();
                    showAlert("Session deleted!", Alert.AlertType.INFORMATION);
                } catch (SQLException ex) {
                    showAlert("Error deleting session: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    public void refresh() {
        try {
            sessions.clear();
            sessions.addAll(sessionDAO.findByUserId(currentUser.getId()));
        } catch (SQLException ex) {
            showAlert("Error loading sessions: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
