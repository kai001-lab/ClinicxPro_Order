package com.team2.clinic.service;

import com.team2.clinic.model.OrderPatientBean;
import com.team2.clinic.model.OrderTreatmentBean;
import com.team2.clinic.model.OrderViewBean;
import com.team2.clinic.repository.OrderPateintRepository;
import com.team2.clinic.repository.OrderRepository;
import com.team2.clinic.repository.OrderTreatmentRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.lang.Integer.parseInt;

@Service
public class OrderService {
    //調用logger
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private OrderRepository repo;
    @Autowired
    private OrderTreatmentRepository treatmentRepository;
    @Autowired
    private OrderPateintRepository patientRepository;
    @Autowired
    private OrderViewBean order = new OrderViewBean();
    private String payStatus;


    //    寄信功能༼ つ ◕_◕ ༽つ༼ つ ◕_◕ ༽つ༼ つ ◕_◕ ༽つ
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public CompletableFuture<Boolean> sendPlainText(String receivers, String subjects, String content, String imagePath) {
        //中斷mail才會使用
        //線程有沒有被中斷 -> 回傳controller
//        if (Thread.currentThread().isInterrupted()) {
//            return CompletableFuture.completedFuture(false);
//        }

        try {
            log.info("開始發送郵件至: {}", receivers);
            //開始計時
            long startTime = System.currentTimeMillis();

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(receivers);
            helper.setSubject(subjects);
            helper.setText(content, true);
            helper.setFrom("ClinicxPro<xxx@gmail.com>");

            mailSender.send(mimeMessage);

            //結束計時
            long endTime = System.currentTimeMillis();
            log.info("郵件發送完成，耗時: {} ms", (endTime - startTime));

            return CompletableFuture.completedFuture(true);
        } catch (MessagingException e) {
            log.error("郵件發送失敗: ", e);
            return CompletableFuture.completedFuture(false);
        }
    }
//    寄信功能༼ つ ◕_◕ ༽つ༼ つ ◕_◕ ༽つ༼ つ ◕_◕ ༽つ

    //根據分頁查詢訂單  DESC
    @Transactional
    public Page<OrderViewBean> getOrdersByPage(Pageable pageable) {
        return repo.findAllByDate(pageable);
    }

    //    =====================================================================================================

    //確認患者有沒有存在 (用姓名去找)
//    public OrderPatientBean findPatientByName(String patientName) {
//        return patientRepository.findByPatientName(patientName);
//    }
    //全部療程 -> 下拉式選單
    public List<OrderTreatmentBean> getAllTreatments() {
        return treatmentRepository.findAll();
    }

    // 新增訂單 ver2
    @Transactional
    public OrderViewBean addOrder(OrderViewBean orderViewBean) {
        return repo.save(orderViewBean);

    }

    // 修改訂單
    @Transactional
    public OrderViewBean updateOrder(OrderViewBean orderViewBean) {
        return repo.save(orderViewBean);

    }

    // 刪除訂單
    @Transactional
    public void deleteOrder(String orderNumber) {
        repo.deleteById(orderNumber);
    }

    //        四項查詢
    //     1.
    //根據患者名稱搜尋 -> 回傳Page
    @Transactional
    public Page<OrderViewBean> getOrdersByPatientName(String patientName, Pageable pageable) {
        return repo.findAllByPatientName(patientName, pageable);
    }

    //    2.
    //根據療程搜尋 -> 回傳Page
    public Page<OrderViewBean> getOrdersByTreatmentName(String treatmentName, Pageable pageable) {
        return repo.findAllByTreatment(treatmentName, pageable);
    }

    //    3.
    @Transactional
    public Page<OrderViewBean> getOrdersByThreapyStatus(int threapyStatus, Pageable pageable) {
        return repo.findByThreapyStatus(threapyStatus, pageable);
    }

    //    4.
    @Transactional
    public Page<OrderViewBean> getOrdersByPayStatus(int payStatus, Pageable pageable) {
        return repo.findByPayStatus(payStatus, pageable);
    }

    //日期區間搜尋
    @Transactional
    public Page<OrderViewBean> getOrderByDateRange(Date startDate, Date endDate, Pageable pageable) {
        return repo.findByOrderDateBetween(startDate, endDate, pageable);
    }

    public OrderTreatmentBean getTreatmentById(String treatmentId) {
        return treatmentRepository.findById(treatmentId).get();
    }

    //根據日期找出每一筆訂單的價格
    public Integer getTotalPricesByDate(Date date) {
        List<OrderViewBean> Orders = repo.findOrderByOrderDate(date);
        int TotalPrice = 0;

        for (OrderViewBean order : Orders) {
//             int price = Integer.parseInt(order.getTreatment().getPrice());
            TotalPrice += order.getTreatment().getPrice();
            System.out.println(order.getTreatment().getPrice());
        }
        return TotalPrice;
    }

    public OrderViewBean getOrderByNumber(String orderNumber) {
        return repo.findById(orderNumber).get();
    }

    public Page<OrderViewBean> findOrdersByPatientNameAndIdCard(String patientName, String patientIdCard, Pageable pageable) {
        return repo.findByPatientNameAndPatientIdCard(patientName, patientIdCard, pageable);
    }
}
