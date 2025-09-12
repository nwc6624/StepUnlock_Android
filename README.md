# StepUnlock

**Earn screen time on distracting apps by completing positive actions.**

StepUnlock is an Android app that helps you reduce doomscrolling by turning access to selected apps into a reward you earn through healthy habits like walking, focusing, staying hydrated, and journaling.

## Features

### Core Mechanics
- **App Locking**: Select which apps or app categories to lock
- **Credit System**: Earn credits by completing positive actions
- **Timed Unlocks**: Spend credits to unlock apps for limited minutes
- **Auto-Relock**: Apps automatically lock when time expires

### Supported Habits
- **Steps**: Earn 2 credits per 1000 steps (Google Fit integration)
- **Focus Sessions**: Earn 6 credits per 25-minute Pomodoro session
- **Water**: Earn 1 credit per 8oz glass (daily cap: 8)
- **Journaling**: Earn 3 credits per 5-minute active typing session

### Privacy-First Design
- All data stored locally on device
- No account required
- No cloud sync
- No remote analytics

## Architecture

### Tech Stack
- **Kotlin** with **Jetpack Compose** (Material 3)
- **MVVM** + Repository pattern
- **Hilt** for Dependency Injection
- **Room** for local database
- **Coroutines** + Flow for async operations
- **WorkManager** for background tasks

### Project Structure
```
app/                    # Main application module
├── ui/
│   ├── screens/        # Screen composables
│   ├── navigation/     # Navigation setup
│   └── theme/         # Material 3 theming
├── services/          # Background services
└── receivers/         # Broadcast receivers

core/                  # Core utilities
├── time/             # Time utilities
├── permissions/      # Permission helpers
└── di/              # Core DI modules

data/                 # Data layer
├── local/           # Room database & entities
├── fitness/         # Google Fit integration
├── repository/      # Repository implementations
└── mapper/          # Entity-Domain mappers

domain/              # Domain layer
├── model/           # Domain models
├── repository/      # Repository interfaces
└── usecase/         # Business logic use cases
```

## Permissions Required

- **Usage Access**: Detect foreground app and show lock screen
- **System Alert Window**: Display overlay for app blocking
- **Foreground Service**: Run unlock timers in background
- **Activity Recognition** (Optional): Google Fit step tracking

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 29+ (targeting API 34)
- Kotlin 1.9.20+

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device or emulator

### Initial Setup
1. **Onboarding**: Learn about the app and privacy approach
2. **Permissions**: Grant Usage Access and System Alert Window
3. **App Selection**: Choose which apps to lock
4. **Habit Configuration**: Enable and configure earning habits

## Usage

### Earning Credits
- **Steps**: Connect Google Fit or manually log steps
- **Focus**: Start a 25-minute Pomodoro timer
- **Water**: Tap to log 8oz glasses throughout the day
- **Journal**: Write for at least 5 minutes in the app

### Spending Credits
- Open a locked app → Lock screen appears
- Select unlock duration (5, 15, 30, or 60 minutes)
- Spend credits to unlock
- App automatically locks when time expires

### Daily Goals & Streaks
- Set daily goals for each habit
- Maintain streaks for bonus multipliers
- Track progress with visual indicators

## Development

### Building
```bash
./gradlew assembleDebug
```

### Testing
```bash
./gradlew test
./gradlew connectedAndroidTest
```

### Code Style
- Follow Kotlin coding conventions
- Use Compose best practices
- Maintain MVVM architecture
- Write unit tests for use cases

## Roadmap

### MVP Features (Current)
- [x] Basic app locking mechanism
- [x] Credit earning through habits
- [x] Timed app unlocks
- [x] Privacy-first local storage

### Post-MVP Features
- [ ] Widgets for quick actions
- [ ] App categories (e.g., "All Social")
- [ ] Custom habit creation
- [ ] Data export/backup
- [ ] Themes and customization
- [ ] Pro features with advanced blocking

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Privacy Policy

StepUnlock is designed with privacy as a core principle:
- All data remains on your device
- No personal information is collected
- No analytics or tracking
- No cloud synchronization
- You control your data completely

## Support

For issues, feature requests, or questions:
1. Check existing GitHub issues
2. Create a new issue with detailed description
3. Provide device information and steps to reproduce

---

**Note**: This app requires Android 10+ (API 29) and is designed to work reliably across different OEM implementations of Android.
