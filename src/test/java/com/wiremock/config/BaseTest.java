package com.wiremock.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import static io.restassured.RestAssured.baseURI;

public class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);
    protected static WireMockServer wireMockServer;
    protected static final int PORT = 8089;
    protected static final String BASE_URL = "http://localhost:" + PORT;

    @BeforeClass
    public void startWireMock() {
        wireMockServer = new WireMockServer(
            WireMockConfiguration.wireMockConfig().port(PORT)
        );
        wireMockServer.start();
        baseURI = BASE_URL;
        log.info("WireMock server started on port {}", PORT);
    }

    @BeforeMethod
    public void resetWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.resetAll();
        }
    }

    @AfterClass
    public void stopWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            log.info("WireMock server stopped");
        }
    }
}
