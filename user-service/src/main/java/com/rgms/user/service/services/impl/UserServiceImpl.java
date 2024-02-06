package com.rgms.user.service.services.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.rgms.user.service.JsonSerializerUtil;
import com.rgms.user.service.entities.BookingStatus;
import com.rgms.user.service.entities.HotelBooking;
import com.rgms.user.service.entities.User;
import com.rgms.user.service.exceptions.ResourceNotFoundException;
import com.rgms.user.service.external.services.HotelService;
import com.rgms.user.service.repository.UserRepository;
import com.rgms.user.service.services.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	
	@Override
	public User saveUser(User user) {
		//generate unique userid
	    String randomUserId = UUID.randomUUID().toString();
		user.setUserId(randomUserId);
		return userRepository.save(user);
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User getUser(String userId) throws Exception {
		
		String message = "Error in getting user by id";
		User user = userRepository.findByUserId(userId);
				
		try {
			if (user == null) {
				message = "User not found for this id :: " + userId;
				throw new ResourceNotFoundException("User not found for this id :: " + userId);
			}
		
			
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(message);
		}
	}

	@Override
	public void addHotelBooking(HotelBooking booking) throws Exception {
		
		User user = getUser(booking.getUserId());
		user.getHotelBookings().add(booking);
		userRepository.save(user);
		
	}
	
	@Override
	@JmsListener(destination="HotelBookingConfirmed")
	public void hotelConfirmed(String bookingPayload) throws Exception {
		HotelBooking booking = JsonSerializerUtil.hotelBookingPayload(bookingPayload);
		
		System.out.println("hotel confirmation message received to user");
		
		addHotelBooking(booking);
	}
	
	@Override
	@JmsListener(destination="HotelBookingUnConfirmed")
	public void hotelUnconfirmed(String bookingPayload) {
		System.out.println("hotel unconfirmation message received to user");
	}
	
	@Override
	@JmsListener(destination = "HotelRoomNotAvailable")
	public void hotelRoomNotAvailable(String bookingPayload) {
      
        logger.info("hotel room not available message received to user");
	}
	
	@Override
	@JmsListener(destination = "HotelPaymentDeclined")
	public void hotelPaymentDeclined(String bookingPayload) {
        
		System.out.println("hotel booking payment declined message received to user");
	}
	
	
	@Override
	@JmsListener(destination = "HotelCanceled")
	public void hotelCanceled(String bookingPayload) throws Exception {
		HotelBooking booking = JsonSerializerUtil.hotelBookingPayload(bookingPayload);
		User user = getUser(booking.getUserId());
		for(HotelBooking b: user.getHotelBookings()) {
			
			if(b.getBookingId().equals(booking.getBookingId())) {
				b.setBookingStatus(BookingStatus.REFUNDED);
				b.setPayment(0f);
			}
			
		}
		userRepository.save(user);
		System.out.println("hotel cancelled and amount refunded successfully");
		
		
	}
	
	
	@Override
	public List<HotelBooking> getHotelBookings(String userId) throws Exception {
		User user = getUser(userId);
		return user.getHotelBookings();
	}

	
	
}
