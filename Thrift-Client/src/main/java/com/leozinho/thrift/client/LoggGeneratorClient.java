package com.leozinho.thrift.client;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leozinho.thrift.logevent.ClientMessage;
import com.leozinho.thrift.logevent.LogEventService;
import com.leozinho.thrift.logevent.LoggingEvent;
import com.leozinho.thrift.logevent.ServerResponse;
import com.leozinho.thrift.logevent.Severity;;

public class LoggGeneratorClient {

	private static Logger logger = LoggerFactory.getLogger(LoggGeneratorClient.class);

	public static void main(String[] args) {
		
		try {
			TTransport transport;
			
			String 	url  = "localhost";
			int 	port = 9090;
			
			logger.debug("Opening connection on {}:{}",url , port);

			transport = new TSocket(url, port);
			transport.open();

			TProtocol protocol = new TBinaryProtocol(transport);
			
			LogEventService.Client client = new LogEventService.Client(protocol);

			ClientMessage message = Builder.create().genEvents().build();
			
			ServerResponse response = client.handleEvents(message);
			
			printResponse(response);
			
			logger.debug("Closing connection on {}:{}",url , port);
			
			transport.close();
			
		} catch (TException x) {
			x.printStackTrace();
		}
	}
	
	private static void printResponse(ServerResponse response) {
		
		if(response.success) {
			logger.debug("Server replied with message [{}]",response.message);
		}
		else {
			logger.warn("Procedure not completed [{}]",response.message);
		}
	}
	
	static class Builder {

		private List<LoggingEvent> logEvents;

		static Builder create() {
			return new Builder();
		}

		Builder genEvents() {
			logEvents = generateEvents();
			return this;
		}
		
		ClientMessage build() {
			return createMessage(logEvents);
		}
		
		private List<LoggingEvent> generateEvents() {

			List<LoggingEvent> list = new ArrayList<>();

			LoggingEvent ev1 = createEv(ZonedDateTime.now(), Severity.INFO, "com.leozinho.test.Test1Class",
					"Test event 1");
			LoggingEvent ev2 = createEv(ZonedDateTime.now(), Severity.INFO, "com.leozinho.test.Test2Class",
					"Test event 2");
			LoggingEvent ev3 = createEv(ZonedDateTime.now(), Severity.INFO, "com.leozinho.test.Test3Class",
					"Test event 3");
			LoggingEvent ev4 = createEv(ZonedDateTime.now(), Severity.INFO, "com.leozinho.test.Test4Class",
					"Test event 4");
			LoggingEvent ev5 = createEv(ZonedDateTime.now(), Severity.INFO, "com.leozinho.test.Test5Class",
					"Test event 5");
			
			list.add(ev1);
			list.add(ev2);
			list.add(ev3);
			list.add(ev4);
			list.add(ev5);
			
			return list;
		}
	}

	private static LoggingEvent createEv(ZonedDateTime time, Severity severity, String source, String message) {
		
		LoggingEvent ev = new LoggingEvent();
		ev.severity = severity;
		ev.sourceName = source;
		ev.message = message;
		ev.time = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss").format(time);
		
		logger.debug("Creating LoggingEvent [{}] severity [{}]", message,severity);
		
		return ev;
	}

	private static ClientMessage createMessage(List<LoggingEvent> logEvents) {
		ClientMessage newMessage = new ClientMessage();
		newMessage.setEvents(logEvents);
		return newMessage;
	}

}
