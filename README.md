<h1 align="center">DockTask</h1>

<p align="center">
  <img src="https://github.com/user-attachments/assets/142dcb77-abfb-4551-86cd-4035ab3c019f" width="120" />
</p>

<p align="center">
  <strong>Desktop task management with millisecond-accurate deadlines and intelligent notifications</strong>
  <br>
  <em>Built for students and professionals who need precision, not just reminders</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-23-orange?style=for-the-badge&logo=openjdk" alt="Java 23" />
  <img src="https://img.shields.io/badge/JavaFX-21-blue?style=for-the-badge" alt="JavaFX 21" />
  <img src="https://img.shields.io/badge/Platform-Windows-0078D6?style=for-the-badge&logo=windows" alt="Windows" />
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="MIT License" />
</p>

---

##  Why DockTask?

I built DockTask to solve my own problem of managing multiple classwork deadlines while learning JavaFX development. What started as a simple to-do list evolved into a full-featured task management system as I explored UI/UX design, data persistence strategies, and memory optimization techniques. 

The core challenge? Basic calendar apps treat "3 PM" and "3:00:47 PM" the same, but when you have multiple assignments due at midnight, lab reports due minutes apart, and group project submissions with precise timing requirements, **seconds matter**.

This project pushed me to think beyond basic functionality and focus on creating a polished, user-friendly experience. **Now it's the tool I use daily to keep my academic life neat and organized.**

---

##  Demo

> **Watch DockTask in action!** [See the demo video](https://www.youtube.com/watch?v=u5oVhOg2F70) or download the installer below to try it yourself.







https://github.com/user-attachments/assets/67e9159e-ff12-4f21-9128-568c9d12a31a








---

##  Download

| Platform | Download | Status |
|----------|----------|--------|
| **Windows** | [DockTask-Setup-0.5.exe](https://github.com/KSaifStack/DockTask/releases/download/0.5/DockTask-Setup-v0.5.exe) | ✅ Available |
| **MacOS** | Coming Soon | 🚧 In Development |
| **Linux** | Coming Soon | 🚧 In Development |

> **Self-contained installer** — No JDK installation required. Everything is bundled.

> **NOTE!** If you run the installer in administration mode, DockTask can only save data with administration!
---

##  Features

### Task Management
- **Priority-based auto-sorting** with color-coded groups for instant visual scanning
- **Real-time countdown timers** tracking tasks down to the second
- **Multi-stage notifications** at 24h, 5h, 1h, 30m, 10m, 1m, and overdue intervals
- **Integrated calendar view** with automatic synchronization when tasks are created or modified
- **Precise task timing** allows scheduling tasks within seconds of each other

### User Experience  
- **System tray integration** — Minimize to tray and keep DockTask running in the background
- **Adaptive theming** — Dark/light modes with persistent preferences across sessions
- **Native window controls** — Platform-integrated UI using [NFX library](https://github.com/xdsswar/nfx-lib)
- **Keyboard shortcuts** — Power user features (Ctrl+Shift+C for instant task creation)
- **Seamless theme switching** with theme-adaptive styling

### Data Management
- **Auto-save functionality** with unsaved changes detection
- **Export/Import capabilities** for data portability and backup
- **Multi-user support** with separate task databases and built-in authentication
- **Local-first storage** — All data stays on your machine
- **Confirmation dialogs** protecting against accidental data loss

---

##  Interface

### Light Mode
<img width="1000" height="964" alt="LightTheme" src="https://github.com/user-attachments/assets/0bf9a7a2-608b-4104-829b-041e38a48728" />

### Dark Mode  
<img width="1000" height="943" alt="DarkTheme" src="https://github.com/user-attachments/assets/27e52f91-d252-4895-90c2-4e5c9ef241a4" />

---

##  Tech Stack

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Language** | Java 23 | Core application logic |
| **UI Framework** | JavaFX 21 | Cross-platform GUI rendering |
| **Styling** | CSS3 | Theme implementation and visual design |
| **Native Integration** | [NFX Library](https://github.com/xdsswar/nfx-lib) | Platform-native window controls and title bar |
| **Architecture** | MVC Pattern | Separation of concerns for maintainability |
| **Data Storage** | File-based serialization | Local persistence with custom separator-based format |
| **Concurrency** | JavaFX Timeline API | Real-time UI updates without blocking |
| **System Integration** | Java AWT TrayIcon | OS-level notifications and system tray |

---

##  Architecture

DockTask implements **Model-View-Controller (MVC)** architecture for maintainable, testable code:

### Model Layer
- **UserData.java** — Centralized data access handling all CRUD operations
- **Data/** directory — File-based storage for user databases and settings (Settings.txt)
- Responsibilities: Data validation, serialization with `<SEP>` separators, persistence operations

### View Layer
- **TaskUi.java** — Main dashboard with task list and real-time countdown timers
- **CalendarUi.java** — Monthly calendar with task indicators and synchronization 
- **CreateTaskUi / UpdateTaskUi** — Task creation and editing interfaces with auto-save
- **Settings.java** — Application configuration panel with export/import, data management
- Responsibilities: UI rendering, user input collection, visual feedback, theme adaptation

### Controller Layer
- Event handlers connecting user actions to data operations
- **ThemeManager** — Coordinating theme state and color adaptation across components
- **Notification System** — Managing system tray alerts based on task deadlines
- **Timeline Controllers** — Orchestrating concurrent countdown updates
- **Navigation Guards** — Preventing accidental data loss during transitions
- Responsibilities: Business logic, state management, component coordination

**Benefits of this approach:**
- Data layer modifications (e.g., migrating to database) require zero UI changes
- UI redesigns proceed independently of data operations
- Individual components can be unit tested in isolation
- New features integrate cleanly without cascading modifications

---

## What I Learned

Building DockTask taught me valuable software engineering lessons:

### Memory Optimization (63% Reduction)
In version 0.1, countdown timers for completed or removed tasks continued running in the background, accumulating on the memory stack. This caused memory usage to grow from ~500MB to over 1000MB during extended sessions.

**Solution implemented:**
- Pause all Timeline threads when the application is minimized or closed
- Clear expired task entries from the active monitoring list after removal
- Implement proper cleanup of countdown timers for non-visible tasks

**Result:** Reduced idle memory footprint from 500MB to 60MB (85% improvement from peak) with sustained performance during long-running sessions. The application now becomes more efficient over time as resources are garbage collected.

### Other Key Learnings

- **Concurrent State Management:** Designed timeline system tracking multiple countdown timers simultaneously without data races or UI freezes

- **Data Persistence:** Built custom file-based serialization with `<SEP>` separators enabling support for embedded links and preventing parsing errors. Added validation to prevent corruption, auto-save with change detection, and safe import/export functionality

- **UI/UX Design:** Created theme-aware component system with instant propagation across 5+ view classes, responsive layouts, and intuitive information hierarchy with color-coded visual distinctions

- **Event-Driven Architecture:** Mastered JavaFX event system, listeners, and callback patterns for responsive interactions. Implemented navigation guards to prevent accidental data loss

- **Software Distribution:** Built self-contained Windows installer with bundled Java runtime for zero-dependency deployment

---

##  Changelog

### v0.5 – December 2025

**Architecture & Data Layer**
- Implemented centralized data storage system with dedicated "Data" folder structure
- Migrated from simple file I/O to robust data serialization with validation
- Created persistent settings storage via Settings.txt configuration file
- Built foundation for modular plugin system with configuration management
- Replaced legacy data parsing with scalable `<SEP>` separator-based system for embedded link support

**User Safety & Data Integrity**
- Designed and implemented modal confirmation dialog system for destructive operations
- Developed auto-save mechanism with unsaved changes detection when creating tasks
- Created navigation guard system to prevent accidental data loss
- Implemented data export/import functionality with comprehensive error handling
- Added validation for all data manipulation operations to prevent corruption

**UI/UX Enhancements**
- Redesigned application interface for improved visual hierarchy and clarity
- Implemented dynamic theme-aware window chrome with color adaptation
- Developed Settings panel with centralized application controls (export/import, refresh, exit)
- Refined interface by removing redundant UI elements

**Feature Additions**
- Dark mode preference persistence across sessions
- Manual data refresh capability
- Graceful application exit with cleanup operations
- Import/export functionality for data portability

**Technical Debt & Refactoring**
- Improved error handling across file operations
- Enhanced data validation to prevent corruption scenarios
- Optimized memory usage for large datasets
- Strengthened separation of concerns in data access layer

### v0.3 – November 2025

**Performance & Optimization**
-  **Reduced memory usage by 63%** by identifying and fixing Timeline leak — implemented pause/resume for background countdowns
   Enhanced task sorting algorithm for faster rendering with large datasets
-  Refactored legacy codebase for better maintainability

**UI/UX Improvements**
-  Redesigned CreateTask UI with cleaner layout and improved responsiveness
-  Enhanced calendar refresh logic with instant update propagation
-  Fixed Sun & Moon icon reset issue with live theme synchronization
-  Revamped clock system with improved real-time accuracy
-  Added keyboard shortcuts for power users (Ctrl+Shift+C for quick task creation)

**Bug Fixes & Quality of Life**
-  UpdateTask changes now instantly reflected in calendar view
-  Improved due date validation and edge case handling
-  Smoother theme transitions between dark/light modes

---

##  Installation

### Windows

1. Download [DockTask-Setup-0.5.exe](https://github.com/KSaifStack/DockTask/releases/download/0.5/DockTask-Setup-v0.5.exe)
2. Run the installer and follow the on-screen prompts (administrator privileges recommended)
3. Launch DockTask from your Desktop or Start Menu

**System Requirements:**
- Windows 10 or later (64-bit)
- 512 MB RAM minimum
- 100 MB free disk space
- 1024x768 display resolution minimum

  

> **Note:** Installer includes bundled Java runtime. No external dependencies or JDK installation required.

---

##  Building from Source

### Prerequisites
- **Java 17+** (JDK 23 recommended)
- **Maven 3.6+** or your IDE with Maven support (IntelliJ IDEA, Eclipse, VSCode)
-  **[NFX Library](https://github.com/xdsswar/nfx-lib)** (Needed to build window)

### Quick Start

#### 1. Clone the Repository
```bash
git clone https://github.com/KSaifStack/DockTask.git
cd DockTask
```

#### 2. Build with Maven
```bash
mvn clean package
```
### Development Setup

#### IntelliJ IDEA
1. Open the project folder (`File` → `Open`)
2. Maven dependencies will auto-import
3. Run the `Main.java` class

#### Eclipse
1. Import as Maven project (`File` → `Import` → `Maven` → `Existing Maven Projects`)
2. Right-click project → `Maven` → `Update Project`
3. Run `Main.java`

#### VS Code
1. Open project folder
2. Install "Java Extension Pack" and "Maven for Java"
3. Maven will auto-detect `pom.xml` and download dependencies
4. Run via the Run button or `F5`

### Project Structure
```
DockTask/
├── pom.xml                    # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ksaifstack/docktask/
│   │   │       ├── Main.java              # Application entry point
│   │   │       ├── model/                 # Data models (Task, User, UserData)
│   │   │       ├── ui/                    # UI components (LoginUI, TaskUI, etc.)
│   │   │       └── util/                  # Utilities (ThemeManager, etc.)
│   │   └── resources/
│   │       ├── images/                    # Icons and graphics
│   │       └── fonts/                     # Custom fonts
│   └── test/                              # Unit tests (coming soon)
├── Data/                                  # User data storage 
└── README.md
```

### Common Build Issues

**Issue:** `JAVA_HOME not set`  
**Fix:** Set your JDK path:
```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-23
# Mac/Linux
export JAVA_HOME=/usr/lib/jvm/java-23-openjdk
```

**Issue:** JavaFX modules not found  
**Fix:** Maven handles JavaFX dependencies automatically via `pom.xml`.  

Make sure you are building the project with Maven (`mvn clean package`) and **not** using `javac` directly.
If the error mentions **NFX**, ensure that the NFX JAR is correctly downloaded and included in the project.  

Refer to the official repository for setup details: https://github.com/xdsswar/nfx-lib 

**Issue:** `Unsupported class file major version 67`  
**Fix:** You need Java 17+ to build. Update your JDK.

---


##  Contributing

Contributions are welcome! Here's how you can help:

- **Report Bugs** — Open an issue with detailed reproduction steps and environment information
- **Suggest Features** — Share ideas via GitHub Discussions with use cases
- **Submit Pull Requests** — Fork, implement with tests, and submit for review

Please ensure all contributions adhere to the existing code style and architectural patterns.

---

##  License

This project is licensed under the **MIT License** — see [LICENSE](LICENSE) file for complete terms and conditions.

---

##  Author

**KSaifStack**
- GitHub: [@KSaifStack](https://github.com/KSaifStack)
- For questions or feature requests, [open an issue](https://github.com/KSaifStack/DockTask/issues)

---

<p align="center">
  <img src="https://github.com/user-attachments/assets/142dcb77-abfb-4551-86cd-4035ab3c019f" width="100" alt="DockTask Logo" />
  <br>
  <em>Built with Java 23 and JavaFX 21</em>
</p>
