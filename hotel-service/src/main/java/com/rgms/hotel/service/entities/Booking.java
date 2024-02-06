package com.rgms.hotel.service.entities;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
	
    private String bookingId;
	
	private String userId;
	
	private String hotelId;
	
	private String hotelName;
	
	private Integer noOfRooms;
	
	private String guestName;
	
	private String guestPhoneNo;
	
	private Date checkInDate;
	
	private Date checkOutDate;
	
	private Float payment;
	
	private BookingStatus bookingStatus;

	
}
