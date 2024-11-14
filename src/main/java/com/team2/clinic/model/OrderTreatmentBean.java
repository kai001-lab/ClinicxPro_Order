package com.team2.clinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Entity
@Table(name = "Treatment")
@Component
@Getter @Setter
public class OrderTreatmentBean implements Serializable {
    @Id @Column(name = "treatment_id")
    private String treatmentId;
    @Column(name = "treatment_name")
    private String treatmentName;
    @Column(name = "price")
    private Integer price;

    public OrderTreatmentBean() {
    }

    public OrderTreatmentBean(Integer price, String treatmentId, String treatmentName) {
        this.price = price;
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
    }
}
