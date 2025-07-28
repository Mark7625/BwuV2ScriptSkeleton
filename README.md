# BwuV2 Script Skeleton

A comprehensive example repository demonstrating the **PermissiveScript**, official framework for BotWithUs (BWU) scripting, showcasing multiple scripts in a single repository with shared custom API components.

## 🚀 Getting Started

### Cloning and Setting Up Your Own Repository

1. **Create a new repository on GitHub**
   - Go to GitHub and create a new repository
   - Don't initialize with README, .gitignore, or license (we'll copy everything from this repo)

2. **Clone this repository locally**
   ```bash
   git clone https://github.com/YOUR_USERNAME/BwuV2ScriptSkeleton.git
   cd BwuV2ScriptSkeleton
   ```

3. **Remove the existing git history and initialize your own**
   ```bash
   # Remove existing git history
   rm -rf .git
   
   # Initialize new git repository
   git init
   git add .
   git commit -m "Initial commit: BWU script skeleton"
   
   # Connect to your new GitHub repository
   git remote add origin https://github.com/YOUR_USERNAME/YOUR_NEW_REPO_NAME.git
   git branch -M main
   git push -u origin main
   ```

4. **Build the project**
   ```bash
   ./gradlew build
   ```

## 📁 Project Structure

This repository demonstrates a **multi-script architecture** with shared components:

```
BwuV2ScriptSkeleton/
├── CustomAPI/          # Shared utility classes across all scripts
├── FlaxPicker/         # PermissiveScript implementation example
├── CowKiller/          # Traditional (non-PermissiveScript) implementation
├── ScriptSkeleton/     # Basic Java/Kotlin script templates
└── build.gradle.kts    # Multi-project Gradle configuration
```

### 🛠️ CustomAPI Package

The `CustomAPI` package provides shared utilities used across all scripts:

- **`TickBasedScript`**: Base class for traditional tick-based scripting with delay management.
- **`Walker`**: Bresenham pathfinding utilities for efficient movement. (Deprecated - Use `Traverse` in `xapi` package)
- **`GameAreas`**: Predefined game area constants and coordinates.
- **`BaseTab`**: ImGui-based UI components for script interfaces.

This demonstrates how you can create reusable components that all your scripts can inherit from, reducing code duplication and improving maintainability.

## 🌾 FlaxPicker Script

The **FlaxPicker** is a complete example of **PermissiveScript** implementation, demonstrating the tree-based task management system.

### Features
- **Two-state operation**: Flax picking and banking
- **Area-based logic**: Taverly flax field and bank
- **Tree-based decision making**: Uses branches and leaf nodes for complex logic
- **Robust error handling**: Built-in validation and failure paths

### State Structure

#### FlaxPickState
- **Backpack Check**: Determines if inventory is full
- **Flax Detection**: Locates flax objects in the designated area  
- **Action Execution**: Picks flax with proper animation delays
- **Area Traversal**: Navigates to flax picking location when needed

#### BankState  
- **Bank Proximity**: Checks if player is near banking area
- **Bank Interface**: Opens bank interface when needed
- **Item Deposit**: Deposits all items and returns to picking
- **Navigation**: Handles movement to banking location

### Code Example
```java
// Branch with multiple conditions using Interlock
shouldPickFlax = new Branch(script, "shouldPickFlax", new Interlock("isFlaxObjNearby",
    new Permissive("flaxObjExists", () -> {
        flaxObj = SceneObjectQuery.newQuery().name("Flax").inside(script.flaxArea).results().nearest();
        return flaxObj != null;
    }),
    new Permissive("playerNotAnimating", () -> script.player.getAnimationId() == -1),
    new Permissive("flaxNearby", () -> Distance.to(script.flaxArea) < INTERACT_DISTANCE)
));
```

## 🐄 CowKiller Script

The **CowKiller** demonstrates a **traditional (non-PermissiveScript)** approach, extending the custom `TickBasedScript` class.

### Features
- **Simple tick-based logic**: Traditional onTick() method implementation
- **Banking integration**: Uses preset loading for efficient banking
- **Target acquisition**: Finds and attacks nearest valid cows
- **Area management**: Uses shared GameAreas from CustomAPI

This script showcases how you can still create effective bots without using the PermissiveScript framework, useful for simpler scripts or when you prefer traditional control flow.

## 📚 PermissiveScript Framework

**PermissiveScript** is the officially supported BWU scripting structure for Java, providing a powerful tree-based task management system for creating complex bot scripts with conditional logic and state management.

### Core Components

#### 1. Tree Structure
The framework uses a tree-based architecture with these key components:

**TreeNode**
- Base abstract class for all nodes in the tree
- Provides core functionality for tree traversal
- Contains description and validation tracking
- Key methods:
  - `execute()`: Performs the node's action
  - `validate()`: Determines if the node should proceed to success or failure
  - `successNode()`: Returns the next node on success
  - `failureNode()`: Returns the next node on failure
  - `traverse()`: Handles tree navigation logic

**Branch**
- Non-leaf node that manages decision logic
- Contains success and failure paths
- Uses Interlocks to determine which path to take
- Can have dynamic node resolution through Callable interfaces
- If any Interlock is true, the Branch will be traversed via its success path. If all interlocks are false, the failure path is traversed.

**LeafNode**
- Terminal node that performs actual actions
- Contains executable logic through Callable or Runnable
- No child nodes

#### 2. Conditional System

**Permissive**
- Basic conditional unit containing:
  - Name: Identifier for the condition
  - Predicate: Supplier<Boolean> that evaluates the condition
  - Result tracking through EvaluationResult

**Interlock**
- Groups multiple Permissive conditions
- All Permissives must be true for the Interlock to be active
- Tracks which condition failed first
- Used by Branch nodes for decision making

#### 3. Interactive Components

**InteractiveLeaf**
- Special LeafNode for handling game interactions
- Supports:
  - Target selection
  - Option text/index based interactions
  - Success action callbacks

### Implementation Pattern

```java
@Info(name = "Example Script", description = "PermissiveScript Example", author = "Developer", version = "1.0")
public class ExampleScript extends PermissiveScript {
  
    @Override
    public boolean onPreTick() {
         // Perform lookups preparing for the next logic loop

        // Pre-tick validation. If false, the loop logic will not execute this tick.
        return super.onPreTick() && someCondition();
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        initStates(
            new StateOne(this, BotState.STATE_ONE.getDescription()),
            new StateTwo(this, BotState.STATE_TWO.getDescription()),
            ...
        );
    }
}
```

### Best Practices

1. **State Organization**
   - Create separate state classes for different bot activities
   - Use descriptive names for nodes and conditions
   - Keep conditions atomic and focused

2. **Node Structure**
   - Use Branches for decision points
   - Use LeafNodes for actual actions
   - Use InteractiveLeaf for game interactions

3. **Condition Management**
   - Group related conditions in Interlocks
   - Use meaningful names for Permissive conditions
   - Keep conditions simple and focused

4. **Error Handling**
   - Implement proper error handling in conditions
   - Use failure paths appropriately
   - Track and log condition results

## ❓ Frequently Asked Questions

### Q: What is PermissiveScript?
**A:** PermissiveScript is the officially supported BWU scripting framework for Java. It provides a tree-based task management system that allows for complex conditional logic and state management through nodes, branches, and interlocks.

### Q: Do I have to use PermissiveScript?
**A:** No, PermissiveScript is recommended but not required. You can create effective scripts using traditional approaches like the `TickBasedScript` shown in the CowKiller example. Or alternatively, use Kotlin and handle your delays via Coroutines.

### Q: How do I add a new script to this repository?
**A:** 
1. Create a new directory under the root (e.g., `MyNewScript/`)
2. Add a `build.gradle.kts` file (copy from existing scripts)
3. Create the standard Maven directory structure: `src/main/java/`
4. Add your script class and any additional classes
5. Update `settings.gradle.kts` to include your new project
6. Ensure any new packages are added to your new project's `module-info.java`

### Q: What's the advantage of multi-script repositories?
**A:** 
- **Shared code**: Common utilities in CustomAPI reduce duplication
- **Consistent build process**: Single Gradle configuration for all scripts
- **Easier maintenance**: Update shared components once, benefit all scripts
- **Better organization**: Related scripts grouped together

### Q: How do I customize the CustomAPI for my needs?
**A:** The CustomAPI is designed to be extended and provide an example for your own api implementations.
If you find that what you're wanting is not covered by the BotWithUs API or Extended API, then the CustomAPI is where you would add your new additions.

You can:
- Add new utility classes to appropriate packages
- Extend `TickBasedScript` for custom base functionality
- Add new areas to `GameAreas` for your scripts
- Create additional UI components extending `BaseTab`

## 🔧 Build Configuration

The project uses a sophisticated Gradle build system that:
- Auto-detects Kotlin files and applies appropriate plugins
- Manages dependencies for all subprojects
- Includes CustomAPI dependency automatically (except for CustomAPI itself)
- Copies built JARs to BWU scripts directory
- Handles Java 22 module system compatibility

## 📄 License

This project is licensed under the terms specified in the LICENSE file.

## 🤝 Contributing

Feel free to fork this repository and customize it for your own BWU scripting projects. This skeleton provides a solid foundation for both simple and complex bot development. 