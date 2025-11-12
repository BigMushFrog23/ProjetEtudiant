package com.studytracker.ui.tabs;

import com.studytracker.dao.SubjectDAO;
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

/**
 * Tab for managing subjects (CRUD operations).
 */
public class SubjectsTab {
    private final User currentUser;
    private final MainWindow mainWindow;
    private final SubjectDAO subjectDAO;
    private final TableView<Subject> table;
    private final ObservableList<Subject> subjects;

    private static final String[][] COLOR_OPTIONS = {
        {"Red", "#e74c3c"},
        {"Blue", "#3498db"},
        {"Green", "#2ecc71"},
        {"Orange", "#f39c12"},
        {"Purple", "#9b59b6"},
        {"Turquoise", "#1abc9c"},
        {"Carrot", "#e67e22"},
        {"Dark Gray", "#34495e"},
        {"Emerald", "#16a085"},
        {"Crimson", "#c0392b"}
    };

    // Helper class to display color name but store hex value
    private static class ColorOption {
        private final String name;
        private final String hexValue;

        public ColorOption(String name, String hexValue) {
            this.name = name;
            this.hexValue = hexValue;
        }

        public String getHexValue() {
            return hexValue;
        }

        @Override
        public String toString() {
            return name; // This is what displays in the ComboBox
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ColorOption) {
                return hexValue.equals(((ColorOption) obj).hexValue);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return hexValue.hashCode();
        }
    }

    public SubjectsTab(User currentUser, MainWindow mainWindow) {
        this.currentUser = currentUser;
        this.mainWindow = mainWindow;
        this.subjectDAO = new SubjectDAO();
        this.subjects = FXCollections.observableArrayList();
        this.table = createTable();
    }

    // Helper method to get ColorOption from hex value
    private ColorOption getColorOptionFromHex(String hexValue) {
        for (String[] colorPair : COLOR_OPTIONS) {
            if (colorPair[1].equals(hexValue)) {
                return new ColorOption(colorPair[0], colorPair[1]);
            }
        }
        return new ColorOption(COLOR_OPTIONS[0][0], COLOR_OPTIONS[0][1]); // Default to first color
    }

    // Helper method to create list of ColorOptions
    private ObservableList<ColorOption> getColorOptions() {
        ObservableList<ColorOption> options = FXCollections.observableArrayList();
        for (String[] colorPair : COLOR_OPTIONS) {
            options.add(new ColorOption(colorPair[0], colorPair[1]));
        }
        return options;
    }

    public VBox getContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Header
        Label titleLabel = new Label("📚 Manage Subjects");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Buttons
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("➕ Add Subject");
        Button editButton = new Button("✏️ Edit");
        Button deleteButton = new Button("🗑️ Delete");

        addButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        buttonBox.getChildren().addAll(addButton, editButton, deleteButton);

        // Event handlers
        addButton.setOnAction(e -> showAddDialog());
        editButton.setOnAction(e -> showEditDialog());
        deleteButton.setOnAction(e -> deleteSelected());

        content.getChildren().addAll(titleLabel, buttonBox, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        return content;
    }

    private TableView<Subject> createTable() {
        TableView<Subject> table = new TableView<>();
        table.setItems(subjects);

        TableColumn<Subject, String> nameCol = new TableColumn<>("Subject Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Subject, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(400);

        TableColumn<Subject, String> colorCol = new TableColumn<>("Color");
        colorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
        colorCol.setPrefWidth(100);
        colorCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String color, boolean empty) {
                super.updateItem(color, empty);
                if (empty || color == null) {
                    setGraphic(null);
                } else {
                    Region colorBox = new Region();
                    colorBox.setPrefSize(60, 20);
                    colorBox.setStyle("-fx-background-color: " + color + "; -fx-border-color: #ccc; -fx-border-width: 1;");
                    setGraphic(colorBox);
                }
            }
        });

        table.getColumns().addAll(nameCol, descCol, colorCol);

        return table;
    }

    private void showAddDialog() {
        Dialog<Subject> dialog = new Dialog<>();
        dialog.setTitle("Add Subject");
        dialog.setHeaderText("Enter subject details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Subject name");

        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(3);

        ComboBox<ColorOption> colorCombo = new ComboBox<>(getColorOptions());
        colorCombo.setValue(colorCombo.getItems().get(0)); // Default to first color

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Color:"), 0, 2);
        grid.add(colorCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Subject subject = new Subject();
                subject.setUserId(currentUser.getId());
                subject.setName(nameField.getText());
                subject.setDescription(descField.getText());
                subject.setColor(colorCombo.getValue().getHexValue());
                return subject;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(subject -> {
            if (subject.getName().isEmpty()) {
                showAlert("Name is required", Alert.AlertType.ERROR);
                return;
            }

            try {
                subjectDAO.create(subject);
                refresh();
                mainWindow.refreshAll();
                showAlert("Subject added successfully!", Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                showAlert("Error adding subject: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void showEditDialog() {
        Subject selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a subject to edit", Alert.AlertType.WARNING);
            return;
        }

        Dialog<Subject> dialog = new Dialog<>();
        dialog.setTitle("Edit Subject");
        dialog.setHeaderText("Edit subject details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(selected.getName());
        TextArea descField = new TextArea(selected.getDescription());
        descField.setPrefRowCount(3);
        ComboBox<ColorOption> colorCombo = new ComboBox<>(getColorOptions());
        colorCombo.setValue(getColorOptionFromHex(selected.getColor()));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Color:"), 0, 2);
        grid.add(colorCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                selected.setName(nameField.getText());
                selected.setDescription(descField.getText());
                selected.setColor(colorCombo.getValue().getHexValue());
                return selected;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(subject -> {
            try {
                subjectDAO.update(subject);
                refresh();
                mainWindow.refreshAll();
                showAlert("Subject updated successfully!", Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                showAlert("Error updating subject: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void deleteSelected() {
        Subject selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a subject to delete", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete subject: " + selected.getName());
        confirm.setContentText("This will also delete all chapters and exams for this subject. Are you sure?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    subjectDAO.delete(selected.getId());
                    refresh();
                    mainWindow.refreshAll();
                    showAlert("Subject deleted successfully!", Alert.AlertType.INFORMATION);
                } catch (SQLException ex) {
                    showAlert("Error deleting subject: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    public void refresh() {
        try {
            subjects.clear();
            subjects.addAll(subjectDAO.findByUserId(currentUser.getId()));
        } catch (SQLException ex) {
            showAlert("Error loading subjects: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
