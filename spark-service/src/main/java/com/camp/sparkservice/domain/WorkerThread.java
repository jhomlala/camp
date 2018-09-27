package com.camp.sparkservice.domain;

public class WorkerThread extends Thread {

	private SparkProcess sparkProcess;

	public WorkerThread(SparkProcess sparkProcess) {
		this.sparkProcess = sparkProcess;
	}

	public void run() {
		System.out.println("Run");
		sparkProcess.execute();
	}

}
