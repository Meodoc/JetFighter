# JetFighter

Implementation of 1975 Atari game "JetFighter" in JavaFX

## Build
Build with gradle using
```shell
./gradlew jar
```

The build .jar will be located in `./build/libs/`

## Run
### Run with gradle run task
```shell
./gradlew run
```

### Build jar file and execute it
```shell
./gradlew jar
java -jar ./build/libs/JetFighter-1.0-SNAPSHOT.jar
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
