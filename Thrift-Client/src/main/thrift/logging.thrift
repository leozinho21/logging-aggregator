namespace java com.leozinho.thrift.logevent

enum Severity {
	  FATAL = 1,
	  ERROR = 2,
	  WARN = 3,
	  INFO = 4,
	  DEBUG = 5,
	  TRACE = 6,
	  ALL = 7,
	  OFF = 8
}

struct LoggingEvent{
	
	# indicates a version number for the logging event schema. Default to 1
	1: required i16 schemaVersion = 1;	

	# represents the time of the logging event. String chosen because its easy to parse in java
	2: required string time = "5-11-2019 10:25:31 am";		

	# logging message itself
	3: optional string message = "An error occurred trying to fetch data.";			
	
	# logging source name
	4: required string sourceName = "com.leozinho.client.TestHandler";			

	# logging severity level
	5: required Severity severity = Severity.INFO;
}

typedef list<LoggingEvent> LogEvents

struct ClientMessage{
	1: LogEvents events;
}

struct ServerResponse{
	1: bool success;
	2: string message;
}

service LogEventService{
	ServerResponse handleEvents(1:ClientMessage message)
}

