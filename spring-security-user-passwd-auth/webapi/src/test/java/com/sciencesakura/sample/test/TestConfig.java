package com.sciencesakura.sample.test;

import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.destination.Destination;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TestConfig {

  @Bean
  Destination destination(DataSource dataSource) {
    return DataSourceDestination.with(dataSource);
  }
}
