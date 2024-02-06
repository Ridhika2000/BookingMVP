package com.rgms.hotel.service.services;


import java.util.Date;
import java.util.List;

import com.rgms.hotel.service.entities.Hotel;

public interface HotelService {

	Hotel saveHotel(Hotel hotel);
	
	List<Hotel> getAllHotels() throws Exception;
	
	Hotel getHotelById(String id) throws Exception;
	
	List<Hotel> searchHotels(String location, Date checkindate, Date checkoutdate, Integer noOfRooms, String hotelName, String sortBy) throws Exception;


	void hotelBooking(String bookingPayload) throws Exception;


	void cancelBooking(String bookingPayload) throws Exception;


	Hotel getHotelBookings(String hotelId) throws Exception;
	
}
