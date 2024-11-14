package com.team2.clinic.repository;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.team2.clinic.model.OrderViewBean;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderViewBean, String> {
    //================================================ 搜尋 ========================================================
    //四種搜尋
    //改用 JPQL 查詢 避免ID重複問題
    //1. 患者名稱
    @Query("SELECT o FROM OrderViewBean o JOIN o.patient p WHERE p.patientName = :patientName ORDER BY o.orderNumber DESC")
    Page<OrderViewBean> findAllByPatientName(String patientName, Pageable pageable);

    //2.療程名稱
    @Query("SELECT o FROM OrderViewBean o JOIN o.treatment t WHERE t.treatmentName = :treatmentName ORDER BY o.orderNumber DESC")
    Page<OrderViewBean> findAllByTreatment(@Param("treatmentName") String treatmentName, Pageable pageable);

    //3.療程進度
    @Query(value = "SELECT * from OrderContent WHERE th_status = ?1 ORDER BY ordernum DESC", nativeQuery = true)
    Page<OrderViewBean> findByThreapyStatus(int threapyStatus, Pageable pageable);

    //4.付款狀況
    @Query(value = "SELECT * from OrderContent WHERE pay_status = ?1 ORDER BY ordernum DESC", nativeQuery = true)
    Page<OrderViewBean> findByPayStatus(int payStatus, Pageable pageable);

    //日期區間搜尋
    @Query(value = "SELECT * from OrderContent WHERE orderdate BETWEEN ?1 AND ?2 ORDER BY ordernum DESC", nativeQuery = true)
    Page<OrderViewBean> findByOrderDateBetween(Date startDate, Date endDate, Pageable pageable);

    List<OrderViewBean> findOrderByOrderDate(Date orderDate);

    //================================================ 搜尋 ========================================================
    //照時間順序拿全部資料 DESC
    @Query(value = "SELECT * from OrderContent ORDER BY ordernum DESC", nativeQuery = true)
    Page<OrderViewBean> findAllByDate(Pageable pageable);

    //拿一筆訂單資料(金流)
    @Query(value = "SELECT * from OrderContent WHERE ordernum = ?1", nativeQuery = true)
    OrderViewBean findByOrderNumber(String orderNumber);

    //根據姓名跟ID 查詢資料 JPQL
    @Query("SELECT o FROM OrderViewBean o JOIN o.patient p " +
            "WHERE p.patientName = :patientName AND p.patientIdCard = :patientIdCard " +
            "ORDER BY o.orderNumber DESC")
    Page<OrderViewBean> findByPatientNameAndPatientIdCard(
            @Param("patientName") String patientName,
            @Param("patientIdCard") String patientIdCard,
            Pageable pageable
    );
}
