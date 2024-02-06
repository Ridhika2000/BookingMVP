package com.rgms.hotel.booking.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rgms.hotel.booking.entities.Booking;
import com.rgms.hotel.booking.services.BookingService;

@RestController
@RequestMapping("/hotelbooking")
public class BookingController {
	
	
	@Autowired
	private BookingService bookingService;
	
	@PostMapping("/users/{userId}/hotel/{hotelId}")
	public ResponseEntity<Booking> bookHotel(@RequestBody Booking booking, @PathVariable String userId, @PathVariable String hotelId) throws Exception{
		Booking b = bookingService.bookHotel(booking, userId, hotelId); 
		return ResponseEntity.ok(b); 
	}
	
	@GetMapping("/hotels/{hotelId}/{checkoutdate}")
	public Integer getBookingsByCheckoutAndId(@PathVariable String hotelId, @PathVariable @DateTimeFormat(pattern="yyyy-MM-dd") Date checkoutdate){
		return bookingService.getBookingsByCheckoutAndId(hotelId, checkoutdate);
	}
	
	@GetMapping("/hotels/{hotelId}")
	public ResponseEntity<List<Booking>> getBookingsByHotelId(@PathVariable String hotelId){
		return ResponseEntity.ok(bookingService.getBookingByHotelId(hotelId));
	}
	
	@PutMapping("/cancel/{bookingId}")
	public ResponseEntity<Booking> cancelBooking(@PathVariable String bookingId) throws Exception{
		
		Booking b = bookingService.cancelBooking(bookingId);
		return ResponseEntity.ok(b);
	}

}
