package com.camp.sparkservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
	@Value("${spark.home}")
	private String sparkHome;

	@Value("${spark.master.uri}")
	private String masterUri;

	@Value("${spark.models.dir}")
	private String sparkModelsDir;

	public String getSparkHome() {
		return sparkHome;
	}

	public void setSparkHome(String sparkHome) {
		this.sparkHome = sparkHome;
	}

	public String getMasterUri() {
		return masterUri;
	}

	public void setMasterUri(String masterUri) {
		this.masterUri = masterUri;
	}

	public String getSparkModelsDir() {
		return sparkModelsDir;
	}

	public void setSparkModelsDir(String sparkModelsDir) {
		this.sparkModelsDir = sparkModelsDir;
	}

}
