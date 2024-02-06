package com.rgms.hotel.booking.external.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.rgms.hotel.booking.entities.Hotel;

@FeignClient(name = "HOTEL-SERVICE")
public interface HotelService {
	
	//get hotel by id
	@GetMapping("/hotels/{hotelId}")
	public Hotel getHotelById(@PathVariable String hotelId);
		
}
