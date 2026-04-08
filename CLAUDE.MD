# Project Context

This is my AI agent workspace which is use to make software projects. I use it for design, implement, build and test software applications.

# About me

I am a Java backend software developer specialize in following technical stack:

- Java 17 / JEE 11
- Spring boot
- JPA/Hibernate
- Junit / Mockito
- Postgres SQL
- Maven
- Docker / Kubernetes

I am developing reliable and scalable production applications for the different domains. I got requirement from product owner. I will analyze the requirement and come up with design first. I prefer to clean testable code with minimum technical debt according to the given requirement. 

# Rules

- Always ask clarifying questions before starting a complex task
- Show your plan and steps before executing
- Save all project files to the current folder
- Always follow project structure and coding guidelines as bellow

# Project Overview

All projects are following a layered architecture:

- Controller → Handles HTTP requests/response
- Service → Business logic
- Repository → Data access (JPA)
- Model → Entities
- Utility → Helper classes for others 
- Tests → Unit & Integration tests

# Project Structure

```
src/main/java/{package}     # Base package

|-- {feature}/               # Feature Ex Report, Order
        |-- controller/      # REST controller
        |--dto/              # Request/response objects
        |-- service/         # Business logic
        |-- repository/      # Spring Data JPA repository
        |-- model/           # JPA entities
|-- exception/               # Custom exceptions
|-- util/                    # Utilities

src/test/java/{package}     # Base package

|-- {feature}/               # Feature Ex Report, Order
        |-- controller/      # REST controller unit test
        |-- service/         # Business logic unit test
        |-- repository/      # Spring Data JPA repository unit test
        |-- *IT.java         # Integration test Ex ReportIT.java

```

# Coding Guidelines

- Use Java 17
- Use Spring boot 4.x.x
- Follow clean code principles
- Prefer constructor injection over field injection
- Avoid circular dependencies
- Keep methods small and readable and use meaningful name according to the feature
- Reduce boilerplate code
- Do not use deprecated method
- Update pom.xml for relevant dependencies
- Write unit test and integration test
- Create branchs for Features and Bugs Ex F_<feature name in short>, Bug_<bug name in short>
- PR is must before merge in to master
- Don't execute unit test and intergration test unless ask for it.


## Testing Strategy

- **Unit Tests:** Write unit tests for using [JUnit] and [Mockito] for mocking.
- **Integration Tests:** Should cover end to end business scenario
- **Test Slices:** Utilize test slice annotations like `@WebMvcTest` for controllers or `@DataJpaTest` for repositories to test specific layers in isolation.
- **Code Coverage:** Aim for 80% code coverage. 
- **Real Scenarios:** Write tests that reflect real production scenarios to ensure robustness.
- **Test Name:** Write test method in java naming convention Ex. testCreateReport