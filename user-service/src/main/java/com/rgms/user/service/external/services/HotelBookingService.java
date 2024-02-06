package com.rgms.user.service.external.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.rgms.user.service.entities.HotelBooking;

@FeignClient(name = "HOTEL-BOOKING-SERVICE")
public interface HotelBookingService {

	//book a hotel
	@PostMapping("/booking/users/{userId}/hotel/{hotelId}")
	public HotelBooking bookHotel(HotelBooking booking, @PathVariable String userId, @PathVariable String hotelId);
}
