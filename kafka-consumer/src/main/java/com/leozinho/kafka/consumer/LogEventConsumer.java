package com.leozinho.kafka.consumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.leozinho.cassandra.domain.LogEvent;
import com.leozinho.cassandra.repository.CassandraConnector;
import com.leozinho.cassandra.repository.KeyspaceRepository;
import com.leozinho.cassandra.repository.LogEventsRepository;

public class LogEventConsumer {

	private static Logger logger = LoggerFactory.getLogger(LogEventConsumer.class);

	public static void main(String[] args) {

		new LogEventConsumer().runConsumer();
	}

	private void runConsumer() {
		Consumer<Object, LogEvent> consumer = createConsumer();
		int noMessageFound = 0;
		int giveUp = 100;
		
		while (true) {
			ConsumerRecords<Object, LogEvent> consumerRecords = consumer.poll(Duration.ofMillis(1000));
			// 1000 is the time in milliseconds consumer will wait if no record is found at
			// broker.
			if (consumerRecords.count() == 0) {
				noMessageFound++;
				if (noMessageFound > giveUp)
					// If no message found count is reached to threshold exit loop.
					break;
				else
					continue;
			}


			LogEventsRepository repo = createRepository();

			consumerRecords.forEach(record -> {
				UUID uuid = repo.insertLogEvent(record.value());
				logger.debug("Saving record with uuid [{}] Key [{}] partition [{}] offset [{}]", uuid, record.key(),
						record.partition(), record.offset());
			});
			// commits the offset of record to broker.
			consumer.commitAsync();
		}
		consumer.close();
	}
	
	private LogEventsRepository createRepository() {
		CassandraConnector connector = new CassandraConnector();
		connector.connect("127.0.0.1", 9042, "datacenter1");
		CqlSession session = connector.getSession();
		KeyspaceRepository keyspaceRepository = new KeyspaceRepository(session);

		keyspaceRepository.createKeyspace("testKeyspace", 1);
		keyspaceRepository.useKeyspace("testKeyspace");

		LogEventsRepository repo = new LogEventsRepository(session);
		repo.createTable("testKeyspace");
		return repo;
	}
	
	
	public static KafkaConsumer<Object, LogEvent> createConsumer() {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumerGroup1");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LoggingEventDeserializer.class.getName());
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		KafkaConsumer<Object, LogEvent> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList("logging-events"));
		return consumer;
	}
}
