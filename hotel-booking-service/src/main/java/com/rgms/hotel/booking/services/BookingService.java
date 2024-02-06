package com.rgms.hotel.booking.services;

import java.util.Date;
import java.util.List;

import com.rgms.hotel.booking.entities.Booking;



public interface BookingService {
	
	Booking bookHotel(Booking book, String userId, String hotelId) throws Exception;

	void confirmBooking(String bookingId);

	void unconfirmBooking(String bookingId);

	List<Booking> getBookingByHotelId(String hotelId);

	Integer getBookingsByCheckoutAndId(String hotelId, Date checkoutdate);

	Booking cancelBooking(String bookingId) throws Exception;

	void cancelHotelBooking(String bookingId);

	void hotelNotAvailable(String bookingId);

	void hotelPaymentDeclined(String bookingId);

}
