package com.studytracker;

import com.studytracker.database.DatabaseManager;
import com.studytracker.ui.LoginWindow;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main application class for the Gamified Study Tracker.
 * Initializes the database and launches the JavaFX application.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize database
        DatabaseManager.getInstance();

        // Show login window
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.show(primaryStage);
    }

    @Override
    public void stop() {
        // Close database connection when application exits
        DatabaseManager.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
