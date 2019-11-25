package com.leozinho.thrift.kafka;


import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;

import com.leozinho.thrift.logevent.LoggingEvent;

public class ProducerCreator {

	public static Producer<Object, LoggingEvent> createProducer() {
		Properties props = new Properties();
		
		//If Kafka is running in a cluster then you can provide comma (,) separated addresses. 
		//For example:localhost:9091,localhost:9092 
		
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "thrift-client");
		
		// we add key serializer class even if we dont use it because kafka requires default key class
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SimpleSerializer.class.getName());
		
		return new KafkaProducer<>(props);
	}
}
