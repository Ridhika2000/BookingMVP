package com.rgms.hotel.service.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.rgms.hotel.service.JsonSerializerUtil;
import com.rgms.hotel.service.entities.Booking;
import com.rgms.hotel.service.entities.DateWiseHotelBooking;
import com.rgms.hotel.service.entities.Hotel;
import com.rgms.hotel.service.exceptions.ResourceNotFoundException;
import com.rgms.hotel.service.external.services.BookingService;
import com.rgms.hotel.service.repository.HotelRepository;
import com.rgms.hotel.service.services.HotelService;



@Service
public class HotelServiceImpl implements HotelService {

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private HotelRepository hotelRepository;

	@Autowired
	private BookingService bookingService;

	private Logger logger = LoggerFactory.getLogger(HotelServiceImpl.class);

	@Override
	public Hotel saveHotel(Hotel hotel) {

		String randomHotelId = UUID.randomUUID().toString();
		hotel.setHotelId(randomHotelId);

		return hotelRepository.save(hotel);
	}

	@Override
	public List<Hotel> getAllHotels() throws Exception {
		try {
			return hotelRepository.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error in getting all hotels.Try again..");
		}
	}

	@Override
	public Hotel getHotelById(String hotelId) throws Exception {
		String message = "Error in getting hotel by id";
		Hotel h = hotelRepository.findByHotelId(hotelId);
		try {
			if (h == null) {
				message = "Hotel not found for this id :: " + hotelId;
				throw new ResourceNotFoundException("Hotel not found for this id :: " + hotelId);
			}

			return h;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(message);
		}
	}

	@Override
	public List<Hotel> searchHotels(String location, Date checkInDate, Date checkOutDate, Integer noOfRooms,
			String hotelName, String sortBy) throws Exception {
		String message = "Error in searching hotel";
		System.out.println(hotelName);
		System.out.println("rrrrrrrrrrrrrrrrrrrrr");
		List<Hotel> h = new ArrayList<Hotel>();
		try {
			
			if (location == null && hotelName == null) {
				message = "To search hotels either give location or hotel name.";
				throw new Exception("To search hotels either give location or hotel name.");
			}
			Sort sort;
			if(sortBy.equals("price")) {
				sort = Sort.by(Sort.Direction.ASC, "currentPrice");
			}else if(sortBy.equals("rating")) {
				sort = Sort.by(Sort.Direction.DESC, "rating");
			}else {
				message = "you can sort by either price or rating.";
				throw new Exception(message);
			}
			List<Hotel> hotels = hotelRepository.findHotels(location, hotelName, sort);
			
			  
			long diffInMillies = Math.abs(checkInDate.getTime() - checkOutDate.getTime());
		    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	    	int availability = 0;
		    	
			for (Hotel hotel : hotels) {
				availability = 0;
				for(int j=0;j<diff;j++) {
					
					Integer checkoutRooms = bookingService.getBookingsByCheckoutAndId(hotel.getHotelId(), checkInDate);
					
					if ((hotel.getRoomsAvailable() != null) && (hotel.getRoomsAvailable() + checkoutRooms) >= noOfRooms) {
						availability = availability + 1;
					}
					
					checkInDate = DateUtils.addDays(checkInDate, 1);
				}
				if(availability == diff) {
					h.add(hotel);
				}
					
		   }

			return h;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(message);
		}

	}


	@Override
	@JmsListener(destination = "BookingRequestReceivedEvent")
	public void hotelBooking(String bookingPayload) throws Exception {
		try {
			System.out.println("rrrrrrrrrrrrr"+bookingPayload);
			Booking booking = JsonSerializerUtil.bookingPayload(bookingPayload);
			
			logger.info("message received in hotel");
			Hotel hotel = getHotelById(booking.getHotelId());
			
			if (hotel != null) {
				
				Date checkindate = booking.getCheckInDate();
				Date checkoutdate = booking.getCheckOutDate();
				  
				long diffInMillies = Math.abs(checkindate.getTime() - checkoutdate.getTime());
			    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
			    int flag = 0;
		    	int availability = 0;
		    	DateWiseHotelBooking d2 = new DateWiseHotelBooking();
			    
			    for(int i=0;i<diff;i++) {
			    	flag = 0;
			    	
			    	for (DateWiseHotelBooking d : hotel.getDateWiseHotelBooking()) {
			    		if (d.getBookingDate().compareTo(checkindate) == 0) {
			    			if(d.getNoOfRoomsBooked() == null) {
			    				if(booking.getNoOfRooms() <= hotel.getRoomsAvailable()) {
			    					d2.setNoOfRoomsBooked(0);
					    			hotelRepository.save(hotel);
					    			flag = flag+1;
			    					
			    				}else {
			    					availability = availability + 1;
			    				}
			    				
			    				break;
			    			}
			    			if (d.getNoOfRoomsBooked() + booking.getNoOfRooms() <= hotel.getRoomsAvailable()) {

					
								flag = flag + 1;
							} else {
								availability = availability + 1;
								
							}
			    		}
			    		
			    	}
			    	if(flag == 0) {
			    		if(booking.getNoOfRooms() <= hotel.getRoomsAvailable()) {
			    			d2.setBookingDate(checkindate);
			    			d2.setNoOfRoomsBooked(0);
			    			hotel.getDateWiseHotelBooking().add(d2);
			    			hotelRepository.save(hotel);
	    				}else {
	    					availability = availability + 1;
	    				}
			    	}
			    	if(availability == 1) {
			    		sendHotelNotAvailableEvent(booking.getBookingId());
			    		return;
			    	}
			    	
			    	checkindate = DateUtils.addDays(checkindate, 1);
			    	
			    }
			   
			    if(availability != 1) {
			    	checkindate = booking.getCheckInDate();
			    	 for(int i=0;i<diff;i++) {
					    	flag = 0;
					    	
					    	for (DateWiseHotelBooking d : hotel.getDateWiseHotelBooking()) {
					    		if (d.getBookingDate()
										.compareTo(checkindate) == 0) {
					    			
					    				
					    			d.setNoOfRoomsBooked(d.getNoOfRoomsBooked()+booking.getNoOfRooms());
					    			
					    		}
					    		
					    	}
					    	hotelRepository.save(hotel);
					    	
					    	checkindate = DateUtils.addDays(checkindate, 1);
					    	
					}
			    	if (booking.getPayment().equals(booking.getNoOfRooms() * hotel.getCurrentPrice())) {

							sendHotelConfirmEvent(booking.getBookingId());
							return;
					} else {
						for(int i=0;i<diff;i++) {
					    	flag = 0;
					    	checkindate = booking.getCheckInDate();
					    	for (DateWiseHotelBooking d : hotel.getDateWiseHotelBooking()) {
					    		if (d.getBookingDate()
										.compareTo(checkindate) == 0) {
					    			
					    				
					    			d.setNoOfRoomsBooked(d.getNoOfRoomsBooked()-booking.getNoOfRooms());
					    			
					    		}
					    		
					    	}
					    	hotelRepository.save(hotel);
					    	
					    	checkindate = DateUtils.addDays(checkindate, 1);
					    	
					    }
						sendHotelPaymentDeclinedEvent(booking.getBookingId());
						return;
				    }
			    	 
			    	 
			    	 
			    	
			    }else {
			    	sendHotelNotConfirmEvent(booking.getBookingId());
			    	return;
			    }

			}

		}
		catch(Exception ex) {
			ex.printStackTrace();
			throw new Exception("Error in booking hotel");
		}
	}

	@Override
	@JmsListener(destination = "BookingCancelRequestReceivedEvent")
	public void cancelBooking(String bookingPayload) throws Exception {
		Booking booking = JsonSerializerUtil.bookingPayload(bookingPayload);
		logger.info("message received in hotel to cancel booking");
		Hotel hotel = hotelRepository.getByHotelId(booking.getHotelId());
		System.out.println(hotel.getHotelName());
		
		Date checkindate = booking.getCheckInDate();
		Date checkoutdate = booking.getCheckOutDate();
		  
		long diffInMillies = Math.abs(checkindate.getTime() - checkoutdate.getTime());
	    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	    int flag = 0;
    	
    	for(int i=0;i<diff;i++) {
	    	flag = 0;
	    	
	    	for (DateWiseHotelBooking d : hotel.getDateWiseHotelBooking()) {
	    		if (d.getBookingDate()
						.compareTo(checkindate) == 0) {
	    			
	    			d.setNoOfRoomsBooked(d.getNoOfRoomsBooked()-booking.getNoOfRooms());
	    			
	    			System.out.println("heeehh");
	    		}
	        }
	    	hotelRepository.save(hotel);
	    	
	    	checkindate = DateUtils.addDays(checkindate, 1);
	    		
	    	}
	    	
		sendHotelCancelEvent(booking.getBookingId());

	}

	public void sendHotelConfirmEvent(String bookingId) {
		logger.info("HotelConfirmEvent");
		jmsTemplate.convertAndSend("HotelConfirmEvent", bookingId);
	}
	
	public void sendHotelNotAvailableEvent(String bookingId) {
		logger.info("HotelNotAvailableEvent");
		jmsTemplate.convertAndSend("HotelNotAvailableEvent", bookingId);
	}
	
	public void sendHotelPaymentDeclinedEvent(String bookingId) {
		logger.info("HotelPaymentDeclinedEvent");
		jmsTemplate.convertAndSend("HotelPaymentDeclinedEvent", bookingId);
	}

	public void sendHotelNotConfirmEvent(String bookingId) {
		logger.info("HotelNotConfirmEvent");
		jmsTemplate.convertAndSend("HotelNotConfirmEvent", bookingId);
	}

	public void sendHotelCancelEvent(String bookingId) {
	    logger.info("Hotel cancel event");
		jmsTemplate.convertAndSend("HotelCancelEvent", bookingId);
	}

	@Override
	public Hotel getHotelBookings(String hotelId) throws Exception {
		try {
			Hotel h = getHotelById(hotelId);

			List<Booking> bookings = bookingService.getBookingsByHotelId(h.getHotelId());

			h.setBookings(bookings);

			return h;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error in finding hotel bookings");
		}

	}

}