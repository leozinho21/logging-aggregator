package com.leozinho.cassandra.repository;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.leozinho.cassandra.domain.LogEvent;
import com.leozinho.cassandra.domain.Severity;

public class ConsumerApp {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        new ConsumerApp().run();
    }

    public void run() {
        CassandraConnector connector = new CassandraConnector();
        connector.connect("127.0.0.1", 9042, "datacenter1");
        CqlSession session = connector.getSession();

        KeyspaceRepository keyspaceRepository = new KeyspaceRepository(session);

        keyspaceRepository.createKeyspace("testKeyspace", 1);
        keyspaceRepository.useKeyspace("testKeyspace");

        LogEventsRepository repo = new LogEventsRepository(session);
        
        repo.createTable("testKeyspace");

//        LogEvent logEvent = createEv(ZonedDateTime.now(), Severity.INFO, "com.leozinho.test.Test10lass",
//				"Test event 5");
//        
//        repo.insertLogEvent(logEvent);
        
        List<LogEvent> videos = repo.selectAll();

        videos.forEach(x -> logger.info(x.toString()));

        connector.close();
    }
    
    private LogEvent createEv(ZonedDateTime time, Severity severity, String source, String message) {
		
    	LogEvent ev = new LogEvent();
    	ev.setSchemaVersion(1);
		ev.setSeverity(severity);
		ev.setSourceName(source);
		ev.setMessage(message);
		ev.setTime(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss").format(time));
		
		logger.debug("Creating LogEvent [{}] severity [{}] source [{}]", message,severity,source);
		
		return ev;
	}
}
