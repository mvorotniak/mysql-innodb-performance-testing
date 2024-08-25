package com.demo.mysqlinnodb.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class QueryUtils {
    
    public final String SELECT_EQUALS = "SELECT * FROM users WHERE birth_date = ?";
    
    public final String SELECT_FROM_TO = "SELECT * FROM users WHERE birth_date >= ? AND birth_date <= ?";
    
    public final String SELECT_IN = "SELECT * FROM users WHERE birth_date IN (?, ?, ?)";
    
    public final String INSERT = "INSERT INTO users (birth_date) VALUES (?)";
}
