# WireMock API Mocking

A Java test project demonstrating API mocking with [WireMock](https://wiremock.org/), [REST Assured](https://rest-assured.io/), and [TestNG](https://testng.org/).

![CI](https://github.com/YOUR_USERNAME/wiremock-api-mocking/actions/workflows/wiremock-tests.yml/badge.svg)

## What's covered

- Mocking GET, POST, DELETE endpoints
- Simulating error responses (401, 403, 404, 429, 500)
- Network delay simulation
- Request matching by query params and headers
- Verifying requests were made

## Tech stack

| Tool | Version |
|------|---------|
| Java | 17 |
| WireMock | 3.3.1 |
| REST Assured | 5.4.0 |
| TestNG | 7.9.0 |
| Jackson | 2.17.2 |

## Project structure

```
src/test/java/com/wiremock/
├── config/
│   └── BaseTest.java          # WireMock server setup/teardown
└── tests/
    ├── UserApiMockTest.java    # CRUD endpoint mocking tests
    └── ErrorAndDelayMockTest.java  # Error codes and delay tests
```

## Running the tests

```bash
mvn test
```

Test reports are generated at `target/surefire-reports/`.
