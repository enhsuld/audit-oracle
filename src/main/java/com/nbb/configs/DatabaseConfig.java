package com.nbb.configs;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan
@EnableTransactionManagement
public class DatabaseConfig extends WebMvcConfigurerAdapter {

  
  @Autowired
  private Environment env;
  
  @Autowired
  private DataSource dataSource;

  @Bean
  public LocalSessionFactoryBean sessionFactory() {
    LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
    sessionFactoryBean.setDataSource(dataSource);
    sessionFactoryBean.setPackagesToScan("com.nbb.models");
    Properties hibernateProperties = new Properties();
    hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
    hibernateProperties.put("hibernate.show_sql", "true");
    hibernateProperties.put("hibernate.enable_lazy_load_no_trans", "true");
    hibernateProperties.put("format_sql", "true");
    hibernateProperties.put("jadira.usertype.autoRegisterUserTypes", "true");
    hibernateProperties.put("jadira.usertype.javaZone", "UTC");
    hibernateProperties.put("jadira.usertype.databaseZone", "UTC");
    
    sessionFactoryBean.setHibernateProperties(hibernateProperties);
    
    return sessionFactoryBean;
  }
  
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("uploads/**").addResourceLocations("/uploads/");
	//	registry.addResourceHandler("upload-dir/**").addResourceLocations("/upload-dir/");
  }	

} 