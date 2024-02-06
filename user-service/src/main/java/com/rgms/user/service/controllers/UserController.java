package com.rgms.user.service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rgms.user.service.entities.HotelBooking;
import com.rgms.user.service.entities.User;
import com.rgms.user.service.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	//create
	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user){
		User user1 = userService.saveUser(user);
		return new ResponseEntity<>(user1, HttpStatus.CREATED);
	}
	
	//single user get
	@GetMapping("/{userId}")
	public ResponseEntity<User> getSingleUser(@PathVariable String userId) throws Exception{
		User user = userService.getUser(userId);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	//all users
	@GetMapping
	public ResponseEntity<List<User>> getAllUsers(){
		List<User> users = userService.getAllUsers();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	//get hotel bookings
	@GetMapping("{userId}/hotels/bookings")
	public ResponseEntity<List<HotelBooking>> getHotelBookings(@PathVariable String userId) throws Exception{
		
		List<HotelBooking> bookings = userService.getHotelBookings(userId);
		return ResponseEntity.ok(bookings);
	}
}
