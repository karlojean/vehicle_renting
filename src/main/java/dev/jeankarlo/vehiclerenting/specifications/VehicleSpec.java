package dev.jeankarlo.vehiclerenting.specifications;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import dev.jeankarlo.vehiclerenting.entity.Booking;
import dev.jeankarlo.vehiclerenting.entity.Location;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.entity.enums.BookingStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class VehicleSpec {
    public static Specification<Vehicle> hasCity(String city) {
        return (root, query, criteriaBuilder) -> {
            Join<Vehicle, Location> locationJoin = root.join("location");

            return criteriaBuilder.equal(criteriaBuilder.lower(locationJoin.get("city")), city.toLowerCase());
        };
    }

    public static Specification<Vehicle> isAvailable(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {

            Subquery<Booking> subquery = query.subquery(Booking.class);
            Root<Booking> bookingRoot = subquery.from(Booking.class);

            Predicate vehicleMatch = criteriaBuilder.equal(bookingRoot.get("vehicle"), root);

            Predicate notCancelled = criteriaBuilder.notEqual(bookingRoot.get("status"), BookingStatus.CANCELLED);

            Predicate startOverlap = criteriaBuilder.lessThan(bookingRoot.get("startDate"), endDate);
            Predicate endOverlap = criteriaBuilder.greaterThan(bookingRoot.get("endDate"), startDate);

            subquery.select(bookingRoot)
                    .where(criteriaBuilder.and(vehicleMatch, notCancelled, startOverlap, endOverlap));

            return criteriaBuilder.not(criteriaBuilder.exists(subquery));
        };
    }

}
