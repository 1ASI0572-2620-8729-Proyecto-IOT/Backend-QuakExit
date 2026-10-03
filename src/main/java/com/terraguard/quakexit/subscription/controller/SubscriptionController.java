package com.terraguard.quakexit.subscription.controller;

import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.subscription.dto.SubscriptionDtos.*;
import com.terraguard.quakexit.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/subscriptions") @RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService service;
    private final UserRepository users;
    @GetMapping("/plans") public List<PlanResponse> plans() { return service.plans(); }
    @GetMapping("/current") public CurrentResponse current(Authentication authentication) { return service.current(user(authentication)); }
    @GetMapping("/features") public List<String> features(Authentication authentication) { return service.features(user(authentication)); }
    @PostMapping("/checkout") public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request, Authentication authentication) { return service.checkout(request, user(authentication)); }
    @PostMapping("/simulate-payment") public PaymentResponse simulate(@Valid @RequestBody PaymentRequest request, Authentication authentication) { return service.simulatePayment(request, user(authentication)); }
    @PostMapping("/webhook") public PaymentResponse webhook(@Valid @RequestBody PaymentRequest request, Authentication authentication) { return service.webhook(request, user(authentication)); }
    @PostMapping("/cancel") public CancelResponse cancel(Authentication authentication) { return service.cancel(user(authentication)); }
    @PostMapping("/renew") public CheckoutResponse renew(Authentication authentication) { return service.renew(user(authentication)); }
    private com.terraguard.quakexit.iam.entity.User user(Authentication authentication) { return users.findByEmailIgnoreCase(authentication.getName()).orElseThrow(); }
}