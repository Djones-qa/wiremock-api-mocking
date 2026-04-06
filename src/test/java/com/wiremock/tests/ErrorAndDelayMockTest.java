package com.wiremock.tests;

import com.wiremock.config.BaseTest;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ErrorAndDelayMockTest extends BaseTest {

    @Test
    public void testSimulate500InternalServerError() {
        wireMockServer.stubFor(get(urlEqualTo("/api/payments"))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "error": "Internal Server Error",
                        "message": "Payment service unavailable"
                    }
                    """)));

        given()
            .when()
            .get("/api/payments")
            .then()
            .statusCode(500)
            .body("error", equalTo("Internal Server Error"));

        log.info("PASS: Simulated 500 Internal Server Error for payment service");
    }

    @Test
    public void testSimulate401Unauthorized() {
        wireMockServer.stubFor(get(urlEqualTo("/api/admin"))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "error": "Unauthorized",
                        "message": "Valid authentication token required"
                    }
                    """)));

        given()
            .when()
            .get("/api/admin")
            .then()
            .statusCode(401)
            .body("error", equalTo("Unauthorized"));

        log.info("PASS: Simulated 401 Unauthorized for admin endpoint");
    }

    @Test
    public void testSimulate403Forbidden() {
        wireMockServer.stubFor(get(urlEqualTo("/api/restricted"))
            .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "error": "Forbidden",
                        "message": "Insufficient permissions"
                    }
                    """)));

        given()
            .when()
            .get("/api/restricted")
            .then()
            .statusCode(403)
            .body("error", equalTo("Forbidden"));

        log.info("PASS: Simulated 403 Forbidden for restricted endpoint");
    }

    @Test
    public void testSimulateNetworkDelay() {
        wireMockServer.stubFor(get(urlEqualTo("/api/slow-service"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withFixedDelay(1000)
                .withBody("""
                    {"message": "Slow service response"}
                    """)));

        long start = System.currentTimeMillis();

        given()
            .when()
            .get("/api/slow-service")
            .then()
            .statusCode(200);

        long duration = System.currentTimeMillis() - start;
        assert duration >= 1000 : "Expected delay of at least 1000ms but was " + duration;
        log.info("PASS: Simulated network delay of 1000ms — actual: {}ms", duration);
    }

    @Test
    public void testSimulateRateLimiting429() {
        wireMockServer.stubFor(get(urlEqualTo("/api/rate-limited"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/json")
                .withHeader("Retry-After", "60")
                .withBody("""
                    {
                        "error": "Too Many Requests",
                        "message": "Rate limit exceeded. Try again in 60 seconds"
                    }
                    """)));

        given()
            .when()
            .get("/api/rate-limited")
            .then()
            .statusCode(429)
            .header("Retry-After", "60")
            .body("error", equalTo("Too Many Requests"));

        log.info("PASS: Simulated 429 rate limiting with Retry-After header");
    }

    @Test
    public void testRequestMatchingWithQueryParams() {
        wireMockServer.stubFor(get(urlPathEqualTo("/api/users"))
            .withQueryParam("role", com.github.tomakehurst.wiremock.client.WireMock.equalTo("qa_engineer"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    [{"id": 1, "name": "Darrius Jones", "role": "qa_engineer"}]
                    """)));

        given()
            .queryParam("role", "qa_engineer")
            .when()
            .get("/api/users")
            .then()
            .statusCode(200)
            .body("[0].role", equalTo("qa_engineer"));

        log.info("PASS: Request matching with query parameter role=qa_engineer");
    }

    @Test
    public void testRequestMatchingWithHeaders() {
        wireMockServer.stubFor(get(urlEqualTo("/api/secure"))
            .withHeader("Authorization", com.github.tomakehurst.wiremock.client.WireMock.equalTo("Bearer test-token-123"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {"message": "Authorized access granted"}
                    """)));

        given()
            .header("Authorization", "Bearer test-token-123")
            .when()
            .get("/api/secure")
            .then()
            .statusCode(200)
            .body("message", equalTo("Authorized access granted"));

        log.info("PASS: Request matching with Authorization header");
    }
}
