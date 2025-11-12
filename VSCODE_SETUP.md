# 🔧 VS Code Setup Guide for Gamified Study Tracker

## Prerequisites

### 1. Install Java 17+
```bash
java -version
```
Download from: https://adoptium.net/

### 2. Install Maven
```bash
mvn -version
```
Download from: https://maven.apache.org/download.cgi

---

## 🚀 Quick Setup for VS Code

### Step 1: Install VS Code Extensions

Open VS Code and install:
1. **Extension Pack for Java** (by Microsoft)
2. **Maven for Java** (by Microsoft)

Press `Ctrl+Shift+X`, search for these extensions, and install.

### Step 2: Open the Project

1. Open VS Code
2. `File > Open Folder`
3. Select the `ProjetEtudiant` folder
4. Trust the workspace when prompted

### Step 3: Let Maven Download Dependencies

Open terminal in VS Code (`` Ctrl+` ``) and run:
```bash
mvn clean install
```

This will download all required libraries including JavaFX.

---

## ▶️ Running the Application

### Easiest Method: Use Maven
```bash
mvn javafx:run
```

This handles everything automatically!

### Alternative: Run from VS Code

1. Open `src/main/java/com/studytracker/Main.java`
2. Click the **Run** button (▶️) above `main` method
3. Or press `F5`

---

## 🐛 Troubleshooting

### "Maven not found"
1. Install Maven from https://maven.apache.org/download.cgi
2. Add to PATH: `C:\Program Files\Apache\maven\bin`
3. Restart VS Code

### "JavaFX runtime missing"
Just use Maven to run:
```bash
mvn javafx:run
```

### Red squiggles everywhere
```bash
mvn clean install
```
Then: `Ctrl+Shift+P` → `Java: Clean Java Language Server Workspace`

---

## ✅ Quick Test

After setup:
```bash
cd ProjetEtudiant
mvn clean install
mvn javafx:run
```

Application should launch! 🎉

---

**For full details, see [README.md](README.md) and [SETUP.md](SETUP.md)**
