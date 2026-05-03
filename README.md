# DockTask — Developer Log

> A desktop task manager with millisecond-accurate deadlines and real-time countdowns.
> Built with JavaFX for students who need precision, not just reminders.

**Current Name:** DockTask &nbsp;|&nbsp; **Previous Names:** To-Do → PlanForge → DockTask
**Current Release:** v0.8.0 &nbsp;|&nbsp; **Platform:** Windows/macOS/Linux (in progress)

> **Have a plugin in mind?** See the full [PluginGuide.md](PluginGuide.md) for a step-by-step walkthrough.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Setup & Installation](#setup--installation)
- [Roadmap](#roadmap)
- [Changelog](#changelog)
- [Dev Notes](#dev-notes)

---

## Project Overview

DockTask started as a simple to-do list and has grown into a full-featured student productivity app. The core idea: unlike generic calendar apps, DockTask is built around **precision timing** — tasks can be scheduled down to the second, with multi-stage notifications and real-time countdowns.

**Intended goal:** A centralized hub for a student's daily tasks, assessments, and activities. Future versions will support a plugin system, AI assistance, recurring tasks, and active recall study tools. 

**Tech Stack:**
| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 23 |
| UI Framework | JavaFX | 25 |
| Styling | CSS3 (with custom `-dt-*` theme variables) | — |
| Native UI | NFX Library (nfx-core) | 1.0.4 |
| System Tray | Dorkbox SystemTray | 3.8 |
| Tray Icon | FXTrayIcon | 4.0.1 |
| Build | Apache Maven | 3.x |
| Testing | JUnit Jupiter | 5.10.1 |
| Architecture | MVC + Plugin Sandbox (`PluginContext` API) | — |
| Storage | File-based serialization (`<SEP>` format) | — |

---

## Setup & Installation

### Requirements
- JavaFX 21+ → [Download](https://gluonhq.com/products/javafx/)
- IntelliJ IDEA (Community Edition recommended) → [Download](https://www.jetbrains.com/idea/download/?section=windows)
- Maven (bundled via `pom.xml`)

### Running the Project
1. Clone the repository
2. Open the project in IntelliJ and let Maven resolve dependencies via `pom.xml`
3. Set VM options for JavaFX modules
4. Run `LoginUi.java` as the main entry point

> **Note:** VS Code is not recommended without additional JavaFX configuration. See [this video](https://www.youtube.com/watch?app=desktop&v=IvsvjUq38Jc) for IntelliJ setup.

### Windows Installer
Download the self-contained installer — no JDK required, Java runtime is bundled.

---

## Roadmap


### v0.8.0 — Plugin System Foundation *(Planned)*

- (Done) Plugin System — sandboxed `PluginContext` API for safe interaction with DockTask
- (Done) Three plugin types: Menu, Widget, and Hybrid
- (Done) `PluginManager` registry with listener-based widget visibility events
- (Done) `PluginUi` — in-app Plugin Hub with scrollable plugin cards
- (Done) Widget show/hide toggle — persisted per-user, live add/remove from canvas
- (Done) Theme-aware plugin CSS via global `-dt-*` CSS variables
- (Done) `PomodoroPlugin` — first-party Hybrid plugin (widget timer + settings menu)
- (Done) `PluginState.txt` — isolated per-user, per-plugin key-value persistence
- [ ] Plugin registry — GitHub-hosted JSON list for community plugins
- [ ] Notifications button
- [ ] Changelog button — click version number in settings to view update log (rendered Markdown)
- [ ] Embedded pictures in task descriptions
- [ ] Replace legacy notification system with Dorkbox (cross-platform support)
- (Done) Redo data functions — each `data.txt` gets a `date.txt` for display history
- (Done) Junit Testing + coverage
- (Done) Revamp task handle from o(log^2n) to o(1)
- (Done) Add new sort formula "priorityScore = PriorityScore = (groupWeight × importance) / (timeRemaining + 1 + OverduePenalty)"
- (Done) Fullscreen task description view (button in bottom-right of UpdateTask)
- (Done) Usable hyperlinks in task text areas
- (Done) Add a button to reset widgets to there original place
- (Done) Revamped task cards — slightly larger with description preview
- (Done) Fixed create task vis not saving upon logout
- (Done) Widgets — movable UI elements (e.g. relocatable `+` button)
- (Done) Redo TaskUi - Reduced TaskUi by over 300 lines and reducing memory usage further
- (Done) Redo CalendarUi - Non-hard coded css values with function changes
- (Done) Fix timeline issues - Fixed timeline breaking when going over a character limit
- (Done) Button hover animations

---
**Planned first-party plugins:**
-  Stopwatch / Pomodoro Timer
-  AI Assistant
-  Motivational Quotes
-  Note Taker
-  Prayer Reminder

---

### v1.5.0 — Recurring Tasks & Themes *(Future)*
- [ ] Recurring tasks — auto-regenerate on a user-defined interval
- [ ] Event tasks — placeholder tasks for recurring real-life events (work, school, etc.)
- [ ] Expanded theme selector (or via plugin?)
- [ ] Automatic update checker via [update4j](https://github.com/update4j/update4j)
- [ ] Fade in/out animations

---

## Changelog

### v0.6.0
- Full Linux and macOS support (minor known issues addressed)
- Replaced AWT tray icon with `FxTrayIcon`
- Implemented `WindowFactory` for platform-aware window borders
- Removed `WindowBorder` on Linux for compatibility
- Tasks display as `MM/DD/YEAR` when over 168 hours (1 week) past due
- Fixed logout bug — previous session data no longer bleeds into new sessions
- Updated JavaFX 21 → JavaFX 25
- Updated `nfx-core` to v1.0.5
- Changed font fully to Lexend across all views
- Added 4 new task colors: Dark Slate Gray, Chocolate, Light Sea Green, Gold
- Fixed overcrowded task pane layout when more than 4 cards are present
- Fixed clock alignment bug when label length is below 21 characters
- Settings pane centered; exit button centered within settings
- Data folder now auto-created on launch if not found
- Fixed hardcoded path issues for cross-machine compatibility
- Overlay panes now have drop shadows
- "No Task Found..." updated to "No Task Found... Click + to add your first task!"
- Fixed version label in settings (was showing `0.5.1v`, now `v0.6.0`)
- NFX no longer requires extra build instructions

---

### v0.5.0
- `Confirmation` class — modal overlay for destructive actions (delete all data, exit, plugin downloads)
- Settings panel — Dark Mode toggle, Export/Import data, Refresh, Exit
- Dark mode preference persisted via `Settings.txt`
- Auto-save with unsaved changes detection on CreateTask back navigation
- Custom window border with theme-reactive color
- New `Data/` folder structure for all user data
- Migrated to `<SEP>` separator format — enables embedded links and image parsing without errors
- GitHub hyperlink added to settings
- General UI cleanup for a cleaner visual hierarchy

---

### v0.3.0 / v0.2.0
- Rebuilt `CreateTaskUi` from scratch
- Calendar refresh button + improved update propagation
- Keyboard shortcuts added
- `UpdateTaskUi` due date editing fixed
- `UpdateTask` changes now reflected instantly in calendar view
- Refactored legacy codebase for maintainability
- **Memory optimization** — fixed `Timeline` leak causing RAM to climb from ~500MB to 1300MB+; reduced idle footprint significantly

---

### v0.1.0 *(Initial Build)*
- Login screen with error handling
- Task listing with live countdown timers
- `CreateTask` UI with custom `CustomDatePicker` class
- `CalendarUi` — displays current day and monthly task layout
- `UpdateTaskUi` — full task editing with overlaying window system
- Live clock on main dashboard
- Plugin button placeholder added to home screen
- Error messages now replace previous ones instead of stacking
- Shadows on button hover states

---

## Dev Notes

### Origins
> *"A to-do list flyout — a Windows flyout (or app) that will add a to-do list using Java. This project should give you an idea of how to make things work."*

What started as a simple flyout experiment grew into a full desktop app across several months of iteration. Name history: **To-Do → PlanForge → DockTask**.

### Known Issues (Linux)
- AppTray icon may not reappear after minimizing in some distros
- Window size slightly altered due to platform rendering differences
- Addressed in v0.6.0 via `WindowFactory` and border removal toggle

### Memory Optimization (v0.3.0)
Identified a `Timeline` thread leak where countdown timers for removed or completed tasks continued running in the background. Fixed by pausing all Timeline threads on minimize/close and clearing expired entries from the active monitor list. Result: idle memory reduced from ~500MB peak to ~60MB.

### Plugin System Design (v0.8.0)
The plugin architecture uses a **sandbox pattern**. Rather than giving plugins direct access to `UserData`, `FontLoader`, or `themeManager`, all interaction goes through `PluginContext` — an interface implemented by `PluginContextImpl` inside the core app. This means:

- **Plugins cannot corrupt core data files** (`Task.txt`, `Settings.txt`) — they only write to their own namespace in `PluginState.txt`
- **Plugins automatically get the right font** via `context.getFont(size)` without importing or knowing about `FontLoader`
- **Plugins automatically respect the active theme** by using `-dt-*` CSS variables in their own CSS file — no conditional logic needed
- **Future-proofing:** The `PluginContext` interface can be extended without breaking existing plugins, and a JAR-based loader can be added later to allow community plugins without recompiling the core app

The listener pattern in `PluginManager` (`addWidgetVisibilityListener`) mirrors the same approach used by `themeManager` — keeping coupling between UI layers minimal and predictable.

For a full developer walkthrough, see [PluginGuide.md](PluginGuide.md).

---