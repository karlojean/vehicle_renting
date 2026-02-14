package dev.jeankarlo.vehiclerenting.integration;

import dev.jeankarlo.vehiclerenting.dto.auth.LoginRequestDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.enums.AccountRole;
import dev.jeankarlo.vehiclerenting.repository.AccountRepository;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

import static io.restassured.RestAssured.*;

public class BaseAuthenticatedTest extends BaseIntegrationTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    private final String DEFAULT_PASSWORD = "secret";

    protected String createAndLoginAsRentingPartner() {
        Account account = createRentingPartnerAccount();
        return loginAndGetToken(account.getEmail());
    }

    protected String createAndLoginAsCustomer() {
        Account account = createCustomerAccount();
        return loginAndGetToken(account.getEmail());
    }

    private Account createRentingPartnerAccount() {
        return createAccountWithRole(AccountRole.PARTNER);
    }

    private Account createCustomerAccount() {
        return createAccountWithRole(AccountRole.RENTER);
    }

    private Account createAccountWithRole(AccountRole role) {
        String email = (role.name() + "-" + UUID.randomUUID() + "@test.com").toLowerCase();

        Account account = new Account();
        account.setUsername(role.name() + "_" + RandomStringUtils.randomAlphanumeric(6));
        account.setEmail(email);
        account.setRole(role);
        account.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        return accountRepository.save(account);
    }

    protected String loginAndGetToken(String email) {
        LoginRequestDTO login = new LoginRequestDTO(email, DEFAULT_PASSWORD);

        return given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    protected Account getCurrentAccount(String token) {
        Long id = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("accounts/me")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getObject("id", Long.class);

        return accountRepository.findById(id).orElseThrow();
    }

}
