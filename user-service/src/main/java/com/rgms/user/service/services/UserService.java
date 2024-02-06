package com.rgms.user.service.services;

import java.util.List;

import com.rgms.user.service.entities.HotelBooking;
import com.rgms.user.service.entities.User;

public interface UserService {
	
	//create
	User saveUser(User user);
	
	//get all users
	List<User> getAllUsers();
	
	//get by id
	User getUser(String userId) throws Exception;
	
	void hotelConfirmed(String bookingPayload) throws Exception;
	
	void hotelUnconfirmed(String bookingPayload);
	
	void addHotelBooking(HotelBooking booking) throws Exception;
	
	void hotelCanceled(String bookingPayload) throws Exception;
	
	void hotelRoomNotAvailable(String bookingPayload);
	
	void hotelPaymentDeclined(String bookingPayload);
	
	List<HotelBooking> getHotelBookings(String userId) throws Exception;
	
}
