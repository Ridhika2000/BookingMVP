package com.rgms.hotel.booking.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rgms.hotel.booking.JsonSerializerUtil;
import com.rgms.hotel.booking.entities.Booking;
import com.rgms.hotel.booking.entities.BookingStatus;
import com.rgms.hotel.booking.entities.Hotel;
import com.rgms.hotel.booking.entities.User;
import com.rgms.hotel.booking.external.services.HotelService;
import com.rgms.hotel.booking.external.services.UserService;
import com.rgms.hotel.booking.repository.BookingRepository;

@Service
public class BookingServiceImpl implements BookingService {

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private HotelService hotelService;

	@Autowired
	private UserService userService;

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	private Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

	@Override
	@Transactional
	public Booking bookHotel(Booking booking, String userId, String hotelId) throws Exception {
		if (Objects.isNull(booking.getBookingId())) {
			booking.setBookingId(UUID.randomUUID().toString());
		}
        System.out.println("ridhiii");
        
		Date checkinDate = booking.getCheckOutDate();
		Date checkoutDate = booking.getCheckOutDate();
		Date today = new Date();
		Date d1 = formatter.parse(formatter.format(today));
		Calendar cal = Calendar.getInstance();
		cal.setTime(d1);
		cal.add(Calendar.YEAR, 1); // to get previous year add -1
		Date nextYear = cal.getTime();

		if (checkinDate.compareTo(nextYear) > 0 && checkoutDate.compareTo(nextYear) > 0) {
			throw new Exception(
					"Error occur while booking hotel.Hotel date cannot extend greater than one year from current date");
		} else {

			booking.setBookingStatus(BookingStatus.PROCESSING);

			User user = userService.getUserById(userId);
			System.out.println(user);
			booking.setUserId(user.getUserId());

			Hotel hotel = hotelService.getHotelById(hotelId);

			booking.setHotelId(hotel.getHotelId());

			if (booking.getNoOfRooms() == null) {

				booking.setNoOfRooms(1);

			}

			Booking b = bookingRepository.save(booking);

			// ActiveMQ
			jmsTemplate.convertAndSend("BookingRequestReceivedEvent", JsonSerializerUtil.serialize(b));

			return b;
		}

	}

	@Override
	@JmsListener(destination = "HotelConfirmEvent")
	public void confirmBooking(String bookingId) {
		logger.info("hotel booking confirmed " + bookingId);

		Booking booking = bookingRepository.findByBookingId(bookingId);
		booking.setBookingStatus(BookingStatus.CONFIRMED);

		Booking b = bookingRepository.save(booking);

		jmsTemplate.convertAndSend("HotelBookingConfirmed", JsonSerializerUtil.serialize(b));
		// RabbbitMQ
		/*
		 * this.orderCreatedEventProducer .produceEventOrderCreatedDelivery(new
		 * OrderCreatedEventMessage(order.getOrderID()));
		 */

	}

	/**
	 * Consume Event from Hotel using ActiveMQ
	 */
	@Override
	@JmsListener(destination = "HotelNotConfirmEvent")
	public void unconfirmBooking(String bookingId) {
		logger.info("booking not confirmed " + bookingId);
		Booking booking = bookingRepository.findByBookingId(bookingId);
		booking.setBookingStatus(BookingStatus.UNCONFIRMED);
		Booking b = bookingRepository.save(booking);

		jmsTemplate.convertAndSend("HotelBookingUnConfirmed", JsonSerializerUtil.serialize(b));

	}

	@Override
	@JmsListener(destination = "HotelNotAvailableEvent")
	public void hotelNotAvailable(String bookingId) {

		logger.info("hotel room not available.All booked " + bookingId);

		Booking booking = bookingRepository.findByBookingId(bookingId);
		booking.setBookingStatus(BookingStatus.NOT_AVAILABLE);
		booking.setPayment(0f);
		Booking b = bookingRepository.save(booking);

		jmsTemplate.convertAndSend("HotelRoomNotAvailable", JsonSerializerUtil.serialize(b));

	}

	@Override
	@JmsListener(destination = "HotelPaymentDeclinedEvent")
	public void hotelPaymentDeclined(String bookingId) {

		logger.info("payment declined.. Try again" + bookingId);

		Booking booking = bookingRepository.findByBookingId(bookingId);
		booking.setBookingStatus(BookingStatus.PAYMENT_DECLINED);
		booking.setPayment(0f);
		Booking b = bookingRepository.save(booking);

		jmsTemplate.convertAndSend("HotelPaymentDeclined", JsonSerializerUtil.serialize(b));

	}

	@Override
	public List<Booking> getBookingByHotelId(String hotelId) {

		return bookingRepository.findByHotelId(hotelId, "CONFIRMED");
	}

	@Override
	public Integer getBookingsByCheckoutAndId(String hotelId, Date checkoutdate) {

		List<Booking> CheckinCheckuoutList = bookingRepository.findBookingsByCheckoutAndId(hotelId, checkoutdate,
				"CONFIRMED");

		Integer totalRooms = 0;
		for (Booking booking : CheckinCheckuoutList) {

			totalRooms += booking.getNoOfRooms();
		}
		return totalRooms;
	}

	@Override
	public Booking cancelBooking(String bookingId) throws Exception {
		String message = "Error in canceling hotel booking";

		try {

			Booking booking = bookingRepository.findByBookingId(bookingId);

			Date today = new Date();
			Date d1 = formatter.parse(formatter.format(today));

			Date checkindate = booking.getCheckInDate();
			Date checkoutdate = booking.getCheckOutDate();
			long diffInMillies = Math.abs(checkindate.getTime() - checkoutdate.getTime());
			long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
			int flag = 0;

			for (int i = 0; i < diff; i++) {
				if (d1.compareTo(checkindate) == 0 || checkindate.compareTo(d1) > 0) {
					flag = flag + 1;
				}

				checkindate = DateUtils.addDays(checkindate, 1);

			}
			if (flag == 0) {
				message = "you cannot cancel hotel now";
				throw new Exception(message);

			}

			BookingStatus b = booking.getBookingStatus();

			if (b.equals(BookingStatus.CONFIRMED)) {

				booking.setBookingStatus(BookingStatus.CANCELLED);
				booking = bookingRepository.save(booking);

				// ActiveMQ
				jmsTemplate.convertAndSend("BookingCancelRequestReceivedEvent", JsonSerializerUtil.serialize(booking));

				return booking;

			} else {

				message = "booking is not confirmed yet.";
				throw new Exception(message);

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(message);
		}
	}

	@Override
	@JmsListener(destination = "HotelCancelEvent")
	public void cancelHotelBooking(String bookingId) {
		System.out.println("Booking canceled " + bookingId);
		Booking booking = bookingRepository.findByBookingId(bookingId);

		booking.setBookingStatus(BookingStatus.REFUNDED);
		booking.setPayment(0f);

		Booking b = bookingRepository.save(booking);

		jmsTemplate.convertAndSend("HotelCanceled", JsonSerializerUtil.serialize(b));

	}

}
