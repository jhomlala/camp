package com.camp.sparkservice.domain;

public class WorkerThread implements Runnable {

	private SparkJob job;

	public WorkerThread(SparkJob job) {
		this.job = job;
	}

	@Override
	public void run() {
		job.run();
	}

}