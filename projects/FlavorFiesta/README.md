# FlavorFiesta

A Spring Boot REST application where users can share and discover recipes, leave comments, and write reviews.

## Modules

The application is organized into the following modules, each with its own service, repository, and controller:

| Module | Entity | Service | Description |
|--------|--------|---------|-------------|
| **User Management** | `User` | `UserService` | Registration, authentication, user CRUD |
| **Recipe Management** | `Recipe` | `RecipeService` | Create, read, update, delete recipes |
| **Comment Management** | `Comment` | `CommentService` | Add and manage comments on recipes |
| **Review Management** | `Review` | `ReviewService` | Rate and review recipes (0-10 scale) |
| **Ranking** | -- | `RankingService` | Rankings by comments, reviews, ratings |

## Architecture

```
Controllers  -->  Services  -->  Repositories  -->  Database
(REST API)       (Logic)        (JPA)              (PostgreSQL)
```

Each module follows this layered pattern. Controllers handle HTTP requests, services contain business logic, and repositories handle database access.

## Prerequisites

- Java 17 or newer
- Maven
- Docker (for the PostgreSQL databases)

## Database Setup

Start the databases using Docker Compose:

```bash
docker compose up -d
```

You can also open `docker-compose.yml` in IntelliJ and click the run button next to the services.

This starts two PostgreSQL containers:
- **flavor-fiesta-db** on port `5432` (application database)
- **flavor-fiesta-test-db** on port `5433` (test database)

Connection details are in `src/main/resources/application.properties` and `application-test.properties`.

> **Port conflict?** If you get a port already in use error, check that no other databases or Docker containers are running on ports 5432/5433. Use `docker ps` to list running containers and `docker stop <container>` to stop them.

## Installing Dependencies

```bash
mvn clean install -DskipTests
```

## Running the Application

```bash
mvn spring-boot:run
```

## Running Tests

Run all tests from the command line:

```bash
mvn test
```

Run a specific test class:

```bash
mvn test -Dtest=BigBangIntegrationTest
```

You can also run tests from **IntelliJ**:
- Right-click any test class and select **Run** to run that single test class.
- Right-click the `src/test/java` folder and select **Run 'Tests in 'java''** to execute all tests in the project.

## Test Structure

```
src/test/java/com/vvss/FlavorFiesta/
├── example_for_homework/    <-- Your homework files go here
│   ├── ModuleAUnitTests.java
│   ├── ModuleBUnitTests.java
│   ├── ModuleCUnitTests.java
│   ├── BigBangIntegrationTest.java
│   ├── IncrementalIntegrationModuleATest.java
│   ├── IncrementalIntegrationModuleBTest.java
│   ├── IncrementalIntegrationModuleCTest.java
│   └── MockingExampleTest.java     <-- Example: how to use @MockBean
├── services/                <-- Example unit & integration tests
├── controllers/             <-- Example controller tests
├── repositories/
└── test_utils/              <-- Base classes for integration tests
    ├── TestDBIntegrationTest.java
    └── TestControllerIntegrationTest.java
```
