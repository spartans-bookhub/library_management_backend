# Library Management System (Backend)

A Spring Boot–based backend application to automate and streamline core library operations such as book cataloging, member registration, borrowing and returning of books, fine calculation, and reporting.

---

## Tech Stack

- **Language:** Java 17+
- **Framework:** Spring Boot
- **Build Tool:** Maven
- **Database:** MySQL
- **ORM:** Spring Data JPA
- **Security:** Spring Security + JWT
- **Testing:** JUnit 5, Mockito
- **API Docs:** Swagger (springdoc-openapi)
- **IDE:** IntelliJ IDEA
  
---

**Testing Strategy**
Unit Testing:
Tools: JUnit, Mockito

**Objective**:
Develop a robust, scalable, and user-friendly to manage books, users, transactions, and notifications.

**Key Features**:

Book Management: CRUD operations for books.
User Management: Registration, authentication, and role-based access.
Transaction Management: Issue, return, renewal/reborrow, and fine calculation.
Search & Discovery: Advanced book search and recommendation engine.
Notifications: Overdue alerts, due date reminders, and system updates.

**System Architecture**

High-Level Components:
Frontend: React.js for dynamic user interfaces.
Backend: Spring Boot for RESTful API services.
Database: MySQL for relational data storage.
Authentication: JWT for secure access.
Notification Service: Push Notifications.
API Documentation: Swagger UI for interactive API exploration.


**API Endpoints**

Book Management:
GET /api/books: Retrieve all books.
GET /api/books/{id}: Retrieve a book by ID.
POST /api/books: Add a new book.
PUT /api/books/{id}: Update an existing book.
DELETE /api/books/{id}: Delete a book.

User Management:
POST /api/users/register: Register a new user.
POST /api/users/login: Authenticate a user.
GET /api/users/{id}: Retrieve user details.

Transaction Management:
POST /api/transactions/issue: Issue a book to a user.
POST /api/transactions/return: Return a book.
GET /api/transactions/{id}: Retrieve transaction details.

Search & Discovery:
GET /api/search: Search books by title, author, or category.

Notifications:
GET /api/notifications: Retrieve user notifications.

**User Stories & Use Cases**
User Stories:

As a User, I want to search for books by title or author so that I can find books of interest.
As a Admin, I want to add new books to the catalog so that users have access to the latest resources.
As a User, I want to issue books so that I can borrow them for reading.
As a User, I want to return books so that I can borrow more books.
As a User, I want to receive notifications about overdue books so that I can avoid fines

Security & Compliance
**Authentication & Authorization:**

JWT Tokens: For stateless authentication.
Role-Based Access Control (RBAC): To enforce user permissions.

**Data Protection:**

Encryption: Use of HTTPS for data in transit.
Hashing: Passwords stored using bcrypt hashing algorithm.
