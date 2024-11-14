package com.team2.clinic.controller;

import com.team2.clinic.model.OrderPatientBean;
import com.team2.clinic.model.OrderTreatmentBean;
import com.team2.clinic.model.OrderViewBean;
import com.team2.clinic.service.OrderPatientService;
import com.team2.clinic.service.OrderService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Controller
public class OrderController extends HttpServlet {
    //調用logger
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderPatientService orderPatientService;

    //=================================== 導向網頁 ===================================
    @GetMapping("/Order")
    public String showOrderView() {
        return "/order/Order";
    }

    // 給顧客看的頁面
    @GetMapping("/Customer")
    public String showCustomer() {
        return "/order/Customer";
    }

    @GetMapping("/order/query")
    public String showQueryPage() {
        return "/order/OrderQuery";
    }

    //===============================下面是功能的實現==================
    //===============================CRUD============================
    //===============================RestFul風格======================
    //    寄信功能༼ つ ◕_◕ ༽つ༼ つ ◕_◕ ༽つ༼ つ ◕_◕ ༽つ
    @PostMapping("/order/sendEmail")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody Map<String, String> requestData, HttpServletRequest request) {
        String email = requestData.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "電子郵件地址不能為空",
                            "status", "error"));
        }
        try {
            log.info("接收到發送郵件請求: {}", email);
            //開始時間
            long Start = System.currentTimeMillis();

            String subject = "付款通知";
            String imgpath = "src/main/resources/static/img/dogo.png";
            String content = "<html><body style=\"margin: 0; padding: 0; font-family: 'Microsoft JhengHei', Arial, sans-serif; line-height: 1.6;\"><div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\"><div style=\"background-color: #2c5282; padding: 20px; border-radius: 8px 8px 0 0;\"><h1 style=\"color: #ffffff; margin: 0; font-size: 24px;\">ClinicxPro 付款通知</h1></div><div style=\"background-color: #ffffff; padding: 20px; border: 1px solid #e2e8f0;\"><p style=\"font-size: 16px; color: #2d3748; margin-bottom: 20px;\">親愛的顧客您好：</p><p style=\"font-size: 16px; color: #2d3748; margin-bottom: 15px;\">感謝您選擇 ClinicxPro 的服務。您有待付款項需要處理：</p><div style=\"text-align: center; margin: 30px 0;\"><a href=\"" + "http://localhost:8080/clinic/Customer\" style=\"background-color: #4299e1; color: #ffffff; padding: 12px 30px; text-decoration: none; border-radius: 5px; font-weight: bold;\">點擊此處前往付款</a></div><div style=\"background-color: #ebf8ff; padding: 15px; border-left: 4px solid #4299e1; margin: 20px 0;\"><p style=\"margin: 0; color: #2b6cb0;\">⏰ 請儘速完成付款，以確保您的預約權益</p></div><p style=\"font-size: 14px; color: #718096; margin-top: 20px;\">如有任何問題，請隨時聯繫我們的客服團隊</p></div><div style=\"background-color: #f7fafc; padding: 15px; text-align: center; border-radius: 0 0 8px 8px;\"><p style=\"color: #718096; font-size: 14px; margin: 0;\">此為系統自動發送郵件，請勿直接回覆</p></div></div></body></html>";

            CompletableFuture<Boolean> future = orderService.sendPlainText(email, subject, content, imgpath);

            // 設定超時時間為10秒
            // 阻止結果直接回傳 -> 等待service 回傳結果
            Boolean result = future.get(20, TimeUnit.SECONDS);

            //從service這邊得到 boolean -> true or false
            if (result) {
                return ResponseEntity.ok(Map.of(
                        "message", "郵件發送成功",
                        "status", "success"
                ));
            } else {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "message", "郵件發送失敗",
                                "status", "error"
                        ));
            }

        } catch (TimeoutException e) {
            log.error("郵件發送超時: ", e);
            return ResponseEntity
                    .status(HttpStatus.REQUEST_TIMEOUT)
                    .body(Map.of(
                            "message", "郵件發送超時，請稍後再試",
                            "status", "error"
                    ));

        } catch (Exception e) {
            log.error("郵件發送異常: ", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "郵件發送時發生異常",
                            "status", "error"
                    ));
        }
    }

    //根據日期者出每筆訂單 -> 回傳總價錢
    @PostMapping("/order/getPrice")
    public ResponseEntity<Integer> getPrice(@RequestBody Map<String, String> requestData) {
        String DATE = requestData.get("date");
//        Date date = new Date(DATE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(DATE);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("發生錯誤!!!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1);
        }
//        Date date = requestData.get("date");
        int TotalPrice = orderService.getTotalPricesByDate(date);

        return ResponseEntity.ok(TotalPrice);
    }


    //所有訂單的分頁 Read
    //用JS寫法
    @ResponseBody
    @GetMapping("/findOrdersByPage")
    public Page<OrderViewBean> getOrdersByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getOrdersByPage(PageRequest.of(page, size));
    }

    // Create 新稱
    //根據患者名稱搜尋 => 回傳布林值(患者存在與否) & patientId
    @GetMapping("/api/orders/check")
    public ResponseEntity<Map<String, Object>> checkPatient(@RequestParam String patientName) {
        String patientId = orderPatientService.getPatientIdByName(patientName);
//        boolean exists = orderPatientService.checkPatientExists(patientName);
        Map<String, Object> response = new HashMap<>();
        if (patientId != null) {
            response.put("exists", true);
            response.put("patientId", patientId);
        } else {
            response.put("exists", false);
        }
        return ResponseEntity.ok(response);
    }

    //得到所有療程 => 用來填充下拉式選單
    @GetMapping("/api/orders/treatments")
    public ResponseEntity<List<OrderTreatmentBean>> getAllTreatments() {
        List<OrderTreatmentBean> treatments = orderService.getAllTreatments();
        return ResponseEntity.ok(treatments);
    }

    //新增
    @ResponseBody
    @PostMapping("/api/orders")
    public ResponseEntity<OrderViewBean> addOrder(@RequestBody OrderViewBean order) {
        OrderViewBean newOrder = orderService.addOrder(order);
        return ResponseEntity.ok(newOrder);
    }

    // Update 更新
    @PutMapping("/api/orders/{orderNumber}")
    public ResponseEntity<OrderViewBean> updateOrder(@PathVariable String orderNumber, @RequestBody OrderViewBean order) {
        OrderViewBean updateOrder = orderService.updateOrder(order);
        return ResponseEntity.ok(updateOrder);
    }

    // Delete 刪除
    @DeleteMapping("/api/orders/{orderNumber}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderNumber) {
        orderService.deleteOrder(orderNumber);
        return ResponseEntity.noContent().build();
    }

    //單項搜尋的功能 根據搜尋欄位決定回傳資料
    @ResponseBody
    @GetMapping("findOrdersBySerach")
    public Page<OrderViewBean> getOrdersBySerach(@RequestParam(required = false) String patientName,
                                                 @RequestParam(required = false) String treatmentItem,
                                                 @RequestParam(required = false) Integer threapyStatus,
                                                 @RequestParam(required = false) Integer payStatus,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (patientName != null) {
            return orderService.getOrdersByPatientName(patientName, pageable);
        } else if (treatmentItem != null) {
            return orderService.getOrdersByTreatmentName(treatmentItem, pageable);
        } else if (threapyStatus != null) {
            return orderService.getOrdersByThreapyStatus(threapyStatus, pageable);
        } else if (payStatus != null) {
            return orderService.getOrdersByPayStatus(payStatus, pageable);
        } else {
            System.out.println("搜尋產生錯誤 (┬┬﹏┬┬)%%??");
            return null;
        }
    }

    //日期區間搜尋
    //傳進來的日期格式會有錯誤 -> @DateTimeFormat(pattern = "yyyy-MM-dd")
    //傳進來的日期格式會有錯誤 -> @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ResponseBody
    @GetMapping("findOrdersByDateRange")
    public Page<OrderViewBean> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderService.getOrderByDateRange(startDate, endDate, pageable);
    }
}
