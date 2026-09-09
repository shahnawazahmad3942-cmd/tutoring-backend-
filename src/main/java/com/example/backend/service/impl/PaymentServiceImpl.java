package com.example.backend.service.impl;

/* Creates a Razorpay order and records a PENDING transaction. Credits are not
granted here — only the webhook, after payment confirmation, writes the ledger entry. */

import com.example.backend.dto.payment.CreateOrderRequest;
import com.example.backend.dto.payment.CreateOrderResponse;
import com.example.backend.entity.CreditPackage;
import com.example.backend.entity.Transaction;
import com.example.backend.entity.User;
import com.example.backend.enums.TransactionStatus;
import com.example.backend.exception.BusinessRuleException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CreditPackageRepository;
import com.example.backend.repository.TransactionRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.PaymentService;
import com.example.backend.enums.LedgerEntryType;
import com.example.backend.service.CreditService;

import com.razorpay.Order;
import com.razorpay.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final String CURRENCY = "INR";
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final RazorpayClient razorpayClient; //talks to Razorpay's API 
    private final UserRepository userRepository;
    private final CreditPackageRepository creditPackageRepository;
    private final TransactionRepository transactionRepository;
    private final String razorpayKeyId;
    private final CreditService creditService;
    private final String webhookSecret;

    public PaymentServiceImpl(
        RazorpayClient razorpayClient,
        UserRepository userRepository,
        CreditPackageRepository creditPackageRepository,
        TransactionRepository transactionRepository,
        @Value("${razorpay.key-id}") String razorpayKeyId,
        CreditService creditService,
        @Value("${razorpay.webhook-secret}") String webhookSecret
    ){
        this.razorpayClient = razorpayClient;
        this.userRepository = userRepository;
        this.creditPackageRepository = creditPackageRepository;
        this.transactionRepository = transactionRepository;
        this.razorpayKeyId = razorpayKeyId;
    }

    @Override
    @Transactional
    public CreateOrderResponse createOrder(Long userId, CreateOrderRequest request){

        User user = userRepository.findById(userId).orElseThrow(
            () -> new ResourceNotFoundException("user not found")
        );

        CreditPackage creditPackage = creditPackageRepository.findById(request.packageId()).orElseThrow(
            () -> new ResourceNotFoundException("credit package not found")
        );

        if(!creditPackage.isActive()){
            throw new BusinessRuleException("This package is no longer available");
        }
        
        //private method for creating the order at razorpay
        Order order = createRazorpayOrder(creditPackage.getPrice());  

        Transaction  transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCreditPackage(creditPackage);
        transaction.setAmountSnapshot(creditPackage.getPrice());
        transaction.setCreditsSnapshot(creditPackage.getCredits());
        transaction.setRazorpayOrderId(order.get("id"));
        transaction.setStatus(TransactionStatus.PENDING);

        Transaction saved = transactionRepository.save(transaction);

        return new CreateOrderResponse(
            saved.getId(),
            saved.getRazorpayOrderId(),
            razorpayKeyId,
            saved.getAmountSnapshot(),
            CURRENCY
        );
    }



    private Order createRazorpayOrder(BigDecimal price) {
        JSONObject options = new JSONObject();
        options.put("amount", price.movePointRight(2).longValueExact());
        options.put("currency", CURRENCY);

        try{
            return razorpayClient.orders.create(options);  //talks to Razorpay's API 
        }catch(RazorpayException ex) {
            throw new BusinessRuleException("Unable to create payment order");
        }
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature){

        
        /*
        Without it, anyone who knows your order id format can 
        POST a fake order.paid and mint themselves credits
         */
        if(!isSignatureValid(payload, signature)){
            throw new BusinessRuleException("Invalid webhook signature");
        }

        JSONObject body = new JSONObject(payload);
        String event = body.optString("event");

        if(!"order.paid".equals(event) && !"payment.captured".equals(event)){
            log.info("Ignoring unhandled Razorpay event: {}", event);
            return;
        }

        JSONObject paymentEntity = body.getJSONObject("payload")
                                       .getJSONObject("payment")
                                       .getJSONObject("entity");
        
        String orderId = paymentEntity.getString("order_id");
        String paymentId = paymentEntity.getString("id");

        Transaction transaction = transactionRepository.findByRazorpayOrderId(orderId).orElse(null);

        if(transaction == null){
            log.warn("Received webhook for unknown order id: {}", orderId);
            return;
        }

        if(transaction.getStatus() == TransactionStatus.SUCCESS){
            log.info("Order {} already settled, ignoring duplicate webhook", orderId);
            return;
        }

        transaction.setRazorpayPaymentId(paymentId);
        transaction.setStatus(TransactionStatus.SUCCESS);

        creditService.recordTransactionEntry(
            transaction.getUser(),
            transaction,
            LedgerEntryType.PURCHASE,
            transaction.getCreditsSnapshot()
        );

        log.info("Granted {} credits to user {} for order {}", 
            transaction.getCreditsSnapshot(), transaction.getUser().getId(), orderId
        );

    }

    private boolean isSignatureValid(String payload, String signature){
        try{
             return Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (RazorpayException ex) {
             return false;
        }
    }

}
