package com.csvprocessor;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class CityWriter implements ItemWriter<Location> {

	private JdbcTemplate jdbcTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(CityWriter.class);
	
	public CityWriter(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public void write(List<? extends Location> locations) throws Exception {
		for(Location location : locations)
		{
			int state_id = 0;
			try {
				/*deepanshu.l ==> country_id and state_id would not be null in any case as this city_writer has been scheduled to run after country_writer and state_writer*/
				
				int country_id = jdbcTemplate.queryForObject("SELECT COUNTRY_ID FROM NE_COUNTRY_MASTER WHERE VC_COUNTRY_NAME ='" + location.getCountry().replace("'", "''") + "'",Integer.class);
			    
				state_id = jdbcTemplate.queryForObject("SELECT STATE_ID FROM NE_STATE_MASTER WHERE VC_STATE = '" + location.getState().replace("'", "''") + "' AND VC_COUNTRY = " + country_id + "", Integer.class);
			    
				int city_id = jdbcTemplate.queryForObject("SELECT CITY_ID FROM NE_CITY_MASTER WHERE VC_CITY = '" + location.getCity().replace("'", "''")+ "' AND IN_STATE = " + state_id + "", Integer.class); 
			    
			    logger.info("City with name:- '" + location.getCity().toUpperCase() + "' and id:- " + city_id + " already exists in ne_city_master.");
			
			}
			catch(EmptyResultDataAccessException ex)
			{
				jdbcTemplate.update("INSERT INTO NE_CITY_MASTER (VC_CITY, IN_STATE, IN_STATUS) VALUES ('" + location.getCity().replace("'", "''") + "', " + state_id + ", 13)");
				
				logger.info("Insertion of City with name:- '" + location.getCity().toUpperCase() + "' successfull in ne_city_master");
			}
		}
	}
	
}