package ru.practicum.shareit.item.repository;

import java.time.LocalDateTime;

public interface ItemWithBookingProjection {

	Long getId();

	String getName();

	String getDescription();

	Boolean getAvailable();

	Long getLastBookingEntityId();

	Long getNextBookingEntityId();

	Long getLastBookingEntityBookerId();

	Long getNextBookingEntityBookerId();

	LocalDateTime getLastBookingEntityStart();

	LocalDateTime getLastBookingEntityEnd();

	LocalDateTime getNextBookingEntityStart();

	LocalDateTime getNextBookingEntityEnd();

}