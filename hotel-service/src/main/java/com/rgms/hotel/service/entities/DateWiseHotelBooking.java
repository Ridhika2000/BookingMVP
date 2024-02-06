package com.rgms.hotel.service.entities;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DateWiseHotelBooking {
	
	private Date bookingDate;
	
	private Integer noOfRoomsBooked;

	
	
}
