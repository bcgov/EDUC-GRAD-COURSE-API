package ca.bc.gov.educ.api.course;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.entity.CourseEntity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableCaching
public class EducCourseApiApplication {

	private static Logger logger = LoggerFactory.getLogger(EducCourseApiApplication.class);

	public static void main(String[] args) {
		logger.debug("########Starting API");
		SpringApplication.run(EducCourseApiApplication.class, args);
		logger.debug("########Started API");
	}

	@Bean
	public ModelMapper modelMapper() {

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.typeMap(CourseEntity.class, Course.class);
		modelMapper.typeMap(Course.class, CourseEntity.class);
		return modelMapper;
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
	@Configuration
	static
	class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	  /**
	   * Instantiates a new Web security configuration.
	   * This makes sure that security context is propagated to async threads as well.
	   */
	  public WebSecurityConfiguration() {
	    super();
	    SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
	  }
	  @Override
	  public void configure(WebSecurity web) {
	    web.ignoring().antMatchers("/v3/api-docs/**",
	            "/actuator/health","/actuator/prometheus",
	            "/swagger-ui/**", "/health");
	  }
	}
}