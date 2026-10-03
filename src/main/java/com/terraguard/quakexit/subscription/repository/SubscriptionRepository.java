package com.terraguard.quakexit.subscription.repository;

import com.terraguard.quakexit.subscription.entity.Subscription;
import com.terraguard.quakexit.subscription.model.SubscriptionEnums.SubscriptionStatus;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findTopBySubscriberIdAndStatusInOrderByCreatedAtDesc(Long subscriberId, Collection<SubscriptionStatus> statuses);
    Optional<Subscription> findTopBySubscriberIdOrderByCreatedAtDesc(Long subscriberId);
}