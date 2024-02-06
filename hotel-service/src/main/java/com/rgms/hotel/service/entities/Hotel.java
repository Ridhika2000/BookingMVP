package com.rgms.hotel.service.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "hotels")
public class Hotel {
	
	@Id
    private String hotelId;
	
	@NotEmpty
	@Size(min=2, message="hotel name should have atleast 2 characters")
	private String hotelName;
	
	@NotBlank(message="location is required")
	private String location;
	
	private String about;
	
	@NotBlank(message="rating is required")
	@Size(min=0, max=10)
	private String rating;
	
	private Float currentPrice;
	
	private Integer roomsAvailable;
	
	private Float breakfastIncluded;
	
	private String freeCancellation;
	
	private String nearByAttractions;
	
	private List<DateWiseHotelBooking> dateWiseHotelBooking = new ArrayList<>();
	
	private List<Booking> bookings = new ArrayList<>();

	
	
	
}
