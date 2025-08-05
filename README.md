# BWU2VScriptsExample

A collection of BotWithUs RuneScape 3 scripts demonstrating various automation techniques and best practices.

## Project Structure

This is a multi-project Gradle workspace containing several example scripts:

### Scripts

#### ChickenKiller
A comprehensive chicken killing and looting script featuring:
- **Combat System**: Automated chicken targeting and attacking
- **Looting System**: Picks up feathers and bones from the ground
- **Banking System**: Automatic banking when inventory is full, health restoration
- **Movement System**: Navigation between chicken area and bank
- **GUI Interface**: Simple checkbox to toggle banking on/off

**Features:**
- Refactored modular architecture with separate classes for different responsibilities
- Real-time delay management to prevent spam clicking
- Health monitoring and automatic healing at bank
- Area-based filtering for efficient item collection
- Robust error handling and logging

#### FlaxPicker
A flax picking automation script (structure present, implementation may vary).

#### ScriptSkeleton
Template projects for creating new scripts:
- **Java version**: Basic script template in Java
- **Kotlin version**: Basic script template in Kotlin

## Getting Started

### Prerequisites
- Java 11 or higher
- BotWithUs client and API
- Gradle (wrapper included)

### Building

Build all projects:
```bash
./gradlew build
```

Build specific project:
```bash
./gradlew :ChickenKiller:build
./gradlew :FlaxPicker:build
./gradlew :ScriptSkeleton:build
```

### Running Scripts

1. Build the project you want to use
2. Copy the generated JAR from `{ProjectName}/build/libs/` to your BotWithUs scripts folder
3. Load the script in the BotWithUs client

## Development

### Code Organization

Each script follows a modular architecture pattern:

```
src/main/java/botwithus/
├── ScriptName.java          # Main script class
├── areas/                   # Game area definitions
├── banking/                 # Banking and inventory management
├── combat/                  # Combat and NPC interaction
├── gui/                     # User interface components
├── loot/                    # Ground item collection
└── movement/                # Player movement and navigation
```

### Key Components

- **Banking**: Handles inventory management, banking, and health restoration
- **Combat**: Manages NPC targeting, attacking, and combat delays
- **Looting**: Ground item detection, filtering, and pickup with anti-spam delays
- **Movement**: Area navigation and readiness checking
- **GUI**: Simple ImGui-based user interfaces

### Best Practices Demonstrated

1. **Modular Design**: Separation of concerns with dedicated classes
2. **Delay Management**: Realistic timing to prevent detection
3. **Error Handling**: Comprehensive exception catching and logging
4. **State Management**: Proper script lifecycle handling
5. **User Interface**: Clean, functional GUIs for script control

## Configuration

### Game Areas

Areas are defined in `areas/GameAreas.java` with coordinate boundaries:
```java
public static final Area CHICKEN_AREA = new Area.Rectangular(
    new Coordinate(2881, 3478, 0), 
    new Coordinate(2889, 3475, 0)
);
```

### Delays and Timing

Configurable delays in script classes:
- Combat delays: 5000ms between attacks
- Looting delays: 1200ms between pickups
- Movement delays: Built into area checking

## Contributing

1. Fork the repository
2. Create a feature branch
3. Follow the established code organization patterns
4. Test thoroughly with the BotWithUs client
5. Submit a pull request

## License

This project is licensed under the terms specified in the LICENSE file.

## Disclaimer

These scripts are for educational purposes. Use responsibly and in accordance with game terms of service.
