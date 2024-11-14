package com.team2.clinic.controller;

import com.team2.clinic.model.OrderPatientBean;
import com.team2.clinic.model.OrderViewBean;
import com.team2.clinic.service.OrderPatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class OrderPatientController {

    @Autowired
    private OrderPatientService orderPatientService;
    //=================================== 導向網頁 ===================================

    @GetMapping("/Patient")
    public String showPatient() {
        return "order/OrderPatient";
    }

    //=================================== 下面是功能的實現 ===================================
    //搜尋全部 -> 分頁 desc
    @ResponseBody
    @GetMapping("/findPatientsByPage")
    public Page<OrderPatientBean> findAllPatientsByPage(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "15") int size) {
        return orderPatientService.getPatientsByPage(PageRequest.of(page, size));
    }

    ///單項搜尋的功能 根據搜尋欄位決定回傳資料
    @ResponseBody
    @GetMapping("findPatientsBySerach")
    public Page<OrderPatientBean> getOrdersBySerach(@RequestParam(required = false) String patientName,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (patientName != null) {
            return orderPatientService.getPatientByPatientName(pageable, patientName);
        } else {
            System.out.println("搜尋產生錯誤 (┬┬﹏┬┬)%%??");
            return null;
        }
    }

    // Create 新稱
    @PostMapping("/api/patients")
    public ResponseEntity<OrderPatientBean> addPatient(@RequestBody OrderPatientBean patient) {
        OrderPatientBean newPatient = orderPatientService.addPatient(patient);
//        order.setOrderNumber("OD021");
        return ResponseEntity.ok(newPatient);
    }

    // Update 更新
    @PutMapping("/api/patients/{patientNumber}")
    public ResponseEntity<OrderPatientBean> updatePatient(@PathVariable String patientNumber, @RequestBody OrderPatientBean patient) {
        OrderPatientBean updatePatient = orderPatientService.updatePatient(patient);
        return ResponseEntity.ok(updatePatient);
    }

    // Delete 刪除
    @DeleteMapping("/api/patients/{patientNumber}")
    public ResponseEntity<Void> deletePatient(@PathVariable String patientNumber) {
        orderPatientService.deletePatient(patientNumber);
        return ResponseEntity.noContent().build();
    }


}
