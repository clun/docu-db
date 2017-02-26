package fr.clunven.docu;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ff4j.FF4j;
import org.ff4j.property.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Implementation to work with RDBMS through JDBC.
 * 
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
@Configuration
@ComponentScan({"fr.clunven.docu", "org.ff4j"})
@EnableTransactionManagement
public class CommonsConfig {
    
    @Autowired
    private FF4j ff4j;
    
    @Bean
    public DataSource basicDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(getPropertyAsString("db.sql.driverclassName"));
        dataSource.setUrl(getPropertyAsString("db.sql.url"));
        dataSource.setUsername(getPropertyAsString("db.sql.username"));
        dataSource.setPassword(getPropertyAsString("db.sql.password"));
        dataSource.setConnectionProperties("useUnicode=yes;characterEncoding=UTF-8;");
        return dataSource;
    }
    
    private String getPropertyAsString(String pName) {
        return getProperty(pName).asString();
    }
    
    private Property<?> getProperty(String pname) {
        return ff4j.getPropertiesStore().readProperty(pname);
    }
}