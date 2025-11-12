package com.studytracker.ui;

import com.studytracker.dao.UserDAO;
import com.studytracker.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Login window with authentication and user registration.
 */
public class LoginWindow {
    private final UserDAO userDAO;

    public LoginWindow() {
        this.userDAO = new UserDAO();
    }

    public void show(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(
            Color.rgb(240, 248, 255), CornerRadii.EMPTY, Insets.EMPTY)));

        // Title
        Label titleLabel = new Label("🎓 Gamified Study Tracker");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.rgb(52, 73, 94));

        Label subtitleLabel = new Label("Track your progress, earn XP, unlock achievements!");
        subtitleLabel.setFont(Font.font("System", 14));
        subtitleLabel.setTextFill(Color.rgb(127, 140, 141));

        // Login form
        GridPane loginForm = new GridPane();
        loginForm.setHgap(10);
        loginForm.setVgap(15);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setMaxWidth(400);

        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(250);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(250);

        loginForm.add(usernameLabel, 0, 0);
        loginForm.add(usernameField, 1, 0);
        loginForm.add(passwordLabel, 0, 1);
        loginForm.add(passwordField, 1, 1);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
        loginButton.setPrefWidth(120);

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
        registerButton.setPrefWidth(120);

        buttonBox.getChildren().addAll(loginButton, registerButton);

        // Message label
        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 12));

        // Event handlers
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText(), stage, messageLabel));
        registerButton.setOnAction(e -> handleRegister(usernameField.getText(), passwordField.getText(), messageLabel));

        // Allow Enter key to login
        passwordField.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText(), stage, messageLabel));

        root.getChildren().addAll(titleLabel, subtitleLabel, loginForm, buttonBox, messageLabel);

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("Study Tracker - Login");
        stage.setResizable(false);
        stage.show();
    }

    private void handleLogin(String username, String password, Stage stage, Label messageLabel) {
        if (username.isEmpty() || password.isEmpty()) {
            showMessage(messageLabel, "Please enter username and password", Color.RED);
            return;
        }

        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                showMessage(messageLabel, "Login successful! Welcome " + username, Color.GREEN);

                // Open main application window
                MainWindow mainWindow = new MainWindow(user);
                mainWindow.show(stage);
            } else {
                showMessage(messageLabel, "Invalid username or password", Color.RED);
            }
        } catch (SQLException ex) {
            showMessage(messageLabel, "Database error: " + ex.getMessage(), Color.RED);
        }
    }

    private void handleRegister(String username, String password, Label messageLabel) {
        if (username.isEmpty() || password.isEmpty()) {
            showMessage(messageLabel, "Please enter username and password", Color.RED);
            return;
        }

        if (password.length() < 4) {
            showMessage(messageLabel, "Password must be at least 4 characters", Color.RED);
            return;
        }

        try {
            // Check if username already exists
            if (userDAO.findByUsername(username) != null) {
                showMessage(messageLabel, "Username already exists", Color.RED);
                return;
            }

            User user = userDAO.create(username, password);
            if (user != null) {
                showMessage(messageLabel, "Registration successful! You can now login", Color.GREEN);
            } else {
                showMessage(messageLabel, "Registration failed", Color.RED);
            }
        } catch (SQLException ex) {
            showMessage(messageLabel, "Database error: " + ex.getMessage(), Color.RED);
        }
    }

    private void showMessage(Label label, String message, Color color) {
        label.setText(message);
        label.setTextFill(color);
    }
}
