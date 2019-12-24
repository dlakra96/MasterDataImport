package com.csvprocessor;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
public class CsvFileToDBConfig{
	
@Autowired
public DataSource dataSource;

@Autowired
public JobBuilderFactory jobBuilderFactory;

@Autowired
public StepBuilderFactory stepBuilderFactory;

private static final String OVERRIDDEN_BY_EXPRESSION = null;

@Bean
@StepScope
public FlatFileItemReader<Location> csvLocationReader(@Value("#{jobParameters[filename]}") String pathToFile){
	
	FlatFileItemReader<Location> csvReader = new FlatFileItemReader<Location>();
	csvReader.setResource(new ClassPathResource(pathToFile));
	csvReader.setLineMapper(new DefaultLineMapper<Location>() {{
		setLineTokenizer(new DelimitedLineTokenizer() {{
			setNames(new String[] {"city_name", "state_name", "country_name"});
		}});
		setFieldSetMapper(new BeanWrapperFieldSetMapper<Location>() {{
			setTargetType(Location.class);
		}});
	}});
	return csvReader;
}

@Bean
public ItemWriter<Location> countryWriter(){
	return new CountryWriter(dataSource);
}

@Bean
public ItemWriter<Location> stateWriter(){
	return new StateWriter(dataSource);
}

@Bean
public ItemWriter<Location> cityWriter(){
	return new CityWriter(dataSource);
}

@Bean 
public ItemWriter<Location> locationWriter(){
	CompositeItemWriter<Location> compositeItemWriter = new CompositeItemWriter<>();
	compositeItemWriter.setDelegates(Arrays.asList(countryWriter(),stateWriter(),cityWriter()));
	return compositeItemWriter;	
}

@Bean
public Step csvFileToDatabaseStep() {
	return stepBuilderFactory.get("csvFileToDatabaseStep")
			                 .<Location,Location>chunk(200)
			                 .reader(csvLocationReader(OVERRIDDEN_BY_EXPRESSION))
			                 .writer(locationWriter())
			                 .taskExecutor(taskExecutor())
			                 .build();
}

@Bean
public TaskExecutor taskExecutor() {
	SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
	asyncTaskExecutor.setConcurrencyLimit(5);
	return asyncTaskExecutor;
}

@Bean
public Job csvFileToDatabaseJob(JobCompletionNotificationListener listener)
{
	return jobBuilderFactory.get("csvFileToDatabaseJob")
							.incrementer(new RunIdIncrementer())
							.listener(listener)
							.flow(csvFileToDatabaseStep())
							.end()
							.build();
}

@Bean
public JobExecutionListener listener() {
	return new JobCompletionNotificationListener();
}

}