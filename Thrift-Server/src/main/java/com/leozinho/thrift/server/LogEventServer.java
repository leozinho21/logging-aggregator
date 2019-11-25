package com.leozinho.thrift.server;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leozinho.thrift.logevent.LogEventService;

public class LogEventServer {

	private static Logger logger = LoggerFactory.getLogger(LogEventServer.class);

	public static void main(String [] args) {
		
		try {
			
			LogEventsHandler handler = new LogEventsHandler();
			
			LogEventService.Processor<LogEventsHandler> processor = new LogEventService.Processor<>(handler);
			
			Runnable runnable = new Runnable() {
			
				public void run() {
					startServer(processor);
				}
			};

			new Thread(runnable).start();
			
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public static void startServer(LogEventService.Processor<LogEventsHandler> processor) {
		
		try {
			int serverPort = 9090;
			
			logger.debug("Starting server on port {}",serverPort);

			TServerTransport serverTransport = new TServerSocket(serverPort);
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
			server.serve();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
