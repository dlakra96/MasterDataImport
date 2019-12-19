package com.csvprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component 
public class JobCompletionNotificationListener extends JobExecutionListenerSupport
{
	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
	
	/*
	 * private final JdbcTemplate jdbcTemplate;
	 * 
	 * public JobCompletionNotificationListener(DataSource dataSource) {
	 * this.jdbcTemplate = new JdbcTemplate(dataSource); }
	 */
	
	
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("======================= Finished with inserting lcoation detials from CSV file into database =======================");
		}
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		if(jobExecution.getStatus() == BatchStatus.STARTING) {
			log.info("======================= Starting with inserting location details from csv file into database =======================");
		}
	}
	
	
}