package com.leozinho.cassandra.domain;

public enum Severity {
	
	
	FATAL(1),ERROR(2), WARN(3),INFO(4),  DEBUG(5),TRACE(6), ALL(7), OFF (8);
	
	int id ;
	
	private Severity(int id) {
		this.id = id;
	}
	
}
