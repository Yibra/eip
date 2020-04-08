package com.fsg.fsgdata.eiprestlet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsg.fsgdata.eiprestlet.properties.ResourcesProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.servlet.http.HttpServlet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

@Configuration
public class ApplicationConfiguration {
    private static Logger log = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Bean
    public ServletRegistrationBean<HttpServlet> camelServletRegistrationBean() {
        ServletRegistrationBean<HttpServlet> registration =
                new ServletRegistrationBean<>(new CamelHttpTransportServlet(), "/api/*");
        registration.setName("CamelServlet");
        return registration;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        objectMapper.setDateFormat(df);
        return objectMapper;
    }

    @Bean(name = "json-jackson")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public JacksonDataFormat jacksonDataFormat(ObjectMapper objectMapper) {
        JacksonDataFormat format = new JacksonDataFormat(objectMapper, HashMap.class);
        format.setAutoDiscoverObjectMapper(true);
        return format;
    }

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.meta")
    public DataSourceProperties metaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("meta-ds")
    @Primary
    @ConfigurationProperties("app.datasource.meta.configuration")
    public HikariDataSource metaDataSource() {
        return metaDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties("app.datasource.demo")
    public DataSourceProperties demoDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("demo-ds")
    @FlywayDataSource
    @ConfigurationProperties("app.datasource.demo.configuration")
    public HikariDataSource demoDataSource() {
        return demoDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties("app.resources")
    public ResourcesProperties resources() {
        return new  ResourcesProperties();
    }

}
