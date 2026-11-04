package org.demo.minicore.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@PropertySource("file:C:/Users/Solum_BLR_IND/Desktop/externalConfig/application.properties")
public class DbConfig {

    @Value("${database.ip}")
    private String ip;

    @Value("${database.port}")
    private String port;

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Value("${database.name}")
    private String databaseName;

    @Value("${database.platform}")
    private String plaform;

    @Value("${sqldatabase.port}")
    private String sqlport;

    @Value("${sqldatabase.username}")
    private String sqlusername;

    @Value("${sqldatabase.password}")
    private String sqlpassword;



//    @Value("${core.datasource.url}")
//    private String url;
//    @Value("${core.datasource.username}")
//    private String username;
//
//    @Value("${core.datasource.password}")
//    private String password;



//    @Bean
//    @ConfigurationProperties(prefix = "core.datasource")
//    @Primary

    // Spring executes this method and registers the returned DataSourceProperties object in its container.
//    public DataSourceProperties dataSourceProperties(){
//        return new DataSourceProperties();
//
//    }

    @Bean
    public DataSource dataSource(){


        HikariDataSource dataSource =new HikariDataSource();
        String driver;

        switch (plaform){
            case "PostgreSQL95":
                String jdbcUrl = "jdbc:postgresql://" + ip + ":"
                        + port + "/" + databaseName;

                driver = "org.postgresql.Driver";

                dataSource.setJdbcUrl(jdbcUrl);
                dataSource.setUsername(username);
                dataSource.setPassword(password);
                dataSource.setDriverClassName(driver);
                break;

            case "SQLServer2025":
                String url = "jdbc:sqlserver://" + ip + ":" + sqlport
                        + ";databaseName=" + databaseName + ";encrypt=true;trustServerCertificate=true";
                driver ="com.microsoft.sqlserver.jdbc.SQLServerDriver";
                dataSource.setDriverClassName(driver);
                dataSource.setJdbcUrl(url);
                dataSource.setUsername(sqlusername);
                dataSource.setPassword(sqlpassword);
        }



      return dataSource;
    }


//    method creates the actual DataSource.
//    @Bean
//    public HikariDataSource dataSource(
//            DataSourceProperties properties){
//
//        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//    }


}
