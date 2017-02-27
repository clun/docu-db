package fr.clunven.docu;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ff4j.FF4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for the batch.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Configuration
@EnableTransactionManagement
public class BatchDocuConfig {

    @Bean
    public FF4j getFF4j() {
        return new FF4j("ff4j.xml");
    }
    
    @Bean
    public DataSource basicDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(readProperty("db.sql.driverclassName"));
        dataSource.setUrl(readProperty("db.sql.url"));
        dataSource.setUsername(readProperty("db.sql.username"));
        dataSource.setPassword(readProperty("db.sql.password"));
        dataSource.setConnectionProperties("useUnicode=yes;characterEncoding=UTF-8;");
        return dataSource;
    }
    
    private String readProperty(String key) {
        return getFF4j().getPropertiesStore().readProperty(key).asString();
    }
}
