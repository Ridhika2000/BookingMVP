package com.rgms.hotel.booking.entities;



import org.springframework.data.annotation.Id;

import jakarta.validation.Valid;
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
public class User {
	
	@Id
	private String userId;
	
	@NotEmpty
	@Size(min = 2, message = "user name should have at least 2 characters")
	private String name;
	
	@NotEmpty
	@Email
	private String email;
	
	@NotBlank(message = "mobileNumber is required")
	@Size(min = 10, max = 10)
	private String phoneNo;
	
	@Valid
	@NotBlank(message = "address is required")
	private String address;
	
	
}

