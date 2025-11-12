# 🛠️ Setup & Installation Guide

## Prerequisites

Before running the Gamified Study Tracker, ensure you have the following installed:

### 1. Java Development Kit (JDK) 17 or Higher

**Check if Java is installed:**
```bash
java -version
```

**Download Java:**
- [Download Oracle JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
- Or [Download OpenJDK 17](https://adoptium.net/)

### 2. Maven (Build Tool)

**Check if Maven is installed:**
```bash
mvn -version
```

**Download Maven:**
- [Download Apache Maven](https://maven.apache.org/download.cgi)
- Follow installation instructions for your OS

**Windows Quick Install:**
1. Download Maven binary zip
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH: `C:\Program Files\Apache\maven\bin`
4. Verify: `mvn -version`

### 3. IDE (Recommended but Optional)

Choose one:
- **IntelliJ IDEA Community Edition** (Recommended) - [Download](https://www.jetbrains.com/idea/download/)
- **Eclipse** - [Download](https://www.eclipse.org/downloads/)
- **VS Code** with Java extensions

## 📦 Installation Steps

### Option 1: Using IntelliJ IDEA (Easiest)

1. **Open IntelliJ IDEA**
2. Click `File > Open`
3. Navigate to the project folder and select it
4. IntelliJ will automatically detect the Maven project
5. Wait for Maven to download dependencies (check bottom right corner)
6. Right-click on `src/main/java/com/studytracker/Main.java`
7. Select `Run 'Main.main()'`

### Option 2: Using Eclipse

1. **Open Eclipse**
2. Click `File > Import > Existing Maven Projects`
3. Browse to the project folder
4. Click `Finish`
5. Wait for Maven to download dependencies
6. Right-click on project > `Run As > Java Application`
7. Select `Main - com.studytracker`

### Option 3: Using Command Line (Maven)

```bash
# Navigate to project directory
cd /path/to/ProjetEtudiant

# Download dependencies and compile
mvn clean install

# Run the application
mvn javafx:run
```

### Option 4: Manual Dependency Setup (Without Maven)

If Maven is not available, you can manually download JAR files:

1. **Download JavaFX SDK:**
   - Visit [JavaFX Downloads](https://gluonhq.com/products/javafx/)
   - Download JavaFX SDK for your platform
   - Extract to a folder

2. **Download Dependencies:**
   - [SQLite JDBC Driver](https://github.com/xerial/sqlite-jdbc/releases)
   - [JBCrypt](https://github.com/jeremyh/jBCrypt/releases)

3. **Add to Classpath in IDE:**
   - Add all JARs from JavaFX `lib` folder
   - Add `sqlite-jdbc-X.X.X.jar`
   - Add `jbcrypt-X.X.jar`

4. **VM Arguments for JavaFX:**
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```

## 🚀 Running the Application

### First Run

1. The application will automatically create `studytracker.db` in the project root
2. You'll see the login screen
3. Click **"Register"** to create your first account:
   - Username: `admin` (or any username)
   - Password: `admin123` (or any password, min 4 characters)
4. Click **"Login"** to enter the application

### Sample Data (Optional)

To quickly test the application, create:
1. **Subjects:** Mathematics, Computer Science, English
2. **Chapters:** For each subject, add 2-3 chapters
3. **Exams:** Add an exam 3 days from now
4. **Study Session:** Log 2 hours of study to earn 20 XP!

## 🐛 Troubleshooting

### Issue: "Module not found" errors

**Solution:**
Ensure JavaFX modules are properly configured:
- For IntelliJ: `File > Project Structure > Libraries > Add JavaFX`
- For Eclipse: Right-click project > `Properties > Java Build Path > Libraries`

### Issue: "Cannot find SQLite driver"

**Solution:**
Maven should download this automatically. If not:
```bash
mvn clean install -U
```

### Issue: Database locked

**Solution:**
Close any other instances of the application or delete `studytracker.db` to start fresh.

### Issue: JavaFX runtime components missing

**Solution:**
Add VM arguments when running:
```
--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
```

## 📁 Project Structure Check

Ensure your project structure looks like this:

```
ProjetEtudiant/
├── pom.xml
├── README.md
├── SETUP.md
├── .gitignore
└── src/
    └── main/
        └── java/
            └── com/
                └── studytracker/
                    ├── Main.java
                    ├── model/
                    ├── dao/
                    ├── database/
                    ├── service/
                    └── ui/
```

## 🔧 Development Setup

### Recommended IntelliJ IDEA Settings

1. **Code Style:** `File > Settings > Editor > Code Style > Java`
   - Tab size: 4
   - Use spaces instead of tabs

2. **Auto-Import:** `File > Settings > Editor > General > Auto Import`
   - Enable "Add unambiguous imports on the fly"

3. **File Encodings:** `File > Settings > Editor > File Encodings`
   - Project Encoding: UTF-8

### Building for Production

To create a JAR file:
```bash
mvn clean package
```

The JAR will be in `target/gamified-study-tracker-1.0-SNAPSHOT.jar`

## 💡 Tips

- **Database Location:** `studytracker.db` is created in the project root directory
- **Reset Database:** Delete `studytracker.db` to start with a clean database
- **Multiple Users:** Each user has completely separate data
- **Backup:** Copy `studytracker.db` to backup your data

## 📞 Support

If you encounter issues:
1. Check this SETUP.md file
2. Review the README.md for usage instructions
3. Verify all prerequisites are correctly installed
4. Check Java and Maven versions

## ✅ Quick Test

After installation, verify everything works:

1. ✅ Application launches without errors
2. ✅ Can register a new user
3. ✅ Can login with created user
4. ✅ All tabs are accessible
5. ✅ Can add a subject
6. ✅ Dashboard displays correctly

## 🎉 You're Ready!

If all tests pass, you're ready to use the Gamified Study Tracker!

Refer to [README.md](README.md) for detailed usage instructions and features.
