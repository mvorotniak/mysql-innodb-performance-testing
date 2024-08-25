package com.demo.mysqlinnodb.service;

import com.demo.mysqlinnodb.util.QueryUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Slf4j
@Service
public class JdbcService {

    private static final int BATCH_SIZE = 10_000;

    private final Connection connection;

    public JdbcService(@Value("${mysql.database.url}") String mysqlDatabaseUrl,
                       @Value("${mysql.username}") String mysqlUsername,
                       @Value("${mysql.password}") String mysqlPassword) {
        try {
            log.info("Connecting to database [{}]...", mysqlDatabaseUrl);
            this.connection = DriverManager.getConnection(mysqlDatabaseUrl, mysqlUsername, mysqlPassword);
            log.info("Successfully connected to database");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String randomSelectEquals() {
        return select(generateRandomDate().toLocalDate(), null, null, null);
    }

    public String randomSelectFromTo() {
        return select(null, generateRandomDate().toLocalDate(), generateRandomDate().toLocalDate(), null);
    }

    public String randomSelectIn() {
        return select(null, null, null, List.of(generateRandomDate().toLocalDate(),
                generateRandomDate().toLocalDate(),
                generateRandomDate().toLocalDate()));
    }
    
    @SneakyThrows
    public String select(LocalDate date, LocalDate from, LocalDate to, List<LocalDate> in) {
        if (Objects.nonNull(date)) {
            try (PreparedStatement statement = connection.prepareStatement(QueryUtils.SELECT_EQUALS)) {
                statement.setDate(1, getSqlDate(date));
                ResultSet resultSet = statement.executeQuery();
                log.info(QueryUtils.SELECT_EQUALS);
                return getFirstResult(resultSet);
            }
        } else if (Objects.nonNull(from) && Objects.nonNull(to)) {
            try (PreparedStatement statement = connection.prepareStatement(QueryUtils.SELECT_FROM_TO)) {
                statement.setDate(1, getSqlDate(from));
                statement.setDate(2, getSqlDate(to));
                ResultSet resultSet = statement.executeQuery();
                log.info(QueryUtils.SELECT_FROM_TO);
                return getFirstResult(resultSet);
            }
        } else if (Objects.nonNull(in)) {
            try (PreparedStatement statement = connection.prepareStatement(QueryUtils.SELECT_IN)) {
                for (int i = 1; i < in.size() + 1; i++) {
                    statement.setDate(i, getSqlDate(in.get(i - 1)));
                }
                ResultSet resultSet = statement.executeQuery();
                log.info(QueryUtils.SELECT_IN);
                return getFirstResult(resultSet);
            }
        }
        return null;
    }

    private String getFirstResult(ResultSet resultSet) throws SQLException {
        return resultSet.next() ? resultSet.getString("id") : null;
    }

    @SneakyThrows
    public void randomInsert() {
        try (PreparedStatement statement = connection.prepareStatement(QueryUtils.INSERT)) {
            Date date = generateRandomDate();
            statement.setDate(1, date);
            statement.executeUpdate();
            log.info(QueryUtils.INSERT.replace("?", date.toString()));
        }
    }

    public void performBatchInsert() {
        try (PreparedStatement statement = connection.prepareStatement(QueryUtils.INSERT)) {
            connection.setAutoCommit(false);

            IntStream.range(1, 40_001).forEach(i -> {
                try {
                    Date date = generateRandomDate();
                    statement.setDate(1, date);
                    statement.addBatch();

                    if (i % BATCH_SIZE == 0) {
                        statement.executeBatch();
                        connection.commit();
                        log.info("Batch {} executed and committed.", i / BATCH_SIZE);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            statement.executeBatch();
            connection.commit();
            log.info("Finished Batch insert.");
        } catch (SQLException e) {
            log.error("Batch insert failed: {}", e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                log.error("Rollback failed: {}", rollbackEx.getMessage());
            }
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                log.error("Failed to restore auto-commit: {}", e.getMessage());
            }
        }
    }

    private Date getSqlDate(LocalDate date) {
        return Date.valueOf(date);
    }

    private Date generateRandomDate() {
        long startMillis = Date.valueOf("1970-01-01").getTime();
        long endMillis = System.currentTimeMillis();
        long randomMillis = ThreadLocalRandom.current().nextLong(startMillis, endMillis);

        return new Date(randomMillis);
    }
}
