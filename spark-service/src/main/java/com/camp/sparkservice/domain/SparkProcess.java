package com.camp.sparkservice.domain;

import org.apache.spark.SparkContext;

public abstract class SparkProcess {
	private SparkContext sparkContext;

	public SparkProcess(SparkContext sparkContext) {
		this.sparkContext = sparkContext;
	}

	public SparkContext getSparkContext() {
		return sparkContext;
	}

	public abstract void execute();
}
