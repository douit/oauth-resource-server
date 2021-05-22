package com.rkc.zds.resource.config;

import java.util.HashMap;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.hibernate.dialect.DerbyTenSevenDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = { "com.rkc.zds" }, excludeFilters = { @ComponentScan.Filter(value = Controller.class, type = FilterType.ANNOTATION) })
@EnableJpaRepositories(
	    basePackages = "com.rkc.zds.resource.repository", 
	    entityManagerFactoryRef = "pcmEntityManager", 
	    transactionManagerRef = "pcmTransactionManager"
	)
@EnableTransactionManagement
@EnableSpringDataWebSupport
@EnableWebSecurity
public class PersistencePcmConfiguration {

	@Bean
	@Primary
	public DataSource pcmDataSource() {
		
		EmbeddedDriver driver;
		SimpleDriverDataSource db = null;
		try {
			driver = (EmbeddedDriver)Class.forName(EmbeddedDriver.class.getName()).newInstance();
			db = new SimpleDriverDataSource(driver, "jdbc:derby:/_/data/pcm/derbyDB", "PCM", "PCM");
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return db;
	}    

    @Bean(name = "pcmEntityManager")
    @Primary
    public LocalContainerEntityManagerFactoryBean pcmEntityManager() {
        LocalContainerEntityManagerFactoryBean em
          = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(pcmDataSource());
        em.setPackagesToScan(
          new String[] { "com.rkc.zds.resource.entity" });

        HibernateJpaVendorAdapter vendorAdapter
          = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect",
        		DerbyTenSevenDialect.class.getName());
        em.setJpaPropertyMap(properties);

        return em;
    }
    
    @Bean
	@Primary
    public JpaVendorAdapter pcmJpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.DERBY);
        return hibernateJpaVendorAdapter;
    }
   
    @Bean
    @Primary
    public PlatformTransactionManager pcmTransactionManager() {
 
        JpaTransactionManager transactionManager
          = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
        		pcmEntityManager().getObject());
        return transactionManager;
    }
    
    @Bean
	@Primary
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
