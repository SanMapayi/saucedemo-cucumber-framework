# SauceDemo Automation Framework (Cucumber + Selenium)

This repository contains a **UI automation framework** built to demonstrate clean test design, BDD principles, and cross-browser execution using **Selenium Grid and Docker**.

The project automates a key SauceDemo user journey:
> **Login → identify the highest priced product (without using sort) → add to cart → validate cart contents**

This framework was designed with **clarity and maintainability** in mind. Intentionally applies a mix of well-known design patterns and commented to improve readability and easy to follow.

---

## Technology Used

- Java 23
- Selenium WebDriver 4
- Cucumber (BDD)
- TestNG
- Maven
- Docker & Docker Compose
- Selenium Grid (Chrome & Firefox)

---

### Prerequisites
- Java 23 installed
- Maven installed
- Browser installed (Chrome / Firefox / Safari)
---

## Design Decisions

### Page Objects and Step Definitions
- **Page Objects**
  - Contain locators and UI interactions only
  - Return values (text, price, count)
  - Do NOT contain business assertions
- **Step Definitions**
  - Contain all assertions and acceptance criteria
  - Read like test scenarios

---

# Project Structure
```java
com.saucedemo (cucumber-framework)
│
├── .idea/
│   └── IntelliJ project configuration (not part of framework logic)
│
├── logfiles/
│   ├── test.log
│   └── error.log
│   └── Runtime execution logs generated via Logback (gitignored)
│
├── screenshots/
│   └── Standard user adds the highest priced item to the cart_<timestamp>.png
│   └── Screenshots captured automatically on scenario failure (gitignored)
│
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── configuration
│   │   │   │   ├── ReadConfig.java
│   │   │   │   │   - Reads and exposes values from config.properties
│   │   │   │   └── LogDirectorySetup.java
│   │   │   │       - Creates log directories before execution
│   │   │   │
│   │   │   ├── core
│   │   │   │   └── Constants.java
│   │   │   │       - Centralised constants (e.g. application URL)
│   │   │   │
│   │   │   └── utilities
│   │   │       └── LoggerUtil.java
│   │   │           - Central SLF4J logger configuration
│   │   │
│   │   └── resources
│   │       └── (reserved for framework-level resources if needed)
│   │
│   └── test
│       ├── java
│       │   ├── base
│       │   │   └── TestBase.java
│       │   │       - WebDriver lifecycle management
│       │   │       - Local and Selenium Grid support
│       │   │       - Thread-safe driver and wait handling
│       │   │
│       │   ├── behaviour
│       │   │   ├── ActionMethods.java
│       │   │   │   - Reusable UI interaction helpers (click, type, wait)
│       │   │   └── GetMethods.java
│       │   │       - Reusable UI getter helpers (getText, waits)
│       │   │
│       │   ├── hooks
│       │   │   └── Hooks.java
│       │   │       - Cucumber lifecycle hooks
│       │   │       - Driver setup and teardown
│       │   │       - Screenshot capture on failure
│       │   │
│       │   ├── pages
│       │   │   ├── LoginPage.java
│       │   │   │   - Login page interactions and UI state getters
│       │   │   └── ProductsPage.java
│       │   │       - Inventory, product details, and cart interactions
│       │   │       - Logic to identify highest priced item without sorting
│       │   │
│       │   ├── runners
│       │   │   └── TestRunner.java
│       │   │       - Cucumber + TestNG runner
│       │   │       - Entry point for test execution
│       │   │
│       │   ├── stepdefinitions
│       │   │   └── SauceDemoSteps.java
│       │   │       - Cucumber step definitions
│       │   │       - All business assertions and validations live here
│       │   │
│       │   └── utilities
│       │       ├── FileUtils.java
│       │       │   - File handling utilities (screenshots/log cleanup)
│       │       └── ScreenshotUtil.java
│       │           - Screenshot capture utility used by Hooks
│       │
│       └── resources
│           ├── config
│           │   ├── config.properties
│           │   │   - Browser selection
│           │   │   - Application URL
│           │   │   - Timeouts and window configuration
│           │   └── extent-config.xml
│           │       - Reserved for future reporting (not actively used)
│           │
│           ├── features
│           │   └── saucedemo.feature
│           │       - Gherkin feature file describing the test scenario
│           │
│           └── logback.xml
│               - Logging configuration
│
├── target/
│   ├── surefire-reports/
│   │   ├── runners.TestRunner.txt
│   │   └── TEST-runners.TestRunner.xml
│   │   - Maven Surefire test execution reports
│   │
│   ├── classes/
│   ├── generated-sources/
│   ├── generated-test-sources/
│   └── maven-status/
│
├── Dockerfile
│   └── Builds a Maven + Java container for test execution
│
├── docker-compose.yml
│   └── Starts Selenium Grid (Hub + Chrome + Firefox)
│   └── Executes Cucumber tests in a container
│
├── pom.xml
│   └── Maven dependency and build configuration
│
└── README.md
    └── Project overview and execution instructions


```



## Browser Configuration & Cross-Browser Testing

### Chrome Password Manager (Disabled Intentionally)

Chrome’s built-in password manager and credential prompts are explicitly disabled when running tests.

These prompts can:
- Appear unexpectedly after login actions
- Overlay UI elements
- Intercept focus and clicks
- Cause false negatives and flaky failures

To ensure stable and deterministic test execution, Chrome is started with preferences that disable:
- Password save prompts
- Credential leak detection

This configuration is applied automatically via browser options and requires no manual intervention.

---

### Supported Browsers

The framework supports cross-browser execution using the following browsers:

- **Chrome**  
  Primary browser for both local and Selenium Grid execution

- **Firefox**  
  Supported locally and via Selenium Grid for cross-browser validation

- **Edge**  
  Supported locally (headless and headed modes)

- **Safari**  
  Supported for **local macOS execution only**  
  *(Safari cannot run inside Docker containers)*

Cross-browser execution is enabled without code changes by configuring the browser value or environment variables.

---

### Selenium Grid Execution

When running via Docker and Selenium Grid, tests are executed on:
- Chrome
- Firefox

This allows the same scenarios to be validated across multiple browsers in a consistent environment.


### No Sorting Used
The highest priced item is identified by:
1. Iterating through all product cards
2. Extracting price text from each item
3. Comparing values programmatically

This satisfies the requirement to **avoid using the sort dropdown**.

---

### Immutable Test Data
Selected product data (index, name, price) is stored using an immutable record:
ProductInfo(index, name, price)


---

### `config.properties` (Local execution)
```properties
browser=safari
url=https://www.saucedemo.com
implicitWait=10
explicitWait=15
pageLoadTimeout=20
headless=false
windowSize=maximize
```
---
## RUN CODE (BASH)
```bash
mvn clean test
```
---
## Test Results & Artefacts

Test execution results are generated automatically by Maven Surefire and can be found under:
target/surefire-reports/

These reports include scenario execution status, failures, and stack traces.

When a scenario fails, additional artefacts are produced:
- **Screenshots** are captured automatically and saved under the `screenshots/` directory
- **Execution logs** are written to the `logfiles/` directory

---
## Observing Failure Handling

To demonstrate failure handling (error logging and screenshot capture),
an optional tagged scenario is included.

Running scenarios with the optional tag, using the usernames and password (on the login page), where in some case the happy path scenario failed:
- Screenshot capture on failure
- Error logging behaviour
- Test reporting via Maven Surefire

This scenario is tagged as optional in the feature file using scenario outline to inject data.



---
## Run Tests with Docker & Selenium Grid
**Prerequisites**

- Docker Desktop installed and running

- Default run (Chrome on Grid)
```bash
docker compose up --build
```
Run on Firefox (no code/config changes)
```bash
dBROWSER=firefox docker compose up --build
```
Run on both Chrome and Firefox (one command)
```bash
docker compose run cucumber-tests \
  bash -lc "BROWSER=chrome mvn test && BROWSER=firefox mvn test"
```
# Selenium Grid UI
While Docker is running, the Grid can be viewed at:
http://localhost:4444

---
## Final Notes

This framework focuses on demonstrating clean automation design, clear test intent, and stable cross-browser execution.






