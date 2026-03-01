package com.chatbot.whatsapp_chatbot.insurance.chatanalytics.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.chatbot.whatsapp_chatbot.insurance.chatanalytics.repository",  // Where is PolicyRepository?
        entityManagerFactoryRef = "chatAnalyticsEntityManagerFactory",
        transactionManagerRef = "chatAnalyticsTransactionManager"
)

public class ChatAnalyticsDataSourceConfig {

    @Bean(name = "chatAnalyticsDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.chatanalytics")
    public DataSource chatAnalyticsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "chatAnalyticsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean chatAnalyticsEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("chatAnalyticsDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.chatbot.whatsapp_chatbot.insurance.chatanalytics.entity")  // Where is Policy entity?
                .persistenceUnit("chatAnalytics")
                .build();
    }

    @Bean(name = "chatAnalyticsTransactionManager")
    public PlatformTransactionManager chatAnalyticsTransactionManager(
            @Qualifier("chatAnalyticsEntityManagerFactory") LocalContainerEntityManagerFactoryBean chatAnalyticsEntityManagerFactory) {
        return new JpaTransactionManager(chatAnalyticsEntityManagerFactory.getObject());
    }
}
