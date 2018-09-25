package com.camp.sparkservice.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.camp.sparkservice.domain.SparkJob;
import com.camp.sparkservice.service.SparkService;

import scala.Tuple2;

@Component
public class ExampleJob implements SparkJob {

	@Autowired
	private SparkService sparkService;
	
	@Override
	public void run() {
		System.out.println("Started!");
		 int slices = 10;
		    int n = 100000 * slices;
		    List<Integer> l = new ArrayList<>(n);
		    for (int i = 0; i < n; i++) {
		      l.add(i);
		    }

		    JavaRDD<Integer> dataSet = sparkService.getSparkContext().parallelize(l, slices);

		    int count = dataSet.map(integer -> {
		      double x = Math.random() * 2 - 1;
		      double y = Math.random() * 2 - 1;
		      return (x * x + y * y <= 1) ? 1 : 0;
		    }).reduce((integer, integer2) -> integer + integer2);

		    System.out.println("Pi is roughly " + 4.0 * count / n);

	}

}