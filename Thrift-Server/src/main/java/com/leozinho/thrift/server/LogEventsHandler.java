package com.leozinho.thrift.server;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leozinho.thrift.kafka.ProducerCreator;
import com.leozinho.thrift.logevent.ClientMessage;
import com.leozinho.thrift.logevent.LogEventService;
import com.leozinho.thrift.logevent.LoggingEvent;
import com.leozinho.thrift.logevent.ServerResponse;

public class LogEventsHandler implements LogEventService.Iface {
	
	private Logger logger = LoggerFactory.getLogger(LogEventsHandler.class);

	Producer<Object, LoggingEvent> producer;
	
	@Override
	public ServerResponse handleEvents(ClientMessage message) throws TException {

		List<LoggingEvent> events = message.getEvents();

		storeEvents(events);

		ServerResponse resp = new ServerResponse();
		resp.success = true;
		resp.message = "Events has been stored.";

		return resp;
	}

	void storeEvents(List<LoggingEvent> events) {

		SimpleThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				runProducer(events);
			}

		});
	}

	void runProducer(List<LoggingEvent> events) {
		
		if(producer == null) {
			producer = ProducerCreator.createProducer();
		}
		
		for (LoggingEvent ev : events) {
			
			// create record with no key
			ProducerRecord<Object, LoggingEvent> record = new ProducerRecord<>("logging-events", ev);
			try {
				RecordMetadata metadata = producer.send(record).get();
				logger.debug("Record sent to partition {} with offset {} ",metadata.partition(), metadata.offset());
			} catch (ExecutionException e) {
				logger.error("Execution error in sending record [{}]",e.getMessage());
			} catch (InterruptedException e) {
				logger.error("Error in sending record [{}]",e.getMessage());
			}
		}
	}
}