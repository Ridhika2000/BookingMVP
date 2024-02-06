package com.rgms.user.service.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import jakarta.validation.constraints.Email;
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
@Document(collection = "users")
public class User {
    
	@Id
	private String userId;
	
	@NotEmpty
	@Size(min=2, message = "user name should have at least 2 characters")
	private String name;
	
	@NotEmpty
	@Email
	private String email;
	
	@NotBlank(message="mobile number is required")
	@Size(min=10, max=10)
	private String phoneNo;
	
	private List<HotelBooking> hotelBookings = new ArrayList<>();	
	
	
}
