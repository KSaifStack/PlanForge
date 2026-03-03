## v0.6.0 
*Feburary 2026*

This release focuses on architectural refinement, cross-platform compatibility, performance stabilization, and UI polish. It establishes DockTask as a stable multi-platform desktop application.

### Cross-Platform Expansion
- Added initial **Linux support**
- Added initial **macOS support**
- Implemented platform-aware window border factory for native-style switching
- Replaced legacy AWT tray icon handling with improved system tray integration
- Resolved hardcoded file path issues for improved portability
- Automatic data folder creation on first launch (if missing)

### Framework & Dependency Upgrades
- Upgraded **JavaFX 21 → 25**
- Updated NFX core library to `1.0.5`
- Simplified build process — no additional build instructions required
- Improved compatibility across Maven-supported IDEs

### UI Refactoring & Codebase Improvements
- Refactored Login UI for improved validation and cleaner logic flow
- Refactored Task UI for better separation of concerns
- Significant internal code documentation added for maintainability
- Improved layout alignment and centering in Settings panel
- Added overlay shadows for improved depth and visual clarity
- Fixed layout overflow when displaying more than four task cards
- Replaced system fonts with **Lexend** for consistent typography
- Updated main menu font styling
- Enhanced “No Task Found” onboarding message

### Performance & Stability
- Fixed logout session data bleed issue
- Fixed clock alignment bug when under 21 characters
- Stabilized window sizing inconsistencies on Linux
- Improved overdue task date formatting logic:
    - Tasks overdue by more than 168 hours now display in `MM/DD/YYYY` format

### Visual Enhancements
- Added four new color themes:
    - Dark Slate Gray
    - Chocolate
    - Light Sea Green
    - Gold
- Improved pane centering and spacing
- Cleaner task-pane spacing and reduced UI congestion