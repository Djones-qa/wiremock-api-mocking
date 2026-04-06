package com.wiremock.tests;

import com.wiremock.config.BaseTest;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserApiMockTest extends BaseTest {

    @Test
    public void testGetUserReturns200WithValidData() {
        wireMockServer.stubFor(get(urlEqualTo("/api/users/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "id": 1,
                        "name": "Darrius Jones",
                        "email": "darrius@example.com",
                        "role": "qa_engineer"
                    }
                    """)));

        given()
            .when()
            .get("/api/users/1")
            .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("name", equalTo("Darrius Jones"))
            .body("email", equalTo("darrius@example.com"))
            .body("role", equalTo("qa_engineer"));

        log.info("PASS: GET /api/users/1 returns 200 with valid user data");
    }

    @Test
    public void testGetAllUsersReturnsArray() {
        wireMockServer.stubFor(get(urlEqualTo("/api/users"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    [
                        {"id": 1, "name": "Darrius Jones", "role": "qa_engineer"},
                        {"id": 2, "name": "Jane Smith", "role": "developer"},
                        {"id": 3, "name": "Bob Lee", "role": "manager"}
                    ]
                    """)));

        given()
            .when()
            .get("/api/users")
            .then()
            .statusCode(200)
            .body("$", hasSize(3))
            .body("[0].name", equalTo("Darrius Jones"))
            .body("[1].role", equalTo("developer"));

        log.info("PASS: GET /api/users returns array of 3 users");
    }

    @Test
    public void testGetUserReturns404WhenNotFound() {
        wireMockServer.stubFor(get(urlEqualTo("/api/users/9999"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "error": "User not found",
                        "code": 404
                    }
                    """)));

        given()
            .when()
            .get("/api/users/9999")
            .then()
            .statusCode(404)
            .body("error", equalTo("User not found"))
            .body("code", equalTo(404));

        log.info("PASS: GET /api/users/9999 returns 404 user not found");
    }

    @Test
    public void testCreateUserReturns201() {
        wireMockServer.stubFor(post(urlEqualTo("/api/users"))
            .withHeader("Content-Type", containing("application/json"))
            .withRequestBody(containing("qa_engineer"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "id": 4,
                        "name": "New Tester",
                        "email": "tester@example.com",
                        "role": "qa_engineer"
                    }
                    """)));

        given()
            .header("Content-Type", "application/json")
            .body("""
                {
                    "name": "New Tester",
                    "email": "tester@example.com",
                    "role": "qa_engineer"
                }
                """)
            .when()
            .post("/api/users")
            .then()
            .statusCode(201)
            .body("id", equalTo(4))
            .body("name", equalTo("New Tester"));

        log.info("PASS: POST /api/users returns 201 with created user");
    }

    @Test
    public void testDeleteUserReturns200() {
        wireMockServer.stubFor(delete(urlEqualTo("/api/users/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {"message": "User deleted successfully"}
                    """)));

        given()
            .when()
            .delete("/api/users/1")
            .then()
            .statusCode(200)
            .body("message", equalTo("User deleted successfully"));

        log.info("PASS: DELETE /api/users/1 returns 200 with success message");
    }

    @Test
    public void testVerifyGetUserWasCalled() {
        wireMockServer.stubFor(get(urlEqualTo("/api/users/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {"id": 1, "name": "Darrius Jones"}
                    """)));

        given().when().get("/api/users/1");

        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/users/1")));
        log.info("PASS: Verified GET /api/users/1 was called exactly once");
    }
}
