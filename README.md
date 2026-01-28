# Messenger Backend

## Overview

This repository contains the **backend** implementation for **Messenger**, a real-time cross-platform mobile chat application. The backend handles user authentication, profile management, private and group messaging, real-time communication, and data persistence.

The frontend is a separate React Native repository. This backend provides REST APIs (via Java Servlets) for non-real-time operations and a WebSocket endpoint for instant message delivery and status updates.

## Key Features (Backend-Supported)

- User registration and mobile number verification
- Profile management (name, image updates)
- Real-time private and group chats with message status (SENT, DELIVERED, READ)
- Group creation, member management, and admin controls
- Chat locking and contact blocking
- Online/offline status tracking
- Search and contact management

## Technology Stack

- **Language**: Java
- **Framework**: Java Servlets (for REST APIs)
- **ORM**: Hibernate
- **Database**: MySQL
- **Real-time Communication**: WebSocket (javax.websocket API)
- **Server**: Apache Tomcat (recommended for deployment)

## Project Structure (Key Packages)

- **controllers/**: Servlet controllers handling HTTP requests  
  (e.g., `UserController`, `UpdateProfileController`, `GroupCreationController`, `ChatLockController`, `ContactBlockController`)
- **services/**: Business logic  
  (e.g., `UserService`, `ChatService`, `GroupService`)
- **entities/**: Hibernate entity classes  
  (e.g., `User`, `Chat`, `Group`, `GroupUsers`, `GroupChat`, `ContactList`, `BaseEntity`, `StatusEnum`)
- **websocket/**: `ChatEndpoint` for real-time messaging and events
- **config/**: Hibernate configuration (`hibernate.cfg.xml`)

## Prerequisites

- Java JDK 8 or higher
- Apache Tomcat 9 or higher
- MySQL Server 8.0+
- IDE (Eclipse/IntelliJ IDEA recommended for Java web projects)

## Setup Instructions

1. **Clone the repository**
   ```
   git clone https://github.com/ChethiyaAbhishek/Messenger_Backend.git
   cd messenger-backend
   ```

2. **Database Setup**
   - Create a MySQL database (e.g., `messenger_db`).
   - Update `hibernate.cfg.xml` with your database credentials:
     ```xml
     <property name="connection.url">jdbc:mysql://localhost:3306/messenger_db</property>
     <property name="connection.username">your_username</property>
     <property name="connection.password">your_password</property>
     ```
   - Hibernate will auto-generate tables on first run (using `<property name="hbm2ddl.auto">update</property>` or similar).

3. **Import and Deploy**
   - Import the project into your IDE as a Java Web Project.
   - Add the project to your Tomcat server.
   - Start Tomcat.

4. **Server URL**
   - Base URL: `http://localhost:8080/[project-context-path]/`
   - WebSocket endpoint: `ws://localhost:8080/[project-context-path]/ChatEndpoint`

## Running the Application

- Start Tomcat from your IDE or command line.
- The backend will be accessible for the React Native frontend to connect via REST APIs and WebSocket.
- Test WebSocket connection using tools like wscat or the frontend app.

## Database Design

The database uses MySQL with the following main entities (mapped via Hibernate):

- `User`
- `Chat` (private messages)
- `Group`
- `GroupUsers`
- `GroupChat` (group messages)
- `ContactList`
- `GroupMsgsStatus` / message status tracking

Refer to the ER Diagram in the project documentation for relationships.

## Security Measures

- Mobile number-based authentication
- Hibernate handles SQL injection prevention
- Session management via WebSocket connections
- Biometric chat lock is handled client-side (frontend)

## Notes

This backend was developed as part of a Handheld Device Programming I (React Native) course project at Java Institute for Advanced Technology.

**Author**: Edirimuni Chethiya Abhishek De Silva  
**Project**: Messenger Chat Application

For full project documentation, see the attached PDF in the main project resources.

## Future Enhancements

- Add JWT or OAuth for enhanced authentication
- Message encryption
- File/media sharing
- Push notifications
