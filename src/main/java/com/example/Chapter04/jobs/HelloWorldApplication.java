package com.example.Chapter04.jobs;

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
public class HelloWorldApplication {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	/*
	 * @Bean public Step step() { return
	 * this.stepBuilderFactory.get("step1").tasklet( (contribution, chunkContext) ->
	 * { System.out.println("Hello, world!"); return RepeatStatus.FINISHED;
	 * }).build(); }
	 */
	
	/*
	 * @Bean public JobParametersValidator validator() {
	 * DefaultJobParametersValidator validator = new
	 * DefaultJobParametersValidator(); validator.setRequiredKeys(new String[] {
	 * "fileName" }); validator.setOptionalKeys(new String[] { "name" }); return
	 * validator; }
	 */
	
	@Bean
	public CompositeJobParametersValidator validator() {
		CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
		DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator(
				new String[] { "fileName" }, 
				new String[] { "name", "run.id" });
		defaultJobParametersValidator.afterPropertiesSet();
		validator.setValidators(Arrays.asList(new ParameterValidator(), defaultJobParametersValidator));
		return validator;
	}
	
	@Bean
	public Job job() {
		return this.jobBuilderFactory.get("basicJob")
				.start(step1())
				.validator(validator())
				.incrementer(new RunIdIncrementer())
				.build();
	}
	
	public Step step1() {
		return this.stepBuilderFactory.get("step1").tasklet(helloworldTasklet(null,null)).build();
	}
	
	/*
	 * @Bean public Tasklet helloworldTasklet() { return (contribution,
	 * chunkContext) -> { String name = (String) chunkContext.getStepContext()
	 * .getJobParameters() .get("name");
	 * 
	 * System.out.println(String.format("Hello, %s!", name)); return
	 * RepeatStatus.FINISHED; }; }
	 */
	
	@StepScope
	@Bean
	public Tasklet helloworldTasklet(@Value("#{jobParameters['name']}") String name,
									 @Value("#{jobParameters['fileName']}") String fileName) {
		return (contribution, chunkContext) -> {			
			System.out.println(String.format("Hello, %s!", name));
			System.out.println(String.format("filename = %s!", fileName));
			return RepeatStatus.FINISHED;
		};
	}


	

	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApplication.class, args);
	}

}
