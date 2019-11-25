package com.leozinho.kafka.consumer;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leozinho.cassandra.domain.LogEvent;

public class LoggingEventDeserializer implements Deserializer<LogEvent> {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public LogEvent deserialize(String topic, byte[] data) {
		
		ObjectMapper mapper = new ObjectMapper();
		
		//because thrift on required properties of LoggingEvent (see logging.thrift file) generates separate boolean setter like fields, 
		//we omit them on our domain model
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		LogEvent object = null;
		
		try {
			object = mapper.readValue(data, LogEvent.class);
		} catch (Exception exception) {
			logger.error("Error in deserializing bytes {}", exception.getMessage());
		}
		return object;
	}

}
