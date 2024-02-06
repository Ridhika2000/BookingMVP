package com.rgms.hotel.booking.entities;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bookings")
public class Booking implements Serializable{
	
	@Id
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
