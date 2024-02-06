package com.rgms.hotel.booking.entities;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Hotel implements Serializable{
	
	private String hotelId;
	
	private String hotelName;
	
	private String location;
	
	private String about;
	
	private Float rating;
	
    private Float currentPrice;
	
	private String breakfastIncluded;
	
	private String freeCancellation;
	
	private String nearbyAttractions;
	 
	
}
