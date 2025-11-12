package com.studytracker.ui.tabs;

import com.studytracker.dao.ExamDAO;
import com.studytracker.dao.SubjectDAO;
import com.studytracker.model.Exam;
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
import java.time.LocalDate;
import java.util.List;

/**
 * Tab for managing exams and deadlines.
 */
public class ExamsTab {
    private final User currentUser;
    private final MainWindow mainWindow;
    private final ExamDAO examDAO;
    private final SubjectDAO subjectDAO;
    private final TableView<Exam> table;
    private final ObservableList<Exam> exams;

    public ExamsTab(User currentUser, MainWindow mainWindow) {
        this.currentUser = currentUser;
        this.mainWindow = mainWindow;
        this.examDAO = new ExamDAO();
        this.subjectDAO = new SubjectDAO();
        this.exams = FXCollections.observableArrayList();
        this.table = createTable();
    }

    public VBox getContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("📝 Manage Exams & Deadlines");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Buttons
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("➕ Add Exam");
        Button editButton = new Button("✏️ Edit");
        Button deleteButton = new Button("🗑️ Delete");
        Button markCompletedButton = new Button("✅ Mark Completed");

        addButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        markCompletedButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, markCompletedButton);

        addButton.setOnAction(e -> showAddDialog());
        editButton.setOnAction(e -> showEditDialog());
        deleteButton.setOnAction(e -> deleteSelected());
        markCompletedButton.setOnAction(e -> markCompleted());

        content.getChildren().addAll(titleLabel, buttonBox, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        return content;
    }

    private TableView<Exam> createTable() {
        TableView<Exam> table = new TableView<>();
        table.setItems(exams);

        // Row coloring based on deadline
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Exam exam, boolean empty) {
                super.updateItem(exam, empty);
                if (empty || exam == null) {
                    setStyle("");
                } else if (exam.isCompleted()) {
                    setStyle("-fx-background-color: #d5f4e622;"); // Light green
                } else if (exam.isOverdue()) {
                    setStyle("-fx-background-color: #e74c3c22;"); // Light red
                } else if (exam.isUpcoming()) {
                    setStyle("-fx-background-color: #f39c1222;"); // Light orange
                } else {
                    setStyle("");
                }
            }
        });

        TableColumn<Exam, String> nameCol = new TableColumn<>("Exam Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Exam, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("examDate"));
        dateCol.setPrefWidth(120);

        TableColumn<Exam, Long> daysUntilCol = new TableColumn<>("Days Until");
        daysUntilCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDaysUntil()));
        daysUntilCol.setPrefWidth(100);
        daysUntilCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Long days, boolean empty) {
                super.updateItem(days, empty);
                if (empty || days == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Exam exam = getTableView().getItems().get(getIndex());
                    if (exam.isCompleted()) {
                        setText("Completed");
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if (days < 0) {
                        setText(Math.abs(days) + " days ago");
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setText(days + " days");
                        setStyle(days <= 7 ? "-fx-text-fill: #f39c12; -fx-font-weight: bold;" : "");
                    }
                }
            }
        });

        TableColumn<Exam, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(300);

        table.getColumns().addAll(nameCol, dateCol, daysUntilCol, descCol);

        return table;
    }

    private void showAddDialog() {
        try {
            List<Subject> userSubjects = subjectDAO.findByUserId(currentUser.getId());
            if (userSubjects.isEmpty()) {
                showAlert("Please create a subject first!", Alert.AlertType.WARNING);
                return;
            }

            Dialog<Exam> dialog = new Dialog<>();
            dialog.setTitle("Add Exam");
            dialog.setHeaderText("Enter exam details");

            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            ComboBox<Subject> subjectCombo = new ComboBox<>(FXCollections.observableArrayList(userSubjects));
            subjectCombo.setPromptText("Select subject");

            TextField nameField = new TextField();
            nameField.setPromptText("Exam name");

            DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(7));

            TextArea descField = new TextArea();
            descField.setPromptText("Description");
            descField.setPrefRowCount(3);

            grid.add(new Label("Subject:"), 0, 0);
            grid.add(subjectCombo, 1, 0);
            grid.add(new Label("Name:"), 0, 1);
            grid.add(nameField, 1, 1);
            grid.add(new Label("Date:"), 0, 2);
            grid.add(datePicker, 1, 2);
            grid.add(new Label("Description:"), 0, 3);
            grid.add(descField, 1, 3);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    if (subjectCombo.getValue() == null) return null;
                    Exam exam = new Exam();
                    exam.setSubjectId(subjectCombo.getValue().getId());
                    exam.setName(nameField.getText());
                    exam.setExamDate(datePicker.getValue());
                    exam.setDescription(descField.getText());
                    return exam;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(exam -> {
                if (exam.getName().isEmpty()) {
                    showAlert("Name is required", Alert.AlertType.ERROR);
                    return;
                }

                try {
                    examDAO.create(exam);
                    refresh();
                    mainWindow.refreshAll();
                    showAlert("Exam added successfully!", Alert.AlertType.INFORMATION);
                } catch (SQLException ex) {
                    showAlert("Error adding exam: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        } catch (SQLException ex) {
            showAlert("Error loading subjects: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showEditDialog() {
        Exam selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an exam to edit", Alert.AlertType.WARNING);
            return;
        }

        Dialog<Exam> dialog = new Dialog<>();
        dialog.setTitle("Edit Exam");
        dialog.setHeaderText("Edit exam details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(selected.getName());
        DatePicker datePicker = new DatePicker(selected.getExamDate());
        TextArea descField = new TextArea(selected.getDescription());
        descField.setPrefRowCount(3);
        CheckBox completedCheck = new CheckBox("Completed");
        completedCheck.setSelected(selected.isCompleted());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descField, 1, 2);
        grid.add(completedCheck, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                selected.setName(nameField.getText());
                selected.setExamDate(datePicker.getValue());
                selected.setDescription(descField.getText());
                selected.setCompleted(completedCheck.isSelected());
                return selected;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(exam -> {
            try {
                examDAO.update(exam);
                refresh();
                mainWindow.refreshAll();
                showAlert("Exam updated successfully!", Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                showAlert("Error updating exam: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void deleteSelected() {
        Exam selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an exam to delete", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete exam: " + selected.getName());
        confirm.setContentText("Are you sure?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    examDAO.delete(selected.getId());
                    refresh();
                    mainWindow.refreshAll();
                    showAlert("Exam deleted successfully!", Alert.AlertType.INFORMATION);
                } catch (SQLException ex) {
                    showAlert("Error deleting exam: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void markCompleted() {
        Exam selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an exam", Alert.AlertType.WARNING);
            return;
        }

        try {
            selected.setCompleted(true);
            examDAO.update(selected);
            refresh();
            mainWindow.refreshAll();
            showAlert("Exam marked as completed!", Alert.AlertType.INFORMATION);
        } catch (SQLException ex) {
            showAlert("Error updating exam: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void refresh() {
        try {
            exams.clear();
            exams.addAll(examDAO.findByUserId(currentUser.getId()));
        } catch (SQLException ex) {
            showAlert("Error loading exams: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
