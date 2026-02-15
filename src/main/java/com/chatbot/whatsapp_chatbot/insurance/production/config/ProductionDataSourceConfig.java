package com.chatbot.whatsapp_chatbot.insurance.production.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.chatbot.whatsapp_chatbot.insurance.production.repository",  // Where is PolicyRepository?
        entityManagerFactoryRef = "productionEntityManagerFactory",
        transactionManagerRef = "productionTransactionManager"
)


public class ProductionDataSourceConfig {

    @Primary
    @Bean(name = "productionDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.production")
    public DataSource productionDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "productionEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean productionEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("productionDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.chatbot.whatsapp_chatbot.insurance.production.entity")  // Where is Policy entity?
                .persistenceUnit("production")
                .build();
    }

    @Primary
    @Bean(name = "productionTransactionManager")
    public PlatformTransactionManager productionTransactionManager(
            @Qualifier("productionEntityManagerFactory") LocalContainerEntityManagerFactoryBean productionEntityManagerFactory) {
        return new JpaTransactionManager(productionEntityManagerFactory.getObject());
    }

}
