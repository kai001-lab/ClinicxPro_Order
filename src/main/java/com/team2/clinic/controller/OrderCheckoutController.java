package com.team2.clinic.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.team2.clinic.model.OrderViewBean;
import com.team2.clinic.service.OrderService;
import com.team2.clinic.service.OrderStripeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class OrderCheckoutController {
    @Autowired
    private OrderStripeService stripeService;
    @Autowired
    private OrderService orderService;

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    //結帳頁面
    @GetMapping("/checkout")
    public String showCheckoutPage() {
        return "order/CustomerCheckOut";
    }

    @PostMapping("/order/search")
    public String searchOrders(@RequestParam String patientName,
                               @RequestParam String patientIdCard,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "4") int size,
                               Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderViewBean> orderPage =
                    orderService.findOrdersByPatientNameAndIdCard(patientName, patientIdCard, pageable);
            model.addAttribute("orders", orderPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", orderPage.getTotalPages());
            return "/order/OrderList";
        } catch (Exception e) {
            model.addAttribute("error", "查詢失敗：" + e.getMessage());
            return "/order/OrderList";
        }
    }

    @PostMapping("/order/create-checkout")
    public void createCheckoutSession(@RequestParam("orderNumber") String orderNumber,
                                      HttpServletResponse response) throws IOException {
        try {
            // 根據訂單號碼獲取訂單
            OrderViewBean order = orderService.getOrderByNumber(orderNumber);

            if (order != null) {
                // 創建 Stripe Session
                Session session = stripeService.createCheckoutSession(order);
                // 重定向到 Stripe 支付頁面
                response.sendRedirect(session.getUrl());
            } else {
                response.sendRedirect("/clinic/error");
            }
        } catch (StripeException e) {
            response.sendRedirect("/clinic/error");
        }
    }

    //成功支付後
    @GetMapping("/order/success")
    public String handlePaymentSuccess(@RequestParam("orderNumber") String orderNumber) {
        try {
            // 更新訂單狀態為已支付
            OrderViewBean order = orderService.getOrderByNumber(orderNumber);
            if (order != null) {
                order.setPayStatus(1);
                order.setPayDate(new Date());
                orderService.updateOrder(order);
            }
            return "/order/success";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

    //取消支付後
    @GetMapping("/order/cancel")
    public String handlePaymentCancel(@RequestParam("orderNumber") String orderNumber) {
        return "/order/cancel";
    }

    // 顯示訂單確認頁面
    @GetMapping("/order/confirm/{orderNumber}")
    public String showConfirmationPage(@PathVariable String orderNumber, Model model) {
        try {
            OrderViewBean order = orderService.getOrderByNumber(orderNumber);
            if (order == null) {
                return "redirect:/error";
            }
            model.addAttribute("order", order);
            model.addAttribute("stripePublicKey", stripePublicKey);
            return "/order/OrderConfirmation";
        } catch (Exception e) {
            return "redirect:/error";
        }
    }

}

