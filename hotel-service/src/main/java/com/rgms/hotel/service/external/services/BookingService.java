package com.rgms.hotel.service.external.services;

import java.util.Date;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.rgms.hotel.service.entities.Booking;


@FeignClient(name = "HOTEL-BOOKING-SERVICE")
public interface BookingService {

    @GetMapping("/hotelbooking/hotels/{hotelId}")
    List<Booking> getBookingsByHotelId(@PathVariable String hotelId);
    
    @GetMapping("/hotelbooking/hotels/{hotelId}/{checkoutdate}")
    Integer getBookingsByCheckoutAndId(@PathVariable String hotelId, @PathVariable @DateTimeFormat(pattern="yyyy-MM-dd") Date checkoutdate);

	

}
