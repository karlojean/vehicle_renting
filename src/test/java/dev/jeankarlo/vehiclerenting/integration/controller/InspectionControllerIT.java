package dev.jeankarlo.vehiclerenting.integration.controller;

import java.io.File;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionInitDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionPatchDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Booking;
import dev.jeankarlo.vehiclerenting.entity.Inspection;
import dev.jeankarlo.vehiclerenting.entity.Location;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.entity.enums.BookingStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionType;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleFuelType;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleType;
import dev.jeankarlo.vehiclerenting.integration.BaseAuthenticatedTest;
import dev.jeankarlo.vehiclerenting.repository.BookingRepository;
import dev.jeankarlo.vehiclerenting.repository.InspectionRepository;
import dev.jeankarlo.vehiclerenting.repository.LocationRepository;
import dev.jeankarlo.vehiclerenting.repository.VehicleRepository;
import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class InspectionControllerIT extends BaseAuthenticatedTest {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private InspectionRepository inspectionRepository;

    @Test
    @DisplayName("Should init pick-up inspection when booking is confirmed")
    public void shouldInitPickUpInspectionWhenBookingIsConfirmed() {
        String partnerToken = createAndLoginAsRentingPartner();
        String renterToken = createAndLoginAsRenter();
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
        String renterToken = createAndLoginAsRenter();
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
        String renterToken = createAndLoginAsRenter();
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
        String renterToken = createAndLoginAsRenter();
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
        String renterToken = createAndLoginAsRenter();
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
    public void shouldFailWhenRenterTriesToInitInspection() {
        String customerToken = createAndLoginAsRenter();
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

    @Test
    @DisplayName("Should update inspection successfully")
    void shouldUpdateInspectionSuccessfully() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        String accountToken = createAndLoginAsRenter();
        Account renterAccount = getCurrentAccount(accountToken);

        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Inspection inspection = new Inspection();
        inspection.setBooking(booking);
        inspection.setType(InspectionType.PICK_UP);
        inspection.setInspectionDate(Instant.now());
        inspection.setStatus(InspectionStatus.PENDING);
        inspectionRepository.save(inspection);

        InspectionPatchDTO inspectionPatchDTO = new InspectionPatchDTO(
                15000,
                80,
                null,
                null,
                null,
                null,
                null);

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .contentType("application/json")
                .body(inspectionPatchDTO)
                .when()
                .patch("/inspections/" + inspection.getId())
                .then()
                .statusCode(200)
                .body("odometerReading", equalTo(15000))
                .body("fuelLevel", equalTo(80))
                .body("isCleanExterior", nullValue())
                .body("isCleanInterior", nullValue())
                .body("hasSmokeSmell", nullValue())
                .body("hasSpareTire", nullValue())
                .body("hasDocuments", nullValue());
    }

    @Test
    @DisplayName("Should complete inspection successfully")
    void shouldCompleteInspectionSuccessfully() {

        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        String accountToken = createAndLoginAsRenter();
        Account renterAccount = getCurrentAccount(accountToken);

        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Inspection inspection = new Inspection();
        inspection.setBooking(booking);
        inspection.setType(InspectionType.PICK_UP);
        inspection.setInspectionDate(Instant.now());
        inspection.setStatus(InspectionStatus.PENDING);
        inspectionRepository.save(inspection);

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .when()
                .patch("/inspections/" + inspection.getId() + "/complete")
                .then()
                .statusCode(200)
                .body("status", equalTo("COMPLETED"));
    }

    @Test
    @DisplayName("Should fail to complete inspection when not pending")
    void shouldFailCompleteInspectionWhenNotPending() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        String accountToken = createAndLoginAsRenter();
        Account renterAccount = getCurrentAccount(accountToken);

        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Inspection inspection = new Inspection();
        inspection.setBooking(booking);
        inspection.setType(InspectionType.PICK_UP);
        inspection.setInspectionDate(Instant.now());
        inspection.setStatus(InspectionStatus.COMPLETED);
        inspectionRepository.save(inspection);

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .when()
                .patch("/inspections/" + inspection.getId() + "/complete")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should fail to complete inspection when not owned")
    void shouldFailCompleteInspectionWhenNotOwned() {
        String partnerToken = createAndLoginAsRentingPartner();
        String anotherPartnerToken = createAndLoginAsRentingPartner();
        Account anotherPartnerAccount = getCurrentAccount(anotherPartnerToken);

        Location location = createAndSaveLocation(anotherPartnerAccount);
        Vehicle vehicle = createVehicle(location, anotherPartnerAccount);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        String accountToken = createAndLoginAsRenter();
        Account renterAccount = getCurrentAccount(accountToken);

        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Inspection inspection = new Inspection();
        inspection.setBooking(booking);
        inspection.setType(InspectionType.PICK_UP);
        inspection.setInspectionDate(Instant.now());
        inspection.setStatus(InspectionStatus.PENDING);
        inspectionRepository.save(inspection);

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .when()
                .patch("/inspections/" + inspection.getId() + "/complete")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Should cancel inspection successfully")
    void shouldCancelInspectionSuccessfully() {

        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        String accountToken = createAndLoginAsRenter();
        Account renterAccount = getCurrentAccount(accountToken);

        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Inspection inspection = new Inspection();
        inspection.setBooking(booking);
        inspection.setType(InspectionType.PICK_UP);
        inspection.setInspectionDate(Instant.now());
        inspection.setStatus(InspectionStatus.PENDING);
        inspectionRepository.save(inspection);

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .when()
                .patch("/inspections/" + inspection.getId() + "/cancel")
                .then()
                .statusCode(200)
                .body("status", equalTo("CANCELLED"));
    }

    @Test
    @DisplayName("Should fail to cancel inspection when not pending")
    void shouldFailToCancelInspectionWhenNotPending() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        String accountToken = createAndLoginAsRenter();
        Account renterAccount = getCurrentAccount(accountToken);

        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Inspection inspection = new Inspection();
        inspection.setBooking(booking);
        inspection.setType(InspectionType.PICK_UP);
        inspection.setInspectionDate(Instant.now());
        inspection.setStatus(InspectionStatus.COMPLETED);
        inspectionRepository.save(inspection);

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .when()
                .patch("/inspections/" + inspection.getId() + "/cancel")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should fail to cancel inspection when not owned")
    void shouldFailToCancelInspectionWhenNotOwned() {
        String partnerToken = createAndLoginAsRentingPartner();
        String anotherPartnerToken = createAndLoginAsRentingPartner();
        Account anotherPartnerAccount = getCurrentAccount(anotherPartnerToken);

        Location location = createAndSaveLocation(anotherPartnerAccount);
        Vehicle vehicle = createVehicle(location, anotherPartnerAccount);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        String accountToken = createAndLoginAsRenter();
        Account renterAccount = getCurrentAccount(accountToken);

        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Inspection inspection = new Inspection();
        inspection.setBooking(booking);
        inspection.setType(InspectionType.PICK_UP);
        inspection.setInspectionDate(Instant.now());
        inspection.setStatus(InspectionStatus.PENDING);
        inspectionRepository.save(inspection);

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .when()
                .patch("/inspections/" + inspection.getId() + "/cancel")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Should upload media successfully")
    void shouldUploadMediaSuccessfully() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        String accountToken = createAndLoginAsRenter();
        Account renterAccount = getCurrentAccount(accountToken);

        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Inspection inspection = new Inspection();
        inspection.setBooking(booking);
        inspection.setType(InspectionType.PICK_UP);
        inspection.setInspectionDate(Instant.now());
        inspection.setStatus(InspectionStatus.PENDING);
        inspectionRepository.save(inspection);

        when(s3FileStorageService.uploadFile(anyString(), any(), anyString(), anyLong(), any()))
                .thenReturn("fake-path/car-image.jpg");
        when(s3FileStorageService.getPublicUrl(anyString(), any()))
                .thenReturn("https://fake-bucket.s3.amazonaws.com/car-image.jpg");

        File imageFile = new File(
                getClass().getClassLoader().getResource("test-medias/car-image.jpg").getFile());

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .multiPart("file", imageFile, "image/jpeg")
                .when()
                .post("/inspections/" + inspection.getId() + "/medias")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Should fail to upload media when not owned")
    void shouldFailToUploadMediaWhenNotOwned() {
        String partnerToken = createAndLoginAsRentingPartner();
        String anotherPartnerToken = createAndLoginAsRentingPartner();
        Account anotherPartnerAccount = getCurrentAccount(anotherPartnerToken);

        Location location = createAndSaveLocation(anotherPartnerAccount);
        Vehicle vehicle = createVehicle(location, anotherPartnerAccount);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        String accountToken = createAndLoginAsRenter();
        Account renterAccount = getCurrentAccount(accountToken);

        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Inspection inspection = new Inspection();
        inspection.setBooking(booking);
        inspection.setType(InspectionType.PICK_UP);
        inspection.setInspectionDate(Instant.now());
        inspection.setStatus(InspectionStatus.PENDING);
        inspectionRepository.save(inspection);

        when(s3FileStorageService.uploadFile(anyString(), any(), anyString(), anyLong(), any()))
                .thenReturn("fake-path/car-image.jpg");
        when(s3FileStorageService.getPublicUrl(anyString(), any()))
                .thenReturn("https://fake-bucket.s3.amazonaws.com/car-image.jpg");

        File imageFile = new File(
                getClass().getClassLoader().getResource("test-medias/car-image.jpg").getFile());

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .multiPart("file", imageFile, "image/jpeg")
                .when()
                .post("/inspections/" + inspection.getId() + "/medias")
                .then()
                .statusCode(403);

    }

    @Test
    @DisplayName("Should fail to upload media when inspection is not pending")
    void shouldFailToUploadMediaWhenInspectionNotPending() {

        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        String accountToken = createAndLoginAsRenter();
        Account renterAccount = getCurrentAccount(accountToken);

        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Inspection inspection = new Inspection();
        inspection.setBooking(booking);
        inspection.setType(InspectionType.PICK_UP);
        inspection.setInspectionDate(Instant.now());
        inspection.setStatus(InspectionStatus.COMPLETED);
        inspectionRepository.save(inspection);

        when(s3FileStorageService.uploadFile(anyString(), any(), anyString(), anyLong(), any()))
                .thenReturn("fake-path/car-image.jpg");
        when(s3FileStorageService.getPublicUrl(anyString(), any()))
                .thenReturn("https://fake-bucket.s3.amazonaws.com/car-image.jpg");

        File imageFile = new File(
                getClass().getClassLoader().getResource("test-medias/car-image.jpg").getFile());

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .multiPart("file", imageFile, "image/jpeg")
                .when()
                .post("/inspections/" + inspection.getId() + "/medias")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should fail to upload more than 20 images for the same vehicle")
    void shouldFailToUploadMoreThan20ImagesForSameVehicle() {
        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        String accountToken = createAndLoginAsRenter();
        Account renterAccount = getCurrentAccount(accountToken);

        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Inspection inspection = new Inspection();
        inspection.setBooking(booking);
        inspection.setType(InspectionType.PICK_UP);
        inspection.setInspectionDate(Instant.now());
        inspection.setStatus(InspectionStatus.PENDING);
        inspectionRepository.save(inspection);

        when(s3FileStorageService.uploadFile(anyString(), any(), anyString(), anyLong(), any()))
                .thenReturn("fake-path/car-image.jpg");
        when(s3FileStorageService.getPublicUrl(anyString(), any()))
                .thenReturn("https://fake-bucket.s3.amazonaws.com/car-image.jpg");

        File imageFile = new File(
                getClass().getClassLoader().getResource("test-medias/car-image.jpg").getFile());

        for (int i = 0; i < 20; i++) {
            given()
                    .header("Authorization", "Bearer " + partnerToken)
                    .multiPart("file", imageFile, "image/jpeg")
                    .when()
                    .post("/inspections/" + inspection.getId() + "/medias")
                    .then()
                    .statusCode(200);
        }

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .multiPart("file", imageFile, "image/jpeg")
                .when()
                .post("/inspections/" + inspection.getId() + "/medias")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should fail to upload unsupported media type")
    void shouldFailToUploadUnsupportedMediaType() {

        String partnerToken = createAndLoginAsRentingPartner();
        Account partnerAccount = getCurrentAccount(partnerToken);

        Location location = createAndSaveLocation(partnerAccount);
        Vehicle vehicle = createVehicle(location, partnerAccount);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        String accountToken = createAndLoginAsRenter();
        Account renterAccount = getCurrentAccount(accountToken);

        Booking booking = createBooking(vehicle, renterAccount, startDate, endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Inspection inspection = new Inspection();
        inspection.setBooking(booking);
        inspection.setType(InspectionType.PICK_UP);
        inspection.setInspectionDate(Instant.now());
        inspection.setStatus(InspectionStatus.PENDING);
        inspectionRepository.save(inspection);

        File unsupportedFile = new File(
                getClass().getClassLoader().getResource("test-medias/unsupported-file.txt").getFile());

        given()
                .header("Authorization", "Bearer " + partnerToken)
                .multiPart("file", unsupportedFile, "text/plain")
                .when()
                .post("/inspections/" + inspection.getId() + "/medias")
                .then()
                .statusCode(415);

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
        booking.setTotalPriceCents((endDate.toEpochDay() - startDate.toEpochDay() + 1) * vehicle.getPricePerDayCents());
        return bookingRepository.save(booking);
    }
}
