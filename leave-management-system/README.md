# TeamHub – Leave Management System

A beginner-friendly leave management web application built with **Java 21, Spring Boot, Spring Security, Spring Data JPA, Thymeleaf, and MySQL**.

## Features

### Employee
- Secure employee login.
- Dashboard showing annual, sick, and casual leave balances.
- Apply for leave with type, start date, end date, and reason.
- View complete leave history and request status.

### Manager
- Separate manager login and protected approval dashboard.
- View pending and all employee leave requests.
- Approve or reject requests with an optional comment.
- Approved leave days are deducted from the employee's balance.

### Validation / business rules
- Start date cannot be in the past.
- End date cannot be earlier than the start date.
- Leave reason is required (maximum 500 characters).
- Insufficient leave balance blocks an application.
- Overlapping PENDING or APPROVED requests are blocked.
- Only pending requests can be approved or rejected.
- Balance is deducted only when a manager approves a request.
- Leave days are counted as calendar days, inclusive of both dates.

## Requirements
- JDK 21
- Apache Maven 3.9+
- MySQL 8+
- VS Code or IntelliJ IDEA (optional)

## 1. Create the database

Start MySQL and run:

```sql
CREATE DATABASE leave_management;
```

The application also uses `createDatabaseIfNotExist=true`, but the MySQL user must have permission to create databases.

## 2. Configure MySQL username and password

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.username=root
spring.datasource.password=root
```

Change `root` to your own MySQL password. Alternatively, set environment variables:

**PowerShell**
```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
```

## 3. Run the project

Open a terminal in the folder containing `pom.xml` and execute:

```bash
mvn spring-boot:run
```

Or build a runnable JAR:

```bash
mvn clean package
java -jar target/leave-management-system-1.0.0.jar
```

Open: http://localhost:8080

On the first successful startup, Hibernate creates the tables and the app inserts demo accounts and balances.

## Demo login accounts

| Role | Username | Password |
|---|---|---|
| Manager | `manager` | `Manager@123` |
| Employee | `employee` | `Employee@123` |
| Employee | `jacob` | `Employee@123` |

Demo employee leave balances:
- Annual: 12 days
- Sick: 6 days
- Casual: 3 days

**Important:** These are development/demo credentials. Change them and add proper account administration before deploying this project publicly.

## Database tables

- `employees` – employee profile, login username, encoded password, department, role.
- `leave_balances` – employee leave type, total days, used days.
- `leave_requests` – dates, leave type, reason, number of days, status, manager comment.

## Main URLs

| URL | Access | Purpose |
|---|---|---|
| `/login` | Public | Login page |
| `/employee/dashboard` | Employee | Employee dashboard |
| `/employee/apply` | Employee | Apply for leave |
| `/employee/history` | Employee | Leave history |
| `/manager/dashboard` | Manager | Approve/reject requests |

## Project structure

```text
leave-management-system/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/com/example/leavemanagement/
        │   ├── config/       # Security and demo data
        │   ├── controller/   # Web routes
        │   ├── model/        # JPA entities and enums
        │   ├── repository/   # Database repositories
        │   └── service/      # Leave business rules
        └── resources/
            ├── static/css/   # Responsive styles
            ├── templates/    # Thymeleaf pages
            └── application.properties
```

## Notes
- This starter counts calendar days, not working days. Weekend/holiday exclusion can be added as a next enhancement.
- The demo initializer inserts accounts only when the `manager` username does not already exist.
- For a real production deployment, add password reset, employee registration/admin screens, audit logging, pagination, stronger concurrency controls for simultaneous approvals, and HTTPS.
