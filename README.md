# Remote Desktop Control in Java


*Being a beginner in Java, I have referred this article : [RemoteDesktopControlJava](https://www.codeproject.com/Articles/36065/Java-Remote-Desktop-Administration). I learned a lot during the development*


## Brief description of project:

- This application works similar to TeamViewer but only for a LAN
- Server PC can control many Client PCs


## Functionalities of project:

- Server PC can view desktop of client PCs
- Server PC can control client PCs with server's mouse and keyboard


## Tools and technologies used:

- Java - Programming language
- Java Swing - Java GUI library
- Java Robot - Java class for functions related to mouse and keyboards events
- Socket programming - Java has built-in classes for this
- Sublime Text Editor - for writing code
- Git - for version control


## How to use?

- Connect two/more laptops/PCs in same network
- Start server program on one PC/laptop. Use port as 50500
- Start client program on other PCs/laptop. Enter LAN IP of server PC (obtained by ipconfig/ifconfig on server PC for Windows/Linux) and port as 50500
- Once connected properly, you can view and control client PC from server PC
