package com.camp.applicationservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@PropertySource(value = { "classpath:application.properties" })
@EnableCassandraRepositories
public class CassandraConfiguration extends AbstractCassandraConfiguration {
	
	@Autowired
	private Environment environment;

	@Bean
    public CassandraClusterFactoryBean cluster() {
        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean(); 
        cluster.setUsername(environment.getProperty("spring.data.cassandra.username"));
        cluster.setPassword(environment.getProperty("spring.data.cassandra.password"));
        cluster.setContactPoints(environment.getProperty("spring.data.cassandra.contactpoints"));
        cluster.setPort(Integer.parseInt(environment.getProperty("spring.data.cassandra.port")));
        return cluster;
    }
	
	@Override
	protected String getKeyspaceName() {
		return environment.getProperty("spring.data.cassandra.keyspace-name");
	}

	@Override
	public String[] getEntityBasePackages() {
		return new String[] { "com.camp.applicationservice.domain" };
	}

	@Override
	public SchemaAction getSchemaAction() {
		return SchemaAction.CREATE_IF_NOT_EXISTS;
	}

}
