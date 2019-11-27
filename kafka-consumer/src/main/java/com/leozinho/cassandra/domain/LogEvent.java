package com.leozinho.cassandra.domain;

import java.time.Instant;
import java.util.UUID;

public class LogEvent {
	
	private UUID id;
	private Integer schemaVersion;
    private String message;
    private String sourceName;
    private Severity severity;
    private Instant	creationDate;
    private String time;
    
    public LogEvent() {}
    
    public LogEvent(UUID id, String message, Instant creationDate) {
        this.id = id;
        this.message = message;
        this.creationDate = creationDate;
    }
    
    @Override
    public String toString() {
    	return "["+creationDate+"] " + severity + "  " + sourceName + " - " + message;
    }
    
    public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}
    
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

	public Integer getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(Integer schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public Instant getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Instant creationDate) {
		this.creationDate = creationDate;
	}

}
