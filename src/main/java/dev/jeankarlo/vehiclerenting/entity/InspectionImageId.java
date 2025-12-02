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
public class InspectionImageId implements Serializable {
    private static final long serialVersionUID = -4456560082660127778L;
    @Column(name = "inspection_id", nullable = false)
    private Long inspectionId;

    @Column(name = "image_id", nullable = false)
    private Long imageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        InspectionImageId entity = (InspectionImageId) o;
        return Objects.equals(this.inspectionId, entity.inspectionId) &&
                Objects.equals(this.imageId, entity.imageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inspectionId, imageId);
    }

}