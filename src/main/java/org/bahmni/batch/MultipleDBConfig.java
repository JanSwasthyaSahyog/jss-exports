package org.bahmni.batch;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MultipleDBConfig {


    @Bean(name = "emrDb")
    @ConfigurationProperties(prefix = "spring.ds_openmrs")
    @Primary
    public DataSource emrDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "emrJDBCTemplate")
    public NamedParameterJdbcTemplate jdbcTemplate(@Qualifier("emrDb") DataSource emrDb){
        return new NamedParameterJdbcTemplate(emrDb);
    }

    @Bean(name = "erpDb")
    @ConfigurationProperties(prefix = "spring.ds_openerp")
    public DataSource erpDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "erpJDBCTemplate")
    public NamedParameterJdbcTemplate erpJdbcTemplate(@Qualifier("erpDb") DataSource erpDb){
        return new NamedParameterJdbcTemplate(erpDb);
    }
}
