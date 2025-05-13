# InterCom - Building Intercom System

A lightweight, Java-based peer-to-peer (P2P) messaging application designed for use within a building's local network (LAN), functioning as an intercom system. It mimics Telegram's basic functionality—without voice/video calls—focusing on secure, local text communication between devices on the same network.

## Features

- **User Account Management**
  - Create account (username + ID)
  - Switch account
  - Logout
  - Users stored in XML format
  - Uses XPath for querying users

- **Peer-to-Peer Messaging (P2P)**
  - Direct LAN messaging using Java Sockets
  - Real-time text messaging
  - No internet required

- **XML-Based Storage**
  - `users.xml` for user info (ID, username, IP)
  - `messages.xml` for storing chat logs (optional)
  - XSLT support for converting `users.xml` into HTML

## Requirements

- Java 11 or higher
- Maven 3.6 or higher
- Apache ActiveMQ (optional, for JMS messaging)

## Building the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/intercom.git
   cd intercom
   ```

2. Build with Maven:
   ```bash
   mvn clean package
   ```

3. Run the application:
   ```bash
   java -jar target/telegram-clone-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Project Structure

```
├── src/
│   ├── main/
│   │   ├── App.java                 # Entry point
│   │   ├── LoginScreen.java        # Login/account switch UI
│   │   ├── ChatScreen.java         # Main chat window
│   │   └── FileTransferHandler.java# (Legacy) File sending/receiving logic (not used)
│   ├── net/
│   │   ├── Server.java             # Listens for incoming messages
│   │   └── Client.java             # Sends messages
│   └── storage/
│       ├── UserManager.java        # XML-based user management
│       └── XPathUtils.java         # XPath helpers for XML
│
├── data/
│   ├── users.xml                   # Stores user info
│   └── messages.xml               # (Optional) chat logs
│
├── xsl/
│   └── users-to-html.xsl          # Optional XSL for XML-to-HTML
│
└── README.md
```

## Usage

1. Start the application
2. Create a new account or login with existing credentials
3. Select a user from the list to start chatting
4. Messages are delivered in real-time over the local network

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Inspired by Telegram's user interface
- Built with Java Swing for the GUI
- Uses Java Sockets for P2P communication

## How to Reopen/Start ActiveMQ

1. Open Command Prompt (Windows) or Terminal (Linux/Mac).
2. Navigate to your ActiveMQ installation directory, for example:
   ```
   cd C:\apache-activemq\bin
   ```
3. Start ActiveMQ with:
   - On Linux/Mac:
     ```
     ./activemq start
     ```
   - On Windows:
     ```
     activemq.bat start
     ```
4. Access the admin console at:
   [http://localhost:8161/admin](http://localhost:8161/admin)
   - Default username/password: `admin` / `admin`