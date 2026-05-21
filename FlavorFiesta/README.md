# FlavorFiesta

A Spring Boot REST application with a web UI where users can share and discover recipes, leave comments, and write reviews.

## Modules

| Module | Pages | Description |
|--------|-------|-------------|
| **Authentication** | `/login`, `/signup` | Login and registration |
| **Recipes** | `/home`, `/recipe/{id}` | Browse and view recipes |
| **Comments** | (on recipe page) | Add comments to recipes |
| **User Profiles** | `/profile`, `/user/{id}`, `/users` | View user profiles |
| **Navigation** | (navbar) | Sign out, navigate between pages |

## Architecture

```
Templates (Thymeleaf)  -->  Controllers  -->  Services  -->  Repositories  -->  Database
(HTML + JS)               (REST + View)     (Logic)        (JPA)              (PostgreSQL)
```

The web UI uses Thymeleaf templates with jQuery for AJAX calls to the REST API.

## Prerequisites

- Java 21
- Maven (or use the included `./mvnw` wrapper)
- Docker (for the PostgreSQL databases)
- Google Chrome (for Selenium tests)

## Database Setup

Start the databases using Docker Compose from the **FlavorFiesta** project directory:

```bash
docker compose up -d
```

You can also open `docker-compose.yml` in IntelliJ and click the run button next to the services.

This starts two PostgreSQL containers:
- **flavor-fiesta-db** on port `5434` (application database)
- **flavor-fiesta-test-db** on port `5433` (test database)

> **Port conflict?** If you get a port already in use error, check that no other databases or Docker containers are running on ports 5433/5434. Use `docker ps` to list running containers and `docker stop <container>` to stop them.

## Installing Dependencies

```bash
./mvnw clean install -DskipTests
```

## Running the Application

```bash
./mvnw spring-boot:run
```

Then open http://localhost:8080 in your browser. The application auto-populates with sample data on first run.

## Running Tests

Run all tests (excluding Selenium UI tests):

```bash
./mvnw test -Dtest='!TestWebApp,!Functionality1Test,!Functionality2Test,!Functionality3Test'
```

Run only the Selenium UI tests (Chrome must be installed):

```bash
./mvnw test -Dtest='TestWebApp'
```

Run a specific test class:

```bash
./mvnw test -Dtest=Functionality1Test
```

You can also run tests from **IntelliJ**:
- Right-click any test class and select **Run** to run that single test class.
- Right-click the `src/test/java` folder and select **Run All Tests** to execute all tests in the project.

## Test Structure

```
src/test/java/com/vvss/FlavorFiesta/
├── example_for_homework/    <-- Your homework files go here
│   ├── Functionality1Test.java
│   ├── Functionality2Test.java
│   └── Functionality3Test.java
├── ui/
│   └── TestWebApp.java      <-- Example: Selenium tests for login (success + failure)
├── bigbang/
│   └── TestUserFlow.java     <-- Example: integration test with MockMvc
├── controllers/              <-- Example controller tests
├── services/                 <-- Example service tests
├── repositories/             <-- Example repository tests
└── test_utils/               <-- Base classes for tests
```

## Available Pages for Selenium Testing

| URL | Page | Key Elements |
|-----|------|-------------|
| `/login` | Login form | `#username`, `#password`, `#submit-button` |
| `/home` | Recipe list | `.card`, `.btn-primary` (View Recipe links) |
| `/recipe/{id}` | Recipe detail + comments | `#comment`, `#add-comment-form`, `.list-group-item` |
| `/profile` | Current user profile | (requires login) |
| `/users` | All users list | (requires login) |
| `/user/{id}` | User profile | (requires login) |

## Selenium Tips

- The application starts on a **random port** during tests. Use `@LocalServerPort` to get the port.
- Use `WebDriverWait` with `ExpectedConditions` instead of `Thread.sleep()`.
- Login is cookie-based: after login, a `credentials` cookie is set. Subsequent pages check for this cookie.
- See `ui/TestWebApp.java` for a working example of Selenium tests with Spring Boot.
