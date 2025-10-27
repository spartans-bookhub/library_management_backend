#  Library Management System (Backend)

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
# API Description 
This API provides access to real-time Library ,allowing user to Borrow various Books of their choice.

# Clone the Repository
git clone https://github.com/spartans-bookhub/library_management_backend.git
cd library-management-backend


Port - http://localhost:9009

Auth API
POST  /login
POST  /register
GET   /healthcheck
POST  /api/password
POST  /forgot-password
POST  /password-reset



Books API
POST   /api/books/
GET   /api/books/list
GET   /api/books/{id}
PUT   /api/books/{id}
DELETE /api/books/{id}
GET    /api/books/search?keyword={keyword}


Cart API 
GET    /api/cart
POST   /api/cart/add/{bookId}
DELETE /api/cart/remove/{bookId}


Transaction API

POST	/api/v1/transactions/book/{bookId}/borrow
POST	/api/v1/transactions/books/borrow
POST	/api/v1/transactions/book/{bookId}/return
GET	/api/v1/transactions/borrowed
GET	/api/v1/transactions/overdue
GET	/api/v1/transactions/history
GET	/api/v1/transactions/can-borrow
GET	/api/v1/transactions
GET	/api/v1/transactions/status?status={status}
GET	/api/v1/transactions/high-fines?fineThreshold={fineThreshold}
GET	/api/v1/transactions/late-users?lateThreshold={lateThreshold}
GET	/api/v1/transactions/books/available
GET	/api/v1/transactions/books/{bookId}/availability
PUT	/api/v1/transactions/books/{bookId}/inventory?quantityChange={value}
PUT	/api/v1/transactions/books/{bookId}/availability?availabilityStatus={status}
GET	/api/v1/transactions/books/low-stock?threshold={value}



User API 

GET	      /api/user/{id}
PUT	     /api/user/{id}
GET	    /api/user
DELETE	/api/user/{id}










    
