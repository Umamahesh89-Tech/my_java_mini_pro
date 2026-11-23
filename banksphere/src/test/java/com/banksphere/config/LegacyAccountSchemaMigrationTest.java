package com.banksphere.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LegacyAccountSchemaMigrationTest {

    @Test
    void copiesLegacyDatesAndRelaxesObsoleteRequiredColumn() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                "jdbc:h2:mem:account-schema-migration;DB_CLOSE_DELAY=-1", "sa", "");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("CREATE TABLE accounts (opened_at DATE, opened_on DATE NOT NULL)");
        jdbcTemplate.update(
                "INSERT INTO accounts (opened_at, opened_on) VALUES (?, ?)",
                null, Date.valueOf("2026-01-15"));

        new LegacyAccountSchemaMigration(jdbcTemplate).run(new DefaultApplicationArguments(new String[0]));

        assertEquals(Date.valueOf("2026-01-15"),
                jdbcTemplate.queryForObject("SELECT opened_at FROM accounts", Date.class));
        jdbcTemplate.update("INSERT INTO accounts (opened_at) VALUES (?)", Date.valueOf("2026-02-01"));
        assertEquals(2, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM accounts", Integer.class));
    }
}
