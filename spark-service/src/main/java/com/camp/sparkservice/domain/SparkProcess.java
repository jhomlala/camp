package com.camp.sparkservice.domain;

import java.util.Date;
import java.util.UUID;

import com.camp.sparkservice.service.SparkService;

public abstract class SparkProcess {
	private SparkService sparkService;
	private String id;
	protected SparkProcessStatus status;
	protected Date startedAt;
	protected int step;

	public SparkProcess(SparkService sparkService) {
		this.sparkService = sparkService;
		this.id = UUID.randomUUID().toString();
		this.status = SparkProcessStatus.PENDING;
	}

	public SparkService getSparkService() {
		return sparkService;
	}

	public String getId() {
		return id;
	}

	public SparkProcessStatus getStatus() {
		return status;
	}

	protected void setStatus(SparkProcessStatus status) {
		this.status = status;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public int getStep() {
		return step;
	}

	public abstract void execute();
}
