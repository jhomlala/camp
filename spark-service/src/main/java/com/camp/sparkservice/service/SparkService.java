package com.camp.sparkservice.service;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.camp.sparkservice.config.ApplicationConfiguration;
import com.camp.sparkservice.domain.ChurnModelBuildProcess;
import com.camp.sparkservice.domain.ChurnModelBuildRequest;
import com.camp.sparkservice.domain.ChurnPredictRequest;
import com.camp.sparkservice.domain.ChurnPredictionProcess;
import com.camp.sparkservice.domain.SparkProcess;
import com.camp.sparkservice.domain.SparkProcessStatus;
import com.camp.sparkservice.domain.WorkerThread;
import com.google.gson.Gson;

@Service
@Scope("singleton")
public class SparkService {
	private Logger logger = LoggerFactory.getLogger(SparkService.class);

	@Autowired
	private ApplicationConfiguration config;
	@Autowired
	private ThreadPoolService threadPoolService;
	@Autowired
	private UserEventService userEventService;

	private SparkSession sparkSession;
	private SparkConf sparkConf;
	private JavaSparkContext sparkContext;
	private Queue<SparkProcess> sparkProcesses;
	private SparkProcess currentProcess;

	private Gson gson;
	
	@PostConstruct
	public void init() {
		logger.info("Init spark connectors");
		sparkConf = new SparkConf().setAppName("Spark").setSparkHome(config.getSparkHome())
				.setMaster(config.getMasterUri());
		sparkContext = new JavaSparkContext(sparkConf);
		sparkSession = SparkSession.builder().sparkContext(sparkContext.sc()).appName("Java Spark SQL basic example")
				.getOrCreate();
		logger.info("Init spark connectos completed");
		sparkProcesses = new LinkedList<SparkProcess>();
		gson = new Gson();
		
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

	public String process(ChurnModelBuildRequest churnModelBuildRequest) {
		logger.info("Received process request: {}",churnModelBuildRequest);
		ChurnModelBuildProcess churnModelBuildProcess = new ChurnModelBuildProcess(this, churnModelBuildRequest);
		sparkProcesses.add(churnModelBuildProcess);
		return churnModelBuildProcess.getId();
	}

	@Scheduled(fixedRate = 10000)
	private void schedule() {
		try {
			logger.info("Spark processes awaiting: {}", sparkProcesses.size());
			if ((currentProcess == null || currentProcess.getStatus() == SparkProcessStatus.FINISHED)
					&& sparkProcesses.size() > 0) {
				logger.info("Polled spark process");
				currentProcess = sparkProcesses.poll();
				startProcessing(currentProcess);
			} else {
				logger.info("No action in scheduler.");
			}

		} catch (Exception exc) {
			logger.error(ExceptionUtils.getFullStackTrace(exc));
		}
	}

	private void startProcessing(SparkProcess sparkProcess) {
		logger.info("Start process spark process with id: {}", sparkProcess.getId());
		threadPoolService.startThread(new WorkerThread(sparkProcess));
	}

	public UserEventService getUserEventService() {
		return userEventService;
	}

	public String process(ChurnPredictRequest churnPredictRequest) {
		logger.info("Received churn predict request: {}", churnPredictRequest);
		ChurnPredictionProcess churnPredictProcess = new ChurnPredictionProcess(this,churnPredictRequest);
		sparkProcesses.add(churnPredictProcess);
		return churnPredictProcess.getId();
	}

	public Gson getGson() {
		return gson;
	}
}
