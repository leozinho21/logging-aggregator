# logging-aggregator
This is a simple implementation of aggregation/ingestion system for logging events. There is client that sends randomly generated logging events to a server that accepts the events and pushes them on to Kafka. Another server consumes the log events and writes them to a Cassandra database.
  - Log events represented by an application simple logger output messages. Consisted by attributes
      - Time. The time log generated..
      - Severity (FATAL, ERROR, WARN, INFO, DEBUG,TRACE, ALL, OFF). This attribute tells how severe is the message. Any         Severity level its more important than others on its right side,meaning that, just for pointing out, setting a logger to a specific level will output logs also for the levels on the right side of the specific selection..
      - Source name. This attribute is the java class name from which the log came from.
      - Message. The message it self
      - Thread could be used, but ommited

One thing about Apache Thrift is that it has its own client-server communication framework which makes communication easy. First we define a transport layer with the implementation of TServerTransport interface (or abstract class, to be more precise). Since we are talking about server, we need to provide a port to listen to. Then we need to define a TServer instance and choose one of the available implementations:

  TSimpleServer – for simple server
  TThreadPoolServer – for multi-threaded server
  TNonblockingServer – for non-blocking multi-threaded server

And provide a processor implementation for chosen server which was already generated by Thrift, LogEventService.Processor class.

From a client perspective, the actions are similar.

Define the transport and point it to our server instance, then choose the suitable protocol. The only difference is that here we initialize the client instance which was, once again, already generated by Thrift, LogEventService.Client class.

Since it is based on .thrift file definitions we can directly call methods described there.

