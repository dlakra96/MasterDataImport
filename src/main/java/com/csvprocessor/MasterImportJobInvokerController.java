package com.csvprocessor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MasterImportJobInvokerController {
	
	@Autowired
	JobLauncher jobLauncher;
	
	@Autowired
	Job csvDataImportJob;
	
	@RequestMapping("/invokeJob")
	public String invokeCsvImportJob(@RequestParam("fileName") String fileName) throws Exception {
		
		/* deepanshu.l ==> commenting the following piece of code to facilitate the supply of file name during application runtime.
		 *
		 * JobParameters jobParameters = new JobParametersBuilder().addLong("time",
		 * System.currentTimeMillis()).toJobParameters();
		 */
		
		JobParametersBuilder jobBuilder = new JobParametersBuilder();
		jobBuilder.addString("filename", fileName);
		jobBuilder.addLong("time", System.currentTimeMillis());
		jobLauncher.run(csvDataImportJob, jobBuilder.toJobParameters());
		
		return "Batch job has been initialized !!!!";
	}
}