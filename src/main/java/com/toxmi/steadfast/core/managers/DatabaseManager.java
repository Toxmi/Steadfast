package com.toxmi.steadfast.core.managers;

import com.toxmi.steadfast.Steadfast;
import com.toxmi.steadfast.core.utils.SQL;
import com.toxmi.steadfast.core.utils.Scheduler;
import org.bukkit.configuration.file.FileConfiguration;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.io.File;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final Steadfast plugin;
    private final Scheduler sch;

    private Connection connection;
    private RowSetFactory factory;
    private String dataBaseType;


    public DatabaseManager(String dataBaseType) {
        instance = this;
        this.dataBaseType = dataBaseType;
        this.plugin = Steadfast.get();
        this.sch = Scheduler.get();
    }

    public synchronized static DatabaseManager get() {
        if (instance == null) {
            instance = new DatabaseManager(Steadfast.get().getDatabaseType());
        }
        return instance;
    }

    public void connect() {

        FileConfiguration config = Steadfast.get().getConfig();
        String url;

        if (dataBaseType.equalsIgnoreCase("sqlite")) {
            File dbFile = new File(plugin.getDataFolder(), "SteadfastData.db");
            File parent = dbFile.getParentFile();
            if(parent != null) parent.mkdirs();
            url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        } else if (dataBaseType.equalsIgnoreCase("postgresql")) {
            url = String.format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s&ssl=true",
                    config.getString("database.host"),
                    config.getString("database.port"),
                    config.getString("database.database"),
                    config.getString("database.username"),
                    config.getString("database.password")
            );
        } else {
            throw new RuntimeException("Unsupported database type: " + config.getString("database.type"));
        }

        try {
            connection = DriverManager.getConnection(url);

            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to database at " + url);
        }
    }

    public Connection connection() {
        if (connection == null) {
            throw new IllegalStateException("Not connected to database");
        }
        return connection;
    }

    public void initTables() {
        try {
            try (Statement st = connection().createStatement()) {

            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize tables", e);
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                throw new RuntimeException("Failed to close database connection", e);
            }
            connection = null;
        }
    }

    private CachedRowSet createCachedRowSet() {
        try {
            if (factory == null) {
                factory = RowSetProvider.newFactory();
            }
            return factory.createCachedRowSet();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeUpdate(SQL sql, Object... parameters) {
        executeStatement(sql,false,parameters);
    }
    public void executeUpdate(String sql, Object... parameters) {
        executeStatement(sql, false, parameters);
    }
    public void executeUpdates(SQL sql, boolean commit, Object... parameters) {
        try {
            connection.setAutoCommit(false);
            executeStatement(sql,false,parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet executeResultStatement (SQL sqlQuery, Object... parameters) {
        return executeStatement(sqlQuery, true, parameters);
    }

    public ResultSet executeStatement(SQL sqlQuery,boolean result, Object... parameters) {
        return executeStatement(sqlQuery.toString(),result,parameters);
    }

    private synchronized ResultSet executeStatement(String sql,boolean result, Object... parameters) {

        try (PreparedStatement st = connection.prepareStatement(sql)) {

            for (int i = 0; i < parameters.length; i++) {
                st.setObject(i +1,parameters[i]);
            }

            if (result) {
                CachedRowSet results = createCachedRowSet();
                results.populate(st.executeQuery());
                return results;
            }
            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute statement: " + sql, e);
        }
        return null;
    }
    public void commit() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to commit transaction", e);
        }
    }
    //executeUpdate for INSERT, UPDATE, DELETE
    //ExecuteQuery for SELECT
}
