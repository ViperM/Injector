## Injector - the easy way to inject your variables

### Overview

Injector is a simple graphical tool, targeted to make easier the process of working with many script/shell commands by
providing the possibility of variable injection and reusage.

### How to use?

1. Open the folder which contains template scripts with variable placeholders
2. Select the file in the left-side panel
3. Lines with all matching variable patterns will be marked by the play button icon at the left margin
4. Click the play button near the selected line to enter the variable edition mode
5. Provide in the table the new value for each variable placeholder or select from one of previously used values -
   observe the final result in the preview box
6. Copy the final command with injected variable values to the clipboard

### Features

* Auto-saving of entered variables (configurable)
* Several predefined variable patterns
* Supported extensions: txt, sh, bat, cmd, ps1
* Zoom in/Zoom out

### JAR building

`mvn clean install`

A fat-jar with all dependencies will be created in the
`target/fatJar` folder

### Redistributable package building

`mvn cleain install -Predistributable`

Executable files will be located in the
`target/redistributable/Injector` folder
