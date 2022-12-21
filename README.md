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

`mvn clean install -Predistributable`

Executable files will be located in the
`target/redistributable/Injector` folder

### Download binaries

TODO

<table>
<tr>
  <td align="center">
        <b>
          <h1>
            <img style="margin-right: 10px" src="docs/img/windows.png" alt="">
            Windows
          </h1>
        </b>
        <p><b>(64-bit)</b></p>
  </td>
  <td align="center"> 
      <b>
          <h1>
            <img style="margin-right: 10px" src="docs/img/linux.png" alt="">
            Linux
          </h1>
        </b>
      <p><b>(64-bit)</b></p>
  </td>

  <td align="center">
        <b>
          <h1>
            <img style="margin-right: 10px" src="docs/img/mac.png" alt="">
            MacOS 
          </h1>
        </b>
        <p><b>(64-bit)</b></p>
  </td>
</tr>
<tr>
  <td>
<b>
  <ul>
      <li>
        <h4>
           <a href="https://github.com/ViperM/Injector/releases/download/v1.0/Injector.zip">Portable (.zip)</a>
        </h4>
      </li>
  </ul>
</b>
  </td>
  <td>
<b>
   <ul>
        <li>
          <h4>
            <a href="https://github.com/ViperM/Injector/releases/download/v1.0/injector_1.0_amd64.deb">Debian Software Package (.deb)</a>
          </h4>
        </li>
   </ul>
</b>
  </td>
  <td>
  <b>
   <ul>
        <li>
          <h4>
            <a href="https://github.com/ViperM/Injector/releases/download/v1.0/Injector-1.0.dmg">MAC Disk Image File (.dmg)</a>
          </h4>
        </li>
   </ul>
</b>
  </td>
</tr>
</table>

### Screenshots

![Screenshot](docs/img/screenshots/injector-main.jpg)
![Screenshot](docs/img/screenshots/injector-variables.jpg)
![Screenshot](docs/img/screenshots/injector-settings.jpg)
