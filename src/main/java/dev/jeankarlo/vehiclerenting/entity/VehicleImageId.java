package dev.jeankarlo.vehiclerenting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class VehicleImageId implements Serializable {
    private static final long serialVersionUID = 7250228533023293575L;
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "image_id", nullable = false)
    private Long imageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VehicleImageId entity = (VehicleImageId) o;
        return Objects.equals(this.imageId, entity.imageId) &&
                Objects.equals(this.vehicleId, entity.vehicleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageId, vehicleId);
    }

}