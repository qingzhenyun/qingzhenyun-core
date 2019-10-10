package com.qingzhenyun.userfile.configuration

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
open class UserFileDataSourceConfiguration {
    // tag::configuration[]
    @Bean
    @Primary
    @ConfigurationProperties("user.datasource.main")
    open fun firstDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @Primary
    @ConfigurationProperties("user.datasource.main")
    open fun firstDataSource(): DataSource {
        return firstDataSourceProperties().initializeDataSourceBuilder().build()
    }

    /*
    @Bean
    @ConfigurationProperties("app.datasource.second")
    open fun secondDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @ConfigurationProperties("app.datasource.second")
    open fun secondDataSource(): DataSource {
        return secondDataSourceProperties().initializeDataSourceBuilder().build()
    }
    */
    // end::configuration[]
}