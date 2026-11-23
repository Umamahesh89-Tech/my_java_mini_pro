package com.banksphere.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@Component
public class LegacyAccountSchemaMigration implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(LegacyAccountSchemaMigration.class);

    private final JdbcTemplate jdbcTemplate;

    public LegacyAccountSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        Integer openedAtNullability = getColumnNullability("OPENED_AT");
        Integer openedOnNullability = getColumnNullability("OPENED_ON");
        if (openedAtNullability == null || openedOnNullability == null) {
            return;
        }

        int copiedRows = jdbcTemplate.update(
                "UPDATE accounts SET opened_at = opened_on WHERE opened_at IS NULL AND opened_on IS NOT NULL");
        if (openedOnNullability == DatabaseMetaData.columnNoNulls) {
            jdbcTemplate.execute("ALTER TABLE accounts ALTER COLUMN opened_on DROP NOT NULL");
            logger.info("Updated legacy accounts schema; copied {} opening dates and relaxed OPENED_ON", copiedRows);
        }
    }

    private Integer getColumnNullability(String columnName) {
        return jdbcTemplate.execute((ConnectionCallback<Integer>) connection -> {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet columns = metadata.getColumns(
                    connection.getCatalog(), connection.getSchema(), "ACCOUNTS", columnName)) {
                return columns.next() ? columns.getInt("NULLABLE") : null;
            }
        });
    }
}
