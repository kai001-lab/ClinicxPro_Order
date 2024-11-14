package com.team2.clinic.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.team2.clinic.model.OrderViewBean;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderStripeService {
    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    @Value("${app.domain}")
    private String domain;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
    //最簡單的表單 -> 最醜
//    public Charge charge(ChargeRequest chargeRequest)throws StripeException {
//        Map chargeParams = new HashMap();
//        chargeParams.put("amount", chargeRequest.getAmount());
//        chargeParams.put("currency", chargeRequest.getCurrency());
//        chargeParams.put("description", chargeRequest.getDescription());
//        chargeParams.put("source", chargeRequest.getStripeToken());
//        return Charge.create(chargeParams);
//    }

    public Session createCheckoutSession(OrderViewBean order) throws StripeException {
        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("twd")
                                .setUnitAmount(getLongAmount(order))
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName("療程項目:" + order.getTreatment().getTreatmentName())
                                                .setDescription("名稱 : " + order.getPatient().getPatientName())
                                                .build()
                                )
                                .build()
                )
                .setQuantity(1L)
                .build();
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(domain + "/order/success?orderNumber=" + order.getOrderNumber())
                .setCancelUrl(domain + "/order/cancel?orderNumber=" + order.getOrderNumber())
                .addLineItem(lineItem)
                .build();

        return Session.create(params);
    }

    // 將金額轉換為長整數的function
    private Long getLongAmount(OrderViewBean order) {
        double amount = order.getTreatment().getPrice();
        return (long) (amount * 100);
    }
}
