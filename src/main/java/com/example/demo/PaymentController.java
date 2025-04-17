package com.example.demo;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/payment")
public class PaymentController {

	
	
	@Autowired
	private PaymentRepository paymentRepository;
    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String razorpayKeySecret;

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> data) throws RazorpayException {
    	int amountInRupees = Integer.parseInt(data.get("amount").toString());
    	int amountInPaise = amountInRupees * 100;

    	System.out.println("Creating Razorpay order for amount: " + amountInRupees);

        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject options = new JSONObject();
        options.put("amount", amountInPaise);

        options.put("currency", "INR");
        options.put("receipt", "txn_" + UUID.randomUUID());

        Order order = client.orders.create(options);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", amountInPaise);
        response.put("key", razorpayKeyId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody Map<String, Object> response) {

        String razorpayPaymentId = (String) response.get("razorpay_payment_id");
        String razorpayOrderId = (String) response.get("razorpay_order_id");
        String razorpaySignature = (String) response.get("razorpay_signature");
        

        try {
            // 1. Verify signature (optional for test mode)
        	String generatedSignature = getSignature(razorpayOrderId + "|" + razorpayPaymentId, razorpayKeySecret);

            if (!generatedSignature.equals(razorpaySignature)) {
                return ResponseEntity.badRequest().body("Invalid signature!");
            }

            // 2. Save to DB
            Payment payment = new Payment();
            payment.setPaymentId(razorpayPaymentId);
            payment.setOrderId(razorpayOrderId);
            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);

            return ResponseEntity.ok("Payment verified and saved!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/razorpay-key")
    @ResponseBody
    public ResponseEntity<String> getRazorpayKey() {
        return ResponseEntity.ok(razorpayKeyId);
    }
    
    
    private String getSignature(String data, String keySecret) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(keySecret.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
        byte[] result = mac.doFinal(data.getBytes());
        return new String(Base64.getEncoder().encode(result));
    }
}
