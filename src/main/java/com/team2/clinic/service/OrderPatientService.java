package com.team2.clinic.service;

import com.team2.clinic.model.OrderPatientBean;
import com.team2.clinic.model.OrderViewBean;
import com.team2.clinic.repository.OrderPateintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderPatientService {
    @Autowired
    private OrderPateintRepository repo;

    //根據分頁查詢患者
    @Transactional
    public Page<OrderPatientBean> getPatientsByPage(Pageable pageable) {
        return repo.findAllByPTId(pageable);
    }

    //查詢患者 => 回傳布林值
    public boolean checkPatientExists(String patientName) {
        return repo.existsByPatientName(patientName);
    }

    //查詢患者 => 回傳Id
    public String getPatientIdByName(String patientName) {
//        OrderPatientBean patients = repo.findByPatientName(patientName);
        List<OrderPatientBean> patients = repo.findByPatientName(patientName);
        //處理返回同名字的患者
        if (patients == null) {
            return null;
        } else if (patients.size() == 1) {
            return patients.get(0).getPatientId();
        } else {
            // 處理 多個患者同名 的情況
            // 返回第一個結果
             return patients.get(0).getPatientId();
        }
    }
    //根據患者名稱搜尋
    @Transactional
    public Page<OrderPatientBean> getPatientByPatientName(Pageable pageable, String patientName) {
        return repo.findAllByPatientName(patientName, pageable);
    }

    // 新增
    @Transactional
    public OrderPatientBean addPatient(OrderPatientBean OrderPatientBean) {
        return repo.save(OrderPatientBean);
    }

    // 修改
    @Transactional
    public OrderPatientBean updatePatient(OrderPatientBean OrderPatientBean) {
        return repo.save(OrderPatientBean);
    }

    // 刪除
    @Transactional
    public void deletePatient(String patientNumber) {
        repo.deleteById(patientNumber);
    }

    public OrderPatientBean getPatientById(String patientId) {
        return repo.findById(patientId).orElse(null);
    }
}
