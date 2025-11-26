package com.ecoride.notification_service.repository;


import com.ecoride.notification_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}