package com.hcmute.jwtnimbus.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Tự tạo database SQL Server trước khi Hibernate khởi tạo Entity.
 * Nhờ vậy chỉ cần import project và Run, không cần chạy file SQL trước.
 */
@Configuration
public class DataSourceConfiguration {

    @Value("${app.database.host}")
    private String host;

    @Value("${app.database.port}")
    private int port;

    @Value("${app.database.name}")
    private String databaseName;

    @Value("${app.database.username}")
    private String username;

    @Value("${app.database.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        validateDatabaseName(databaseName);
        createDatabaseIfMissing();

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setJdbcUrl(buildJdbcUrl(databaseName));
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(1);
        dataSource.setPoolName("JwtNimbusSqlServerPool");
        return dataSource;
    }

    private void createDatabaseIfMissing() {
        String masterUrl = buildJdbcUrl("master");
        String sql = "IF DB_ID(N'" + databaseName + "') IS NULL CREATE DATABASE [" + databaseName + "]";

        try (Connection connection = DriverManager.getConnection(masterUrl, username, password);
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
            System.out.println("[JWT NIMBUS] Database sẵn sàng: " + databaseName);
        } catch (SQLException ex) {
            throw new IllegalStateException(
                "Không thể kết nối SQL Server hoặc tạo database '" + databaseName + "'. " +
                "Hãy kiểm tra SQL Server đang chạy, TCP/IP port " + port +
                ", tài khoản '" + username + "' và mật khẩu trong application-local.properties.", ex);
        }
    }

    private String buildJdbcUrl(String dbName) {
        return "jdbc:sqlserver://" + host + ":" + port +
            ";databaseName=" + dbName +
            ";encrypt=true;trustServerCertificate=true" +
            ";sendStringParametersAsUnicode=true";
    }

    private void validateDatabaseName(String name) {
        if (name == null || !name.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("Tên database không hợp lệ: " + name);
        }
    }
}
