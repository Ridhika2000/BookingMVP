package com.rgms.hotel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rgms.hotel.service.entities.Booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonSerializerUtil {

  private static final Logger log = LoggerFactory.getLogger(JsonSerializerUtil.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();
  
  private static final ObjectMapper mapper  = new ObjectMapper()
	        .findAndRegisterModules()
	        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  public static String serialize(Object object) {
    String retVal = null;
    try {
      retVal = objectMapper.writeValueAsString(object);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return retVal;
  }

  public static Booking bookingPayload(String respData) {
    Booking booking = null;
    try {
    	booking = mapper.readValue(respData, Booking.class);
    	System.out.println("gggggggg"+booking);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return booking;
  }


}