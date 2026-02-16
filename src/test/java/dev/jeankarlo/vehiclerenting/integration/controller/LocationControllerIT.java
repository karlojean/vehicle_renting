package dev.jeankarlo.vehiclerenting.integration.controller;

import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Location;
import dev.jeankarlo.vehiclerenting.integration.BaseAuthenticatedTest;
import dev.jeankarlo.vehiclerenting.repository.LocationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class LocationControllerIT extends BaseAuthenticatedTest {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    @DisplayName("Should create location successfully")
    void shouldCreateLocationSuccessfully() {
        String partnerToken = createAndLoginAsRentingPartner();

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .contentType("application/json")
                .body("""
                {
                      "addressLine": "123 Main Street, Suite 100",
                      "city": "San Francisco",
                      "state": "California",
                      "pinCode": "94102",
                      "country": "United States",
                      "latitude": 37.7749,
                      "longitude": -122.4194
                }
            """)
                .when()
                .post("/locations")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("addressLine", equalTo("123 Main Street, Suite 100"))
                .body("city", equalTo("San Francisco"))
                .body("state", equalTo("California"))
                .body("pinCode", equalTo("94102"))
                .body("country", equalTo("United States"))
                .body("latitude", equalTo(37.7749f))
                .body("longitude", equalTo(-122.4194f))
            ;

    }

    @Test
    @DisplayName("Should fail to create location when user is a renter")
    void shouldFailCreateLocationWhenUserIsRenter() {
        String customerToken = createAndLoginAsRenter();

        given()
                .header("Authorization", "Bearer " + customerToken)
                .contentType("application/json")
                .body("""
                {
                      "addressLine": "123 Main Street, Suite 100",
                      "city": "San Francisco",
                      "state": "California",
                      "pinCode": "94102",
                      "country": "United States",
                      "latitude": 37.7749,
                      "longitude": -122.4194
                }
            """)
                .when()
                .post("/locations")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Should get all locations by partner")
    void shouldGetAllLocationsByPartner() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partner = getCurrentAccount(partnerToken);
        createLocationByPartner(partner);

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .when()
                .get("/locations")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)));
    }


    @Test
    @DisplayName("Should fail to create location when missing required fields")
    void shouldFailCreateLocationWhenMissingRequiredFields() {
        String partnerToken = createAndLoginAsRentingPartner();

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .contentType("application/json")
                .body("""
                            {
                                  "city": "San Francisco",
                                  "state": "California",
                                  "pinCode": "94102",
                                  "country": "United States",
                                  "latitude": 37.7749,
                                  "longitude": -122.4194
                            }
                        """)
                .when()
                .post("/locations")
                .then()
                .statusCode(422)
                .body("fieldMessages", hasSize(greaterThanOrEqualTo(1)));
    }


    private void createLocationByPartner(Account account) {

        Location location = new Location();
        location.setAddressLine("123 Main Street, Suite 100");
        location.setCity("San Francisco");
        location.setState("California");
        location.setPinCode("94102");
        location.setCountry("United States");
        location.setLatitude(BigDecimal.valueOf(37.7749));
        location.setLongitude(BigDecimal.valueOf(-122.4194));
        location.setPartner(account);

        locationRepository.save(location);
    }

}


