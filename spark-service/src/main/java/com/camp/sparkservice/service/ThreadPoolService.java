package com.camp.sparkservice.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import com.camp.sparkservice.domain.WorkerThread;

@Service
public class ThreadPoolService {

	private ExecutorService executorService;

	public ThreadPoolService() {
		executorService = Executors.newFixedThreadPool(5);
	}

	public void processJob(WorkerThread thread) {
		executorService.execute(thread);
	}

}