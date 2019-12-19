package com.csvprocessor;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Profile("default")
@PropertySource("classpath:application.yml")

public class DBConfig {
	
	private static final String DB2_DATABASE_DRIVER_PROPERTY_NAME = "db2.db.driver";
	private static final String DB2_DATABASE_URL_PROPERTY_NAME = "db2.db.bluemix.url";
	private static final String DB2_DATABASE_USERNAME_PROPERTY_NAME = "db2.db.bluemix.username"; 
	private static final String DB2_DATABASE_PASSWORD_PROPERTY_NAME = "db2.db.bluemix.password";
	
	private static final Logger logger = LoggerFactory.getLogger("CsvAppLogger");
	
	@Resource
	private Environment env;
	
	@Bean
	@Qualifier("dataSource")
	public BasicDataSource getDataSource() {
		
		BasicDataSource dataSource = new BasicDataSource();
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		
		if(VCAP_SERVICES != null)
		{
			logger.error("::ERROR:: VCAP_SERVICES should always be null in local.");
		}
		else
		{
			logger.info("::SUCCESS:: VCAP_SERVICES found null. Initializing database with local DB properties.");
			System.out.println("=======================================");
			System.out.println("SPRING.BATCH.JOB.ENABLED ::- " + env.getRequiredProperty("spring.batch.job.enabled"));
			System.out.println("SPRING.MAIN.ALLOW_BEAN_DEFINITIONS_OVERRIDING ::- " + env.getRequiredProperty("spring.main.allow-bean-definition-overriding"));
			dataSource.setDriverClassName(env.getRequiredProperty(DB2_DATABASE_DRIVER_PROPERTY_NAME));
			dataSource.setUrl(env.getRequiredProperty(DB2_DATABASE_URL_PROPERTY_NAME));
			dataSource.setUsername(env.getRequiredProperty(DB2_DATABASE_USERNAME_PROPERTY_NAME));
			dataSource.setPassword(env.getRequiredProperty(DB2_DATABASE_PASSWORD_PROPERTY_NAME));
			dataSource.setMinIdle(5);
			dataSource.setMaxIdle(10);
		}
		return dataSource;
	
	}
	
	@Bean
	NamedParameterJdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}
}