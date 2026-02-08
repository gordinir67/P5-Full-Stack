package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    boolean existsByUserIdAndThemeId(Long userId, Integer themeId);
    Optional<Subscription> findByUserIdAndThemeId(Long userId, Integer themeId);
    List<Subscription> findAllByUserId(Long userId);
}