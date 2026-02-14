package dev.jeankarlo.vehiclerenting.integration.controller;

import dev.jeankarlo.vehiclerenting.integration.BaseIntegrationTest;
import dev.jeankarlo.vehiclerenting.dto.auth.LoginRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.auth.RegisterRequestDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.enums.AccountRole;
import dev.jeankarlo.vehiclerenting.repository.AccountRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class AuthControllerIT extends BaseIntegrationTest {


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void cleanUp() {
        accountRepository.deleteAll();
    }

    @Test()
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUserSuccessfully() {

        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(
                "JohnDoe",
                "johndoe@mail.com",
                "111111",
                AccountRole.PARTNER
        );

        given()
                .contentType(ContentType.JSON)
                .body(registerRequestDTO)
                .when()
                    .post("auth/register")
                .then()
                    .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Should not register new user with invalid email")
    void shouldNotRegisterNewUserWithInvalidEmail() {

        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(
                "JohnDoe",
                "johndoe@",
                "111111",
                AccountRole.PARTNER
        );

        given()
                .contentType(ContentType.JSON)
                .body(registerRequestDTO)
                .when()
                    .post("auth/register")
                .then()
                    .statusCode(422)
                .body("fieldMessages[0].fieldName", equalTo("email"));
    }

    @Test
    @DisplayName("Should not register new user with existing email")
    void shouldNotRegisterNewUserWithExistingEmail() {
        Account account = new Account();
        account.setUsername("JohnDoe");
        account.setEmail("johndoe@mail.com");
        account.setRole(AccountRole.PARTNER);
        account.setPassword(passwordEncoder.encode("secret"));

        accountRepository.save(account);

        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(
                "JohnDoe12",
                "johndoe@mail.com",
                "111111",
                AccountRole.PARTNER
        );

        given()
                .contentType(ContentType.JSON)
                .body(registerRequestDTO)
                .when()
                    .post("auth/register")
                .then()
                    .statusCode(409)
                    .body("error", equalTo("Conflict"));
    }


    @Test
    @DisplayName("Should not register new user with existing username")
    void shouldNotRegisterNewUserWithExistingUsername() {
        Account account = new Account();
        account.setUsername("JohnDoe");
        account.setEmail("johndoe@mail.com");
        account.setRole(AccountRole.PARTNER);
        account.setPassword(passwordEncoder.encode("secret"));

        accountRepository.save(account);

        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(
                "JohnDoe",
                "johndoe22@mail.com",
                "1111111",
                AccountRole.PARTNER
        );

        given()
                .contentType(ContentType.JSON)
                .body(registerRequestDTO)
                .when()
                .post("auth/register")
                .then()
                .statusCode(409)
                .body("error", equalTo("Conflict"));
    }

    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() {
        Account account = new Account();
        account.setUsername("JohnDoe");
        account.setEmail("johndoe@mail.com");
        account.setRole(AccountRole.PARTNER);
        account.setPassword(passwordEncoder.encode("secret"));

        accountRepository.save(account);

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("johndoe@mail.com", "secret");

        given()
                .contentType(ContentType.JSON)
                .body(loginRequestDTO)
                .when()
                    .post("auth/login")
                .then()
                    .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    @DisplayName("Should not login with unregistered email")
    void shouldNotLoginWithUnregisteredEmail() {

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("usernotcreated@mail.com", "secret");
        given()
                .contentType(ContentType.JSON)
                .body(loginRequestDTO)
                .when()
                    .post("auth/login")
                .then()
                    .statusCode(401)
                .body("error", equalTo("Unauthorized"));

    }

}
