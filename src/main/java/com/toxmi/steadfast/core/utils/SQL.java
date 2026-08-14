package com.toxmi.steadfast.core.utils;

import com.toxmi.steadfast.Steadfast;

public enum SQL {
    CREATE_CLAIM_TABLE
            ("""
                    CREATE TABLE IF NOT EXISTS Claims (
                        claimID TEXT PRIMARY KEY,
                        claimName TEXT NOT NULL UNIQUE,
                        claimPower INTEGER DEFAULT 0,
                        claimChestLocation TEXT DEFAULT NULL /* x;y;z;world*/
                    );
            """,
            """
                    CREATE TABLE IF NOT EXISTS Claims (
                        claimID TEXT PRIMARY KEY,
                        claimName TEXT NOT NULL UNIQUE,
                        claimPower INTEGER DEFAULT 0,
                        claimChestLocation TEXT DEFAULT NULL
                    );
            """
            ),
    CREATE_PLAYER_TABLE
            ("""
                        
            """,
            """
            
            """
            );

    private final String sqliteQuery;
    private final String postgresqlQuery;

    SQL(String sqlite, String postgresql) {
        sqliteQuery = sqlite;
        postgresqlQuery = postgresql;

    }
    @Override
    public String toString() {
        return Steadfast.get().getDatabaseType().equalsIgnoreCase("sqlite") ? sqliteQuery : postgresqlQuery;
    }
}
