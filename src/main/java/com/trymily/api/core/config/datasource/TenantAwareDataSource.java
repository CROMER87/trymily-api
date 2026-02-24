package com.trymily.api.core.config.datasource;

import com.trymily.api.core.tenant.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

@Slf4j
public class TenantAwareDataSource extends DelegatingDataSource {

    public TenantAwareDataSource(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        setTenantContext(connection);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = super.getConnection(username, password);
        setTenantContext(connection);
        return connection;
    }

    private void setTenantContext(Connection connection) throws SQLException {
        UUID tenantId = TenantContextHolder.getTenantId();
        String databaseProductName = connection.getMetaData().getDatabaseProductName();
        
        // Skip session variable setting for non-Postgres databases (e.g., H2 during tests)
        if (!"PostgreSQL".equalsIgnoreCase(databaseProductName)) {
            log.trace("Skipping tenant context for non-PostgreSQL database: {}", databaseProductName);
            return;
        }

        try (Statement statement = connection.createStatement()) {
            if (tenantId != null) {
                log.debug("Setting session tenant context to {}", tenantId);
                statement.execute(String.format("SET app.current_tenant = '%s'", tenantId));
            } else {
                log.debug("No tenant context found, resetting session variable");
                statement.execute("RESET app.current_tenant");
            }
        }
    }
}
