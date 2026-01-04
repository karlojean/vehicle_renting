package dev.jeankarlo.vehiclerenting.controller;


import dev.jeankarlo.vehiclerenting.BaseAuthenticatedTest;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleRequestDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Booking;
import dev.jeankarlo.vehiclerenting.entity.Location;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.entity.enums.BookingStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleFuelType;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleType;
import dev.jeankarlo.vehiclerenting.repository.AccountRepository;
import dev.jeankarlo.vehiclerenting.repository.BookingRepository;
import dev.jeankarlo.vehiclerenting.repository.LocationRepository;
import dev.jeankarlo.vehiclerenting.repository.VehicleRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import static org.junit.jupiter.api.Assertions.assertFalse;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class VehicleControllerIT extends BaseAuthenticatedTest {

    @Autowired
    private AccountRepository accountRepository;


    @LocalServerPort
    private int port;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void cleanUp() {
        bookingRepository.deleteAll();
        vehicleRepository.deleteAll();
        locationRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create vehicle successfully")
    void shouldCreateVehicleSuccessfully() {
        String token = createAndLoginAsRentingPartner();
        Account owner = getCurrentAccount(token);

        Location location = createAndSaveLocation(owner);

        locationRepository.save(location);

        VehicleRequestDTO vehicleRequestDTO = new VehicleRequestDTO(
                "Toyota",
                "Corolla",
                VehicleFuelType.PETROL,
                VehicleType.SEDAN,
                2020,
                "ABC-1234",
                "Blue",
                5000L,
                "Carro confortável e econômico, perfeito para viagens.",
                location.getId()
        );

        given()
                .contentType(ContentType.JSON)
                .body(vehicleRequestDTO)
                .header("Authorization", "Bearer " + token)
            .when()
                .post("/vehicles")
            .then()
                .statusCode(201)
                .body("id", notNullValue());

    }

    @Test
    @DisplayName("Should fail to create vehicle when user is customer")
    void shouldFailToCreateVehicleWhenUserIsCustomer() {
        String token = createAndLoginAsCustomer();
        Account owner = getCurrentAccount(token);

        Location location = createAndSaveLocation(owner);

        VehicleRequestDTO vehicleRequestDTO = new VehicleRequestDTO(
                "Toyota",
                "Corolla",
                VehicleFuelType.PETROL,
                VehicleType.SEDAN,
                2020,
                "ABC-1234",
                "Blue",
                5000L,
                "Carro confortável e econômico, perfeito para viagens.",
                location.getId()
        );

        given()
                .contentType(ContentType.JSON)
                .body(vehicleRequestDTO)
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/vehicles")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Should fail to create vehicle when fields are missing")
    void shouldFailToCreateVehicleWithFieldsAreMissing() {
        String token = createAndLoginAsRentingPartner();

        VehicleRequestDTO vehicleRequestDTO = new VehicleRequestDTO(
                "",
                "",
                null,
                null,
                null,
                "",
                "",
                null,
                "",
                null
        );

        given()
                .contentType(ContentType.JSON)
                .body(vehicleRequestDTO)
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/vehicles")
                .then()
                .statusCode(422)
                .body("fieldMessages.fieldName", containsInAnyOrder("brand", "model", "fuelType", "vehicleType",
                        "yearManufactured", "licensePlate", "pricePerDayCents", "description", "locationId"))
                .body("fieldMessages", hasSize(9))
            ;
    }

    @Test
    @DisplayName("Should List all Vehicle Of THe Authenticated Renting Partner")
    void shouldListAllVehiclesOfTheAuthenticatedRentingPartner() {
        String token = createAndLoginAsRentingPartner();
        Account account = getCurrentAccount(token);

        Location location = createAndSaveLocation(account);

        Vehicle vehicle1 = createVehicle(location, account);
        Vehicle vehicle2 = createVehicle(location, account);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer "  + token)
        .when()
            .get("/vehicles")
        .then()
            .statusCode(200)
            .body("", hasSize(2));
    }

    @Test
    @DisplayName("Should not include other partner vehicle in list")
    void shouldNotIncludeOtherPartnerVehicleInList() {
        String foreignPartnerToken = createAndLoginAsRentingPartner();
        Account foreignPartner = getCurrentAccount(foreignPartnerToken);
        Location foreignPartnerLocation = createAndSaveLocation(foreignPartner);

        Vehicle foreignPartnerVehicle = createVehicle(foreignPartnerLocation, foreignPartner);

        String token = createAndLoginAsRentingPartner();
        Account account = getCurrentAccount(token);
        Location location = createAndSaveLocation(account);
        Vehicle vehicle1 = createVehicle(location, account);
        Vehicle vehicle2 = createVehicle(location, account);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer "  + token)
        .when()
            .get("/vehicles")
        .then()
            .statusCode(200)
            .body("", hasSize(2))
            .body("id", not(hasItem(foreignPartnerVehicle.getId().intValue())));

    }

    @Test
    @DisplayName("Should get vehicle by id successfully")
    void shouldGetVehicleByIdSuccessfully() {
        String token = createAndLoginAsRentingPartner();
        Account account = getCurrentAccount(token);
        Location location = createAndSaveLocation(account);
        Vehicle vehicle = createVehicle(location, account);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer "  + token)
        .when()
            .get("/vehicles/{id}", vehicle.getId())
        .then()
            .statusCode(200)
            .body("id", equalTo(vehicle.getId().intValue()));
    }

    @Test
    @DisplayName("Should not get vehicle by id when it belongs to another partner")
    void shouldNotGetVehicleByIdWhenItBelongsToAnotherPartner() {
        String foreignPartnerToken = createAndLoginAsRentingPartner();
        Account foreignPartner = getCurrentAccount(foreignPartnerToken);
        Location foreignPartnerLocation = createAndSaveLocation(foreignPartner);
        Vehicle foreignPartnerVehicle = createVehicle(foreignPartnerLocation, foreignPartner);

        String token = createAndLoginAsRentingPartner();

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer "  + token)
        .when()
            .get("/vehicles/{id}", foreignPartnerVehicle.getId())
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should delete vehicle by id successfully")
    void shouldDeleteVehicleByIdSuccessfully() {
        String token = createAndLoginAsRentingPartner();
        Account account = getCurrentAccount(token);
        Location location = createAndSaveLocation(account);
        Vehicle vehicle = createVehicle(location, account);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer "  + token)
        .when()
            .delete("/vehicles/{id}", vehicle.getId())
        .then()
            .statusCode(204);
    }

    @Test
    @DisplayName("Should not delete vehicle by id when it belongs to another partner")
    void shouldNotDeleteVehicleByIdWhenItBelongsToAnotherPartner() {
        String foreignPartnerToken = createAndLoginAsRentingPartner();
        Account foreignPartner = getCurrentAccount(foreignPartnerToken);
        Location foreignPartnerLocation = createAndSaveLocation(foreignPartner);
        Vehicle foreignPartnerVehicle = createVehicle(foreignPartnerLocation, foreignPartner);

        String token = createAndLoginAsRentingPartner();

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer "  + token)
        .when()
            .delete("/vehicles/{id}", foreignPartnerVehicle.getId())
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should fail to delete vehicle that do not exist")
    void shouldFailToDeleteVehicleThatDoNotExist() {
        String token = createAndLoginAsRentingPartner();

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer "  + token)
        .when()
            .delete("/vehicles/{id}", 9999)
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should deactivate vehicle successfully")
    void shouldDeactivateVehicleSuccessfully() {
        String token = createAndLoginAsRentingPartner();
        Account account = getCurrentAccount(token);
        Location location = createAndSaveLocation(account);
        Vehicle vehicle = createVehicle(location, account);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer "  + token)
        .when()
            .patch("/vehicles/{id}/deactivate", vehicle.getId())
        .then()
            .statusCode(204);

        Vehicle afterVehicle = vehicleRepository.findById(vehicle.getId()).orElseThrow();
        assertFalse(afterVehicle.getIsActive());
    }

    @Test
    @DisplayName("Should activate vehicle successfully")
    void shouldActivateVehicleSuccessfully() {
        String token = createAndLoginAsRentingPartner();
        Account account = getCurrentAccount(token);
        Location location = createAndSaveLocation(account);
        Vehicle vehicle = createVehicle(location, account);
        vehicle.setIsActive(false);
        vehicleRepository.save(vehicle);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer "  + token)
        .when()
            .patch("/vehicles/{id}/activate", vehicle.getId())
        .then()
            .statusCode(204);

        Vehicle afterVehicle = vehicleRepository.findById(vehicle.getId()).orElseThrow();
        assert(afterVehicle.getIsActive());
    }

    @Test
    @DisplayName("Should return available vehicles when filters match")
    void shouldReturnAvailableVehiclesWhenFiltersMatch() {
        String token = createAndLoginAsCustomer();
        Account account = getCurrentAccount(token);
        Location location = createAndSaveLocation(account);
        Vehicle vehicle = createVehicle(location, account);

        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = startDate.plusDays(3);


        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer "  + token)
            .queryParam("city", location.getCity())
            .queryParam("startDate", startDate.toString())
            .queryParam("endDate", endDate.toString())
        .when()
            .get("/vehicles/available")
        .then()
            .statusCode(200)
            .body("", hasSize(1))
            .body("id", hasItem(vehicle.getId().intValue()));
    }

    @Test
    @DisplayName("Should not return vehicle already rented in the requested period")
    void shouldNotReturnVehicleAlreadyRentedInTheRequestedPeriod() {
        String token = createAndLoginAsCustomer();
        Account account = getCurrentAccount(token);

        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);

        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = startDate.plusDays(3);

        String foreignCustomerToken = createAndLoginAsCustomer();
        Account foreignCustomer = getCurrentAccount(foreignCustomerToken);
        Booking booking = new Booking();
        booking.setVehicle(vehicle);
        booking.setRenter(foreignCustomer);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPriceCents(10000L);

        bookingRepository.save(booking);

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer "  + token)
            .queryParam("city", location.getCity())
            .queryParam("startDate", startDate.toString())
            .queryParam("endDate", endDate.minusDays(1).toString())
        .when()
            .get("/vehicles/available")
        .then()
            .statusCode(200)
            .body("", hasSize(0));
    }


    private Vehicle createVehicle(Location location, Account account) {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand("Brand " + UUID.randomUUID());
        vehicle.setModel("Generic Model");
        vehicle.setFuelType(VehicleFuelType.PETROL);
        vehicle.setVehicleType(VehicleType.SEDAN);
        vehicle.setYearManufactured(2020);
        vehicle.setLicensePlate("ABC-" + new Random().nextInt(1000, 10000));
        vehicle.setColor("Blue");
        vehicle.setPricePerDayCents(5000L);
        vehicle.setDescription("Generic vehicle for test");
        vehicle.setLocation(location);
        vehicle.setOwner(account);
        return vehicleRepository.save(vehicle);
    }

    private Location createAndSaveLocation(Account owner) {
        Location location = new Location();
        location.setAddressLine("Rua das Flores, 123");
        location.setCity("São Paulo");
        location.setState("SP");
        location.setCountry("Brasil");
        location.setPinCode("01234-567");
        location.setLatitude(BigDecimal.valueOf(-23.55052));
        location.setLongitude(BigDecimal.valueOf(-46.633308));
        location.setOwner(owner);

        return locationRepository.save(location);
    }
}
