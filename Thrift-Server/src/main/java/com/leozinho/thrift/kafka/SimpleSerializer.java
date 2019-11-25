package com.leozinho.thrift.kafka;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leozinho.thrift.logevent.LoggingEvent;

public class SimpleSerializer implements Serializer<LoggingEvent> {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public byte[] serialize(String topic, LoggingEvent data) {
		
		byte[] retVal = null;
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			retVal = objectMapper.writeValueAsString(data).getBytes();
		} catch (Exception exception) {
			logger.error("Error in serializing object [{}],[{}]" , data , exception.getMessage());
		}
		
		return retVal;
	}

}
