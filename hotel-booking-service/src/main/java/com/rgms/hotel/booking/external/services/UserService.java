package com.rgms.hotel.booking.external.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.rgms.hotel.booking.entities.User;

@FeignClient(name = "USER-SERVICE")
public interface UserService {
	
	//get user by id
	@GetMapping("/users/{userId}")
	public User getUserById(@PathVariable String userId);

}


