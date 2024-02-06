package com.rgms.hotel.service.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rgms.hotel.service.entities.Hotel;
import com.rgms.hotel.service.services.HotelService;


@RestController
@RequestMapping("/hotels")
public class HotelController {

	@Autowired
	private HotelService hotelService;
	
	// create
	@PostMapping
	public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {
		Hotel h = hotelService.saveHotel(hotel);
		return ResponseEntity.status(HttpStatus.CREATED).body(h);
	}

	// get hotel bookings
	@GetMapping("/{hotelId}/booking")
	public ResponseEntity<Hotel> getHotelBookings(@PathVariable String hotelId) throws Exception {
		Hotel hotel = hotelService.getHotelBookings(hotelId);
		return ResponseEntity.ok(hotel);
	}

	// get hotel by id
	@GetMapping("/{hotelId}")
	public ResponseEntity<Hotel> getHotelById(@PathVariable String hotelId) throws Exception {
		Hotel hotel = hotelService.getHotelById(hotelId);
		return ResponseEntity.ok(hotel);
	}

	// get all hotels
	@GetMapping
	public ResponseEntity<List<Hotel>> getAllHotels() throws Exception {
		List<Hotel> allHotels = hotelService.getAllHotels();
		return ResponseEntity.ok(allHotels);
	}

	// searching hotels
	@GetMapping("/search")
	public ResponseEntity<List<Hotel>> searchHotels(@RequestParam(value="location",required = false) String location,
			@RequestParam("checkin") @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkindate,
			@RequestParam("checkout") @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkoutdate,
			@RequestParam(defaultValue = "1") Integer noofrooms, @RequestParam(value="hotelname",required = false) String hotelname,
			@RequestParam(defaultValue = "price") String sortby) throws Exception {
		System.out.println("gggggggggggg");
		List<Hotel> hotels = hotelService.searchHotels(location, checkindate, checkoutdate, noofrooms, hotelname,
				sortby);
		return ResponseEntity.ok(hotels);

	}
	
}
