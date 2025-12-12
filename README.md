# JetFighter

Implementation of 1975 Atari game "JetFighter" in Kotlin with JavaFX

## Requirements

- Java version: 15

## Run

### Run with gradle run task

```shell
./gradle run   # Linux, Mac
./gradlew run  # Windows
```

### Build jar file and execute it

```shell
./gradle jar   # Linux, Mac
./gradlew jar  # Windows
java -jar ./build/libs/*.jar
```

## Controls

### General

| Key            | Action            |
| -------------- | ----------------- |
| <kbd>R</kbd>   | Restart game      |
| <kbd>Esc</kbd> | Pause/resume game |

### Black Jet

| Key               | Action       |
| ----------------- | ------------ |
| <kbd>A     </kbd> | Rotate left  |
| <kbd>D     </kbd> | Rotate right |
| <kbd>Space </kbd> | Shoot        |

### White Jet

| Key                    | Action       |
| ---------------------- | ------------ |
| <kbd>Left Arrow </kbd> | Rotate left  |
| <kbd>Right Arrow</kbd> | Rotate right |
| <kbd>Enter      </kbd> | Shoot        |
