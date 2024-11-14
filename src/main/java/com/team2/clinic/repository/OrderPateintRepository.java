package com.team2.clinic.repository;

import com.team2.clinic.model.OrderViewBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.team2.clinic.model.OrderPatientBean;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderPateintRepository extends JpaRepository<OrderPatientBean,String> {
    //照患者id順序拿全部資料 DESC
    @Query(value = "SELECT * from PatientInfo ORDER BY pt_id DESC",nativeQuery = true)
    Page<OrderPatientBean> findAllByPTId(Pageable pageable);

    @Query(value = "SELECT * FROM PatientInfo WHERE pt_name = ?1 ORDER BY pt_id DESC",nativeQuery = true)
    Page<OrderPatientBean> findAllByPatientName(String patientName,Pageable pageable);


//    OrderPatientBean findByPatientName(String patientName);
    //根據患者名稱回傳布林值
    boolean existsByPatientName(String patientName);

    List<OrderPatientBean> findByPatientName(String patientName);
}
