package com.team2.clinic.repository;

import com.team2.clinic.model.OrderTreatmentBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTreatmentRepository extends JpaRepository<OrderTreatmentBean,String> {
    OrderTreatmentBean findByTreatmentName(String treatmentName);
}
