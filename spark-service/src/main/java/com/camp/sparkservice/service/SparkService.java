package com.camp.sparkservice.service;

import javax.annotation.PostConstruct;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.camp.sparkservice.config.ApplicationConfiguration;
import com.camp.sparkservice.domain.SparkJob;
import com.camp.sparkservice.domain.WorkerThread;

@Service
@Scope("singleton")
public class SparkService {
	private Logger logger = LoggerFactory.getLogger(SparkService.class);

	@Autowired
	private ApplicationConfiguration config;
	@Autowired
	private ThreadPoolService threadPoolService;

	private SparkSession sparkSession;
	private SparkConf sparkConf;
	private JavaSparkContext sparkContext;

	@PostConstruct
	public void init() {
		logger.info("Init spark connectors");
		sparkConf = new SparkConf().setAppName("Spark").setSparkHome(config.getSparkHome())
				.setMaster(config.getMasterUri());
		sparkContext = new JavaSparkContext(sparkConf);
		sparkSession = SparkSession.builder().sparkContext(sparkContext.sc()).appName("Java Spark SQL basic example")
				.getOrCreate();
		logger.info("Init spark connectos completed");
	}

	public void startJob(SparkJob job) {
		job.run();
	}

	public void startAsyncJob(SparkJob job) {
		logger.info("Starting async job.");
		threadPoolService.processJob(new WorkerThread(job));
	}

	public SparkSession getSparkSession() {
		return sparkSession;
	}

	public SparkConf getSparkConf() {
		return sparkConf;
	}

	public JavaSparkContext getSparkContext() {
		return sparkContext;
	}

}
