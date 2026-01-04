package dev.jeankarlo.vehiclerenting.controller;

import dev.jeankarlo.vehiclerenting.BaseAuthenticatedTest;
import dev.jeankarlo.vehiclerenting.dto.booking.BookingRequestDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Booking;
import dev.jeankarlo.vehiclerenting.entity.Location;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.entity.enums.BookingStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleFuelType;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleType;
import dev.jeankarlo.vehiclerenting.repository.BookingRepository;
import dev.jeankarlo.vehiclerenting.repository.LocationRepository;
import dev.jeankarlo.vehiclerenting.repository.VehicleRepository;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BookingControllerIT extends BaseAuthenticatedTest {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @DisplayName("Should create booking successfully")
    void shouldCreateBookingSuccessfully() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);
        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);

        String customerToken = createAndLoginAsCustomer();
        BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(
                vehicle.getId(),
                LocalDate.now().plusDays(4),
                LocalDate.now().plusDays(10)
        );

        given()
                .header("Authorization", "Bearer " + customerToken)
                .contentType("application/json")
                .body(bookingRequestDTO)
        .when()
                .post("/bookings")
        .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Should fail to create booking when vehicle is already rented in the requested date")
    void shouldFailToCreateBookingWhenAlreadyRentedInTheRequestedDate() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);
        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);
        String foreignCustomerToken = createAndLoginAsCustomer();
        Account foreignCustomerAccount = getCurrentAccount(foreignCustomerToken);

        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = startDate.plusDays(5);

        createBooking(vehicle, foreignCustomerAccount, startDate, endDate);

        String customerToken = createAndLoginAsCustomer();

        given()
                .header("Authorization", "Bearer " + customerToken)
                .contentType("application/json")
                .body(new BookingRequestDTO(
                        vehicle.getId(),
                        startDate,
                        endDate
                ))
        .when()
                .post("/bookings")
        .then()
                .statusCode(409)
                .body("error", equalTo("Conflict"));
    }

    @Test
    @DisplayName("Should fail to create booking when vehicle does not exist")
    void shouldFailToCreateBookingWhenVehicleDoesNotExist() {
        String customerToken = createAndLoginAsCustomer();

        given()
                .header("Authorization", "Bearer " + customerToken)
                .contentType("application/json")
                .body(new BookingRequestDTO(
                        99L,
                        LocalDate.now().plusDays(5),
                        LocalDate.now().plusDays(10)
                ))
        .when()
                .post("/bookings")
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should fail to create booking when start date is after end date")
    void shouldFailToCreateBookingWhenStartDateIsAfterEndDate() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);
        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);

        String customerToken = createAndLoginAsCustomer();

        given()
                .header("Authorization", "Bearer " + customerToken)
                .contentType("application/json")
                .body(new BookingRequestDTO(
                        vehicle.getId(),
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(5)
                ))
        .when()
                .post("/bookings")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should fail to create booking when start date is equal to end date")
    void shouldFailToCreateBookingWhenStartDateIsEqualToEndDate() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);
        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);

        String customerToken = createAndLoginAsCustomer();
        LocalDate date = LocalDate.now().plusDays(5);

        given()
                .header("Authorization", "Bearer " + customerToken)
                .contentType("application/json")
                .body(new BookingRequestDTO(
                        vehicle.getId(),
                        date,
                        date
                ))
        .when()
                .post("/bookings")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should accept booking successfully")
    void shouldAcceptBookingSuccessfully() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);
        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);

        String foreignCustomerToken = createAndLoginAsCustomer();
        Account foreignCustomerAccount = getCurrentAccount(foreignCustomerToken);

        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = startDate.plusDays(5);

        Booking booking = createBooking(vehicle, foreignCustomerAccount, startDate, endDate);

        given()
                .header("Authorization", "Bearer " + partnerToken)
        .when()
                .patch("/bookings/{bookingId}/confirm", booking.getId())
        .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Should cancel booking successfully")
    void shouldCancelBookingSuccessfully() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);
        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);

        String foreignCustomerToken = createAndLoginAsCustomer();
        Account foreignCustomerAccount = getCurrentAccount(foreignCustomerToken);

        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = startDate.plusDays(5);

        Booking booking = createBooking(vehicle, foreignCustomerAccount, startDate, endDate);

        given()
                .header("Authorization", "Bearer " + partnerToken)
        .when()
                .patch("/bookings/{bookingId}/cancel", booking.getId())
        .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Should fail to accept booking when not owner")
    void shouldFailToAcceptBookingWhenNotOwner() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);
        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);

        String foreignCustomerToken = createAndLoginAsCustomer();
        Account foreignCustomerAccount = getCurrentAccount(foreignCustomerToken);

        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = startDate.plusDays(5);

        Booking booking = createBooking(vehicle, foreignCustomerAccount, startDate, endDate);

        String anotherPartnerToken = createAndLoginAsRentingPartner();

        given()
                .header("Authorization", "Bearer " + anotherPartnerToken)
        .when()
                .patch("/bookings/{bookingId}/confirm", booking.getId())
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Should fail to cancel booking when not owner")
    void shouldFailToCancelBookingWhenNotOwner() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);
        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);

        String foreignCustomerToken = createAndLoginAsCustomer();
        Account foreignCustomerAccount = getCurrentAccount(foreignCustomerToken);

        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = startDate.plusDays(5);

        Booking booking = createBooking(vehicle, foreignCustomerAccount, startDate, endDate);

        String anotherPartnerToken = createAndLoginAsRentingPartner();

        given()
                .header("Authorization", "Bearer " + anotherPartnerToken)
                .when()
                .patch("/bookings/{bookingId}/cancel", booking.getId())
                .then()
                .statusCode(403);
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

    private Booking createBooking(Vehicle vehicle, Account customer, LocalDate startDate, LocalDate endDate) {
        Booking booking = new Booking();
        booking.setVehicle(vehicle);
        booking.setRenter(customer);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setTotalPriceCents( (endDate.toEpochDay() - startDate.toEpochDay() + 1) * vehicle.getPricePerDayCents());
        return bookingRepository.save(booking);
    }

}
