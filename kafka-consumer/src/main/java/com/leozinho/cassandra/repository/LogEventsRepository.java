package com.leozinho.cassandra.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;
import com.datastax.oss.driver.api.querybuilder.select.Select;

import com.leozinho.cassandra.domain.LogEvent;
import com.leozinho.cassandra.domain.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LogEventsRepository {
	
	private static final String TABLE_NAME = "logging_events_by_severity";

    private CqlSession session;

    public LogEventsRepository(CqlSession session) {
    	this.session = session;
    }

    public void createTable(String keyspace) {
    	
        CreateTable createTable = SchemaBuilder.createTable(TABLE_NAME).ifNotExists()
          .withPartitionKey("event_id", DataTypes.UUID)
          .withClusteringColumn("severity", DataTypes.TEXT)
          .withClusteringColumn("time", DataTypes.TEXT)
          .withClusteringColumn("source_name", DataTypes.TEXT)
          .withColumn("message", DataTypes.TEXT)
          .withColumn("schema_version", DataTypes.INT);

        executeStatement(createTable.build(), keyspace);
    }
    
    public void insertLogEvents(List<LogEvent> logEvents) {
    	logEvents.forEach(ev -> insertLogEvent(ev));
    }
    
    public UUID insertLogEvent(LogEvent logEvent) {
        return insertLogEvent(logEvent, null);
    }

    public UUID insertLogEvent(LogEvent logEvent, String keyspace) {
        UUID eventId = UUID.randomUUID();

        logEvent.setId(eventId);

        RegularInsert insertInto = QueryBuilder.insertInto(TABLE_NAME)
          .value("event_id", QueryBuilder.bindMarker())
          .value("severity", QueryBuilder.bindMarker())
          .value("time", QueryBuilder.bindMarker())
          .value("source_name", QueryBuilder.bindMarker())
          .value("message", QueryBuilder.bindMarker())
          .value("schema_version", QueryBuilder.bindMarker());

        SimpleStatement insertStatement = insertInto.build();

        if (keyspace != null) {
            insertStatement = insertStatement.setKeyspace(keyspace);
        }

        PreparedStatement preparedStatement = session.prepare(insertStatement);

        BoundStatement statement = preparedStatement.bind()
          .setUuid(0, logEvent.getId())
          .setString(1, logEvent.getSeverity().name())
          .setString(2, logEvent.getTime())
          .setString(3, logEvent.getSourceName())
          .setString(4, logEvent.getMessage())
          .setInt(5, 	logEvent.getSchemaVersion());

        session.execute(statement);

        return eventId;
    }

    public List<LogEvent> selectAll() {
        return selectAll(null);
    }

    public List<LogEvent> selectAll(String keyspace) {
    	
        Select select = QueryBuilder.selectFrom(TABLE_NAME).all();

        ResultSet resultSet = executeStatement(select.build(), keyspace);

        List<LogEvent> result = new ArrayList<>();

        resultSet.map(x -> {
        	LogEvent ev = new LogEvent();
        	ev.setId(x.getUuid("event_id"));
        	ev.setSeverity(Severity.valueOf(x.getString("severity")));
        	ev.setTime(x.getString("time"));
        	ev.setSourceName(x.getString("source_name"));
        	ev.setMessage(x.getString("message"));
        	ev.setSchemaVersion(x.getInt("schema_version"));
        	return ev;
        }).forEach(result::add);

        return result;
    }

    private ResultSet executeStatement(SimpleStatement statement, String keyspace) {
        if (keyspace != null) {
            statement.setKeyspace(CqlIdentifier.fromCql(keyspace));
        }

        return session.execute(statement);
    }
}
