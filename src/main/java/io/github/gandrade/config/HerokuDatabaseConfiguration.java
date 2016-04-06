package io.github.gandrade.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;

@Configuration
@Profile(Constants.SPRING_PROFILE_HEROKU)
public class HerokuDatabaseConfiguration {

    private final Logger log = LoggerFactory.getLogger(HerokuDatabaseConfiguration.class);

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        log.debug("Configuring Heroku Datasource");

        String herokuUrl = System.getenv("JDBC_DATABASE_URL");
        if (herokuUrl != null) {
            return DataSourceBuilder
                .create(dataSourceProperties.getClassLoader())
                .type(HikariDataSource.class)
                .url(herokuUrl)
                .build();
        } else {
            throw new ApplicationContextException("Heroku database URL is not configured, you must set $JDBC_DATABASE_URL");
        }
    }
}
