package com.leozinho.kafka.consumer;

public class Constants {

	//If Kafka is running in a cluster then you can provide comma (,) separated addresses. 
	//For example:localhost:9091,localhost:9092 
	public static String KAFKA_BROKERS = "localhost:9092";

	public static String TOPIC_NAME = "logging-events";

	public static String GROUP_ID_CONFIG = "consumerGroup10";

	public static Integer MAX_NO_MESSAGE_FOUND_COUNT = 100;

	public static String OFFSET_RESET_EARLIER = "earliest";

	public static Integer MAX_POLL_RECORDS = 1;
}
