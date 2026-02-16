package dev.jeankarlo.vehiclerenting.integration.controller;

import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.integration.BaseAuthenticatedTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AccountControllerIT extends BaseAuthenticatedTest {


    @Test
    @DisplayName("Should return account details for authenticated user")
    void shouldReturnAccountDetailsForAuthenticatedUser() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);
        String token = loginAndGetToken(partnerAccount.getEmail());

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/accounts/me")
        .then()
            .statusCode(200)
            .body("email", equalTo(partnerAccount.getEmail()))
            .body("username", equalTo(partnerAccount.getUsername()))
            .body("role", equalTo(partnerAccount.getRole().name()));
    }

    @Test
    @DisplayName("Should return 401 for unauthenticated user")
    void shouldReturn401ForUnauthenticatedUser() {
        given()
        .when()
            .get("/accounts/me")
        .then()
            .statusCode(401);
    }

}
