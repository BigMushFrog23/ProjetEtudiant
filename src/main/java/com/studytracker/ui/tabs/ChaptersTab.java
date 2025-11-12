package com.studytracker.ui.tabs;

import com.studytracker.dao.ChapterDAO;
import com.studytracker.dao.SubjectDAO;
import com.studytracker.model.Chapter;
import com.studytracker.model.Subject;
import com.studytracker.model.User;
import com.studytracker.ui.MainWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Tab for managing chapters with color-coded status.
 */
public class ChaptersTab {
    private final User currentUser;
    private final MainWindow mainWindow;
    private final ChapterDAO chapterDAO;
    private final SubjectDAO subjectDAO;
    private final TableView<Chapter> table;
    private final ObservableList<Chapter> chapters;
    private ComboBox<Subject> subjectFilter;

    public ChaptersTab(User currentUser, MainWindow mainWindow) {
        this.currentUser = currentUser;
        this.mainWindow = mainWindow;
        this.chapterDAO = new ChapterDAO();
        this.subjectDAO = new SubjectDAO();
        this.chapters = FXCollections.observableArrayList();
        this.table = createTable();
    }

    public VBox getContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("📖 Manage Chapters");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Filter by subject
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label filterLabel = new Label("Filter by Subject:");
        subjectFilter = new ComboBox<>();
        subjectFilter.setPromptText("All Subjects");
        subjectFilter.setPrefWidth(200);
        subjectFilter.setOnAction(e -> applyFilter());
        Button clearFilterButton = new Button("Clear");
        clearFilterButton.setOnAction(e -> {
            subjectFilter.setValue(null);
            applyFilter();
        });

        filterBox.getChildren().addAll(filterLabel, subjectFilter, clearFilterButton);

        // Buttons
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("➕ Add Chapter");
        Button editButton = new Button("✏️ Edit");
        Button deleteButton = new Button("🗑️ Delete");
        Button markInProgressButton = new Button("▶️ In Progress");
        Button markCompletedButton = new Button("✅ Complete");

        addButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        markInProgressButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        markCompletedButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, markInProgressButton, markCompletedButton);

        addButton.setOnAction(e -> showAddDialog());
        editButton.setOnAction(e -> showEditDialog());
        deleteButton.setOnAction(e -> deleteSelected());
        markInProgressButton.setOnAction(e -> updateStatus(Chapter.ChapterStatus.IN_PROGRESS));
        markCompletedButton.setOnAction(e -> updateStatus(Chapter.ChapterStatus.COMPLETED));

        content.getChildren().addAll(titleLabel, filterBox, buttonBox, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        return content;
    }

    private TableView<Chapter> createTable() {
        TableView<Chapter> table = new TableView<>();
        table.setItems(chapters);

        // Custom row factory for color coding
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Chapter chapter, boolean empty) {
                super.updateItem(chapter, empty);
                if (empty || chapter == null) {
                    setStyle("");
                } else {
                    setStyle("-fx-background-color: " + chapter.getStatus().getColor() + "22;");
                }
            }
        });

        TableColumn<Chapter, String> nameCol = new TableColumn<>("Chapter Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(250);

        TableColumn<Chapter, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().getDisplayName()));
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    Chapter chapter = getTableView().getItems().get(getIndex());
                    setStyle("-fx-font-weight: bold; -fx-text-fill: " + chapter.getStatus().getColor() + ";");
                }
            }
        });

        TableColumn<Chapter, Integer> hoursCol = new TableColumn<>("Est. Hours");
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("estimatedHours"));
        hoursCol.setPrefWidth(100);

        TableColumn<Chapter, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(300);

        table.getColumns().addAll(nameCol, statusCol, hoursCol, descCol);

        return table;
    }

    private void showAddDialog() {
        try {
            List<Subject> userSubjects = subjectDAO.findByUserId(currentUser.getId());
            if (userSubjects.isEmpty()) {
                showAlert("Please create a subject first!", Alert.AlertType.WARNING);
                return;
            }

            Dialog<Chapter> dialog = new Dialog<>();
            dialog.setTitle("Add Chapter");
            dialog.setHeaderText("Enter chapter details");

            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            ComboBox<Subject> subjectCombo = new ComboBox<>(FXCollections.observableArrayList(userSubjects));
            subjectCombo.setPromptText("Select subject");

            TextField nameField = new TextField();
            nameField.setPromptText("Chapter name");

            TextArea descField = new TextArea();
            descField.setPromptText("Description");
            descField.setPrefRowCount(3);

            Spinner<Integer> hoursSpinner = new Spinner<>(0, 100, 5);

            grid.add(new Label("Subject:"), 0, 0);
            grid.add(subjectCombo, 1, 0);
            grid.add(new Label("Name:"), 0, 1);
            grid.add(nameField, 1, 1);
            grid.add(new Label("Description:"), 0, 2);
            grid.add(descField, 1, 2);
            grid.add(new Label("Est. Hours:"), 0, 3);
            grid.add(hoursSpinner, 1, 3);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    if (subjectCombo.getValue() == null) return null;
                    Chapter chapter = new Chapter();
                    chapter.setSubjectId(subjectCombo.getValue().getId());
                    chapter.setName(nameField.getText());
                    chapter.setDescription(descField.getText());
                    chapter.setEstimatedHours(hoursSpinner.getValue());
                    chapter.setStatus(Chapter.ChapterStatus.NOT_STARTED);
                    return chapter;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(chapter -> {
                if (chapter.getName().isEmpty()) {
                    showAlert("Name is required", Alert.AlertType.ERROR);
                    return;
                }

                try {
                    chapterDAO.create(chapter);
                    refresh();
                    mainWindow.refreshAll();
                    showAlert("Chapter added successfully!", Alert.AlertType.INFORMATION);
                } catch (SQLException ex) {
                    showAlert("Error adding chapter: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        } catch (SQLException ex) {
            showAlert("Error loading subjects: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showEditDialog() {
        Chapter selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a chapter to edit", Alert.AlertType.WARNING);
            return;
        }

        Dialog<Chapter> dialog = new Dialog<>();
        dialog.setTitle("Edit Chapter");
        dialog.setHeaderText("Edit chapter details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(selected.getName());
        TextArea descField = new TextArea(selected.getDescription());
        descField.setPrefRowCount(3);
        Spinner<Integer> hoursSpinner = new Spinner<>(0, 100, selected.getEstimatedHours());
        ComboBox<Chapter.ChapterStatus> statusCombo = new ComboBox<>(
            FXCollections.observableArrayList(Chapter.ChapterStatus.values()));
        statusCombo.setValue(selected.getStatus());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Est. Hours:"), 0, 2);
        grid.add(hoursSpinner, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(statusCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                selected.setName(nameField.getText());
                selected.setDescription(descField.getText());
                selected.setEstimatedHours(hoursSpinner.getValue());
                selected.setStatus(statusCombo.getValue());
                return selected;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(chapter -> {
            try {
                chapterDAO.update(chapter);
                refresh();
                mainWindow.refreshAll();
                showAlert("Chapter updated successfully!", Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                showAlert("Error updating chapter: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void deleteSelected() {
        Chapter selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a chapter to delete", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete chapter: " + selected.getName());
        confirm.setContentText("Are you sure?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    chapterDAO.delete(selected.getId());
                    refresh();
                    mainWindow.refreshAll();
                    showAlert("Chapter deleted successfully!", Alert.AlertType.INFORMATION);
                } catch (SQLException ex) {
                    showAlert("Error deleting chapter: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void updateStatus(Chapter.ChapterStatus newStatus) {
        Chapter selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a chapter", Alert.AlertType.WARNING);
            return;
        }

        try {
            selected.setStatus(newStatus);
            chapterDAO.update(selected);
            refresh();
            mainWindow.refreshAll();
            showAlert("Status updated to: " + newStatus.getDisplayName(), Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error updating status: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void applyFilter() {
        try {
            chapters.clear();
            Subject filterSubject = subjectFilter.getValue();

            if (filterSubject == null) {
                // Show all chapters for user's subjects
                List<Subject> userSubjects = subjectDAO.findByUserId(currentUser.getId());
                for (Subject subject : userSubjects) {
                    chapters.addAll(chapterDAO.findBySubjectId(subject.getId()));
                }
            } else {
                chapters.addAll(chapterDAO.findBySubjectId(filterSubject.getId()));
            }
        } catch (SQLException ex) {
            showAlert("Error loading chapters: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void refresh() {
        try {
            // Update subject filter
            List<Subject> userSubjects = subjectDAO.findByUserId(currentUser.getId());
            subjectFilter.setItems(FXCollections.observableArrayList(userSubjects));

            // Refresh chapters
            applyFilter();
        } catch (SQLException ex) {
            showAlert("Error loading data: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
