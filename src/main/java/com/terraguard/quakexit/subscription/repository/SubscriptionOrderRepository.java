package com.terraguard.quakexit.subscription.repository;

import com.terraguard.quakexit.subscription.entity.SubscriptionOrder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionOrderRepository extends JpaRepository<SubscriptionOrder, Long> {
    Optional<SubscriptionOrder> findByOrderCode(String orderCode);
}