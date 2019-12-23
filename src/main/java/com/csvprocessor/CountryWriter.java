package com.csvprocessor;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class CountryWriter implements ItemWriter<Location> {
	
	private JdbcTemplate jdbcTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(CountryWriter.class);
	
	public CountryWriter(DataSource dataSource)
	{
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void write(List<? extends Location> locations) throws Exception {
		for(Location location : locations)
		{
			try{
				int country_id = jdbcTemplate.queryForObject("SELECT COUNTRY_ID FROM NE_COUNTRY_MASTER WHERE VC_COUNTRY_NAME ='" + location.getCountry().replace("'", "''") + "'",Integer.class);
				logger.info("Country with Name:- " + location.getCountry() + ", Id:- " + country_id + " already exists in ne_country_master table.");
			}
			catch(EmptyResultDataAccessException ex)
			{
				jdbcTemplate.update("INSERT INTO NE_COUNTRY_MASTER (VC_COUNTRY_NAME, IN_STATUS) VALUES ('" + location.getCountry().replace("'", "''") + "', 13)");
				logger.info("Insertion of new country with name '" + location.getCountry().toUpperCase() + "' in ne_country_master table successfull.");
			}
			
		}
	}
	
	
}