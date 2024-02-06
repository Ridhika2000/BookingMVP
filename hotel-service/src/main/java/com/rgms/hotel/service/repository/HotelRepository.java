package com.rgms.hotel.service.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.rgms.hotel.service.entities.Hotel;

public interface HotelRepository extends MongoRepository<Hotel, String>{

	@Query(fields  = "{bookings: 0}")
	Hotel findByHotelId(String hotelId);
	
	Hotel getByHotelId(String hotelId);

	List<Hotel> findByRating(Float rating);
	List<Hotel> findByLocation(String location);
	
	@Query(value = "{$or :[{location: ?0},{hotelName: ?1}]}")
	List<Hotel> findHotels(String location, String hotelName, Sort sort);
	
	
	@Query(value = "{$and :[{hotelId: ?0},{roomsAvailable : { $gte: ?1 }}] }", fields = "{roomsAvailable : 0}")
	Hotel findHotelDetails(String hotelId,Integer noOfRooms);

}
