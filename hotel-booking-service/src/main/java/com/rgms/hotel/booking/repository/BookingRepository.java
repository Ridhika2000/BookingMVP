package com.rgms.hotel.booking.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.rgms.hotel.booking.entities.Booking;



public interface BookingRepository extends MongoRepository<Booking,String>{
	
	Booking findByBookingId(String bookingId);
	
	@Query(value = "{$and : [{hotelId: ?0},{bookingStatus: ?1}]}")
	List<Booking> findByHotelId(String hotelId, String status);

	
	@Query(value = "{$and :[{hotelId: ?0},{checkOutDate: ?1},{bookingStatus: ?2}] }")
	List<Booking> findBookingsByCheckoutAndId(String hotelId, Date checkoutdate, String bookingStatus);

}
