package com.rgms.user.service.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {
    
	private String hotelId;
	
	private String hotelName;
	
	private String location;
	
	private String about;
	
	private String rating;
	
	private Float currentPrice;
	
	private Float breakfastIncluded;
	
	private String freeCancellation;
	
	private String nearByAttractions;

	
	
}
