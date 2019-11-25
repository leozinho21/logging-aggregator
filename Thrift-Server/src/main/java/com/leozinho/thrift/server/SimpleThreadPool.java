package com.leozinho.thrift.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleThreadPool {

	 static ExecutorService executor = Executors.newFixedThreadPool(5);
	 
	 public static void execute(Runnable task) {
		 executor.execute(task);
	 }
}
