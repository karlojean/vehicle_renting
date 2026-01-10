package dev.jeankarlo.vehiclerenting.controller;

import dev.jeankarlo.vehiclerenting.BaseAuthenticatedTest;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionInitDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Booking;
import dev.jeankarlo.vehiclerenting.entity.Location;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.entity.enums.BookingStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionType;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleFuelType;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleType;
import dev.jeankarlo.vehiclerenting.repository.BookingRepository;
import dev.jeankarlo.vehiclerenting.repository.LocationRepository;
import dev.jeankarlo.vehiclerenting.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class InspectionControllerIT extends BaseAuthenticatedTest {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private LocationRepository locationRepository;

    @Test
    @DisplayName("Should init pick-up inspection when booking is confirmed")
    public void shouldInitPickUpInspectionWhenBookingIsConfirmed() {
        String partnerToken = createAndLoginAsRentingPartner();
        String renterToken = createAndLoginAsCustomer();
        Account partnerAccount = getCurrentAccount(partnerToken);
        Account renterAccount = getCurrentAccount(renterToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);
        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        InspectionInitDTO inspectionInitDTO = new InspectionInitDTO(booking.getId(), InspectionType.PICK_UP);

        given()
            .header("Authorization", "Bearer " + partnerToken)
            .contentType("application/json")
            .body(inspectionInitDTO)
        .when()
            .post("/inspections")
        .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("Should not init pick-up inspection when booking is not confirmed")
    public void shouldNotInitPickUpInspectionWhenBookingIsNotConfirmed() {
        String partnerToken = createAndLoginAsRentingPartner();
        String renterToken = createAndLoginAsCustomer();
        Account partnerAccount = getCurrentAccount(partnerToken);
        Account renterAccount = getCurrentAccount(renterToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);
        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);

        InspectionInitDTO inspectionInitDTO = new InspectionInitDTO(booking.getId(), InspectionType.PICK_UP);

        given()
            .header("Authorization", "Bearer " + partnerToken)
            .contentType("application/json")
            .body(inspectionInitDTO)
        .when()
            .post("/inspections")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should init drop-off inspection when booking is active")
    public void shouldInitDropOffInspectionWhenBookingIsActive() {
        String partnerToken = createAndLoginAsRentingPartner();
        String renterToken = createAndLoginAsCustomer();
        Account partnerAccount = getCurrentAccount(partnerToken);
        Account renterAccount = getCurrentAccount(renterToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);

        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now().plusDays(1);
        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.ACTIVE);
        bookingRepository.save(booking);

        InspectionInitDTO inspectionInitDTO = new InspectionInitDTO(booking.getId(), InspectionType.DROP_OFF);

        given()
            .header("Authorization", "Bearer " + partnerToken)
            .contentType("application/json")
            .body(inspectionInitDTO)
        .when()
            .post("/inspections")
        .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("Should not init drop-off inspection when booking is not active")
    public void shouldNotInitDropOffInspectionWhenBookingIsNotActive() {
        String partnerToken = createAndLoginAsRentingPartner();
        String renterToken = createAndLoginAsCustomer();
        Account partnerAccount = getCurrentAccount(partnerToken);
        Account renterAccount = getCurrentAccount(renterToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);

        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now().plusDays(1);
        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);

        InspectionInitDTO inspectionInitDTO = new InspectionInitDTO(booking.getId(), InspectionType.DROP_OFF);

        given()
            .header("Authorization", "Bearer " + partnerToken)
            .contentType("application/json")
            .body(inspectionInitDTO)
        .when()
            .post("/inspections")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Should fail when partner tries to init inspection for booking they don't own")
    public void shouldFailWhenPartnerTriesToInitInspectionForBookingTheyDontOwn() {
        String partnerToken = createAndLoginAsRentingPartner();
        String anotherPartnerToken = createAndLoginAsRentingPartner();
        String renterToken = createAndLoginAsCustomer();
        Account anotherPartnerAccount = getCurrentAccount(anotherPartnerToken);
        Account renterAccount = getCurrentAccount(renterToken);

        Location location = createAndSaveLocation(anotherPartnerAccount);
        Vehicle vehicle = createVehicle(location, anotherPartnerAccount);

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);
        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        InspectionInitDTO inspectionInitDTO = new InspectionInitDTO(booking.getId(), InspectionType.PICK_UP);

        given()
            .header("Authorization", "Bearer " + partnerToken)
            .contentType("application/json")
            .body(inspectionInitDTO)
        .when()
            .post("/inspections")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Should fail when customer tries to init inspection")
    public void shouldFailWhenCustomerTriesToInitInspection() {
        String customerToken = createAndLoginAsCustomer();
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);
        Account customerAccount = getCurrentAccount(customerToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);
        Booking booking = createBooking(vehicle, customerAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        InspectionInitDTO inspectionInitDTO = new InspectionInitDTO(booking.getId(), InspectionType.PICK_UP);

        given()
            .header("Authorization", "Bearer " + customerToken)
            .contentType("application/json")
            .body(inspectionInitDTO)
        .when()
            .post("/inspections")
        .then()
            .statusCode(403);
    }

    private Location createAndSaveLocation(Account partner) {
        Location location = new Location();
        location.setAddressLine("Rua das Flores, 123");
        location.setCity("São Paulo");
        location.setState("SP");
        location.setCountry("Brasil");
        location.setPinCode("01234-567");
        location.setLatitude(BigDecimal.valueOf(-23.55052));
        location.setLongitude(BigDecimal.valueOf(-46.633308));
        location.setPartner(partner);

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
        vehicle.setPartner(account);
        return vehicleRepository.save(vehicle);
    }

    private Booking createBooking(Vehicle vehicle, Account customer, LocalDate startDate, LocalDate endDate) {
        Booking booking = new Booking();
        booking.setVehicle(vehicle);
        booking.setRenter(customer);
        booking.setStatus(BookingStatus.PENDING);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setTotalPriceCents( (endDate.toEpochDay() - startDate.toEpochDay() + 1) * vehicle.getPricePerDayCents());
        return bookingRepository.save(booking);
    }
}
