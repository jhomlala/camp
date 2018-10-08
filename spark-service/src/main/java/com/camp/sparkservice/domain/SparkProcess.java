package com.camp.sparkservice.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.apache.spark.sql.SparkSession;

import com.camp.sparkservice.service.SparkService;

public abstract class SparkProcess implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6324744500698184894L;
	private SparkService sparkService;
	private String id;
	protected SparkProcessStatus status;
	protected Date startedAt;
	protected int step;
	protected String error;
	protected String result;

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
		
	public String getError() {
		return error;
	}

	public String getResult() {
		return result;
	}

	public SparkSession sparkSession() {
		return sparkService.getSparkSession();
	}

	public abstract void execute();
}
