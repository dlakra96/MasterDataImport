package com.csvprocessor;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class StateWriter implements ItemWriter<Location> {

	private JdbcTemplate jdbcTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(StateWriter.class);
	
	public StateWriter(DataSource dataSource)
	{
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void write(List<? extends Location> locations) throws Exception {
		for(Location location : locations)
		{
			int country_id = 0;
			
			try {
				 /*deepanshu.l ==> country_id would not come null in any case as this state_writer is scheduled after country_writer which would definitely ensure 
									that we have desired country present in ne_country_master table. */
				
				country_id = jdbcTemplate.queryForObject("SELECT COUNTRY_ID FROM NE_COUNTRY_MASTER WHERE VC_COUNTRY_NAME ='" + location.getCountry().replace("'", "''") + "'",Integer.class);
				
				int state_id = jdbcTemplate.queryForObject("SELECT STATE_ID FROM NE_STATE_MASTER WHERE VC_STATE = '" + location.getState().replace("'", "''") + "' AND VC_COUNTRY = " + country_id + "", Integer.class);
			
				logger.info("State with name:- '" + location.getState().toUpperCase() + "' and Id:- " + state_id + " already exists in ne_state_master table.");
			}
			catch(EmptyResultDataAccessException ex)
			{
				jdbcTemplate.update("INSERT INTO NE_STATE_MASTER (VC_STATE, VC_COUNTRY, IN_STATUS) VALUES ('" + location.getState().replace("'", "''") + "', "+ country_id + ", 13)");
				
				logger.info("insertion of new state with name :- '" + location.getState().toUpperCase() + "' successfull in ne_state_master table.");
			}
		}
	}
	
}